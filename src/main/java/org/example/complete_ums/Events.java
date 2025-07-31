package org.example.complete_ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.example.complete_ums.CommonTable.EventListView;
import org.example.complete_ums.Databases.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Events implements Initializable {

    // FXML Components from your Events.fxml file
    @FXML
    private ListView<EventListView> eventsListView;
    @FXML
    private VBox eventDetailsPane;
    @FXML
    private Label eventNameLabel;
    @FXML
    private Label eventDateLabel;
    @FXML
    private Label eventTimeLabel;
    @FXML
    private Label eventLocationLabel;
    @FXML
    private Label eventTypeLabel;
    @FXML
    private Label eventOrganizerLabel;
    @FXML
    private TextArea eventDescriptionArea;
    @FXML
    private Button registerButton;

    private ObservableList<EventListView> eventMasterList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initially hide the details pane
        eventDetailsPane.setVisible(true);

        // Delegate the ListView's visual setup to your new helper class
        EventListView.setupCellFactory(eventsListView);

        // Add a listener to handle selection changes
        eventsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayEventDetails(newSelection);
            }
        });

        // Load the event data from the database
        loadEvents();
    }

    /**
     * Fetches event data from the database and populates the ListView.
     */
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
            eventsListView.setItems(eventMasterList);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions (e.g., show an alert)
        }
    }

    /**
     * Displays the details of the selected event on the right-hand side pane.
     * @param event The event to display.
     */
    private void displayEventDetails(EventListView event) {
        eventDetailsPane.setVisible(true);

        eventNameLabel.setText(event.getEventName());
        eventDateLabel.setText(event.getEventDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));

        String timeString = "N/A";
        if (event.getStartTime() != null) {
            timeString = event.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
            if (event.getEndTime() != null) {
                timeString += " - " + event.getEndTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
            }
        }
        eventTimeLabel.setText(timeString);

        eventLocationLabel.setText(event.getLocation() != null ? event.getLocation() : "N/A");
        eventTypeLabel.setText(event.getEventType() != null ? event.getEventType() : "N/A");
        eventOrganizerLabel.setText(event.getOrganizerName() != null ? event.getOrganizerName() : "University");
        eventDescriptionArea.setText(event.getEventDescription() != null ? event.getEventDescription() : "No description available.");
    }

}
