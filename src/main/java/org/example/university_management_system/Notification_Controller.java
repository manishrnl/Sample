package org.example.university_management_system;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.example.university_management_system.CommonTable.NotificationsTable;
import org.example.university_management_system.Databases.AuthenticationSQLQuery;
import org.example.university_management_system.Databases.DatabaseConnection;
import org.example.university_management_system.Databases.UsersSQLQuery;
import org.example.university_management_system.Java_StyleSheet.Theme_Manager;
import org.example.university_management_system.ToolsClasses.AlertManager;
import org.example.university_management_system.ToolsClasses.LoadFrame;
import org.example.university_management_system.ToolsClasses.SessionManager;
import org.example.university_management_system.ToolsClasses.WrappedTextCellFactory;

import javax.swing.plaf.basic.BasicTreeUI;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class Notification_Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private DatePicker expiryDatePicker;
    @FXML
    private TextField userNameField, messageField, txtSearch, titleField, targetUserIDField;
    @FXML
    private Button btnRefresh, markAsDoneButton;
    @FXML
    private TableColumn<NotificationsTable, String> colIsRead, colMessage, colTargetRole,
            colTitle, colCreatedOn, colExpiryDate, colTargetUserName;
    @FXML
    private TableColumn<NotificationsTable, Integer> colTargetUserID;
    @FXML
    private ComboBox<String> comboRoleFilter, targetRoleCombo;
    @FXML
    private TableView<NotificationsTable> notificationsTable;
    ObservableList<NotificationsTable> data = FXCollections.observableArrayList();

    SessionManager sessionManager = SessionManager.getInstance();
    LoadFrame loadFrame;
    AlertManager alertManager;
    Connection connection = DatabaseConnection.getConnection();
    UsersSQLQuery usersSQLQuery = new UsersSQLQuery();

    AuthenticationSQLQuery authenticationSQLQuery = new AuthenticationSQLQuery();

    public Notification_Controller() throws SQLException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userNameField.setText("");

        txtSearch.textProperty().addListener((observableValue, oldValue, newValue) -> searchData(newValue));
        colCreatedOn.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().createdOnDateProperty());
        colExpiryDate.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().expiryDateProperty().asString());
        colIsRead.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().isReadProperty());
        colMessage.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().messageProperty());
        // Styling for wrap column for Table View for Message Table
        // colMessage.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());

        colMessage.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());
        colTitle.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());
        colIsRead.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());
        colExpiryDate.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());
        colTargetUserName.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());
        colTargetUserID.setCellFactory(new WrappedTextCellFactory<NotificationsTable,Integer>());
        colCreatedOn.setCellFactory(new WrappedTextCellFactory<NotificationsTable,String>());
        colTargetRole.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());

        colTargetRole.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().targetRoleProperty());
        colTitle.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().titleProperty());
        loadsNotifications("");
        colTargetUserID.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().targetUserIdProperty().asObject());
        colTargetUserName.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().targetUserNameProperty());
        comboRoleFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            txtSearch.setText("");
            loadsNotifications(newValue);
        });

        notificationsTable.setOnMouseClicked(mouseEvent -> {
            NotificationsTable selectedRow = notificationsTable.getSelectionModel().getSelectedItem();
            if (selectedRow != null) {
                errorMessageLabel.setText("");
                refreshMarkAsDoneButtonText(selectedRow.getIsRead());
                titleField.setText(selectedRow.getTitle());
                messageField.setText(selectedRow.getMessage());
                targetRoleCombo.setValue(selectedRow.getTargetRole());
                expiryDatePicker.setValue(selectedRow.getExpiryDate());
            }
        });
        Platform.runLater(() -> Theme_Manager.applyTheme(root.getScene()));
    }

    private void refreshMarkAsDoneButtonText(String isRead) {
        markAsDoneButton.setText("Read".equals(isRead) ? "Mark as Incomplete" : "Mark as " +
                "Complete");
    }

    private void searchData(String newValue) {
        int count = 0;
        ObservableList<NotificationsTable> filteredData = FXCollections.observableArrayList();
        String lowerNewValue = newValue.toLowerCase();

        for (NotificationsTable notification : data) {
            boolean matches = false;

            if (safeContains(notification.getTitle(), lowerNewValue) ||
                    safeContains(notification.getTargetUserName(), lowerNewValue) ||
                    safeContains(notification.getIsRead(), lowerNewValue) ||
                    safeContains(notification.getTargetRole(), lowerNewValue) ||
                    safeContains(notification.getMessage(), lowerNewValue)) {
                matches = true;
            }
            try {
                int userId = Integer.parseInt(newValue);
                if (notification.getTargetUserId() == userId) {
                    matches = true;
                }
            } catch (NumberFormatException ignored) {
            }

            if (matches) {
                filteredData.add(notification);
                count++;
            }
        }
        notificationsTable.setItems(filteredData);
        loadFrame.setMessage(errorMessageLabel,
                "Found total of " + count + " notifications for fields: " + newValue, "GREEN");
    }

    private boolean safeContains(String source, String targetLower) {
        return source != null && source.toLowerCase().contains(targetLower);
    }

    private void loadsNotifications(String filterRole) {
        data.clear();
        int count = 0;
        notificationsTable.getItems().clear();
        String query;
        if (filterRole == null || filterRole.isEmpty() || filterRole.equals("All Roles")) {
            query = "SELECT * FROM Notifications ";
        } else {
            query = "SELECT * FROM Notifications WHERE Target_Role = ?";
        }

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            if (filterRole != null && !filterRole.isEmpty() && !filterRole.equals("All Roles")) {
                pstmt.setString(1, filterRole);
            }


            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String Title = resultSet.getString("Title");
                String Message = resultSet.getString("Message");
                String TargetRole = resultSet.getString("Target_Role");
                String IsRead = resultSet.getString("Is_Read");
                String CreatedOnDate = resultSet.getString("Created_On");
                LocalDate ExpiryDate = resultSet.getDate("Expiry_Date").toLocalDate();
                String readOn = resultSet.getString("Read_On");
                int Notification_Id = resultSet.getInt("Notification_Id");
                int Created_By_User_Id = resultSet.getInt("Created_By_User_Id");
                int TargetUserId = resultSet.getInt("Target_User_Id");
                String TargetUserName = authenticationSQLQuery.getUserNameFromAuthentication(TargetUserId, errorMessageLabel);
                ++count;
                NotificationsTable table = new NotificationsTable(Title, Message, TargetRole,
                        IsRead, CreatedOnDate, ExpiryDate, readOn, Notification_Id,
                        Created_By_User_Id, TargetUserId, TargetUserName);
                data.add(table);
            }
            notificationsTable.setItems(data);
            loadFrame.setMessage(errorMessageLabel, "Found total of " + count + " notifications for fields : " + filterRole,
                    "GREEN");
        } catch (Exception ex) {
            loadFrame.setMessage(errorMessageLabel, "", "RED");
        }
    }


    public void handleCloseOperation(ActionEvent actionEvent) {
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.close(); // Close the current stage
    }

    public void handleRefresh(ActionEvent actionEvent) {
        txtSearch.setText("");
        loadsNotifications("");
        loadFrame.setMessage(errorMessageLabel, "Notifications Refreshed", "GREEN");
    }

    public void handleDeleteNotifications(ActionEvent actionEvent) {
        NotificationsTable selectedNotification = notificationsTable.getSelectionModel().getSelectedItem();
        if (selectedNotification == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select a notification to delete", "RED");
            return;
        }
        Optional<ButtonType> response =
                alertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "Delete " +
                        "Notifications", "Do you really want to proceed  ? ", "On pressing OK " +
                        "button , the selected notifications will be deleted permanently .");
        if (response.isPresent() && response.get() != ButtonType.OK) {
            return;
        }
        String query = "DELETE FROM Notifications WHERE Title = ? AND Message = ? AND Target_Role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, selectedNotification.getTitle());
            pstmt.setString(2, selectedNotification.getMessage());
            pstmt.setString(3, selectedNotification.getTargetRole());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                handleRefresh(actionEvent);
                loadFrame.setMessage(errorMessageLabel, "Notification deleted successfully", "GREEN");
            } else {
                loadFrame.setMessage(errorMessageLabel, "Failed to delete notification", "RED");
            }
        } catch (SQLException e) {
            loadFrame.setMessage(errorMessageLabel, "Error deleting notification: " + e.getMessage(), "RED");
        }
    }

    public void handleUpdateNotifications(ActionEvent actionEvent) {
        String Title = titleField.getText().trim();
        String Message = messageField.getText().trim();
        String TargetRole = targetRoleCombo.getSelectionModel().getSelectedItem();
        //String IsRead = resultSet.getString("Is_Read");

        String ExpiryDate = String.valueOf(expiryDatePicker.getValue());

        if (Title != null) {

        }

    }

    public void handleAddNotifications(ActionEvent actionEvent) throws SQLException {

    }

    public void handleMarkAsDoneNotifications(ActionEvent actionEvent) {
        NotificationsTable markAsDone = notificationsTable.getSelectionModel().getSelectedItem();
        if (markAsDone != null) {
            System.out.println("Session :" + sessionManager.getUserID() + "created by : " + markAsDone.getCreated_By_User_Id() + " target :" + markAsDone.getTargetUserId());
            if ((markAsDone.getCreated_By_User_Id() == sessionManager.getUserID()) || markAsDone.getTargetUserId() == sessionManager.getUserID()) {
                String toggleReadUnread = markAsDone.getIsRead().equals("Read") ? "Un-Read" : "Read";
                try (PreparedStatement pstmt = connection.prepareStatement(
                        "UPDATE Notifications SET Is_Read = ? WHERE Notification_Id = ?")) {
                    pstmt.setString(1, toggleReadUnread);
                    pstmt.setString(2, String.valueOf(markAsDone.getNotificationId()));

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        loadFrame.setMessage(errorMessageLabel, "Notification is marked as " + toggleReadUnread,
                                "GREEN");
                        markAsDone.setIsRead(toggleReadUnread);
                        refreshMarkAsDoneButtonText(toggleReadUnread);
                        notificationsTable.refresh(); // If needed
                    }
                } catch (Exception ex) {
                    loadFrame.setMessage(errorMessageLabel,
                            "Something went wrong while marking as read: " + ex.getMessage(), "RED");
                }
            } else loadFrame.setMessage(errorMessageLabel, "You do not have permission to " +
                    "mark " +
                    "Notifications as read Unread that is assigned for others", "RED");
        } else
            loadFrame.setMessage(errorMessageLabel, "Select a Notifications to MArk it read" +
                    " / Unread", "RED");
    }
}
