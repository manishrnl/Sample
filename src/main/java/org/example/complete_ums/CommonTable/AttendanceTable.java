package org.example.complete_ums.CommonTable;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceTable {

    private SimpleIntegerProperty attendanceId, userId;
    private SimpleStringProperty colRole, status, remarks, fullName, fathersName;
    private ObjectProperty<LocalTime> timeIn, timeOut;
    private ObjectProperty<LocalDate> attendanceDate;

    public AttendanceTable(int userId, String fullName, String fathersName, int attendanceId,
                           LocalDate attendanceDate,LocalTime timeIn,LocalTime timeOut, String Role, String status, String remarks) {
        this.colRole = new SimpleStringProperty(Role);
        this.fullName = new SimpleStringProperty(fullName);
        this.fathersName = new SimpleStringProperty(fathersName);
        this.userId = new SimpleIntegerProperty(userId);
        this.attendanceId = new SimpleIntegerProperty(attendanceId);
        this.attendanceDate = new SimpleObjectProperty<>(attendanceDate);
        this.timeIn = new SimpleObjectProperty<>(timeIn);
        this.timeOut = new SimpleObjectProperty<>(timeOut);
        this.status = new SimpleStringProperty(status);
        this.remarks = new SimpleStringProperty(remarks);

    }

    public String getColRole() {
        return colRole.get();
    }

    public SimpleStringProperty colRoleProperty() {
        return colRole;
    }

    public int getUserId() {
        return userId.get();
    }

    public SimpleIntegerProperty userIdProperty() {
        return userId;
    }

    public String getFullName() {
        return fullName.get();
    }

    public SimpleStringProperty fullNameProperty() {
        return fullName;
    }

    public String getFathersName() {
        return fathersName.get();
    }

    public SimpleStringProperty fathersNameProperty() {
        return fathersName;
    }

    public int getColUserId() {
        return userId.get();
    }

    public SimpleIntegerProperty colUserIdProperty() {
        return userId;
    }

    public int getAttendanceId() {
        return attendanceId.get();
    }

    public SimpleIntegerProperty attendanceIdProperty() {
        return attendanceId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate.get();
    }

    public ObjectProperty<LocalDate> attendanceDateProperty() {
        return attendanceDate;
    }

    public LocalTime getTimeIn() {
        return timeIn.get();
    }

    public ObjectProperty<LocalTime> timeInProperty() {
        return timeIn;
    }

    public LocalTime getTimeOut() {
        return timeOut.get();
    }

    public ObjectProperty<LocalTime> timeOutProperty() {
        return timeOut;
    }


    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public String getRemarks() {
        return remarks.get();
    }

    public SimpleStringProperty remarksProperty() {
        return remarks;
    }

}
