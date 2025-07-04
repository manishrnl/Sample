package org.example.university_management_system.ToolsClasses;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

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


    public static void addNewFrame(Stage currentStage, Class<?> contextClass, String fxmlPath,
                                   String title, boolean wantHeader) throws IOException {

        URL fxmlUrl = contextClass.getResource("/org/example/university_management_system/" + fxmlPath);
        if (fxmlUrl == null) {
            throw new IOException("FXML resource not found: " + fxmlPath + ". Please check your FXML file path and deployment.");
        }

        Parent root = FXMLLoader.load(fxmlUrl);
        Stage newStage = new Stage();

        if (!wantHeader) {
            newStage.initStyle(StageStyle.TRANSPARENT); // For rounded corners
        } else {
            newStage.initStyle(StageStyle.DECORATED);
        }

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT); // Makes corners invisible

        newStage.setTitle(title);
        newStage.setScene(scene);
        newStage.initOwner(currentStage); // Optional
        newStage.show();
    }


    public static void setMessage(Label label, String message, String color) {
        label.setText(message);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 15px; -fx-font-weight: bold;");
    }


}