package org.example.complete_ums.ToolsClasses;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.Java_StyleSheet.Theme_Manager;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomAlertController implements Initializable {

    @FXML
    private ImageView iconImageView;
    @FXML
    private Label titleLabel, headerLabel, messageLabel;
    @FXML
    private HBox buttonBar;
    @FXML
    private VBox root;

    private Stage dialogStage;

    private Optional<ButtonType> result = Optional.empty();

    public Optional<ButtonType> getResult() {
        return result;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            Scene scene = root.getScene();
            if (scene != null) {
                Theme_Manager.applyTheme(scene);
            }
        });

    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setData(Alert.AlertType alertType, String title, String header, String message) {
        titleLabel.setText(title);
        headerLabel.setText(header);
        messageLabel.setText(message);

        String iconPath = switch (alertType) {
            case INFORMATION -> "/org/example/complete_ums/Images/info.png";
            case WARNING ->
                    "/org/example/complete_ums/Images/warning.png"; // Fixed path
            case ERROR ->
                    "/org/example/complete_ums/Images/error.png";     // Fixed path
            case CONFIRMATION ->
                    "/org/example/complete_ums/Images/confirmation.png"; // Fixed path
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


}
