package org.example.complete_ums.Admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Label;

public class Settings {

    @FXML
    private ToggleButton themeToggle;

    @FXML
    private ComboBox<String> fontSizeComboBox;

    @FXML
    private ComboBox<String> fontStyleComboBox;

    @FXML
    private Label confirmationLabel;

    @FXML
    private void initialize() {

        fontSizeComboBox.setOnAction(event -> applyFontSize());
    }

    @FXML
    private void applyFontSize() {

    }



    public void applyTheme(ActionEvent event) {
    }

    public void resetSettings(ActionEvent event) {
    }

    public void saveAndExit(ActionEvent event) {
    }
}