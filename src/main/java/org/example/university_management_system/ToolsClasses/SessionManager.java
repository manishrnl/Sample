package org.example.university_management_system.ToolsClasses;

public class SessionManager {
    private static SessionManager instance;
    private int UserID;
    private String UserName, firstName, lastName, roleType, Password_Hash;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public int getUserID() {
        return UserID;
    }

    public String getUserName() {
        return UserName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setUserID(int UserID) {
        this.UserID = UserID;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFullName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;

    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getRole() {
        return roleType;
    }

    public void setRole(String roleType) {
        this.roleType = roleType;
    }

    public String getPassword() {
        return Password_Hash;
    }

    public void setPassword(String Password_Hash) {
        this.Password_Hash = Password_Hash;
    }

    public void clearAll() {
        this.firstName = null;
        this.lastName = null;
        this.UserName = null;
        this.UserID = 0;
        this.Password_Hash = null;
        this.roleType = null;
    }

}
