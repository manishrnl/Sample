package org.example.complete_ums.CommonTable;

public class Employee {
    private final int userId;
    private final String firstName;
    private final String lastName;
    private final String role;

    public Employee(int userId, String firstName, String lastName, String role) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
}