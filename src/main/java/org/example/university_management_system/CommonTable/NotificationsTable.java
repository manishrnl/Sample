package org.example.university_management_system.CommonTable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;

/*
 Notification_Id INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(100) NOT NULL,
    Message TEXT NOT NULL,
    Target_Role ENUM('Student', 'Teacher', 'Staff', 'Admin', 'Accountant', 'Librarian', 'All Roles') DEFAULT 'All Roles',
    Target_User_Id INT,
    Created_By_User_Id INT NOT NULL,
    Created_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Expiry_Date DATE,
    Is_Read BOOLEAN DEFAULT FALSE,
    Read_On TIMESTAMP,
 */
public class NotificationsTable {
    private SimpleStringProperty title, message, targetRole, isRead, createdOnDate, readOn,targetUserName;
    private SimpleIntegerProperty notificationId, created_By_User_Id, targetUserId;
    private SimpleObjectProperty<LocalDate> expiryDate;



    public NotificationsTable(String title, String message, String targetRole, String isRead
            , String createdOnDate, LocalDate expiryDate, String readOn, int notificationId,
                              int created_By_User_Id, int targetUserId,
                              String targetUserName) {
        this.title = new SimpleStringProperty(title);
        this.message = new SimpleStringProperty(message);
        this.readOn = new SimpleStringProperty(readOn);
        this.notificationId = new SimpleIntegerProperty(notificationId);
        this.created_By_User_Id = new SimpleIntegerProperty(created_By_User_Id);
        this.targetRole = new SimpleStringProperty(targetRole);
        this.isRead = new SimpleStringProperty(isRead);
        this.createdOnDate = new SimpleStringProperty(createdOnDate);
        this.expiryDate = new SimpleObjectProperty<>(expiryDate);
        this.targetUserId = new SimpleIntegerProperty(targetUserId);
        this.targetUserName = new SimpleStringProperty(targetUserName);

    }
    public int getTargetUserId() {
        return targetUserId.get();
    }

    public SimpleIntegerProperty targetUserIdProperty() {
        return targetUserId;
    }

    public void setTargetUserId(int targetUserId) {
        this.targetUserId.set(targetUserId);
    }


    public String getReadOn() {
        return readOn.get();
    }

    public SimpleStringProperty readOnProperty() {
        return readOn;
    }

    public void setReadOn(String readOn) {
        this.readOn.set(readOn);
    }

    public int getNotificationId() {
        return notificationId.get();
    }

    public SimpleIntegerProperty notificationIdProperty() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId.set(notificationId);
    }

    public int getCreated_By_User_Id() {
        return created_By_User_Id.get();
    }

    public SimpleIntegerProperty created_By_User_IdProperty() {
        return created_By_User_Id;
    }

    public void setCreated_By_User_Id(int created_By_User_Id) {
        this.created_By_User_Id.set(created_By_User_Id);
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getMessage() {
        return message.get();
    }

    public SimpleStringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public String getTargetRole() {
        return targetRole.get();
    }

    public SimpleStringProperty targetRoleProperty() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole.set(targetRole);
    }

    public String getIsRead() {
        return isRead.get();
    }

    public SimpleStringProperty isReadProperty() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead.set(isRead);
    }

    public String getCreatedOnDate() {
        return createdOnDate.get();
    }

    public SimpleStringProperty createdOnDateProperty() {
        return createdOnDate;
    }

    public void setCreatedOnDate(String createdOnDate) {
        this.createdOnDate.set(createdOnDate);
    }

    public LocalDate getExpiryDate() {
        return expiryDate.get();
    }

    public SimpleObjectProperty<LocalDate> expiryDateProperty() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate.set(LocalDate.parse(expiryDate));
    }


    public String getTargetUserName() {
        return targetUserName.get();
    }

    public SimpleStringProperty targetUserNameProperty() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName.set(targetUserName);
    }

}
