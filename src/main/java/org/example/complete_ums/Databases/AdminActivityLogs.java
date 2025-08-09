package org.example.complete_ums.Databases;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import org.example.complete_ums.ToolsClasses.LoadFrame;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AdminActivityLogs {
    public static AdminActivityLogs instance;
    int Log_Id, Admin_User_Id, Target_Record_Id;
    String Action_Type, Target_Table, Action_Details, IP_Address;
    LocalDateTime Action_Timestamp;
    Connection connection = DatabaseConnection.getConnection();
    ObservableList<String> data = FXCollections.observableArrayList();

    public AdminActivityLogs() throws SQLException {
    }

    public ObservableList<String> getAdminLogsData(int log_Id, int targetId) {
        String query = "Select * from Admin_Activity_Log where Log_Id=? AND " +
                "Target_Record_Id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, log_Id);
            pstmt.setInt(2, targetId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {

                Log_Id = rs.getInt("Log_Id");
                Admin_User_Id = rs.getInt("Admin_User_Id");
                Target_Record_Id = rs.getInt("Target_Record_Id");
                Action_Type = rs.getString("Action_Type");
                Target_Table = rs.getString("Target_Table");
                Action_Details = rs.getString("Action_Details");
                IP_Address = rs.getString("IP_Address");
                IP_Address = rs.getString("IP_Address");
                Action_Timestamp = rs.getTimestamp("Action_Timestamp").toLocalDateTime();
                data.addAll(String.valueOf(Log_Id), String.valueOf(Admin_User_Id),
                        String.valueOf(Target_Record_Id), Action_Type, Target_Table, Action_Details, IP_Address, String.valueOf(Action_Timestamp));
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertAdminLogsData(Label label, int adminUserId, int targetRecordId,
                                    String actionType, String targetTableName, String actionDetails) {

        // Use a default value for IP address in case of an exception
        String ipAddress = "Unknown";
        try {
            // Get the IP address and store it as a string
            InetAddress ipAddr = InetAddress.getLocalHost();
            ipAddress = ipAddr.getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("Could not get local host IP address: " + e.getMessage());
            // You can optionally display this error to the user
            // loadFrame.setMessage(label, "Could not get IP address.", "RED");
        }

        String query = "INSERT INTO Admin_Activity_Log (Admin_User_Id, Action_Type, Target_Table, Target_Record_Id, Action_Details, IP_Address) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, adminUserId);
            pstmt.setString(2, actionType);
            pstmt.setString(3, targetTableName);
            pstmt.setInt(4, targetRecordId);
            pstmt.setString(5, actionDetails);

            // Set the IP address string directly
            pstmt.setString(6, ipAddress);

            pstmt.executeUpdate();
            System.out.println("Admin activity logged successfully.");

        } catch (SQLException e) {
            System.err.println("Error logging admin activity: " + e.getMessage());
            e.printStackTrace();
            // loadFrame.setMessage(label, "Database error: " + e.getMessage(), "RED");
        }
    }
}
