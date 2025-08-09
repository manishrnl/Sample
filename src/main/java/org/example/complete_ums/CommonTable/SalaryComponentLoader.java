package org.example.complete_ums.CommonTable;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.complete_ums.Databases.DatabaseConnection;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SalaryComponentLoader {

    public ObservableList<SalaryComponent> loadDeductionComponents() throws SQLException {
        ObservableList<SalaryComponent> deductionComponents = FXCollections.observableArrayList();
        String query = "SELECT Component_Name, Component_Type, Amount FROM Salary_Components WHERE Component_Type = 'Deduction'";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("Component_Name");
                String type = rs.getString("Component_Type");
                double amount = rs.getDouble("Amount");
                deductionComponents.add(new SalaryComponent(name, type, amount));
            }

        } catch (SQLException e) {
            System.err.println("Failed to load salary components from the database.");
            throw e;
        }

        return deductionComponents;
    }

}