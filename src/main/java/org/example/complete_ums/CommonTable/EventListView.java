package org.example.complete_ums.CommonTable;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// This class now serves as both the Data Model and the View Helper.
public class EventListView {
    private final int eventId;
    private final String eventName;
    private final String eventDescription;
    private final String eventType;
    private final LocalDate eventDate;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String location;
    private final String organizerName;

    public EventListView(int eventId, String eventName, String eventDescription, String eventType, LocalDate eventDate, LocalTime startTime, LocalTime endTime, String location, String organizerName) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.organizerName = organizerName;
    }

    // --- Getters for all properties ---
    public int getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public String getEventDescription() { return eventDescription; }
    public String getEventType() { return eventType; }
    public LocalDate getEventDate() { return eventDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public String getOrganizerName() { return organizerName; }

    public static void setupCellFactory(ListView<EventListView> listView) {
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(EventListView event, boolean empty) {
                super.updateItem(event, empty);

                if (empty || event == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox(2);
                    Label nameLabel = new Label(event.getEventName());
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    Label dateLabel = new Label(event.getEventDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
                    dateLabel.setStyle("-fx-font-size: 12px;");

                    vbox.getChildren().addAll(nameLabel, dateLabel);
                    setGraphic(vbox);
                }
            }
        });
    }

    @Override
    public String toString() {
        return eventName;
    }
}
