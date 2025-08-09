package org.example.complete_ums.ToolsClasses;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class LoadFrame {

    public static void loadNewFrame(Stage currentStage, Class<?> contextClass, String fxmlPath, String title) {
        // --- NEW --- Show loading spinner before starting the background task
        Stage loadingStage = createLoadingStage(currentStage);
        loadingStage.show();

        Task<Parent> loadTask = createLoadTask(contextClass, fxmlPath);

        loadTask.setOnSucceeded(event -> {
            loadingStage.close(); // Close spinner
            if (currentStage != null) {
                currentStage.close(); // Close old stage
            }
            Parent root = loadTask.getValue();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle(title);
            newStage.show();
        });

        loadTask.setOnFailed(event -> {
            loadingStage.close();
            loadTask.getException().printStackTrace();
            // Optionally show an error alert to the user
        });

        new Thread(loadTask).start();
    }
    public static void addNewFrame(Stage currentStage, Class<?> contextClass, String fxmlPath,
                                   String title, boolean wantHeader) {
        Stage loadingStage = createLoadingStage(currentStage);
        loadingStage.show();

        Task<Parent> loadTask = createLoadTask(contextClass, fxmlPath);

        loadTask.setOnSucceeded(event -> {
            loadingStage.close(); // Close spinner
            Parent root = loadTask.getValue();
            Stage newStage = new Stage();

            if (!wantHeader) {
                newStage.initStyle(StageStyle.TRANSPARENT);
            } else {
                newStage.initStyle(StageStyle.DECORATED);
            }

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            // --- THIS IS THE FIX ---
            // Automatically apply the stylesheets from the parent window to the new scene.
            if (currentStage != null && !currentStage.getScene().getStylesheets().isEmpty()) {
                scene.getStylesheets().addAll(currentStage.getScene().getStylesheets());
            }

            newStage.setTitle(title);
            newStage.setScene(scene);
            newStage.initOwner(currentStage);
            newStage.show();
        });

        loadTask.setOnFailed(event -> {
            loadingStage.close();
            loadTask.getException().printStackTrace();
            // Optionally show an error alert to the user
        });

        new Thread(loadTask).start();
    }

    private static Task<Parent> createLoadTask(Class<?> contextClass, String fxmlPath) {
        return new Task<>() {
            @Override
            protected Parent call() throws Exception {
                URL fxmlUrl =
                        contextClass.getResource("/org/example/complete_ums/" + fxmlPath);
                if (fxmlUrl == null) {
                    throw new IOException("FXML resource not found: " + fxmlPath);
                }
                return FXMLLoader.load(fxmlUrl);
            }
        };
    }

    private static Stage createLoadingStage(Stage owner) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            stage.initOwner(owner);
        }
        ProgressIndicator pIndicator = new ProgressIndicator();
        pIndicator.setPrefSize(70, 70);

        VBox vbox = new VBox(pIndicator);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: transparent;"); // Transparent background
        Scene scene = new Scene(vbox, 150, 150);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        return stage;
    }

    public static void setMessage(Label label, String message, String color) {
        label.setText(message);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 15px; -fx-font-weight: " +
                "bold; -fx-alignment: CENTER;");
    }
}