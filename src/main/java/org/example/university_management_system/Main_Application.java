package org.example.university_management_system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.university_management_system.Java_StyleSheet.Theme_Manager;
import org.example.university_management_system.ToolsClasses.NavigationManager;

import java.io.IOException;

public class Main_Application extends Application {

    NavigationManager navigationManager = NavigationManager.getInstance();

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/org/example" +
                "/university_management_system/Login.fxml")));
        navigationManager.getInstance().setPrimaryStage(stage);
        Theme_Manager.applyTheme(scene);
        navigationManager.getInstance().navigateTo("Login.fxml"
        );
    }

    public static void main(String[] args) {
        launch();
    }
}