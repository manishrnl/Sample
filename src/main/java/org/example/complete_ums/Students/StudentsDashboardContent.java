package org.example.complete_ums.Students;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.NavigationManager;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StudentsDashboardContent implements Initializable {
    public ScrollPane mainScrollPane;
    LoadFrame loadFrame;
    Connection connection = DatabaseConnection.getConnection();
    AlertManager alertManager = new AlertManager();
    SessionManager sessionManager = SessionManager.getInstance();
    Button3DEffect button3DEffect;
    NavigationManager navigationManager = NavigationManager.getInstance();
    @FXML
    private Label totalActiveStudents, LastLoginOn, YourDepartments, YourCourses,
            TotalStudents, TotalStaff, errorMessageLabel, LibraryUsage, UpcomingExams, AttendancePercent, TotalAccountants,
            totalTeacher, totalAdmin, totalLibrarians, overallNotifications, yourNotifications;
    @FXML
    private BarChart<String, Number> userMonthlyAttendanceChart;

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
    int totActiveStudents = 0, totStudents = 0, totStaff = 0, totAdmin = 0, totAccountant = 0,
            totTeacher = 0,
            totLibrarian = 0, totPendingApproval = 0, totRejectedApproval = 0, readNotification = 0, unreadNotification = 0;
    int totalPresent = 0, totalAbsent = 0, totalLeave = 0, totalLate = 0, totalHalfDay = 0, presentToday = 0;

    public StudentsDashboardContent() throws SQLException {
    }

    int totalCount = 0;
    private final int userIdLoggedIn = sessionManager.getUserID();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getLastLoginAttempts();
        getTotalActiveStudents();
        totStudents = getTotalCount("Users", "Role", "Student");


        totAdmin = getTotalCount("Users", "Role", "Admin");


        totAccountant = getTotalCount("Users", "Role", "Accountant");


        totLibrarian = getTotalCount("Users", "Role", "Librarian");

        totTeacher = getTotalCount("Users", "Role", "Teacher");


        totStaff = getTotalCount("Users", "Role", "Staff");


        totPendingApproval = getTotalCount("Users", "Admin_Approval_Status", "Pending");
        totRejectedApproval = getTotalCount("Users", "Admin_Approval_Status", "Rejected");

        readNotification = getAdminNotificationCount("Read", userIdLoggedIn);
        unreadNotification = getAdminNotificationCount("Un-Read", userIdLoggedIn);
        yourNotifications.setText("Read :" + readNotification + "   Un -Read: " + unreadNotification);

        readNotification = getNotificationCount("Read");
        unreadNotification = getNotificationCount("Un-Read");
        overallNotifications.setText("Read :" + readNotification + "   Un -Read: " + unreadNotification);

        TotalStudents.setText(String.valueOf(totStudents));
        TotalAccountants.setText(String.valueOf(totAccountant));
        totalAdmin.setText(String.valueOf(totAdmin));
        totalTeacher.setText(String.valueOf(totTeacher));
        totalLibrarians.setText(String.valueOf(totLibrarian));
        TotalStaff.setText(String.valueOf(totStaff));

        setDepartmentAndCourses();
        getMonthlyUserAttendanceReport(userIdLoggedIn);
        populateMonthlyUserAttendanceBarChart(userMonthlyAttendanceChart, userIdLoggedIn);

    }


    private void getTotalActiveStudents() {

        String query = "SELECT COUNT(*) FROM Users WHERE Role = 'Student' AND User_Status = " +
                "'Active'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                int count = resultSet.getInt(1); // Get the first column of the first row
                totalActiveStudents.setText(String.valueOf(count));
            }
        } catch (Exception e) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Admin Says",
                    "Something weird happened while counting total Active Admins: " + e.getMessage());
        }
    }


    private void setDepartmentAndCourses() {

        String query = "select * from Students where User_Id=" + userIdLoggedIn;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                YourCourses.setText(resultSet.getString("Course"));
                YourDepartments.setText(resultSet.getString("Stream"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void populateMonthlyUserAttendanceBarChart(BarChart<String, Number> barChart, int userId) {
        barChart.setTitle("Your ViewAttendance Summary (This Month )");
        barChart.getData().clear();
        barChart.setLegendVisible(false);

        NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
        yAxis.setLabel("Number of Days");
        yAxis.setTickLabelFormatter(null); // Remove the percentage formatter.
        yAxis.setAutoRanging(true);        // Let JavaFX determine the best range for the axis.

        String query = "SELECT Status, COUNT(*) AS status_count " +
                "FROM Attendances " +
                "WHERE User_Id = ? AND MONTH(Attendance_Date) = MONTH(CURDATE()) AND YEAR(Attendance_Date) = YEAR(CURDATE()) " +
                "GROUP BY Status";

        try {
            Map<String, Integer> monthlyCounts = new HashMap<>();
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId); // Set the user ID parameter safely.
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String status = rs.getString("Status");
                    int count = rs.getInt("status_count");
                    monthlyCounts.put(status, count);
                }
            }
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            List<String> statuses = Arrays.asList("Present", "Absent", "Leave", "Late", "Half Day");
            for (String status : statuses) {
                int count = monthlyCounts.getOrDefault(status, 0);
                XYChart.Data<String, Number> data = new XYChart.Data<>(status, count);
                series.getData().add(data);
            }
            barChart.getData().add(series);
            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    String tooltipText = String.format("%d days recorded as %s",
                            data.getYValue().intValue(),
                            data.getXValue()
                    );
                    Tooltip.install(data.getNode(), new Tooltip(tooltipText));
                }
            }

        } catch (SQLException e) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Chart Error",
                    "Could not load your monthly attendance data: " + e.getMessage());
            e.printStackTrace();
        }


    }

    private int getAdminNotificationCount(String value, int userID) {
        String query = "SELECT COUNT(*) FROM Notifications WHERE Is_Read = ? AND " +
                "Target_User_Id =?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, value);
            pstmt.setInt(2, userID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching notification count: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return 0;
    }

    private int getNotificationCount(String value) {
        String query = "SELECT COUNT(*) FROM Notifications WHERE Is_Read = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, value);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching notification count: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return 0;
    }

    private void getLastLoginAttempts() {
        String lastLoginText = "N/A"; // Default value if no login record is found
        String query = "SELECT Last_Login FROM Authentication WHERE User_Id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userIdLoggedIn); // Set parameter for the query

            try (ResultSet rs = pstmt.executeQuery()) {


                if (rs.next()) {
                    // This logic runs on the background thread
                    LocalDateTime lastLoginOn = rs.getTimestamp("Last_Login").toLocalDateTime();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");
                    lastLoginText = lastLoginOn.format(formatter);
                }

                // A final variable is needed to be used inside the lambda
                final String finalLoginText = lastLoginText;

                // Schedule the successful UI update to run on the main FX thread
                Platform.runLater(() -> {
                    LastLoginOn.setText(finalLoginText);
                });
            }
        } catch (Exception ex) {
            Platform.runLater(() -> {

                LastLoginOn.setText("Null");
            });
        }

    }

    private int getTotalCount(String tableName, String columnName, String value) {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ?";
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

    private void getMonthlyUserAttendanceReport(int userIdLoggedIn) {
        String attendanceQuery = "SELECT Status, COUNT(Attendance_Id) AS NumberOfPeople " +
                "FROM Attendances " +
                "WHERE User_Id = ? AND MONTH(Attendance_Date) = MONTH(CURDATE()) AND YEAR(Attendance_Date) = YEAR(CURDATE()) " +
                "GROUP BY Status";

        // Initialize monthly counters
        int totalPresent = 0, totalAbsent = 0, totalLeave = 0, totalLate = 0, totalHalfDay = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(attendanceQuery)) {

            // 2. Safely set the logged-in user's ID as a parameter.
            pstmt.setInt(1, userIdLoggedIn);

            ResultSet rs = pstmt.executeQuery();
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            while (rs.next()) {
                String status = rs.getString("Status");
                int count = rs.getInt("NumberOfPeople");
                switch (status) {
                    case "Present" -> totalPresent = count;
                    case "Absent" -> totalAbsent = count;
                    case "Leave" -> totalLeave = count;
                    case "Late" -> totalLate = count;
                    case "Half Day" -> totalHalfDay = count;
                }
            }

            // Add data to the pie chart only if the count is greater than zero
            if (totalPresent > 0) pieChartData.add(new PieChart.Data("Present", totalPresent));
            if (totalAbsent > 0) pieChartData.add(new PieChart.Data("Absent", totalAbsent));
            if (totalLeave > 0) pieChartData.add(new PieChart.Data("Leave", totalLeave));
            if (totalLate > 0) pieChartData.add(new PieChart.Data("Late", totalLate));
            if (totalHalfDay > 0)
                pieChartData.add(new PieChart.Data("Half Day", totalHalfDay));

            // 3. Update calculation logic for the monthly report.
            int totalDaysInReport = totalPresent + totalAbsent + totalLeave + totalLate + totalHalfDay;
            int daysAttended = totalPresent + totalLate + totalHalfDay; // Days considered as "attended"
            double attendancePercentage = (totalDaysInReport == 0) ? 0 : ((double) daysAttended / totalDaysInReport) * 100;

            // 4. Update UI component titles and text for clarity.
            AttendancePieChart.setTitle("Total College day Opens this Month :" + totalDaysInReport);
            AttendancePercent.setText(String.format("%.2f%%", attendancePercentage));

            AttendancePieChart.setData(pieChartData);
            AttendancePieChart.setLabelsVisible(true);
            AttendancePieChart.setLegendVisible(true);

            // This logic correctly updates labels and tooltips, so it can be kept.
            pieChartData.forEach(data -> {
                String label = String.format("%s (%d)", data.getName(), (int) data.getPieValue());
                data.setName(label);
            });

            AttendancePieChart.getData().forEach(data -> {
                String originalName = data.getName().replaceAll(" \\(.*\\)$", "");
                double percentage = (totalDaysInReport == 0) ? 0 : (data.getPieValue() / totalDaysInReport) * 100;
                String tooltipMsg = String.format("%s: %d days (%.1f%%)",
                        originalName,
                        (int) data.getPieValue(),
                        percentage);

                Tooltip tooltip = new Tooltip(tooltipMsg);
                Tooltip.install(data.getNode(), tooltip);
            });

        } catch (SQLException e) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Error",
                    "Could not fetch your monthly attendance report:\n" + e.getMessage());
            e.printStackTrace();
        }
    }
}