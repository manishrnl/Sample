package org.example.university_management_system.ToolsClasses;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.university_management_system.Java_StyleSheet.Button3DEffect;

import java.util.Objects;
import java.util.Optional;

public class CustomAlertController {

    @FXML
    private ImageView iconImageView;
    @FXML
    private Label titleLabel, headerLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private HBox buttonBar;

    private Stage dialogStage;
    private Optional<ButtonType> result = Optional.empty();

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setData(Alert.AlertType alertType, String title, String header,
                        String message) {
        titleLabel.setText(title);
        headerLabel.setText(header);
        messageLabel.setText(message);

        String iconPath = switch (alertType) {
            case INFORMATION -> "/org/example/university_management_system/Images/info.png";
            case WARNING ->
                    "/org/example/university_management_system/Images/warning.png"; // Fixed path
            case ERROR ->
                    "/org/example/university_management_system/Images/error.png";     // Fixed path
            case CONFIRMATION ->
                    "/org/example/university_management_system/Images/confirmation.png"; // Fixed path
            default -> null;
        };

        if (iconPath != null) {
            try {
                iconImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
            } catch (Exception e) {
                System.err.println("Could not load icon: " + iconPath);
            }
        }

        if (alertType == Alert.AlertType.CONFIRMATION) {
            createButton("   Cancel   ", ButtonType.CANCEL, false, "button-green");
            createButton("    OK      ", ButtonType.OK, true, "button-red");
        } else {
            createButton("    OK      ", ButtonType.OK, true, "button-red");
        }
    }

    private void createButton(String text, ButtonType buttonType, boolean isDefault, String styleClass) {
        Button button = new Button(text);
        button.setDefaultButton(isDefault);
        if (styleClass != null && !styleClass.isEmpty()) {
            button.getStyleClass().add(styleClass);
        }

        Button3DEffect.applyEffect(button, "/sound/error.mp3");//(button, "/sound/sound2.mp3");
        button.setOnAction(event -> {
            result = Optional.of(buttonType);
//            if (result.equals("      OK     ")) {
//                //If alert type is error then play error sound else play sound 2
//
//                System.out.println("OK button clicked");
//            } else {
//                System.out.println("Cancel button clicked");
//            }
            dialogStage.close();
        });
        buttonBar.getChildren().add(button);
    }

    public Optional<ButtonType> getResult() {
        return result;
    }
}
