package org.example.complete_ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.example.complete_ums.CommonTable.AttendanceTable;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Objects;
import java.util.ResourceBundle;

public class MarkAttendance implements Initializable {
    Button3DEffect button3DEffect;
    AlertManager alertManager = new AlertManager();
    @FXML
    private ImageView usersImage;
    @FXML
    private ComboBox<String> attendanceStatusComboBox;
    @FXML
    private Label attendanceIdField, userIdField, dateField, timeInField, timeOutField, remarksField, fullNameField,
            fathersNameField;


    SessionManager sessionManager = SessionManager.getInstance();
    Connection connection = DatabaseConnection.getConnection();
    LoadFrame loadFrame;

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
    private ComboBox<String> filterTypeComboBox, roleComboBox, statusComboBox, monthComboBox;
    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private Button btnCancel, btnMarkAllAttendance, btnApplyFilter;
    @FXML
    private DatePicker fromDatePicker, toDatePicker;

    public MarkAttendance() throws SQLException {
    }

    private ObservableList<AttendanceTable> masterData = FXCollections.observableArrayList();
    ObservableList<Integer> yearList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button3DEffect.applyEffect(btnCancel, "/sound/hover.mp3");
        button3DEffect.applyEffect(btnApplyFilter, "/sound/hover.mp3");
        button3DEffect.applyEffect(btnMarkAllAttendance, "/sound/hover.mp3");
        errorMessageLabel.setText("");
        loadAttendanceData();
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
                toggleEnableDisableOtherFields(true, true, false, true, true);
                errorMessageLabel.setText("");
                loadFrame.setMessage(errorMessageLabel, "Please select Filter By Year to " +
                        "proceed .\n Irrelevant fields are set to Disable and you are only " +
                        "allowed to insert the valid details to start filtering...", "GREEN");
            } else if (newValue.equals("Date Range")) {
                toggleEnableDisableOtherFields(true, true, true, false, true);
                errorMessageLabel.setText("");
                loadFrame.setMessage(errorMessageLabel, "Please Select To Date and From Date" +
                        " to proceed .\n Irrelevant fields are set to Disable and you are " +
                        "only allowed to insert the valid details to start filtering...", "GREEN");
            } else if (newValue.equals("By Month")) {
                toggleEnableDisableOtherFields(true, false, true, true, true);
                errorMessageLabel.setText("");
                loadFrame.setMessage(errorMessageLabel, "Please Select Filter by Month to " +
                        "proceed .\n Irrelevant fields are set to Disable and you are only " +
                        "allowed to insert the valid details to start filtering...", "GREEN");
            } else if (newValue.equals("By Status")) {
                toggleEnableDisableOtherFields(false, true, true, true, true);
                errorMessageLabel.setText("");
                loadFrame.setMessage(errorMessageLabel, "Please Select Filter By Status to " +
                        "proceed .\n Irrelevant fields are set to Disable and you are only " +
                        "allowed to insert the valid details to start filtering...", "GREEN");
            } else if (newValue.equals("By Role Type")) {
                toggleEnableDisableOtherFields(true, true, true, true, false);
                errorMessageLabel.setText("");
                loadFrame.setMessage(errorMessageLabel, "Please Select Filter By Role Type " +
                        "to " +
                        "proceed .\n Irrelevant fields are set to Disable and you are only " +
                        "allowed to insert the valid details to start filtering...", "GREEN");
            }
        });

        attendanceTable.setOnMouseClicked(mouseEvent -> {
            AttendanceTable selected = attendanceTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int userID = selected.getColUserId();
                LocalDate date = LocalDate.now();
                LocalTime time = LocalTime.now();

                userIdField.setText(" " + userID);
                fullNameField.setText(selected.getFullName());
                fathersNameField.setText(selected.getFathersName());
                attendanceIdField.setText(" " + selected.getAttendanceId());
                timeInField.setText("10:00:00.762897500");
                timeOutField.setText("4:00:00.762897500");
                dateField.setText(" " + date);
                remarksField.setText(selected.getRemarks());
                attendanceStatusComboBox.setValue(selected.getStatus());
                loadUsersImage(userID);

            }
        });
    }

    private void loadUsersImage(int userID) {
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                String imageQuery = "SELECT Photo_URL FROM Users WHERE User_Id=?";
                try (PreparedStatement stmt = connection.prepareStatement(imageQuery)) {
                    stmt.setInt(1, userID);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        try (InputStream is = rs.getBinaryStream("Photo_URL")) {
                            if (is != null) {
                                return new Image(is); // Return the loaded image
                            }
                        }
                    }
                }
                return null;
            }
        };
        loadImageTask.setOnSucceeded(event -> {
            Image profileImage1 = loadImageTask.getValue(); // Get the result from the task

            if (profileImage1 != null) {
                usersImage.setImage(profileImage1);
            } else {
                Image defaultImage = new Image(getClass().getResourceAsStream("/org/example/complete_ums/Images/UserName.png"));
                usersImage.setImage(defaultImage);
            }
        });
        loadImageTask.setOnFailed(event -> {
            Throwable e = loadImageTask.getException();
            System.err.println("Error loading profile image: " + e.getMessage());
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load profile image",
                    "An error occurred while retrieving the image from the database.");
        });
        new Thread(loadImageTask).start();
    }

    private void enableAllFields() {
        statusComboBox.setDisable(false);
        monthComboBox.setDisable(true);
        yearComboBox.setDisable(false);
        fromDatePicker.setDisable(false);
        toDatePicker.setDisable(false);
        roleComboBox.setDisable(false);
    }

    private void toggleEnableDisableOtherFields(Boolean toggleStatusCombo, Boolean toggleMonthCombo, Boolean toggleyearCombo, Boolean toggleDateRange, Boolean toggleRoleCombo) {
        if (toggleStatusCombo) {
            statusComboBox.setDisable(toggleStatusCombo);
            monthComboBox.setDisable(toggleMonthCombo);
            yearComboBox.setDisable(toggleyearCombo);
            fromDatePicker.setDisable(toggleDateRange);
            toDatePicker.setDisable(toggleDateRange);
            statusComboBox.getSelectionModel().clearSelection();
            roleComboBox.setDisable(toggleRoleCombo);

        }
        if (toggleMonthCombo) {
            statusComboBox.setDisable(toggleStatusCombo);
            monthComboBox.setDisable(toggleMonthCombo);
            yearComboBox.setDisable(toggleyearCombo);
            fromDatePicker.setDisable(toggleDateRange);
            toDatePicker.setDisable(toggleDateRange);
            monthComboBox.getSelectionModel().clearSelection();
            roleComboBox.setDisable(toggleRoleCombo);
        }
        if (toggleyearCombo) {
            statusComboBox.setDisable(toggleStatusCombo);
            monthComboBox.setDisable(toggleMonthCombo);
            yearComboBox.setDisable(toggleyearCombo);
            fromDatePicker.setDisable(toggleDateRange);
            toDatePicker.setDisable(toggleDateRange);
            yearComboBox.getSelectionModel().clearSelection();
            roleComboBox.setDisable(toggleRoleCombo);
        }
        if (toggleDateRange) {
            statusComboBox.setDisable(toggleStatusCombo);
            monthComboBox.setDisable(toggleMonthCombo);
            yearComboBox.setDisable(toggleyearCombo);
            fromDatePicker.setDisable(toggleDateRange);
            toDatePicker.setDisable(toggleDateRange);
            fromDatePicker.setValue(null);
            toDatePicker.setValue(null);
            roleComboBox.setDisable(toggleRoleCombo);
        }
        if (toggleRoleCombo) {
            statusComboBox.setDisable(toggleStatusCombo);
            monthComboBox.setDisable(toggleMonthCombo);
            yearComboBox.setDisable(toggleyearCombo);
            fromDatePicker.setDisable(toggleDateRange);
            toDatePicker.setDisable(toggleDateRange);
            statusComboBox.getSelectionModel().clearSelection();
            roleComboBox.setDisable(toggleRoleCombo);
        }
    }

    private void loadAttendanceData() {
        ObservableList<AttendanceTable> attendanceList = FXCollections.observableArrayList();
        errorMessageLabel.setText("");
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());

        String query = " WITH RankedAttendances AS ( SELECT a.User_Id, a.Attendance_Id, a.Attendance_Date, a.Time_In, a.Time_Out, a.Status, a.Remarks, CONCAT(u.First_Name, ' ', u.Last_Name) AS Full_Name, u.Fathers_Name, u.Role, ROW_NUMBER() OVER (PARTITION BY a.User_Id ORDER BY a.Attendance_Date DESC, a.Time_In DESC) as rn FROM Attendances a JOIN Users u ON a.User_Id = u.User_Id ) SELECT User_Id, Attendance_Id, Attendance_Date, Time_In, Time_Out, Status, Remarks, Full_Name, Fathers_Name, Role FROM RankedAttendances WHERE rn = 1 ORDER BY User_Id ASC ";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                AttendanceTable record = new AttendanceTable(rs.getInt("User_Id"),
                        rs.getString("Full_Name"), rs.getString("Fathers_Name"),
                        rs.getInt("Attendance_Id"),
                        rs.getDate("Attendance_Date").toLocalDate(),
                        rs.getTime("Time_In") != null ? (rs.getTime("Time_In").toLocalTime()) : null,
                        rs.getTime("Time_Out") != null ? (rs.getTime("Time_Out").toLocalTime()) : null,
                        rs.getString("Role"),
                        rs.getString("Status"),
                        rs.getString("Remarks")
                );
                attendanceList.add(record);
            }
            masterData.setAll(attendanceList);
            attendanceTable.setItems(attendanceList);
        } catch (SQLException e) {
            e.printStackTrace();
            errorMessageLabel.setText("Failed to load attendance data.");
        }
    }

    public void applyFilteredResult(MouseEvent mouseEvent) {
        errorMessageLabel.setText("");

        Integer year;
        String monthName, statusCombo, roleCombo;
        LocalDate toDate, fromDate;
        String filterdCombo = filterTypeComboBox.getSelectionModel().getSelectedItem();
        if (filterdCombo != null) {
            switch (filterdCombo) {
                case "All":
                    roleCombo = roleComboBox.getSelectionModel().getSelectedItem();
                    year = yearComboBox.getSelectionModel().getSelectedItem();
                    fromDate = fromDatePicker.getValue();
                    toDate = toDatePicker.getValue();
                    monthName = monthComboBox.getSelectionModel().getSelectedItem();
                    statusCombo = statusComboBox.getSelectionModel().getSelectedItem();

                    if (year == null || fromDate == null || toDate == null || statusCombo == null || roleCombo == null) {
                        loadFrame.setMessage(errorMessageLabel, "Please make sure all filters (Year, Date, Month, Status ,Role Type ) are selected.......", "RED");
                        return;
                    }

                    if (fromDate.isAfter(toDate)) {
                        LocalDate swap = fromDate;
                        fromDate = toDate;
                        toDate = swap;
                    }
                    ObservableList<AttendanceTable> filtered = FXCollections.observableArrayList(masterData);

                    filtered = filtered.filtered(record -> record.getAttendanceDate().getYear() == year);
                    /* Above code is same as
                    for(AttendanceTable record:masterData){
                        if(record.getAttendanceDate().getYear() == year)
                            filtered.add(record);
                        attendanceTAble.setItemsa(filtered);
                    }
                     */


                    filtered = filtered.filtered(record -> record.getColRole().equals(roleCombo));

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
                    roleCombo = roleComboBox.getSelectionModel().getSelectedItem();
                    System.out.println("Role:" + roleCombo);
                    filterTableByRoleType(roleCombo);
                    break;
            }

        } else
            loadFrame.setMessage(errorMessageLabel, "Please select Type of filter you want to Apply from Filter By DropDown Menu", "RED");

    }

    private void filterTableByRoleType(String roleCombo) {
        ObservableList<AttendanceTable> manisha = FXCollections.observableArrayList();
        for (AttendanceTable records : masterData) {
            if (Objects.equals(records.getColRole(), roleCombo)) {
                manisha.add(records);
            }
        }
        attendanceTable.setItems(manisha);
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

    public void handleCancel(ActionEvent actionEvent) {

    }

    public void handleMarkAllAttendance(ActionEvent actionEvent) {
    }
}

