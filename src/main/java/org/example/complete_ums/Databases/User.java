package org.example.complete_ums.Databases;

public class User {
    private int userId;
    private String UserName;
    private String roleType;
    private String password;

    private String User_Status;
    private String Admin_Approval_Status ;
    private String First_Name ;
    private String Last_Name ;

    public User(int userId, String username, String roleType) {
        this.userId = userId;
        this.UserName = username;
        this.roleType = roleType;
    }

    // --- Getters ---
    public int getUserId() {
        return userId;
    }

    public void setUserID(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getLast_Name() {
        return Last_Name;
    }

    public void setLast_Name(String last_Name) {
        Last_Name = last_Name;
    }

    public String getFirst_Name() {
        return First_Name;
    }

    public void setFirst_Name(String first_Name) {
        First_Name = first_Name;
    }

    public String getAdmin_Approval_Status() {
        return Admin_Approval_Status;
    }

    public void setAdmin_Approval_Status(String admin_Approval_Status) {
        Admin_Approval_Status = admin_Approval_Status;
    }

    public String getUser_Status() {
        return User_Status;
    }

    public void setUser_Status(String user_Status) {
        User_Status = user_Status;
    }
}
