package org.example.university_management_system.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.university_management_system.Databases.DatabaseConnection;
import org.example.university_management_system.Java_StyleSheet.Button3DEffect;
import org.example.university_management_system.ToolsClasses.AlertManager;
import org.example.university_management_system.ToolsClasses.NavigationManager;
import org.example.university_management_system.ToolsClasses.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class DashboardContent implements Initializable {
    public ScrollPane mainScrollPane;
    public Label LibraryUsage;
    public Label UpcomingExams;
    public Label AttendancePercent;
    AlertManager alertManager = new AlertManager();
    SessionManager sessionManager = SessionManager.getInstance();
    Button3DEffect button3DEffect;
    NavigationManager navigationManager = NavigationManager.getInstance();


    @FXML
    private Label ActiveCourses, PendingApprovals, TotalFaculty, TotalStudents,
            TotalStaff, errorMessageLabel, TotalDepartments, LoginAttempts, Feedback;
    @FXML
    private LineChart departmentsLineChart, enrollmentsLineChart;

    @FXML
    private PieChart AttendancePieChart;
    @FXML
    private AnchorPane contentArea;

    @FXML
    private MenuButton menuAdminProfile;

    @FXML
    private VBox root;

    @FXML
    private HBox titleBar;

    @FXML
    private TextField txtSearch;
    @FXML


    int totalStudents = 0, totalStudents1 = 0, totalStudents2 = 0, totalStudents3 = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[][] presentData = {
                {"Jan", "90"},
                {"Feb", "85"},
                {"Mar", "95"}
        };

        addSeriesToLineChart(departmentsLineChart, "Present", presentData);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Present", 23),
                new PieChart.Data("Absent", 24),
                new PieChart.Data("Leave", 53)
        );

        pieChartData.forEach(data -> {
            String label = String.format("%s (%.0f)", data.getName(), data.getPieValue());
            data.setName(label);
        });

        AttendancePieChart.setLabelsVisible(true);
        AttendancePieChart.setLegendVisible(true);

        AttendancePieChart.getData().addAll(pieChartData);

        getFailedAttempts();
        totalStudents = getTotalCount("Users", "Role", "Teacher");
        totalStudents1 = getTotalCount("Users", "Role", "Admin");
        totalStudents2 = getTotalCount("Users", "Role", "Accountant");
        totalStudents3 = getTotalCount("Users", "Role", "Librarian");
        TotalFaculty.setText(String.valueOf(totalStudents + totalStudents1 + totalStudents2 + totalStudents3));

        totalStudents = getTotalCount("Users", "Admin_Approval_Status", "Pending");
        totalStudents1 = getTotalCount("Users", "Admin_Approval_Status", "Rejected");
        PendingApprovals.setText(String.valueOf("Total Pending Approvals :" + (totalStudents + totalStudents1)));

        totalStudents = getTotalCount("Users", "Role", "Student");
        TotalStudents.setText(String.valueOf(totalStudents));

        totalStudents = getTotalCount("Users", "Role", "Staff");
        TotalStaff.setText(String.valueOf(totalStudents));
        getTotalAttendanceReport();
    }

    int totalPresent = 0, totalAbsent = 0, totalLeave = 0, totalLate = 0, totalHalfDay = 0,
            presentToday = 0;

    public void addSeriesToLineChart(LineChart<String, Number> lineChart, String legendName, String[][] dataPoints) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(legendName);

        for (String[] point : dataPoints) {
            String x = point[0]; // e.g. "Jan"
            Number y = Double.parseDouble(point[1]); // e.g. "85"
            series.getData().add(new XYChart.Data<>(x, y));
        }

        lineChart.getData().add(series);
    }
    private void getFailedAttempts() {
        String query = "SELECT COUNT(*) AS failed FROM Authentication WHERE Lockout_Until IS NOT NULL";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            int failedAttempts = 0;
            if (rs.next()) {
                failedAttempts = rs.getInt("failed");
            }
            LoginAttempts.setText("Failed Login Attempts : " + failedAttempts);
        } catch (Exception ex) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Admin Says",
                    "Something went wrong while counting failed login attempts:\n" + ex.getMessage());
        }
    }

    private int getTotalCount(String tableName, String columnName, String value) {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ? AND " +
                "User_Status = 'Active'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, value); // bind value (e.g., "Student")
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next())
                    return resultSet.getInt(1);
            }
        } catch (Exception ex) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Admin Says",
                    "Something weird happened while counting total records:\n " + query + " " +
                            ": " + ex.getMessage());
        }
        return 0;
    }

    private void getTotalAttendanceReport() {
        String attendanceQuery = "SELECT s.StatusName AS Status, COUNT(a.Attendance_Id) AS NumberOfPeople FROM ( SELECT 'Present' AS StatusName UNION ALL SELECT 'Absent' UNION ALL SELECT 'Leave' UNION ALL SELECT 'Late' UNION ALL SELECT 'Half Day' " +
                ") AS s LEFT JOIN ( SELECT att.Attendance_Id, att.Status FROM Attendances att JOIN Users u ON att.User_Id = u.User_Id WHERE att.Attendance_Date = CURDATE() AND u.User_Status = 'Active' ) AS a ON s.StatusName = a.Status GROUP BY s.StatusName";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(attendanceQuery)) {
            ResultSet rs = pstmt.executeQuery();
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            while (rs.next()) {
                String status = rs.getString("Status");
                int count = rs.getInt("NumberOfPeople"); // Use the count directly from the query
                switch (status) {
                    case "Present" -> totalPresent = count;
                    case "Absent" -> totalAbsent = count;
                    case "Leave" -> totalLeave = count;
                    case "Late" -> totalLate = count;
                    case "Half Day" -> totalHalfDay = count;
                }
            }
            if (totalPresent > 0) pieChartData.add(new PieChart.Data("Present", totalPresent));
            if (totalAbsent > 0) pieChartData.add(new PieChart.Data("Absent", totalAbsent));
            if (totalLeave > 0) pieChartData.add(new PieChart.Data("Leave", totalLeave));
            if (totalLate > 0) pieChartData.add(new PieChart.Data("Late", totalLate));
            if (totalHalfDay > 0)
                pieChartData.add(new PieChart.Data("Half Day", totalHalfDay));
            int totalStudents = totalPresent + totalAbsent + totalLeave + totalLate + totalHalfDay;
            int presentToday = totalPresent + totalLate + totalHalfDay;
            double attendancePercentage = (totalStudents == 0) ? 0 : ((double) presentToday / totalStudents) * 100;

            AttendancePieChart.setTitle("Total Attendance Report: " + totalStudents);
            AttendancePercent.setText(String.format("%.2f%%", attendancePercentage)); //  Format to 2 decimal place

            // 5. Set the initial data on the chart. At this point, the slice labels will be just the names (e.g., "Present").
            AttendancePieChart.setData(pieChartData);

            // 6. Set chart properties. Show both the legend AND the slice labels.
            AttendancePieChart.setLabelsVisible(true);
            AttendancePieChart.setLegendVisible(true);


            // This will update the legend text and pie chart Labels with value
            pieChartData.forEach(data -> {
                String label = String.format("%s (%d) ", data.getName(),
                        (int) data.getPieValue());
                data.setName(label);
            });

            // 8. Add interactive tooltips to each slice for a better user experience.
            // The tooltip will show the precise count and percentage on hover.
            AttendancePieChart.getData().forEach(data -> {
                // Extract the original status name (e.g., from "Present (35)") and clean it up
                String originalName = data.getName().replaceAll(" \\(.*\\)$", "");
                double percentage = (totalStudents == 0) ? 0 : (data.getPieValue() / totalStudents) * 100;
                String tooltipMsg = String.format("%s: %d students (%.1f%%)",
                        originalName,
                        (int) data.getPieValue(),
                        percentage);

                Tooltip tooltip = new Tooltip(tooltipMsg);
                Tooltip.install(data.getNode(), tooltip);
            });

        } catch (SQLException e) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Admin Says",
                    "Something went wrong while fetching attendance report:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

}