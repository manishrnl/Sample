package org.example.complete_ums;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.complete_ums.Java_StyleSheet.Theme_Manager;
import org.example.complete_ums.ToolsClasses.NavigationManager;

import java.io.IOException;

public class Main_Application extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // 1. Load the FXML for the first screen just once
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/complete_ums/Login.fxml"));

        // 2. Prepare the navigation manager for FUTURE navigation
        NavigationManager.getInstance().setPrimaryStage(stage);

        // 3. Create the scene and apply the theme
        Scene scene = new Scene(root);
        Theme_Manager.applyTheme(scene);

        // 4. Set the initial scene and show the stage
        stage.setTitle("University Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}