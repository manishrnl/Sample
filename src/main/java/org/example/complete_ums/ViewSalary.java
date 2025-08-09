package org.example.complete_ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.complete_ums.CommonTable.SalaryTable;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ViewSalary implements Initializable {

    public ViewSalary() throws SQLException {
    }

    Connection connection = DatabaseConnection.getConnection();
    SessionManager sessionManager = SessionManager.getInstance();

    @FXML
    private Label roleLabel, nameLabel;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button resetButton, searchButton;

    @FXML
    private TextField searchNameField;

    private final String roleLoggedIn = sessionManager.getRole();
    private final int userIdLoggedIn = sessionManager.getUserID();
    LocalDate currentDate = LocalDate.now();
    @FXML
    private TableColumn<SalaryTable, String> transactionIdColumn, nameColumn, roleColumn;
    @FXML
    private TableColumn<SalaryTable, Double> deductionsColumn, grossSalaryColumn, netSalaryColumn;
    @FXML
    private TableColumn<SalaryTable, Integer> userIDColumn;
    @FXML
    private TableColumn<SalaryTable, LocalDate> paymentDateColumn;
    @FXML
    private TableView<SalaryTable> salaryTable;
    ObservableList<SalaryTable> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateRoleComboBox();
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameColumnProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleColumnProperty());
        deductionsColumn.setCellValueFactory(cellData -> cellData.getValue().deductionsColumnProperty().asObject());
        grossSalaryColumn.setCellValueFactory(cellData -> cellData.getValue().grossSalaryColumnProperty().asObject());
        netSalaryColumn.setCellValueFactory(cellData -> cellData.getValue().netSalaryColumnProperty().asObject());
        transactionIdColumn.setCellValueFactory(cellData -> cellData.getValue().transactionIdColumnProperty());
        userIDColumn.setCellValueFactory(cellData -> cellData.getValue().userIDColumnProperty().asObject());
        paymentDateColumn.setCellValueFactory(cellData -> cellData.getValue().paymentDateColumnProperty());
        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.equals("All"))
                salaryTable.setItems(masterData);
            else
                sortTableByRole(newVal);
        });
        searchNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                sortTableByName(newValue);
        });
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                sortTableByLastDate(newValue);
            }
        });
        loadSalaryData();

    }


    private void populateRoleComboBox() {
        if (roleLoggedIn.equals("Admin") || roleLoggedIn.equals("Accountant")) {
            roleComboBox.getItems().addAll("All", "Admin", "Accountant", "Teacher", "Staff",
                    "Librarian");
        } else {
            roleLabel.setVisible(false);
            nameLabel.setVisible(false);
            searchNameField.setVisible(false);
            roleComboBox.setVisible(false);
        }
    }

    private void sortTableByLastDate(LocalDate newValue) {
        ObservableList<SalaryTable> filtered = FXCollections.observableArrayList();
        for (SalaryTable record : masterData) {
            LocalDate paymentDate = (LocalDate) record.getPaymentDateColumn();
            if (paymentDate.isBefore(newValue)) {
                filtered.add(record);
            }
        }
        salaryTable.setItems(filtered);
    }


    private void sortTableByName(String newValue) {
        String lowerCase = newValue.toLowerCase();
        ObservableList<SalaryTable> filtered = FXCollections.observableArrayList();
        for (SalaryTable record : masterData) {
            if (record.getNameColumn().toLowerCase().contains(lowerCase))
                filtered.add(record);
        }
        salaryTable.setItems(filtered);
    }

    private void sortTableByRole(String role) {
        ObservableList<SalaryTable> filtered = FXCollections.observableArrayList();
        for (SalaryTable record : masterData) {
            if (record.getRoleColumn().equals(role)) {
                filtered.add(record);
            }

            salaryTable.setItems(filtered);
        }
    }

    public void loadSalaryData() {
        Double grossAmt = 0.0, deduction = 0.0, netAmt = 0.0;
        ObservableList<SalaryTable> record = FXCollections.observableArrayList();
        String query;

        if (roleLoggedIn.equalsIgnoreCase("Admin") || roleLoggedIn.equalsIgnoreCase("Accountant")) {
            // Admin and Accountant can view all salary records
            query = "    SELECT sp.Salary_Payment_Id, u.User_Id, CONCAT(u.First_Name, ' ', u" +
                    ".Last_Name) AS fullName, u.Role, sp.Gross_Amount, sp.Total_Deductions, sp.Net_Amount_Paid, sp.Payment_Date, sp.Transaction_Id FROM Salary_Payments sp JOIN Users u ON sp.User_Id = u.User_Id " +
                    "WHERE u.Role != 'Student' ORDER BY sp.Payment_Date DESC";
        } else {
            // Other roles can view only their own salary
            query = "SELECT sp.Salary_Payment_Id, u.User_Id, CONCAT(u.First_Name, ' ', u.Last_Name) AS fullName, u.Role, sp.Gross_Amount, sp.Total_Deductions, sp.Net_Amount_Paid, sp.Payment_Date, sp.Transaction_Id FROM Salary_Payments sp JOIN Users u ON sp.User_Id = u.User_Id WHERE u.User_Id = ? AND u.Role != 'Student' ORDER BY sp.Payment_Date DESC";
        }

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            if (!roleLoggedIn.equalsIgnoreCase("Admin") && !roleLoggedIn.equalsIgnoreCase("Accountant")) {
                pstmt.setInt(1, userIdLoggedIn);  // Set the logged-in user's ID
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                grossAmt = rs.getDouble("Gross_Amount");
                deduction = rs.getDouble("Total_Deductions");
                netAmt = grossAmt - deduction;

                SalaryTable temp = new SalaryTable(
                        rs.getString("Transaction_Id"),
                        rs.getInt("User_Id"),
                        rs.getString("fullName"),
                        rs.getString("Role"),
                        deduction,
                        rs.getDate("Payment_Date").toLocalDate(),
                        grossAmt,
                        netAmt
                );

                record.add(temp);
            }
            masterData.setAll(record);
            salaryTable.setItems(record);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading salary data: " + e.getMessage());
        }
    }
}