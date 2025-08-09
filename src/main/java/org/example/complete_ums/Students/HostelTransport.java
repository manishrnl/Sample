package org.example.complete_ums.Students;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.example.complete_ums.CommonTable.EventListView;
import org.example.complete_ums.Databases.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class HostelTransport implements Initializable {
    @FXML
    private ListView announcementsListView;
    EventListView eventListView;

    private ObservableList<EventListView> eventMasterList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Delegate the ListView's visual setup to your new helper class
        EventListView.setupCellFactory(announcementsListView);

        // Add a listener to handle selection changes
        announcementsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
              //  displayEventDetails(newSelection);
            }
        });

        // Load the event data from the database
        loadEvents();
    }

    private void loadEvents() {
        eventMasterList.clear();
        // Query to get events and also the name of the organizer by joining with the Users table
        String query = "SELECT e.*, u.First_Name, u.Last_Name FROM Events e " +
                "LEFT JOIN Users u ON e.Organized_By_User_Id = u.User_Id " +
                "ORDER BY e.Event_Date ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                // Now creates instances of the external Event class
                eventMasterList.add(new EventListView(
                        rs.getInt("Event_Id"),
                        rs.getString("Event_Name"),
                        rs.getString("Event_Description"),
                        rs.getString("Event_Type"),
                        rs.getDate("Event_Date").toLocalDate(),
                        rs.getTime("Start_Time") != null ? rs.getTime("Start_Time").toLocalTime() : null,
                        rs.getTime("End_Time") != null ? rs.getTime("End_Time").toLocalTime() : null,
                        rs.getString("Location"),
                        rs.getString("First_Name") + " " + rs.getString("Last_Name")
                ));
            }
            announcementsListView.setItems(eventMasterList);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions (e.g., show an alert)
        }
    }

}