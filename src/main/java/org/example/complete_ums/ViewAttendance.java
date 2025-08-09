package org.example.complete_ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.example.complete_ums.CommonTable.AttendanceTable;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ResourceBundle;

public class ViewAttendance implements Initializable {
    SessionManager sessionManager = SessionManager.getInstance();
    Connection connection = DatabaseConnection.getConnection();
    LoadFrame loadFrame;

    @FXML
    private Button applyFilterButton;
    @FXML
    private TextField searchByUserID;
    @FXML
    private TableView<AttendanceTable> attendanceTable;

    @FXML
    private TableColumn<AttendanceTable, Integer> colUserId, colAttendanceId;
    @FXML
    private TableColumn<AttendanceTable, String> colRole, colFathersName, colFullName,
            colRemarks,
            colStatus;
    @FXML
    private TableColumn<AttendanceTable, LocalTime> colTimeIn,
            colTimeOut;
    @FXML
    private TableColumn<AttendanceTable, LocalDate> colDate;
    @FXML
    private Label errorMessageLabel;

    @FXML
    private ComboBox<String> filterByRoleType, filterTypeComboBox, statusComboBox, monthComboBox;
    @FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    private DatePicker fromDatePicker, toDatePicker;

    public ViewAttendance() throws SQLException {
    }

    private ObservableList<AttendanceTable> masterData = FXCollections.observableArrayList();
    ObservableList<Integer> yearList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addNewUsersIntoAttendanceTable();
        errorMessageLabel.setText("");
        colDate.setCellValueFactory(cellData -> cellData.getValue().attendanceDateProperty());
        colRemarks.setCellValueFactory(cellData -> cellData.getValue().remarksProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colTimeIn.setCellValueFactory(cellData -> cellData.getValue().timeInProperty());
        colTimeOut.setCellValueFactory(cellData -> cellData.getValue().timeOutProperty());
        colAttendanceId.setCellValueFactory(cellData -> cellData.getValue().attendanceIdProperty().asObject());
        colUserId.setCellValueFactory(cellData -> cellData.getValue().userIdProperty().asObject());
        colFullName.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        colFathersName.setCellValueFactory(cellData -> cellData.getValue().fathersNameProperty());
        colRole.setCellValueFactory(cellData -> cellData.getValue().colRoleProperty());
        loadAttendanceData(sessionManager.getUserID());
        yearList.add(2022);
        yearList.add(2023);
        yearList.add(2024);
        int currentYear = LocalDate.now().getYear();
        if (!yearList.contains(currentYear))
            yearList.add(currentYear);
        yearComboBox.setItems(yearList);

        filterTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            if (newValue.equals("All")) {
                enableAllFields();
                errorMessageLabel.setText("");
                loadFrame.setMessage(errorMessageLabel, "Please Skip Filter By Month , fill " +
                        "other details to proceed .\n Irrelevant fields are set to Disable and you are only allowed to insert the valid details to start filtering...", "GREEN");
            } else if (newValue.equals("By Year")) {
                toggleEnableDisableOtherFields(true, true, false, true);
                errorMessageLabel.setText("");
                loadFrame.setMessage(errorMessageLabel, "Please select Filter By Year to " +
                        "proceed .\n Irrelevant fields are set to Disable and you are only " +
                        "allowed to insert the valid details to start filtering...", "GREEN");
            } else if (newValue.equals("Date Range")) {
                toggleEnableDisableOtherFields(true, true, true, false);
                errorMessageLabel.setText("");
                loadFrame.setMessage(errorMessageLabel, "Please Select To Date and From Date" +
                        " to proceed .\n Irrelevant fields are set to Disable and you are " +
                        "only allowed to insert the valid details to start filtering...", "GREEN");
            } else if (newValue.equals("By Month")) {
                toggleEnableDisableOtherFields(true, false, true, true);
                errorMessageLabel.setText("");
                loadFrame.setMessage(errorMessageLabel, "Please Select Filter by Month to " +
                        "proceed .\n Irrelevant fields are set to Disable and you are only " +
                        "allowed to insert the valid details to start filtering...", "GREEN");
            } else if (newValue.equals("By Status")) {
                toggleEnableDisableOtherFields(false, true, true, true);
                errorMessageLabel.setText("");
                loadFrame.setMessage(errorMessageLabel, "Please Select Filter By Status to " +
                        "proceed .\n Irrelevant fields are set to Disable and you are only " +
                        "allowed to insert the valid details to start filtering...", "GREEN");
            }
        });
        searchByUserID.textProperty().addListener((Observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                attendanceTable.setItems(masterData);
            } else {
                ObservableList<AttendanceTable> filteredList = FXCollections.observableArrayList();
                for (AttendanceTable record : masterData) {
                    if (record.getUserId() == Integer.parseInt(newValue)) {
                        filteredList.add(record);
                    }
                }
                attendanceTable.setItems(filteredList);
            }
        });
    }

    private void addNewUsersIntoAttendanceTable() {
        String query = "INSERT INTO Attendances (User_Id, Attendance_Date, Status, Remarks) " +
                "SELECT u.User_Id, CURDATE(), 'Present', 'Auto-added' " +
                "FROM Users u " +
                "WHERE NOT EXISTS ( " +
                "    SELECT 1 FROM Attendances a " +
                "    WHERE a.User_Id = u.User_Id AND a.Attendance_Date = CURDATE()" +
                ")";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int rowsInserted = stmt.executeUpdate();
            System.out.println("Inserted " + rowsInserted + " new users into Attendances table.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void enableAllFields() {
        statusComboBox.setDisable(false);
        monthComboBox.setDisable(true);
        yearComboBox.setDisable(false);
        fromDatePicker.setDisable(false);
        toDatePicker.setDisable(false);
    }

    private void toggleEnableDisableOtherFields(Boolean toggleStatusCombo, Boolean toggleMonthCombo, Boolean toggleyearCombo, Boolean toggleDateRange) {
        if (toggleStatusCombo) {
            statusComboBox.setDisable(toggleStatusCombo);
            monthComboBox.setDisable(toggleMonthCombo);
            yearComboBox.setDisable(toggleyearCombo);
            fromDatePicker.setDisable(toggleDateRange);
            toDatePicker.setDisable(toggleDateRange);
            statusComboBox.getSelectionModel().clearSelection();

        }
        if (toggleMonthCombo) {
            statusComboBox.setDisable(toggleStatusCombo);
            monthComboBox.setDisable(toggleMonthCombo);
            yearComboBox.setDisable(toggleyearCombo);
            fromDatePicker.setDisable(toggleDateRange);
            toDatePicker.setDisable(toggleDateRange);
            monthComboBox.getSelectionModel().clearSelection();
        }
        if (toggleyearCombo) {
            statusComboBox.setDisable(toggleStatusCombo);
            monthComboBox.setDisable(toggleMonthCombo);
            yearComboBox.setDisable(toggleyearCombo);
            fromDatePicker.setDisable(toggleDateRange);
            toDatePicker.setDisable(toggleDateRange);
            yearComboBox.getSelectionModel().clearSelection();
        }
        if (toggleDateRange) {
            statusComboBox.setDisable(toggleStatusCombo);
            monthComboBox.setDisable(toggleMonthCombo);
            yearComboBox.setDisable(toggleyearCombo);
            fromDatePicker.setDisable(toggleDateRange);
            toDatePicker.setDisable(toggleDateRange);
            fromDatePicker.setValue(null);
            toDatePicker.setValue(null);
        }
    }

    private void loadAttendanceData(int userID) {
        ObservableList<AttendanceTable> attendanceList = FXCollections.observableArrayList();
        errorMessageLabel.setText("");
        String query = "";
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        String userRole = sessionManager.getRole();


        try {
            if (userRole.equals("Student") || userRole.equals("Staff") || userRole.equals("Librarian") || userRole.equals("Accountant")) {
                // Only view their own attendance
                query = "SELECT a.User_Id, a.Attendance_Id, a.Attendance_Date, a.Time_In, a.Time_Out, a.Status, a.Remarks, " +
                        "CONCAT(u.First_Name, ' ', u.Last_Name) AS Full_Name, u.Fathers_Name, u.Role " +
                        "FROM Attendances a " +
                        "JOIN Users u ON a.User_Id = u.User_Id " +
                        "WHERE a.User_Id = ? " +
                        "ORDER BY a.Attendance_Date ASC";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, userID);

            } else if (userRole.equals("Teacher")) {
                // View their own and students' attendance
                query = "SELECT a.User_Id, a.Attendance_Id, a.Attendance_Date, a.Time_In, a.Time_Out, a.Status, a.Remarks, " +
                        "CONCAT(u.First_Name, ' ', u.Last_Name) AS Full_Name, u.Fathers_Name, u.Role " +
                        "FROM Attendances a " +
                        "JOIN Users u ON a.User_Id = u.User_Id " +
                        "WHERE u.Role = 'Student' OR a.User_Id = ? " +
                        "ORDER BY a.Attendance_Date ASC";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, userID);

            } else if (userRole.equals("Admin")) {
                // View attendance for all users
                query = "SELECT a.User_Id, a.Attendance_Id, a.Attendance_Date, a.Time_In, a.Time_Out, a.Status, a.Remarks, " +
                        "CONCAT(u.First_Name, ' ', u.Last_Name) AS Full_Name, u.Fathers_Name, u.Role " +
                        "FROM Attendances a " +
                        "JOIN Users u ON a.User_Id = u.User_Id " +
                        "ORDER BY a.Attendance_Date ASC";
                preparedStatement = connection.prepareStatement(query);
            }

            if (preparedStatement != null) {
                rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    AttendanceTable record = new AttendanceTable(
                            rs.getInt("User_Id"),
                            rs.getString("Full_Name"),
                            rs.getString("Fathers_Name"),
                            rs.getInt("Attendance_Id"),
                            rs.getDate("Attendance_Date").toLocalDate(),
                            rs.getTime("Time_In") != null ? rs.getTime("Time_In").toLocalTime() : null,
                            rs.getTime("Time_Out") != null ? rs.getTime("Time_Out").toLocalTime() : null,
                            rs.getString("Role"),
                            rs.getString("Status"),
                            rs.getString("Remarks")
                    );
                    attendanceList.add(record);
                }
            }

            masterData.setAll(attendanceList);
            attendanceTable.setItems(attendanceList);

        } catch (SQLException e) {
            e.printStackTrace();
            errorMessageLabel.setText("Failed to load attendance data.");
        } finally {
            try {
                if (rs != null) rs.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void applyFilteredResult(MouseEvent mouseEvent) {
        errorMessageLabel.setText("");

        Integer year;
        String monthName, statusCombo;
        LocalDate toDate, fromDate;
        String filterdCombo = filterTypeComboBox.getSelectionModel().getSelectedItem();
        if (filterdCombo != null) {
            switch (filterdCombo) {
                case "All":
                    year = yearComboBox.getSelectionModel().getSelectedItem();
                    fromDate = fromDatePicker.getValue();
                    toDate = toDatePicker.getValue();
                    monthName = monthComboBox.getSelectionModel().getSelectedItem();
                    statusCombo = statusComboBox.getSelectionModel().getSelectedItem();

                    if (year == null || fromDate == null || toDate == null || statusCombo == null) {
                        loadFrame.setMessage(errorMessageLabel, "Please make sure all " +
                                        "filters (Year, Date, Month, Status) are selected.......",
                                "RED");
                        return;
                    }

                    if (fromDate.isAfter(toDate)) {
                        LocalDate swap = fromDate;
                        fromDate = toDate;
                        toDate = swap;
                    }

                    // Start from full list
                    ObservableList<AttendanceTable> filtered = FXCollections.observableArrayList(masterData);

                    filtered = filtered.filtered(record -> record.getAttendanceDate().getYear() == year);
                    /* Above code is same as
                    for(AttendanceTable record:masterData){
                        if(record.getAttendanceDate().getYear() == year)
                            filtered.add(record);
                        attendanceTAble.setItemsa(filtered);
                    }
                     */
                    LocalDate finalFromDate = fromDate;
                    LocalDate finalToDate = toDate;
                    filtered = filtered.filtered(record -> {
                        LocalDate d = record.getAttendanceDate();
                        return (d.isEqual(finalFromDate) || d.isAfter(finalFromDate)) && (d.isEqual(finalToDate) || d.isBefore(finalToDate));
                    });


                    if (!statusCombo.equalsIgnoreCase("All")) {
                        filtered = filtered.filtered(record -> record.getStatus().equalsIgnoreCase(statusCombo));
                    }

                    attendanceTable.setItems(filtered);
                    break;


                case "By Year":
                    year = yearComboBox.getSelectionModel().getSelectedItem();
                    if (year == null) {
                        loadFrame.setMessage(errorMessageLabel, "Please selects Year from " +
                                "Drop Down Menu to proceed....", "RED");
                        return;
                    }
                    filterTableByYear(year);
                    break;
                case "Date Range":
                    toDate = fromDatePicker.getValue();
                    fromDate = toDatePicker.getValue();

                    if (toDate == null) {
                        loadFrame.setMessage(errorMessageLabel, "Please choose starting Date." +
                                "...", "RED");
                        return;
                    }
                    if (fromDate == null) {
                        loadFrame.setMessage(errorMessageLabel, "Please choose end Date." +
                                "...", "RED");
                        return;
                    }
                    filterTableByDateRange(fromDate, toDate);
                    break;
                case "By Month":
                    monthName = monthComboBox.getSelectionModel().getSelectedItem();
                    if (monthName == null) {
                        loadFrame.setMessage(errorMessageLabel, "Select Month Name to " +
                                "Proceed with filtering processes...", "RED");
                        return;
                    }
                    if (monthName.equalsIgnoreCase("All Months")) {
                        attendanceTable.setItems(masterData);
                    } else
                        filterTableByMonthName(monthName);
                    break;
                case "By Status":
                    statusCombo = statusComboBox.getSelectionModel().getSelectedItem();
                    if (statusCombo == null) {
                        loadFrame.setMessage(errorMessageLabel, "Please Select Status from " +
                                "Status Box to proceed Filtering process...", "RED");
                        return;
                    }
                    if (statusCombo.equals("All"))
                        attendanceTable.setItems(masterData);
                    else
                        filterTableByStatus(statusCombo);
                    break;

                case "By Role Type":
                    String roleType = filterByRoleType.getSelectionModel().getSelectedItem();
                    if (roleType == null) {
                        loadFrame.setMessage(errorMessageLabel, "Please select Role Type from " +
                                "Drop Down Menu to proceed....", "RED");
                        return;
                    }
                    filterTableByRoleType(roleType);
                    break;
            }

        } else
            loadFrame.setMessage(errorMessageLabel, "Please select Type of filter you want to Apply from Filter By DropDown Menu", "RED");

    }

    private void filterTableByRoleType(String roleType) {
        ObservableList<AttendanceTable> filtered = FXCollections.observableArrayList();
        for (AttendanceTable record : masterData) {
            if (record.getColRole().equals(roleType)) {
                filtered.add(record);
            }
            attendanceTable.setItems(filtered);
        }
    }

    private void filterTableByYear(int year) {
        ObservableList<AttendanceTable> filtered = FXCollections.observableArrayList();
        for (AttendanceTable records : masterData) {
            int tableYear = records.getAttendanceDate().getYear();
            if (tableYear == year) {
                filtered.add(records);
            }
        }
        attendanceTable.setItems(filtered);
    }

    private void filterTableByDateRange(LocalDate fromDate, LocalDate toDate) {
        ObservableList<AttendanceTable> filtered = FXCollections.observableArrayList();

        for (AttendanceTable record : masterData) {
            LocalDate date = record.getAttendanceDate();
            if ((date.isEqual(fromDate) || date.isAfter(fromDate)) && (date.isEqual(toDate) || date.isBefore(toDate)) || (date.isEqual(fromDate) || date.isAfter(toDate)) && (date.isEqual(toDate) || date.isBefore(fromDate)))
            //Above line also filters dete rane if entered in wrong format like 2025-07-19  to 2025-06-12
            {
                filtered.add(record);
            }
        }

        attendanceTable.setItems(filtered);
    }

    private void filterTableByMonthName(String monthName) {
        ObservableList<AttendanceTable> filtered = FXCollections.observableArrayList();

        try {
            Month selectedMonth = Month.valueOf(monthName.toUpperCase());

            for (AttendanceTable record : masterData) {
                if (record.getAttendanceDate().getMonth() == selectedMonth) {
                    filtered.add(record);
                }
            }

            attendanceTable.setItems(filtered);
        } catch (IllegalArgumentException e) {
            errorMessageLabel.setText("Invalid month selected: " + monthName);
        }
    }

    private void filterTableByStatus(String statusCombo) {
        ObservableList<AttendanceTable> filtered = FXCollections.observableArrayList();

        for (AttendanceTable record : masterData) {
            if (record.getStatus().equalsIgnoreCase(statusCombo)) {
                filtered.add(record);  // ✅ Add matching record
            }
            if (record.getStatus().equalsIgnoreCase("All")) {
                attendanceTable.setItems(filtered);

            }
        }
        attendanceTable.setItems(filtered); // ✅ Set filtered list to table
    }

}

