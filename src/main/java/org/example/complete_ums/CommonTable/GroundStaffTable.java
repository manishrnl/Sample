package org.example.complete_ums.CommonTable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class GroundStaffTable {
    private SimpleStringProperty  Designation, Email,
            EmploymentType, FullName, Status;
    private SimpleIntegerProperty userID;
    private SimpleLongProperty Mobile;

    public GroundStaffTable(int userID, Long Mobile,String Designation,
                            String Email, String EmploymentType, String FullName, String Status) {
        this.userID = new SimpleIntegerProperty(userID);
        this.Mobile = new SimpleLongProperty(Mobile);

        this.Designation = new SimpleStringProperty(Designation);
        this.Email = new SimpleStringProperty(Email);
        this.EmploymentType = new SimpleStringProperty(EmploymentType);
        this.FullName = new SimpleStringProperty(FullName);
        this.Status = new SimpleStringProperty(Status);

    }

    public String getDesignation() {
        return Designation.get();
    }

    public SimpleStringProperty designationProperty() {
        return Designation;
    }

    public String getEmail() {
        return Email.get();
    }

    public SimpleStringProperty emailProperty() {
        return Email;
    }

    public String getEmploymentType() {
        return EmploymentType.get();
    }

    public SimpleStringProperty employmentTypeProperty() {
        return EmploymentType;
    }

    public String getFullName() {
        return FullName.get();
    }

    public SimpleStringProperty fullNameProperty() {
        return FullName;
    }

    public String getStatus() {
        return Status.get();
    }

    public SimpleStringProperty statusProperty() {
        return Status;
    }

    public int getUserID() {
        return userID.get();
    }

    public SimpleIntegerProperty userIDProperty() {
        return userID;
    }

    public long getMobile() {
        return Mobile.get();
    }

    public SimpleLongProperty mobileProperty() {
        return Mobile;
    }


}
