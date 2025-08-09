package org.example.complete_ums.Students;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TimeTable implements Initializable {

    @FXML
    private Label fridayLabel, mondayLabel, saturdayLabel, thursdayLabel, tuesdayLabel, wednesdayLabel, weeklyTimetableLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        LocalDate tuesday = monday.plusDays(1);
        LocalDate wednesday = monday.plusDays(2);
        LocalDate thursday = monday.plusDays(3);
        LocalDate friday = monday.plusDays(4);
        LocalDate saturday = monday.plusDays(5);

        mondayLabel.setText(monday.format(formatter));
        tuesdayLabel.setText(tuesday.format(formatter));
        wednesdayLabel.setText(wednesday.format(formatter));
        thursdayLabel.setText(thursday.format(formatter));
        fridayLabel.setText(friday.format(formatter));
        saturdayLabel.setText(saturday.format(formatter));

        String defaultStyle = "-fx-background-color: transparent;";
        mondayLabel.setStyle(defaultStyle);
        tuesdayLabel.setStyle(defaultStyle);
        wednesdayLabel.setStyle(defaultStyle);
        thursdayLabel.setStyle(defaultStyle);
        fridayLabel.setStyle(defaultStyle);
        saturdayLabel.setStyle(defaultStyle);

        String highlightStyle = "-fx-background-color: #14331b; -fx-text-fill: white; -fx-padding: 5px; -fx-background-radius: 5;";
        switch (today.getDayOfWeek()) {
            case MONDAY:
                mondayLabel.setStyle(highlightStyle);
                break;
            case TUESDAY:
                tuesdayLabel.setStyle(highlightStyle);
                break;
            case WEDNESDAY:
                wednesdayLabel.setStyle(highlightStyle);
                break;
            case THURSDAY:
                thursdayLabel.setStyle(highlightStyle);
                break;
            case FRIDAY:
                fridayLabel.setStyle(highlightStyle);
                break;
            case SATURDAY:
                saturdayLabel.setStyle(highlightStyle);
                break;
        }

        weeklyTimetableLabel.setText("Weekly Timetable From: \t\t\t" + monday.format(formatter) +
                "\t\t to\t\t " + saturday.format(formatter));
    }
}