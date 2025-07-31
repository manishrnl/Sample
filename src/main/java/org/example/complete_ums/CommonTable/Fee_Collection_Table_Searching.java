package org.example.complete_ums.CommonTable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Fee_Collection_Table_Searching {

    private SimpleStringProperty firstName, lastName, userName, regNo, rollNo;
    private SimpleIntegerProperty userID;

    public Fee_Collection_Table_Searching(int userID, String firstName, String lastName, String userName,
                                          String regNo, String rollNo) {

        this.userID = new SimpleIntegerProperty(userID);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.userName = new SimpleStringProperty(userName);
        this.regNo = new SimpleStringProperty(regNo);
        this.rollNo = new SimpleStringProperty(rollNo);

    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public String getUserName() {
        return userName.get();
    }

    public SimpleStringProperty userNameProperty() {
        return userName;
    }

    public String getRegNo() {
        return regNo.get();
    }

    public SimpleStringProperty regNoProperty() {
        return regNo;
    }

    public String getRollNo() {
        return rollNo.get();
    }

    public SimpleStringProperty rollNoProperty() {
        return rollNo;
    }

    public int getUserID() {
        return userID.get();
    }

    public SimpleIntegerProperty userIDProperty() {
        return userID;
    }
}