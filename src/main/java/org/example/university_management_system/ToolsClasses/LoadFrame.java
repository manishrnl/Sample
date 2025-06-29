package org.example.university_management_system.ToolsClasses;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class LoadFrame {


    public static void loadNewFrame(Stage currentStage, Class<?> contextClass, String fxmlPath, String title) throws IOException {
        if (currentStage != null) {
            currentStage.close();
        }
        URL fxmlUrl = contextClass.getResource("/org/example/university_management_system/" + fxmlPath);
        if (fxmlUrl == null) {
            throw new IOException("FXML resource not found: " + fxmlPath + ". Please check your FXML file path and deployment.");
        }
        Parent root = FXMLLoader.load(fxmlUrl);
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setTitle(title);
        newStage.show();
    }


    public static void setMessage(Label label, String message, String color) {
        label.setText(message);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 15px; -fx-font-weight: bold;");
    }

//    public static void showAlert(Alert.AlertType alertType, String header, String title, String message) {
//        Alert alert = new Alert(alertType);
//        alert.setTitle(title);
//        alert.setHeaderText(header);
//        alert.setContentText(message);
//        alert.showAndWait();
//
//    }
//
//    public static Optional<ButtonType> showResponseAlert(Alert.AlertType alertType, String title, String header,
//                                                         String message) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle(title);
//        alert.setHeaderText(header);
//        alert.setContentText(message);
//        Optional<ButtonType> result = alert.showAndWait();
//        return result;
//    }

}