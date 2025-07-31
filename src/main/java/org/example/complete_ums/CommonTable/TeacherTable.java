package org.example.complete_ums.CommonTable;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class TeacherTable {


    private SimpleStringProperty colPan, colUserName, colFullName;
    private SimpleLongProperty colAadhar, colMobile;
    private SimpleIntegerProperty colUserId;

    public TeacherTable(String colPan, String colUserName, String colFullName,
                           long colAadhar, long colMobile,
                           int colUserId) {
        this.colPan = new SimpleStringProperty(colPan);
        this.colUserName = new SimpleStringProperty(colUserName);
        this.colAadhar = new SimpleLongProperty(colAadhar);
        this.colMobile = new SimpleLongProperty(colMobile);
        this.colUserId = new SimpleIntegerProperty(colUserId);
        this.colFullName = new SimpleStringProperty(colFullName);
    }

    public String getColFullName() {
        return colFullName.get();
    }

    public SimpleStringProperty colFullNameProperty() {
        return colFullName;
    }

    public String getColPan() {
        return colPan.get();
    }

    public SimpleStringProperty colPanProperty() {
        return colPan;
    }

    public String getColUserName() {
        return colUserName.get();
    }

    public SimpleStringProperty colUserNameProperty() {
        return colUserName;
    }

    public long getColAadhar() {
        return colAadhar.get();
    }

    public SimpleLongProperty colAadharProperty() {
        return colAadhar;
    }

    public long getColMobile() {
        return colMobile.get();
    }

    public SimpleLongProperty colMobileProperty() {
        return colMobile;
    }

    public int getColUserId() {
        return colUserId.get();
    }

    public SimpleIntegerProperty colUserIdProperty() {
        return colUserId;
    }
}
