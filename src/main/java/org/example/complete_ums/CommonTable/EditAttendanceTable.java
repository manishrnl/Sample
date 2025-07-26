package org.example.complete_ums.CommonTable;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public class EditAttendanceTable {

    private SimpleIntegerProperty attendanceId;
    private SimpleStringProperty status, remarks, colMarkAttendance;
    private ObjectProperty<LocalTime> timeIn, timeOut;
    private ObjectProperty<LocalDate> attendanceDate;

    public EditAttendanceTable(int attendanceId, LocalDate attendanceDate, LocalTime timeIn,
                               LocalTime timeOut, String status, String remarks, String colMarkAttendance) {
        this.attendanceId = new SimpleIntegerProperty(attendanceId);
        this.attendanceDate = new SimpleObjectProperty<>(attendanceDate);
        this.timeIn = new SimpleObjectProperty<>(timeIn);
        this.timeOut = new SimpleObjectProperty<>(timeOut);
        this.status = new SimpleStringProperty(status);
        this.remarks = new SimpleStringProperty(remarks);
        this.colMarkAttendance = new SimpleStringProperty(colMarkAttendance);

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

    public String getColMarkAttendance() {
        return colMarkAttendance.get();
    }

    public void setColMarkAttendance(String colMarkAttendance) {
        this.colMarkAttendance.set(colMarkAttendance);
    }

    public SimpleStringProperty colMarkAttendanceProperty() {
        return colMarkAttendance;
    }
}
