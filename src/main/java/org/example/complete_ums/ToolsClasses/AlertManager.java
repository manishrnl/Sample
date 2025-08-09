package org.example.complete_ums.ToolsClasses;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Optional;

public class AlertManager {

    private static Optional<ButtonType> show(Alert.AlertType alertType, String title, String header, String message) {

        try {
            FXMLLoader loader = new FXMLLoader(AlertManager.class.getResource("/org/example/complete_ums/customAlerts.fxml"));
            Parent root = loader.load();

            CustomAlertController controller = loader.getController();

            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);
            controller.setData(alertType, title, header, message);

            // --- KEY CHANGES FOR ROUNDED CORNERS ---
            // 1. Hide the default white, rectangular window frame.
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            // 2. Make the scene background transparent so our rounded corners show through.
            scene.setFill(null);

            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            return controller.getResult();

        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static void showAlert(Alert.AlertType alertType, String title, String header, String message) {
        show(alertType, title, header, message);
    }

    public static Optional<ButtonType> showResponseAlert(Alert.AlertType alertType, String title,
                                                         String header,
                                                         String message) {
        return show(alertType, title, header, message);
    }
}
