package org.example.complete_ums.Admin;

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
import javafx.util.StringConverter;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.NavigationManager;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class AdminDashboardContent implements Initializable {
    public ScrollPane mainScrollPane;
    Connection connection = DatabaseConnection.getConnection();
    AlertManager alertManager = new AlertManager();
    SessionManager sessionManager = SessionManager.getInstance();
    Button3DEffect button3DEffect;
    NavigationManager navigationManager = NavigationManager.getInstance();


    @FXML
    private Label ActiveCourses, PendingApprovals, TotalFaculty, TotalStudents,
            TotalStaff, errorMessageLabel, TotalDepartments, LoginAttempts, Feedback, LibraryUsage, UpcomingExams, AttendancePercent, TotalAccountants, totalTeacher, totalAdmin, totalLibrarians, ReadUnreadNotifications, adminsNotifications;
    @FXML
    private BarChart<String, Number> individualAttendaceBarChart;

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


    int totStudents = 0, totStaff = 0, totAdmin = 0, totAccountant = 0, totTeacher = 0,
            totLibrarian = 0, totPendingApproval = 0, totRejectedApproval = 0, readNotification = 0, unreadNotification = 0;
    int totalPresent = 0, totalAbsent = 0, totalLeave = 0, totalLate = 0, totalHalfDay = 0, presentToday = 0;

    public AdminDashboardContent() throws SQLException {
    }

    int totalCount = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // --- Note: I have removed the static data for 'departmentsLineChart' ---
        // --- and replaced it with dynamic data for 'enrollmentsLineChart' below. ---

        getFailedAttempts();
        totStudents = getTotalCount("Users", "Role", "Student");
        totAdmin = getTotalCount("Users", "Role", "Admin");
        totAccountant = getTotalCount("Users", "Role", "Accountant");
        totLibrarian = getTotalCount("Users", "Role", "Librarian");
        totTeacher = getTotalCount("Users", "Role", "Teacher");
        totStaff = getTotalCount("Users", "Role", "Staff");

        totPendingApproval = getTotalCount("Users", "Admin_Approval_Status", "Pending");
        totRejectedApproval = getTotalCount("Users", "Admin_Approval_Status", "Rejected");

        readNotification = getAdminNotificationCount("Read", sessionManager.getUserID());
        unreadNotification = getAdminNotificationCount("Un-Read", sessionManager.getUserID());
        adminsNotifications.setText("Read :" + readNotification + "   Un -Read: " + unreadNotification);


        readNotification = getNotificationCount("Read");
        unreadNotification = getNotificationCount("Un-Read");
        ReadUnreadNotifications.setText("Read :" + readNotification + "   Un -Read: " + unreadNotification);
        PendingApprovals.setText(String.valueOf("" + (totPendingApproval + totRejectedApproval)));
        TotalStudents.setText(String.valueOf(totStudents));
        TotalAccountants.setText(String.valueOf(totAccountant));
        totalAdmin.setText(String.valueOf(totAdmin));
        totalTeacher.setText(String.valueOf(totTeacher));
        totalLibrarians.setText(String.valueOf(totLibrarian));
        TotalStaff.setText(String.valueOf(totStaff));

        // Call the methods to populate the charts
        getTotalAttendanceReport();
        populateAttendanceByRoleChart(); // --- NEW ---
    }

    // --- UPDATED METHOD ---
    /**
     * Fetches attendance data, calculates percentages for each status within a role,
     * and populates the BarChart. The Y-axis is formatted to show percentages.
     */
    private void populateAttendanceByRoleChart() {
        BarChart<String, Number> barChart = individualAttendaceBarChart;

        barChart.setTitle("Attendance Distribution by Role (This Year)");
        barChart.getData().clear();

        // --- Step 1: Configure the Y-Axis to display percentages ---
        NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
        yAxis.setLabel("Percentage (%)");
        // This formatter adds a "%" symbol to the axis labels
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.format("%.0f%%", object.doubleValue());
            }

            @Override
            public Number fromString(String string) {
                // Not needed for displaying, but required to implement
                return Double.parseDouble(string.replace("%", ""));
            }
        });
        // Set the axis range from 0% to 100%
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10); // Show a tick mark every 10%

        String query =
                "SELECT " +
                        "   u.Role, " +
                        "   a.Status, " +
                        "   COUNT(*) AS status_count " +
                        "FROM Attendances a " +
                        "JOIN Users u ON a.User_Id = u.User_Id " +
                        "WHERE YEAR(a.Attendance_Date) = YEAR(CURDATE()) " +
                        "GROUP BY u.Role, a.Status " +
                        "ORDER BY u.Role";

        try {
            // Step 2: Fetch raw count data (same as before)
            Map<String, Map<String, Integer>> dataMap = new HashMap<>();
            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String role = rs.getString("Role");
                    String status = rs.getString("Status");
                    int count = rs.getInt("status_count");
                    dataMap.computeIfAbsent(role, k -> new HashMap<>()).put(status, count);
                }
            }

            // Step 3: Create series using calculated percentages
            if (!dataMap.isEmpty()) {
                List<String> statuses = Arrays.asList("Present", "Absent", "Leave", "Late", "Half Day");
                for (String status : statuses) {
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(status);

                    for (String role : dataMap.keySet()) {
                        Map<String, Integer> roleData = dataMap.get(role);
                        int totalForRole = roleData.values().stream().mapToInt(Integer::intValue).sum();
                        int count = roleData.getOrDefault(status, 0);

                        // Calculate the percentage and use it as the Y-value for the bar
                        double percentage = (totalForRole == 0) ? 0 : ((double) count / totalForRole) * 100.0;

                        series.getData().add(new XYChart.Data<>(role, percentage));
                    }
                    barChart.getData().add(series);
                }
            }

            // Step 4: Add detailed tooltips (logic is now in the helper method)
            for (XYChart.Series<String, Number> series : barChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) {
                        // Pass the series to the helper to know the status
                        Tooltip.install(data.getNode(), createAttendanceTooltip(series, data, dataMap));
                    }
                }
            }

        } catch (SQLException e) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Chart Error",
                    "Could not load attendance by role data: " + e.getMessage());
            e.printStackTrace();
        }
    }


// --- UPDATED HELPER FUNCTION ---
    /**
     * Creates a formatted Tooltip that shows both the raw count and the percentage.
     *
     * @param series  The series the data point belongs to (to get the status name).
     * @param data    The specific data point (bar) to create a tooltip for.
     * @param dataMap The complete map of all raw attendance counts.
     * @return A configured Tooltip object.
     */
    private Tooltip createAttendanceTooltip(XYChart.Series<String, Number> series, XYChart.Data<String, Number> data, Map<String, Map<String, Integer>> dataMap) {
        String role = data.getXValue();
        String status = series.getName(); // Get status from the series name
        Map<String, Integer> roleData = dataMap.get(role);

        if (roleData == null || roleData.isEmpty()) {
            return new Tooltip("No data available for " + role);
        }

        int count = roleData.getOrDefault(status, 0);
        double percentage = data.getYValue().doubleValue(); // Get percentage directly from the bar's value

        String tooltipText = String.format("%s for %s: %d records (%.1f%%)",
                status,
                role,
                count,
                percentage
        );

        return new Tooltip(tooltipText);
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

    private void getFailedAttempts() {
        String query = "SELECT COUNT(*) AS failed FROM Authentication WHERE Lockout_Until IS NOT NULL";
        try (
                PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            int failedAttempts = 0;
            if (rs.next()) {
                failedAttempts = rs.getInt("failed");
            }
            LoginAttempts.setText("" + failedAttempts);
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
        // --- Note: Your original PieChart logic is preserved here ---
        String attendanceQuery = "SELECT s.StatusName AS Status, COUNT(a.Attendance_Id) AS NumberOfPeople FROM ( SELECT 'Present' AS StatusName UNION ALL SELECT 'Absent' UNION ALL SELECT 'Leave' UNION ALL SELECT 'Late' UNION ALL SELECT 'Half Day' " +
                ") AS s LEFT JOIN ( SELECT att.Attendance_Id, att.Status FROM Attendances att JOIN Users u ON att.User_Id = u.User_Id WHERE att.Attendance_Date = CURDATE() AND u.User_Status = 'Active' ) AS a ON s.StatusName = a.Status GROUP BY s.StatusName";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(attendanceQuery)) {
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
            if (totalPresent > 0) pieChartData.add(new PieChart.Data("Present", totalPresent));
            if (totalAbsent > 0) pieChartData.add(new PieChart.Data("Absent", totalAbsent));
            if (totalLeave > 0) pieChartData.add(new PieChart.Data("Leave", totalLeave));
            if (totalLate > 0) pieChartData.add(new PieChart.Data("Late", totalLate));
            if (totalHalfDay > 0)
                pieChartData.add(new PieChart.Data("Half Day", totalHalfDay));
            int totalStudents = totalPresent + totalAbsent + totalLeave + totalLate + totalHalfDay;
            int presentToday = totalPresent + totalLate + totalHalfDay;
            double attendancePercentage = (totalStudents == 0) ? 0 : ((double) presentToday / totalStudents) * 100;

            AttendancePieChart.setTitle("Today's Attendance Report: " + totalStudents);
            AttendancePercent.setText(String.format("%.2f%%", attendancePercentage));

            AttendancePieChart.setData(pieChartData);

            AttendancePieChart.setLabelsVisible(true);
            AttendancePieChart.setLegendVisible(true);

            pieChartData.forEach(data -> {
                String label = String.format("%s (%d) ", data.getName(),
                        (int) data.getPieValue());
                data.setName(label);
            });

            AttendancePieChart.getData().forEach(data -> {
                String originalName = data.getName().replaceAll(" \\(.*\\)$", "");
                double percentage = (totalStudents == 0) ? 0 : (data.getPieValue() / totalStudents) * 100;
                String tooltipMsg = String.format("%s: %d people (%.1f%%)",
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