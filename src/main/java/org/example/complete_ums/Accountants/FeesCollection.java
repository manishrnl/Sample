package org.example.complete_ums.Accountants;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.complete_ums.CommonTable.Fee_Collection_Table;
import org.example.complete_ums.CommonTable.Fee_Collection_Table_Searching;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal; // Import BigDecimal
import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.text.ParseException; // Import ParseException for NumberFormat parsing
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

public class FeesCollection implements Initializable {

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final Connection connection = DatabaseConnection.getConnection();
    LoadFrame loadFrame;
    AlertManager alertManager;

    @FXML
    private ImageView usersImage;
    @FXML
    private TableView<Fee_Collection_Table_Searching> fee_Collection_Table_Searching;
    @FXML
    private TableColumn<Fee_Collection_Table_Searching, Integer> userIDCol;
    @FXML
    private TableColumn<Fee_Collection_Table_Searching, String> lastNameCol, firstNameCol, userNameCol, regNoCol, rollNoCol;
    @FXML
    private TableColumn<Fee_Collection_Table, Integer> colAcademicYear, colFeeMonth, colFeeRecordId;
    @FXML
    private TableColumn<Fee_Collection_Table, String> colSemester;
    @FXML
    private TableColumn<Fee_Collection_Table, Double> colAmountDue, colAmountPaid;
    @FXML
    private TableColumn<Fee_Collection_Table, Date> colDueDate, colPaymentDate;
    @FXML
    private TableColumn<Fee_Collection_Table, String> colRemarks, colReceiptNumber, colPaymentStatus, colFeeType, colTransactionId;
    @FXML
    private TableView<Fee_Collection_Table> fee_collection_table;
    @FXML
    private TextField checkTransactionID, newAcademicYearField, newAmountDueField, newAmountPaidField, newFeeMonthField, newReceiptNumberField, newTransactionIdField, studentSearchField, newDueDateField, newPaymentDateField;
    @FXML
    private ComboBox<String> newFeeTypeComboBox, newPaymentMethodComboBox, newPaymentStatusComboBox, newSemesterComboBox;
    @FXML
    private TextArea newRemarksArea;
    @FXML
    private Button clearFormButton, recordPaymentButton;
    @FXML
    private Label totalFeeStatusLabel, errorMessageLabel, searchedPaymentDate, searchedFullName, searchedPaidAmount, searchedReceiptNumber;
    boolean foundTransactionId = false;
    private int selectedStudentId = -1;
    private final NumberFormat indianFormat = NumberFormat.getInstance(new Locale("en", "IN"));

    public FeesCollection() throws SQLException {
        indianFormat.setMinimumFractionDigits(2);
        indianFormat.setMaximumFractionDigits(2);
    }

    ObservableList<Fee_Collection_Table> masterData = FXCollections.observableArrayList();
    String paymentDate = null, fullName = "", amountPaid = "", receitNo = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        errorMessageLabel.setText("");
        populateFeeTypesComboBox();
        populateComboBoxes();

        userIDCol.setCellValueFactory(cellData -> cellData.getValue().userIDProperty().asObject());
        firstNameCol.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameCol.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        userNameCol.setCellValueFactory(cellData -> cellData.getValue().userNameProperty());
        regNoCol.setCellValueFactory(cellData -> cellData.getValue().regNoProperty());
        rollNoCol.setCellValueFactory(cellData -> cellData.getValue().rollNoProperty());

        colAcademicYear.setCellValueFactory(cellData -> cellData.getValue().colAcademicYearProperty().asObject());
        colFeeMonth.setCellValueFactory(cellData -> cellData.getValue().colFeeMonthProperty());
        colFeeRecordId.setCellValueFactory(cellData -> cellData.getValue().colFeeRecordIdProperty().asObject());
        colSemester.setCellValueFactory(cellData -> cellData.getValue().colSemesterProperty());
        colDueDate.setCellValueFactory(cellData -> cellData.getValue().colDueDateProperty());
        colPaymentDate.setCellValueFactory(cellData -> cellData.getValue().colPaymentDateProperty());
        colAmountDue.setCellValueFactory(cellData -> cellData.getValue().colAmountDueProperty().asObject());
        colAmountPaid.setCellValueFactory(cellData -> cellData.getValue().colAmountPaidProperty().asObject());
        colRemarks.setCellValueFactory(cellData -> cellData.getValue().colRemarksProperty());
        colReceiptNumber.setCellValueFactory(cellData -> cellData.getValue().colReceiptNumberProperty());
        colPaymentStatus.setCellValueFactory(cellData -> cellData.getValue().colPaymentStatusProperty());
        colFeeType.setCellValueFactory(cellData -> cellData.getValue().colFeeTypeProperty());
        colTransactionId.setCellValueFactory(cellData -> cellData.getValue().colTransactionIdProperty());

        studentSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            studentSearchField.setText(newValue.toUpperCase());
            searchUsers(newValue);
        });
        searchUsers(""); // Initial search with empty string to load all students or first one.
        checkTransactionID.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                checkTransactionID.setText(newValue.toUpperCase());
        });
        Platform.runLater(() -> {
            fee_Collection_Table_Searching.setOnMouseClicked(mouseEvent -> {
                Fee_Collection_Table_Searching selected = fee_Collection_Table_Searching.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    selectedStudentId = selected.getUserID();
                    fetchFeeRecords(selectedStudentId);
                    loadUsersImage(selectedStudentId);
                } else {
                    selectedStudentId = -1;
                    fee_collection_table.setItems(FXCollections.emptyObservableList());
                    masterData.clear(); // Clear masterData if no student is selected
                    setStudentsTotalFeesLabel(); // Update totals to zero
                }
            });
        });
        Platform.runLater(() ->

        {
            fee_collection_table.setOnMouseClicked(mouseEvent -> {
                Fee_Collection_Table selected = fee_collection_table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    insertTablesDataIntoTextField(selected);
                }
            });
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
                return null; // Return null if no image is found
            }
        };

        loadImageTask.setOnSucceeded(event -> {
            Image profileImagess = loadImageTask.getValue(); // Get the result from the task
            if (profileImagess != null) {
                usersImage.setImage(profileImagess);
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

    private void insertTablesDataIntoTextField(Fee_Collection_Table selected) {
        String date = String.valueOf(LocalDate.now());

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime = now.format(formatter);

        newReceiptNumberField.setText("MANISH" + formattedDateTime);
        String dueDate = (selected.getColDueDate() != null) ? String.valueOf(selected.getColDueDate()) : date;
        newFeeTypeComboBox.setValue(selected.getColFeeType());
        newAcademicYearField.setText(String.valueOf(LocalDate.now().getYear()));
        newSemesterComboBox.setValue(selected.getColSemester());
        newFeeMonthField.setText(String.valueOf(LocalDate.now().getMonthValue()));
        newDueDateField.setText(dueDate);
        newPaymentDateField.setText(date);
        newPaymentStatusComboBox.setValue("Paid");
        newRemarksArea.setText(selected.getColRemarks());
        newAmountDueField.setText(indianFormat.format(selected.getColAmountDue()));
        newAmountPaidField.setText("");
        newPaymentMethodComboBox.setValue("Cash");
    }

    private void searchUsers(String searchValue) {
        String query = "SELECT U.User_Id, U.First_Name, U.Last_Name, A.UserName, S.Registration_Number, S.Roll_Number " +
                "FROM Users U JOIN Authentication A ON U.User_Id = A.User_Id JOIN Students S ON U.User_Id = S.User_Id " +
                "WHERE LOWER(A.UserName) LIKE LOWER(?) OR CAST(U.User_Id AS CHAR) LIKE? OR " +
                "LOWER(S.Registration_Number) LIKE LOWER(?) OR LOWER(S.Roll_Number) LIKE LOWER(?)";

        ObservableList<Fee_Collection_Table_Searching> results = FXCollections.observableArrayList();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            String pattern = "%" + searchValue.trim() + "%";
            for (int i = 1; i <= 4; i++) {
                pstmt.setString(i, pattern);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new Fee_Collection_Table_Searching(
                            rs.getInt("User_Id"),
                            rs.getString("First_Name"),
                            rs.getString("Last_Name"),
                            rs.getString("UserName"),
                            rs.getString("Registration_Number"),
                            rs.getString("Roll_Number")
                    ));
                }

                fee_Collection_Table_Searching.setItems(results);
                if (!results.isEmpty()) {
                    selectedStudentId = results.get(0).getUserID();
                    loadFrame.setMessage(errorMessageLabel, "", "BLACK"); // Clear previous error
                } else {
                    loadFrame.setMessage(errorMessageLabel, "No student found matching " + searchValue + ".", "RED");
                    selectedStudentId = -1;
                    fee_collection_table.setItems(FXCollections.emptyObservableList());
                    masterData.clear(); // Clear masterData
                    setStudentsTotalFeesLabel(); // Update totals to zero
                    handleClearForm(new ActionEvent());

                }
            }
        } catch (SQLException e) {
            Platform.runLater(() -> {
                loadFrame.setMessage(errorMessageLabel,
                        "Failed to search users: " + e.getMessage(), "RED");
                e.printStackTrace();
            });
        }
    }

    private void setStudentsTotalFeesLabel() {
        errorMessageLabel.setText("");
        BigDecimal totalDefaultCourseFee = BigDecimal.ZERO;
        BigDecimal totalAmountPaidFromRecords = BigDecimal.ZERO;
        BigDecimal feesPendingOverall = BigDecimal.ZERO;

        String studentCourse = null;
        String fetchDepartmentsQuery = "SELECT Course FROM Students WHERE User_Id =?";
        try (PreparedStatement pstmt = connection.prepareStatement(fetchDepartmentsQuery)) {
            pstmt.setInt(1, selectedStudentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    studentCourse = rs.getString("Course");
                }
            }
        } catch (SQLException e) {
            Platform.runLater(() -> {
                loadFrame.setMessage(errorMessageLabel, "Database Error: Failed to fetch student course: " + e.getMessage(), "RED");
                e.printStackTrace();
            });
        }

        if (studentCourse != null && !studentCourse.trim().isEmpty()) { // Check if studentCourse is not null or empty
            String fetchDefaultAmountQuery = "SELECT SUM(Default_Amount) AS TotalDefaultAmount FROM Fee_Types WHERE Departments =?";
            try (PreparedStatement pstmt2 = connection.prepareStatement(fetchDefaultAmountQuery)) {
                pstmt2.setString(1, studentCourse);
                try (ResultSet rs2 = pstmt2.executeQuery()) {
                    if (rs2.next()) {
                        BigDecimal defaultAmount = rs2.getBigDecimal("TotalDefaultAmount");
                        if (defaultAmount != null) {
                            totalDefaultCourseFee = defaultAmount;
                        }
                    }
                }
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    loadFrame.setMessage(errorMessageLabel, "Database Error: Failed to fetch default fee amount: " + e.getMessage(), "RED");
                    e.printStackTrace();
                });
            }
        }

        if (masterData != null) {
            for (Fee_Collection_Table record : masterData) {
                // Convert double (from Fee_Collection_Table) to BigDecimal for safe arithmetic
                totalAmountPaidFromRecords = totalAmountPaidFromRecords.add(BigDecimal.valueOf(record.getColAmountPaid()));
            }
        }

        if (totalFeeStatusLabel != null) {
            feesPendingOverall = totalDefaultCourseFee.subtract(totalAmountPaidFromRecords);

            totalFeeStatusLabel.setText(String.format("Total fees: %s    |     Paid till " +
                            "date: %s     |    Pending: %s",
                    indianFormat.format(totalDefaultCourseFee),
                    indianFormat.format(totalAmountPaidFromRecords),
                    indianFormat.format(feesPendingOverall)));
        } else {
            System.err.println("Error: totalFeeStatusLabel is null. Ensure it's declared @FXML and in FXML.");
        }

        newAmountDueField.setText(indianFormat.format(feesPendingOverall));
        newAmountPaidField.setText(indianFormat.format(totalAmountPaidFromRecords));
    }

    private void fetchFeeRecords(int studentId) {
        errorMessageLabel.setText("");
        ObservableList<Fee_Collection_Table> records = FXCollections.observableArrayList();
        String sql = "SELECT SF.Fee_Record_Id, FT.Fee_Type_Name, SF.Academic_Year, SF.Semester, SF.Fee_Month, " +
                "SF.Amount_Due, SF.Amount_Paid, SF.Due_Date, SF.Payment_Date, SF.Payment_Status, " +
                "SF.Transaction_Id, SF.Receipt_Number, SF.Remarks " +
                "FROM Student_Fees SF JOIN Fee_Types FT ON SF.Fee_Type_Id = FT.Fee_Type_Id " +
                "WHERE SF.User_Id =? " +
                "ORDER BY SF.Academic_Year DESC, SF.Fee_Month IS NULL, SF.Fee_Month DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Integer feeMonth = rs.getObject("Fee_Month") != null ? rs.getInt("Fee_Month") : null;
                    String semester = rs.getString("Semester");

                    // Retrieve as BigDecimal and then convert to double for Fee_Collection_Table
                    BigDecimal amountDue = rs.getBigDecimal("Amount_Due");
                    BigDecimal amountPaid = rs.getBigDecimal("Amount_Paid");

                    records.add(new Fee_Collection_Table(
                            rs.getInt("Academic_Year"),
                            feeMonth,
                            rs.getInt("Fee_Record_Id"),
                            semester,
                            rs.getDate("Due_Date"),
                            rs.getDate("Payment_Date"),
                            amountDue != null ? amountDue.doubleValue() : 0.0, // Safely convert to double
                            amountPaid != null ? amountPaid.doubleValue() : 0.0, // Safely convert to double
                            rs.getString("Remarks"),
                            rs.getString("Receipt_Number"),
                            rs.getString("Payment_Status"),
                            rs.getString("Fee_Type_Name"),
                            rs.getString("Transaction_Id")
                    ));
                }
                masterData.setAll(records); // Use setAll to replace existing items, not addAll
                fee_collection_table.setItems(masterData);
                setStudentsTotalFeesLabel(); // Update the totals label after setting the data

                if (records.isEmpty()) {
                    loadFrame.setMessage(errorMessageLabel, "No Records. No fee records " +
                            "found for student ID: " + studentId, "RED");
                } else {
                    loadFrame.setMessage(errorMessageLabel, "", "BLACK"); // Clear message if records found
                }

            }
        } catch (SQLException e) {
            Platform.runLater(() -> {
                loadFrame.setMessage(errorMessageLabel, "Database Error! Failed to fetch " +
                        "fee records: " + e.getMessage(), "RED");
                e.printStackTrace();
            });
        }
    }

    private void populateFeeTypesComboBox() {
        ObservableList<String> types = FXCollections.observableArrayList();
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT " +
                "Distinct Fee_Type_Name FROM Fee_Types ORDER BY Fee_Type_Name");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                types.add(rs.getString("Fee_Type_Name"));
            }
            Platform.runLater(() -> newFeeTypeComboBox.setItems(types));
        } catch (SQLException e) {
            Platform.runLater(() -> {
                loadFrame.setMessage(errorMessageLabel,
                        "Database error occurred. Failed to load fee types: " + e.getMessage(), "RED");
                e.printStackTrace();
            });
        }
    }

    private void populateComboBoxes() {
        Platform.runLater(() -> {
            newSemesterComboBox.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8"));
            newPaymentStatusComboBox.setItems(FXCollections.observableArrayList("Paid", "Unpaid", "Partial", "Waived"));
            newPaymentMethodComboBox.setItems(FXCollections.observableArrayList("Bank " +
                    "Transfer", "Cash", "Cheque", "UPI"));
        });
    }

    private BigDecimal parseCurrencyToBigDecimal(String currencyString) throws ParseException {
        if (currencyString == null || currencyString.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        // Use NumberFormat to parse the string, which handles locale-specific formatting (like commas)
        Number number = indianFormat.parse(currencyString.trim());
        return new BigDecimal(number.toString()); // Convert the parsed number to its string representation then to BigDecimal
    }

    private Boolean checkIfTransactionIdExists(String transactionId) {

        String query = "SELECT s.Receipt_Number, s.Transaction_Id, s.Amount_Paid, s.Payment_Date, CONCAT(u.First_Name, ' ', u.Last_Name) AS FullName FROM  Student_Fees s JOIN Users u ON u.User_Id = s.User_Id WHERE s.Transaction_Id = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, transactionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                foundTransactionId = true;
                paymentDate = rs.getString("Payment_Date");
                fullName = rs.getString("FullName");
                amountPaid = rs.getString("Amount_Paid");
                receitNo = rs.getString("Receipt_Number");
                return true;
            }
        } catch (Exception e) {
            loadFrame.setMessage(errorMessageLabel, "Database error occured : " + e.getMessage()
                    , "RED");
        }
        if (foundTransactionId) {

            loadFrame.setMessage(errorMessageLabel,
                    "Users found with given Transaction IDS :" + transactionId,
                    "GREEN");
        } else {
            loadFrame.setMessage(errorMessageLabel,
                    "Could not find users with given transaction IDS :" + transactionId,
                    "RED");
            searchedPaymentDate.setText("");
            searchedFullName.setText("");
            searchedPaidAmount.setText("");
            searchedReceiptNumber.setText("");
        }
        return false;
    }

    @FXML
    public void handleUpdateRecords(ActionEvent event) throws ParseException {
        errorMessageLabel.setText("");
        Fee_Collection_Table selected = fee_collection_table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select a record from the table to update.", "RED");
            return;
        }

        int feeRecordId = selected.getColFeeRecordId();
        String studentCourse = null;
        String fetchStudentCourseSql = "SELECT S.Course FROM Students S JOIN Student_Fees SF ON S.User_Id = SF.User_Id WHERE SF.Fee_Record_Id = ?";
        try (PreparedStatement pstmtCourse = connection.prepareStatement(fetchStudentCourseSql)) {
            pstmtCourse.setInt(1, feeRecordId);
            try (ResultSet rsCourse = pstmtCourse.executeQuery()) {
                if (rsCourse.next()) {
                    studentCourse = rsCourse.getString("Course");
                } else {
                    loadFrame.setMessage(errorMessageLabel, "Error: Student course not found for selected fee record.", "RED");
                    return;
                }
            }
        } catch (SQLException e) {
            Platform.runLater(() -> {
                loadFrame.setMessage(errorMessageLabel, "Database Error: Failed to get student course for update: " + e.getMessage(), "RED");
                e.printStackTrace();
            });
            return;
        }
        Integer feeTypeId = null;
        String fetchFeeTypeIdSql = "SELECT Fee_Type_Id FROM Fee_Types WHERE Fee_Type_Name = ? AND Departments = ?";
        try (PreparedStatement pstmtFeeType = connection.prepareStatement(fetchFeeTypeIdSql)) {
            pstmtFeeType.setString(1, newFeeTypeComboBox.getValue());
            pstmtFeeType.setString(2, studentCourse);
            try (ResultSet rsFeeType = pstmtFeeType.executeQuery()) {
                if (rsFeeType.next()) {
                    feeTypeId = rsFeeType.getInt("Fee_Type_Id");
                } else {
                    loadFrame.setMessage(errorMessageLabel, "Error: Fee Type '" + newFeeTypeComboBox.getValue() + "' for department '" + studentCourse + "' not found.", "RED");
                    return;
                }
            }
        } catch (SQLException e) {
            Platform.runLater(() -> {
                loadFrame.setMessage(errorMessageLabel, "Database Error: Failed to fetch Fee_Type_Id: " + e.getMessage(), "RED");
                e.printStackTrace();
            });
            return;
        }

        int feesPaidByUserId = sessionManager.getUserID();
        String feeType = newFeeTypeComboBox.getValue(); // Already used for feeTypeId lookup
        int academicYear = Integer.parseInt(newAcademicYearField.getText().trim());
        String semester = newSemesterComboBox.getValue();
        Integer feeMonth = newFeeMonthField.getText().trim().isEmpty() ? null : Integer.parseInt(newFeeMonthField.getText().trim());

        BigDecimal amountDue = parseCurrencyToBigDecimal(newAmountDueField.getText());
        BigDecimal amountPaid = parseCurrencyToBigDecimal(newAmountPaidField.getText().trim().isEmpty() ? "0" : newAmountPaidField.getText());

        LocalDate dueDate = LocalDate.parse(newDueDateField.getText());
        LocalDate paymentDate = LocalDate.parse(newPaymentDateField.getText());
        String paymentStatus = newPaymentStatusComboBox.getValue();
        String transactionId = newTransactionIdField.getText().trim();
        String remarks = newRemarksArea.getText();
        if (feeType == null || feeType.isEmpty() || semester == null || semester.isEmpty() ||
                paymentStatus == null || paymentStatus.isEmpty() || dueDate == null) {
            loadFrame.setMessage(errorMessageLabel, "Missing Fields. Please fill in " +
                    "all required fields.", "RED");
            return;
        }
        if (!transactionId.isEmpty() && checkIfTransactionIdExists(transactionId)) {
            loadFrame.setMessage(errorMessageLabel, "Transaction ID '" + transactionId + "' already exists for another record. Please use a unique ID.", "RED");
            return;
        }
        String receiptNumber = newReceiptNumberField.getText().trim();
        if (receiptNumber.isEmpty() && selected.getColReceiptNumber() != null) {
            receiptNumber = selected.getColReceiptNumber();
        } else if (receiptNumber.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            receiptNumber = "MANISH" + now.format(formatter);
            newReceiptNumberField.setText(receiptNumber); // Update the UI field
        }
        String sql = "UPDATE Student_Fees SET Fees_Paid_By_User_Id = ?, User_Id = ?, Fee_Type_Id = ?, Academic_Year = ?, Semester = ?, Fee_Month = ?, Amount_Due = ?, Amount_Paid = ?, Due_Date = ?,Payment_Date = ?, Payment_Status = ?, Transaction_Id = ?, Receipt_Number = ?, Remarks = ? WHERE Fee_Record_Id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            pstmt.setInt(paramIndex++, feesPaidByUserId);
            pstmt.setInt(paramIndex++, selectedStudentId); // Ensure this is the student ID associated with the record
            pstmt.setInt(paramIndex++, feeTypeId); // Set the Fee_Type_Id
            pstmt.setInt(paramIndex++, academicYear);
            pstmt.setString(paramIndex++, semester);
            pstmt.setObject(paramIndex++, feeMonth, Types.INTEGER);
            pstmt.setBigDecimal(paramIndex++, amountDue);
            pstmt.setBigDecimal(paramIndex++, amountPaid);
            pstmt.setDate(paramIndex++, Date.valueOf(dueDate));
            pstmt.setObject(paramIndex++, paymentDate != null ? Date.valueOf(paymentDate) : null, Types.DATE);
            pstmt.setString(paramIndex++, paymentStatus);
            pstmt.setObject(paramIndex++, transactionId.isEmpty() ? null : transactionId, Types.VARCHAR);
            pstmt.setObject(paramIndex++, receiptNumber.isEmpty() ? null : receiptNumber, Types.VARCHAR); // Set receiptNumber
            pstmt.setObject(paramIndex++, remarks.isEmpty() ? null : remarks, Types.VARCHAR); // Remarks
            pstmt.setInt(paramIndex++, feeRecordId); // Set the WHERE clause parameter

            int result = pstmt.executeUpdate();

            Platform.runLater(() -> {
                if (result > 0) {
                    loadFrame.setMessage(errorMessageLabel, "Success! Record updated successfully.", "GREEN");
                    fetchFeeRecords(selectedStudentId); // Re-fetch to update the table and totals
                } else {
                    loadFrame.setMessage(errorMessageLabel, "Failure! Failed to update record. No rows affected.", "RED");
                }
            });

        } catch (SQLException e) {
            Platform.runLater(() -> {
                loadFrame.setMessage(errorMessageLabel, "Database Error! Failed to update record: " + e.getMessage(), "RED");
                e.printStackTrace();
            });
        } catch (NumberFormatException e) {
            Platform.runLater(() -> loadFrame.setMessage(errorMessageLabel, "Invalid " +
                    "Input! Please enter valid numeric values for academic year, fee month, " +
                    "and amounts.", "RED"));
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddPayment(ActionEvent event) {
        errorMessageLabel.setText("");
        if (selectedStudentId == -1) {
            loadFrame.setMessage(errorMessageLabel, "No Student Selected. Please select a " +
                    "student before recording a payment.", "RED");
            return;
        }

        String studentCourse = null;
        String fetchStudentCourseSql = "SELECT Course FROM Students WHERE User_Id = ?";
        try (PreparedStatement pstmtCourse = connection.prepareStatement(fetchStudentCourseSql)) {
            pstmtCourse.setInt(1, selectedStudentId);
            try (ResultSet rsCourse = pstmtCourse.executeQuery()) {
                if (rsCourse.next()) {
                    studentCourse = rsCourse.getString("Course");
                } else {
                    loadFrame.setMessage(errorMessageLabel, "Error: Student course not found.", "RED");
                    return;
                }
            }
        } catch (SQLException e) {
            Platform.runLater(() -> {
                loadFrame.setMessage(errorMessageLabel, "Database Error: Failed to get student course: " + e.getMessage(), "RED");
                e.printStackTrace();
            });
            return; // Exit if unable to get student course
        }
        try {
            int feesPaidByUserId = sessionManager.getUserID();
            String feeType = newFeeTypeComboBox.getValue();
            int academicYear = Integer.parseInt(newAcademicYearField.getText().trim());
            String semester = newSemesterComboBox.getValue();
            Integer feeMonth = newFeeMonthField.getText().trim().isEmpty() ? null : Integer.parseInt(newFeeMonthField.getText().trim());

            BigDecimal amountDue = parseCurrencyToBigDecimal(newAmountDueField.getText());
            BigDecimal amountPaid = parseCurrencyToBigDecimal(newAmountPaidField.getText().trim().isEmpty() ? "0" : newAmountPaidField.getText());

            LocalDate dueDate = LocalDate.parse(newDueDateField.getText());
            LocalDate paymentDate = LocalDate.parse(newPaymentDateField.getText());
            String paymentStatus = newPaymentStatusComboBox.getValue();
            String transactionId = newTransactionIdField.getText().trim();
            String remarks = newRemarksArea.getText().trim();
            String mode = newPaymentMethodComboBox.getValue().trim();
            if (feeType == null || feeType.isEmpty() || semester == null || semester.isEmpty() || paymentStatus == null || paymentStatus.isEmpty() || dueDate == null) {
                loadFrame.setMessage(errorMessageLabel, "Missing Fields. Please fill in " +
                        "all required fields.", "RED");
                return;
            }
            if (checkIfTransactionIdExists(transactionId)) {
                loadFrame.setMessage(errorMessageLabel, "Same Transaction ID exists . Could " +
                        "not Update Table . It is already added in database. For details check  transaction ID who has submitted fees on their names", "RED");
                return;
            }
            String sql = "INSERT INTO Student_Fees (Fees_Paid_By_User_Id, User_Id, Fee_Type_Id, Academic_Year, Semester, " +
                    "Fee_Month, Amount_Due, Amount_Paid, Due_Date, Payment_Date, " +
                    "Payment_Status, Transaction_Id, Receipt_Number, Remarks,Payment_Mode) " +
                    "VALUES (?,?, (SELECT Fee_Type_Id FROM Fee_Types WHERE Fee_Type_Name =? " +
                    "AND Departments =?),?,?,?,?,?,?,?,?,?,?,?,?)";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, feesPaidByUserId);
                pstmt.setInt(2, selectedStudentId);
                pstmt.setString(3, feeType);
                pstmt.setString(4, studentCourse); // Pass the retrieved student's course as Departments
                pstmt.setInt(5, academicYear);
                pstmt.setString(6, semester);
                pstmt.setObject(7, feeMonth, Types.INTEGER);
                pstmt.setBigDecimal(8, amountDue);
                pstmt.setBigDecimal(9, amountPaid);
                pstmt.setDate(10, Date.valueOf(dueDate));
                pstmt.setObject(11, paymentDate != null ? Date.valueOf(paymentDate) : null, Types.DATE);
                pstmt.setString(12, paymentStatus);
                pstmt.setObject(13, transactionId.isEmpty() ? null : transactionId, Types.VARCHAR);

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                String formattedDateTime = now.format(formatter);
                newReceiptNumberField.setText("MANISH" + formattedDateTime);

                String receiptNumber = newReceiptNumberField.getText().trim();
                pstmt.setObject(14, receiptNumber.isEmpty() ? null : receiptNumber, Types.VARCHAR);
                pstmt.setObject(15, remarks.isEmpty() ? "Paid" : remarks, Types.VARCHAR); // Note the parameter index shift due to new parameter
                pstmt.setString(16, mode);

                int result = pstmt.executeUpdate();
                Platform.runLater(() -> {
                    if (result > 0) {
                        loadFrame.setMessage(errorMessageLabel, "Success! Fee payment " +
                                "recorded successfully.", "GREEN");
                        handleClearForm(new ActionEvent());
                        fetchFeeRecords(selectedStudentId);
                    } else {
                        loadFrame.setMessage(errorMessageLabel, "Failure! Failed to record fee" +
                                " payment.", "RED");
                    }
                });
            }
        } catch (NumberFormatException e) {
            Platform.runLater(() -> loadFrame.setMessage(errorMessageLabel, "Invalid " +
                    "Input! Please enter valid numeric values for academic year, fee month, " +
                    "and amounts.", "RED"));
        } catch (SQLException e) {
            Platform.runLater(() -> {
                loadFrame.setMessage(errorMessageLabel, "Database Error! Failed to record " +
                        "payment: " + e.getMessage(), "RED");
                e.printStackTrace();
            });
        } catch (ParseException e) {
            Platform.runLater(() -> loadFrame.setMessage(errorMessageLabel, "Invalid amount format. Please use a valid number format.", "RED"));
            e.printStackTrace();
        }
    }

    @FXML
    public void handleClearForm(ActionEvent event) {
        Platform.runLater(() -> {
            newFeeTypeComboBox.getSelectionModel().clearSelection();
            newAcademicYearField.clear();
            newSemesterComboBox.getSelectionModel().clearSelection();
            newFeeMonthField.clear();
            newAmountDueField.clear();
            newAmountPaidField.clear();
            newDueDateField.setText(null); // Or set to a default, like LocalDate.now()
            newPaymentDateField.setText(null); // Or set to a default, like LocalDate.now()
            newPaymentMethodComboBox.getSelectionModel().clearSelection();
            newTransactionIdField.clear();
            newReceiptNumberField.clear();
            newPaymentStatusComboBox.getSelectionModel().clearSelection();
            newRemarksArea.clear();
            errorMessageLabel.setText("");
        });
    }

    @FXML
    private void handleSearchTransactionID(ActionEvent event) {
        Boolean bool = checkIfTransactionIdExists(checkTransactionID.getText().trim());
        if (bool) {
            searchedPaymentDate.setText(paymentDate);
            searchedFullName.setText(fullName);
            searchedPaidAmount.setText(amountPaid);
            searchedReceiptNumber.setText(receitNo);
            System.out.println("triggered Button");
        }
    }
}