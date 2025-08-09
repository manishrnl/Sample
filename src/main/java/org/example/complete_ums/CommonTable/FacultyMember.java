package org.example.complete_ums.CommonTable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FacultyMember {
    private final StringProperty userId;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty role;
    private final StringProperty photoUrl, Designation;

    public FacultyMember(String userId, String firstName, String lastName, String role,
                         String Designation, String photoUrl) {
        this.userId = new SimpleStringProperty(userId);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.role = new SimpleStringProperty(role);
        this.photoUrl = new SimpleStringProperty(photoUrl);
        this.Designation = new SimpleStringProperty(Designation);
    }

    public String getUserId() {
        return userId.get();
    }

    public StringProperty userIdProperty() {
        return userId;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public String getRole() {
        return role.get();
    }

    public StringProperty roleProperty() {
        return role;
    }

    public String getPhotoUrl() {
        return photoUrl.get();
    }

    public StringProperty photoUrlProperty() {
        return photoUrl;
    }

    public String getDesignation() {
        return Designation.get();
    }

    public StringProperty DesignationProperty() {
        return Designation;
    }
}