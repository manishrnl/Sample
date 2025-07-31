package org.example.complete_ums.Databases;

import javafx.scene.control.Label;
import org.example.complete_ums.ToolsClasses.LoadFrame;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationSQLQuery {
    public AuthenticationSQLQuery() throws SQLException {
    }

    LoadFrame loadFrame;
    String query = "";
    Connection connection = DatabaseConnection.getConnection();
    ResultSet resultSet;

// select * from Authentication where User_Id=3;
    public String getUserNameFromAuthentication(int userID, Label errorMessageLabel) {
        query = "SELECT UserName FROM Authentication WHERE User_Id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userID);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString("UserName");
            }
        } catch (Exception e) {
            LoadFrame.setMessage(errorMessageLabel, "Something weired Happened at class " +
                    "AuthenticationSQLQuery.getUserNameFromAuthentication , Message:" + e.getMessage(), "RED");
        }

      return null;
    }

}
