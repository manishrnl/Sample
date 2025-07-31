package org.example.complete_ums.ToolsClasses;

import java.util.Date;

public class SessionManager {
    private static SessionManager instance;
    private int UserID;
    private String UserName, firstName, lastName, roleType, Password_Hash, email, pan, aadhar, phone, address, country;
    private Date DOB;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getMobile() {
        return null;
    }

    public void setDOB(Date DOB) {
        this.DOB = DOB;
    }

    public Date getDOB() {
        return this.DOB;
    }

    public void clearAll() {
        this.email = null;
        this.pan = null;
        this.aadhar = null;
        this.phone = null;
        this.address = null;
        this.country = null;
        this.firstName = null;
        this.lastName = null;
        this.UserName = null;
        this.UserID = 0;
        this.DOB = null;
        this.Password_Hash = null;
        this.roleType = null;
    }

}
