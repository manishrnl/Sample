package org.example.complete_ums.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.example.complete_ums.CommonTable.AdminLogsTable;
import org.example.complete_ums.Databases.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class Logs implements Initializable {

    @FXML
    public Label errorMessageLabel;
    @FXML
    private DatePicker filterDatePicker;


    @FXML
    private TextField searchTextField;
    @FXML
    private TableColumn<AdminLogsTable, String> actionTypeColumn, targetTableColumn, actionDetailsColumn, ipAddressColumn;
    @FXML
    private TableColumn<AdminLogsTable, LocalDateTime> actionTimestampColumn;

    @FXML
    private TableColumn<AdminLogsTable, Integer> logIdColumn, targetRecordIdColumn, adminUserIdColumn;

    @FXML
    private TableView<AdminLogsTable> adminLogsTable;

    private ObservableList<AdminLogsTable> masterData = FXCollections.observableArrayList();
    private ObservableList<AdminLogsTable> filtered = FXCollections.observableArrayList();
    private Connection connection = DatabaseConnection.getConnection();

    public Logs() throws SQLException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actionTypeColumn.setCellValueFactory(cellData -> cellData.getValue().actionTypeColumnProperty());
        targetTableColumn.setCellValueFactory(cellData -> cellData.getValue().targetTableColumnProperty());
        actionDetailsColumn.setCellValueFactory(cellData -> cellData.getValue().actionDetailsColumnProperty());
        ipAddressColumn.setCellValueFactory(cellData -> cellData.getValue().ipAddressColumnProperty());
        actionTimestampColumn.setCellValueFactory(cellData -> cellData.getValue().actionTimestampColumnProperty());
        logIdColumn.setCellValueFactory(cellData -> cellData.getValue().logIdColumnProperty().asObject());
        targetRecordIdColumn.setCellValueFactory(cellData -> cellData.getValue().targetRecordIdColumnProperty().asObject());
        adminUserIdColumn.setCellValueFactory(cellData -> cellData.getValue().adminUserIdColumnProperty().asObject());
        loadLogsData();

        filterDatePicker.valueProperty().addListener((observable, old, newVal) -> {
            filterData();
        });
        searchTextField.textProperty().addListener((observable, old, newVal) -> {
            filterData();
        });
    }

    private void loadLogsData() {
        masterData.clear();
        String query = "SELECT * FROM Admin_Activity_Log";

        try {

            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                masterData.add(new AdminLogsTable(
                        rs.getInt("Log_Id"),
                        rs.getInt("Admin_User_Id"),
                        rs.getString("Action_Type"),
                        rs.getString("Target_Table"),
                        rs.getInt("Target_Record_Id"),
                        rs.getString("Action_Details"),
                        rs.getTimestamp("Action_Timestamp").toLocalDateTime(),
                        rs.getString("IP_Address")
                ));
            }
            filterData(); // Apply initial filter after loading
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close connection: " + e.getMessage());
                }
            }
        }
    }

    private void filterData() {
        filtered.clear();
        LocalDate dateFilter = filterDatePicker.getValue();
        String textFilter = searchTextField.getText().trim().toLowerCase();

        for (AdminLogsTable record : masterData) {
            boolean dateMatches = (dateFilter == null) || record.getActionTimestampColumn().toLocalDate().equals(dateFilter);

            boolean textMatches = (textFilter.isEmpty()) ||
                    record.getActionDetailsColumn().toLowerCase().contains(textFilter) ||
                    record.getIpAddressColumn().toLowerCase().contains(textFilter) ||
                    record.getActionTypeColumn().toLowerCase().contains(textFilter) ||
                    String.valueOf(record.getActionTimestampColumn()).contains(textFilter) ||
                    String.valueOf(record.getAdminUserIdColumn()).contains(textFilter) ||
                    String.valueOf(record.getLogIdColumn()).contains(textFilter) ||

                    String.valueOf(record.getTargetRecordIdColumn()).contains(textFilter);

            if (dateMatches && textMatches) {
                filtered.add(record);
            }
        }
        adminLogsTable.setItems(filtered);
    }
}
