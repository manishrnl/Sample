package org.example.complete_ums;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.complete_ums.Java_StyleSheet.Theme_Manager;
import org.example.complete_ums.ToolsClasses.NavigationManager;

import java.io.IOException;

public class Main_Application extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/complete_ums/Login.fxml"));
        NavigationManager.getInstance().setPrimaryStage(stage);
        Scene scene = new Scene(root);
        Theme_Manager.applyTheme(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("University Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}