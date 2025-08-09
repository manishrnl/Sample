package org.example.complete_ums.Students;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.complete_ums.CommonTable.FinanceFeeTable;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Finance implements Initializable {
    SessionManager sessionManager = SessionManager.getInstance();
    Connection connection = DatabaseConnection.getConnection();
    @FXML
    private TableColumn<FinanceFeeTable, Integer> academicYearCol;
    @FXML
    private TableColumn<FinanceFeeTable, Double> amountPaidCol, amountDueCol;
    @FXML
    private TableColumn<FinanceFeeTable, LocalDate> paymentDateCol, dueDateCol;
    @FXML
    private TableColumn<FinanceFeeTable, String> receiptNumberCol, statusCol, feeTypeCol;
    @FXML
    private TableView<FinanceFeeTable> financeFeeTable;
    @FXML
    private Button downloadReceiptButton, payNowButton;
    @FXML
    private Label outstandingBalanceLabel, totalFeesDueLabel, totalFeesPaidLabel;
    int userIdLoggedIn = sessionManager.getUserID();

    public Finance() throws SQLException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        receiptNumberCol.setCellValueFactory(cellData -> cellData.getValue().receiptNumberColProperty());
        academicYearCol.setCellValueFactory(cellData -> cellData.getValue().academicYearColProperty().asObject());
        amountPaidCol.setCellValueFactory(cellData -> cellData.getValue().amountPaidColProperty().asObject());
        amountDueCol.setCellValueFactory(cellData -> cellData.getValue().amountDueColProperty().asObject());
        paymentDateCol.setCellValueFactory(cellData -> cellData.getValue().paymentDateColProperty());
        dueDateCol.setCellValueFactory(cellData -> cellData.getValue().dueDateColProperty());
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusColProperty());
        feeTypeCol.setCellValueFactory(cellData -> cellData.getValue().feeTypeColProperty());

        populateLabelDetails(userIdLoggedIn);
        getFeesLoaded(userIdLoggedIn);
    }

    private void populateLabelDetails(int userIdLoggedIn) {
        String query = "SELECT SUM(Amount_Due), SUM(Amount_Paid) FROM Student_Fees WHERE User_Id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userIdLoggedIn);
            ResultSet resultSet = pstmt.executeQuery();
            double totalDue;
            double totalPaid;
            if (resultSet.next()) {
                totalDue = resultSet.getDouble(1);
                totalPaid = resultSet.getDouble(2);
            } else {
                totalPaid = 0.0;
                totalDue = 0.0;
            }
            double outstandingBalance = totalDue - totalPaid;
            Platform.runLater(() -> {
                totalFeesDueLabel.setText(String.format("₹ %,.2f", totalDue));
                totalFeesPaidLabel.setText(String.format("₹ %,.2f", totalPaid));
                outstandingBalanceLabel.setText(String.format("₹ %,.2f", outstandingBalance));
                if (outstandingBalance > 0) {
                    outstandingBalanceLabel.setStyle("-fx-text-fill: red;");
                } else {
                    outstandingBalanceLabel.setStyle("-fx-text-fill: green;");
                }
            });

        } catch (SQLException e) {
            System.err.println("Error fetching financial summary: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                totalFeesDueLabel.setText("Error");
                totalFeesPaidLabel.setText("Error");
                outstandingBalanceLabel.setText("Error");
            });
        }
    }

    private void getFeesLoaded(int userIdLoggedIn) {
        String query = "select * from student_fees where User_Id=" + userIdLoggedIn;
        ObservableList<FinanceFeeTable> sample = FXCollections.observableArrayList();
        LocalDate date = LocalDate.now();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String receipt = resultSet.getString("Receipt_Number");
                int academicYear = resultSet.getInt("Academic_Year");
                Double amountPaid = resultSet.getDouble("Amount_Paid");
                Double amountDue = resultSet.getDouble("Amount_Due");
                java.sql.Date sqlpaymentDate = resultSet.getDate("Payment_Date");
                java.sql.Date sqldueDate = resultSet.getDate("Due_Date");
                String paymentStatus = resultSet.getString("Payment_Status");
                String remarks = resultSet.getString("Remarks");

                String receiptNo = (receipt != null) ? receipt : null;
                LocalDate paymentDate = (sqlpaymentDate != null) ? sqlpaymentDate.toLocalDate() : null;
                LocalDate dueDate = (sqldueDate != null) ? sqldueDate.toLocalDate() : null;
                FinanceFeeTable data = new FinanceFeeTable(receiptNo, academicYear, amountPaid, amountDue, paymentDate,
                        dueDate, paymentStatus, remarks);
                sample.add(data);
            }
            financeFeeTable.setItems(sample);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
