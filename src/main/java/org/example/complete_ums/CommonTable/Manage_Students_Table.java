package org.example.complete_ums.CommonTable;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Manage_Students_Table {

    private SimpleIntegerProperty userId;
    private SimpleLongProperty studentsMobile, aadhar;
    private SimpleStringProperty username, firstName, lastName, email, pan;
    private String fullName;

    public Manage_Students_Table(int userId, String username, String firstName, String lastName,
                                 String email, long studentsMobile, long aadhar, String
                                         pan) {
        this.userId = new SimpleIntegerProperty(userId);
        this.username = new SimpleStringProperty(username);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.email = new SimpleStringProperty(email);
        this.studentsMobile = new SimpleLongProperty(studentsMobile);
        this.aadhar = new SimpleLongProperty(aadhar);
        this.pan = new SimpleStringProperty(pan);
    }

    public int getUserId() {
        return userId.get();
    }

    public SimpleIntegerProperty userIdProperty() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public long getStudentsMobile() {
        return studentsMobile.get();
    }

    public SimpleLongProperty studentsMobileProperty() {
        return studentsMobile;
    }

    public void setStudentsMobile(long studentsMobile) {
        this.studentsMobile.set(studentsMobile);
    }

    public long getAadhar() {
        return aadhar.get();
    }

    public SimpleLongProperty aadharProperty() {
        return aadhar;
    }

    public void setAadhar(int aadhar) {
        this.aadhar.set(aadhar);
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getPan() {
        return pan.get();
    }

    public SimpleStringProperty panProperty() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan.set(pan);
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public void setFullName(SimpleStringProperty firstName, SimpleStringProperty lastName) {
        this.firstName = firstName;
        this.lastName = lastName;

    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
