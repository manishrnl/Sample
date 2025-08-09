package org.example.complete_ums;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.complete_ums.CommonTable.LibrariansTable;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.ToolsClasses.LoadFrame;

import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SalaryDistributionController implements Initializable {
    public SalaryDistributionController() throws SQLException {
    }

    LoadFrame loadFrame;
    Connection connection = DatabaseConnection.getConnection();
    private LibrariansTable selectedEmployee;
    Button3DEffect button3DEffect;

    @FXML
    private Button calculateButton, saveButton, cancelButton;
    @FXML
    private TextField employeeNameField, employeeRoleField, baseSalaryField, totalDaysField, presentDaysField, unpaidLeaveDaysField, attendanceDeductionField, grossAmountField, totalDeductionsField, netSalaryField, remarksField;

    @FXML
    private TableColumn<LibrariansTable, String> colPan, colUserName, colFullName, colRoleType;
    @FXML
    private TableColumn<LibrariansTable, Long> colAadhar, colMobile;
    @FXML
    private TableColumn<LibrariansTable, Integer> colUserId;
    @FXML
    private TableView<LibrariansTable> salaryDeuctionsTable;
    @FXML
    private Label errorMessageLabel;

    ObservableList<LibrariansTable> masterData = FXCollections.observableArrayList();
    private List<Integer> notSkippedValue = new ArrayList<>();//this is best as compared to List.

    private List<Integer> allValue = new ArrayList<>();
    private List<Integer> skippedValue = new ArrayList<>();
    private Set<Integer> paidUsers = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button3DEffect.applyEffect(cancelButton, "/sound/hover.mp3");
        button3DEffect.applyEffect(saveButton, "/sound/hover.mp3");
        button3DEffect.applyEffect(calculateButton, "/sound/hover.mp3");
        colPan.setCellValueFactory(new PropertyValueFactory<>("colPan"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("colFullName"));
        colAadhar.setCellValueFactory(new PropertyValueFactory<>("colAadhar"));
        colMobile.setCellValueFactory(new PropertyValueFactory<>("colMobile"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("colUserId"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("colUserName"));
        colRoleType.setCellValueFactory(new PropertyValueFactory<>("colRoleType"));

        loadSalaryDistributionTable();

        salaryDeuctionsTable.setRowFactory(tv -> new TableRow<LibrariansTable>() {
            @Override
            protected void updateItem(LibrariansTable item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    int userId = item.getColUserId();
                    if (paidUsers.contains(userId)) {
                        setStyle("-fx-background-color: #487a3f;");
                    } else if (userId == (selectedEmployee != null ? selectedEmployee.getColUserId() : -1)) {
                        setStyle("-fx-background-color: #98873c;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        salaryDeuctionsTable.setOnMouseClicked(event -> {
            selectedEmployee = salaryDeuctionsTable.getSelectionModel().getSelectedItem();
            if (selectedEmployee != null) {
                clearFields();
                LocalDate previousMonthDate = LocalDate.now().minusMonths(1);

                employeeNameField.setText(selectedEmployee.getColFullName());
                employeeRoleField.setText(selectedEmployee.getColRoleType());

                fetchEmployeeSalaryAndAttendance(selectedEmployee.getColUserId(), previousMonthDate);
            }
        });


    }

    private void loadSalaryDistributionTable() {
        ObservableList<LibrariansTable> tableData = FXCollections.observableArrayList();
        String query = "SELECT u.User_Id, u.Role, u.Mobile, u.Pan, u.Aadhar, a.UserName, u.First_Name, u.Last_Name FROM Users u " +
                "JOIN Authentication a ON u.User_Id = a.User_Id " +
                "WHERE u.Role != 'Student'";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String firstName = resultSet.getString("First_Name");
                String lastName = resultSet.getString("Last_Name");
                int userID = resultSet.getInt("User_Id");
                long mobile = resultSet.getLong("Mobile");
                String pan = resultSet.getString("Pan");
                long aadhar = resultSet.getLong("Aadhar");
                String userName = resultSet.getString("UserName");
                String role = resultSet.getString("Role");
                LibrariansTable table = new LibrariansTable(pan, userName, firstName + " " + lastName, aadhar, mobile, userID, role);
                tableData.add(table);
            }

            masterData.setAll(tableData); // Replace instead of appending!
            salaryDeuctionsTable.setItems(masterData);

        } catch (SQLException e) {
            loadFrame.setMessage(errorMessageLabel, "Could not load data due to a technical " +
                    "issue: " + e.getMessage(), "RED");
            throw new RuntimeException("Error loading employee data", e);
        }
    }


    private void fetchEmployeeSalaryAndAttendance(int userId, LocalDate selectedDate) {
        YearMonth yearMonth = YearMonth.from(selectedDate);
        int year = yearMonth.getYear();
        int month = yearMonth.getMonthValue();
        int daysInMonth = yearMonth.lengthOfMonth();

        String query = "SELECT COALESCE(T.Salary, S.Salary, L.Salary, ACC.Salary) AS BaseSalary, " +
                "    COUNT(CASE WHEN A.Status IN ('Present', 'Late', 'Half Day','S.L','C.L') THEN 1 END) AS PaidDays, " +
                "    COUNT(CASE WHEN A.Status IN ('Absent', 'Leave') THEN 1 END) AS UnpaidLeaveDays " +
                "FROM Users U " +
                "LEFT JOIN Teachers T ON U.User_Id = T.User_Id AND U.Role = 'Teacher' " +
                "LEFT JOIN Staffs S ON U.User_Id = S.User_Id AND U.Role = 'Staff' " +
                "LEFT JOIN Admins AD ON U.User_Id = AD.User_Id AND U.Role = 'Admin' " +
                "LEFT JOIN Accountants ACC ON U.User_Id = ACC.User_Id AND U.Role = 'Accountant' " +
                "LEFT JOIN Librarians L ON U.User_Id = L.User_Id AND U.Role = 'Librarian' " +
                "LEFT JOIN Attendances A ON U.User_Id = A.User_Id AND YEAR(A.Attendance_Date) = ? AND MONTH(A.Attendance_Date) = ? " +
                "WHERE U.User_Id = ? " +
                "GROUP BY U.User_Id";

        String holidayQuery = "SELECT COUNT(*) AS Holidays FROM Events WHERE Event_Type = 'Holiday' AND YEAR(Event_Date) = ? AND MONTH(Event_Date) = ?";

        try (PreparedStatement ps = connection.prepareStatement(query);
             PreparedStatement holidayPs = connection.prepareStatement(holidayQuery)) {

            ps.setInt(1, year);
            ps.setInt(2, month);
            ps.setInt(3, userId);

            holidayPs.setInt(1, year);
            holidayPs.setInt(2, month);

            ResultSet rs = ps.executeQuery();
            ResultSet holidayRs = holidayPs.executeQuery();

            if (rs.next()) {
                double baseSalary = rs.getDouble("BaseSalary");
                long paidDays = rs.getLong("PaidDays");
                int unpaidLeaveDays = rs.getInt("UnpaidLeaveDays");
                long holidays = 0;
                if (holidayRs.next()) {
                    holidays = holidayRs.getLong("Holidays");
                }

                baseSalaryField.setText(String.format("%.2f", baseSalary));
                totalDaysField.setText(String.valueOf(daysInMonth));
                presentDaysField.setText(String.valueOf(paidDays + holidays));
                unpaidLeaveDaysField.setText(String.valueOf(unpaidLeaveDays));

                // The key calculation change
                double totalDeductionFromAttendance;
                double bonusAmount = 1000.00; // As per your logic
                double deductionRate = 0.03333; // 3.333%

                if (unpaidLeaveDays == 0) {
                    remarksField.setText("100 % Attendance. Congrats. Bonus Applied of 1,000 for this month");
                    totalDeductionFromAttendance = -bonusAmount;
                } else {
                    totalDeductionFromAttendance = baseSalary * deductionRate * unpaidLeaveDays;
                    remarksField.setText("You were absent for " + unpaidLeaveDays + " Days. No Bonus for this month");
                }

                attendanceDeductionField.setText(String.format("%.2f", totalDeductionFromAttendance));
                totalDeductionsField.setText(String.format("%.2f", totalDeductionFromAttendance)); //
                // Resetting deductions for manual input
            } else {
                loadFrame.setMessage(errorMessageLabel, "Failed to fetch salary data for " + "user ID: " + userId, "RED");
            }
        } catch (SQLException e) {
            loadFrame.setMessage(errorMessageLabel, "Failed to execute database query: " + e.getMessage(), "RED");
        }
    }

    @FXML
    private void calculateFinalSalary() {
        errorMessageLabel.setText("");
        try {
            double baseSalary = Double.parseDouble(baseSalaryField.getText());
            double attendanceDeduction = Double.parseDouble(attendanceDeductionField.getText());

            double totalDeductions = 0.0, grossAmount = 0.0;
            if (totalDeductionsField.getText() != null && !totalDeductionsField.getText().trim().isEmpty()) {
                totalDeductions = Double.parseDouble(totalDeductionsField.getText());
            }
            if (totalDeductions > 0)
                grossAmount = baseSalary - attendanceDeduction;
            else if (totalDeductions < 0)
                grossAmount = baseSalary - attendanceDeduction;

            double netSalary = grossAmount;
            netSalaryField.setText(String.format("%.2f", netSalary));
            grossAmountField.setText(String.format("%.2f", netSalary));

        } catch (NumberFormatException e) {
            loadFrame.setMessage(errorMessageLabel, "Please ensure all number fields are " +
                    "correctly loaded.An invalid number format was encountered during calculation: " + e.getMessage(), "RED");
        }
    }

    @FXML
    private void cancel() {
        clearFields();
    }

    private void clearFields() {
        errorMessageLabel.setText("");
        employeeNameField.clear();
        employeeRoleField.clear();
        baseSalaryField.clear();
        totalDaysField.clear();
        presentDaysField.clear();
        unpaidLeaveDaysField.clear();
        attendanceDeductionField.clear();
        grossAmountField.clear();
        totalDeductionsField.clear();
        netSalaryField.clear();
        if (remarksField != null) {
            remarksField.clear();
        }
        salaryDeuctionsTable.getSelectionModel().clearSelection();

    }


    private boolean isSalaryPaidForCurrentMonth(int userId) throws SQLException {
        LocalDate currentDate = LocalDate.now();
        String query = "SELECT COUNT(*) FROM Salary_Payments WHERE User_Id = ? AND Salary_Month = ? AND Salary_Year = ? AND Payment_Status = 'Paid'";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, currentDate.getMonthValue());
            ps.setInt(3, currentDate.getYear());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public void saveAndPayOneByOne(int userID, String fullName, String roleType) throws SQLException {
        errorMessageLabel.setText("");
        try {
            LocalDate paymentDate = LocalDate.now();
            int month = paymentDate.getMonthValue();
            int year = paymentDate.getYear();
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String formattedDateTime = "MANISH-" + now.format(formatter) + "-USER-" + userID;
            double grossAmount = Double.parseDouble(grossAmountField.getText());

            String deductText = totalDeductionsField.getText();
            double totalDeductions = 0.0;
            if (deductText != null && !deductText.trim().isEmpty()) {
                totalDeductions = Double.parseDouble(deductText);
            }

            double netAmountPaid = Double.parseDouble(netSalaryField.getText());
            String remarks = remarksField.getText();
            if (remarks == null || remarks.trim().isEmpty()) {
                remarks = "No Remarks";
            }

            String paymentQuery = "INSERT INTO Salary_Payments (User_Id, Salary_Month, Salary_Year, Gross_Amount, Total_Deductions, Net_Amount_Paid, Payment_Date, Payment_Status, Remarks, Transaction_Id) VALUES (?, ?, ?, ?, ?, ?, ?, 'Paid', ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(paymentQuery)) {
                ps.setInt(1, userID);
                ps.setInt(2, month);
                ps.setInt(3, year);
                ps.setDouble(4, grossAmount);
                ps.setDouble(5, totalDeductions);
                ps.setDouble(6, netAmountPaid);
                ps.setDate(7, Date.valueOf(paymentDate));
                ps.setString(8, remarks);
                ps.setString(9, formattedDateTime);
                ps.executeUpdate();
                paidUsers.add(userID);
            }

            Platform.runLater(this::loadSalaryDistributionTable);
            loadFrame.setMessage(errorMessageLabel, "Salary payment for " + fullName + " has been successfully recorded.", "GREEN");
            clearFields();

        } catch (SQLException | NumberFormatException e) {
            loadFrame.setMessage(errorMessageLabel, "A database or number format error occurred while saving: " + e.getMessage(), "RED");
        }
    }


    @FXML
    private void saveAndPay() throws SQLException {
        errorMessageLabel.setText("");
        saveAndPayOneByOne(selectedEmployee.getColUserId(), selectedEmployee.getColFullName(), selectedEmployee.getColRoleType());
    }


    @FXML
    public void payAllInOneClick() throws SQLException {
        errorMessageLabel.setText("");
        notSkippedValue.clear();
        allValue.clear();
        skippedValue.clear();

        for (LibrariansTable selectedEmployee : masterData) {
            int userID = selectedEmployee.getColUserId();
            if (!isSalaryPaidForCurrentMonth(userID)) {
                if (!notSkippedValue.contains(userID)) {
                    notSkippedValue.add(userID);
                }
                this.selectedEmployee = selectedEmployee;
                // Pay logic only; avoid reloading table
                clearFields();
                LocalDate previousMonthDate = LocalDate.now().minusMonths(1);
                employeeNameField.setText(selectedEmployee.getColFullName());
                employeeRoleField.setText(selectedEmployee.getColRoleType());

                fetchEmployeeSalaryAndAttendance(userID, previousMonthDate);
                calculateFinalSalary();
                saveAndPayOneByOne(userID, selectedEmployee.getColFullName(), selectedEmployee.getColRoleType());
            } else {
                if (!allValue.contains(userID)) {
                    allValue.add(userID);
                }
            }
        }

        for (Integer i : allValue) {
            if (!notSkippedValue.contains(i) && !skippedValue.contains(i)) {
                skippedValue.add(i);
            }
        }


        // Reload data ONCE
        Platform.runLater(this::loadSalaryDistributionTable);

        if (skippedValue.size() == masterData.size()) {
            loadFrame.setMessage(errorMessageLabel, "All Users skipped as Salary is already " +
                            "paid for this month ! . Salary will only be creditted onece in a month ",
                    "RED");
        } else if (!skippedValue.isEmpty()) {
            loadFrame.setMessage(errorMessageLabel,
                    "All Users salary is creditted Successfully skipping Users with UserID " +
                            ":" + skippedValue + " ,because their salary is creditted already",
                    "ORANGE");
        } else {
            loadFrame.setMessage(errorMessageLabel, "Salary paid for all users successfully " +
                    ". Cross check if you are not satisfiesd with my Software" +
                    "!", "GREEN");
        }
    }


}