package org.example.complete_ums.ToolsClasses;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Theme_Manager;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NavigationManager {
    private static final NavigationManager instance = new NavigationManager();
    private Stage currentStage;
    private final List<String> history = new ArrayList<>();
    private int currentIndex = -1;

    public NavigationManager() {
    }

    public static NavigationManager getInstance() {
        return instance;
    }

    public void setPrimaryStage(Stage initialStage) {
        this.currentStage = initialStage;
    }

    public void navigateTo(String fxmlPath) {
        if (currentStage == null && !fxmlPath.endsWith("Login.fxml")) {
            System.err.println("Error: Primary stage has not been set.");
            return;
        }
        Stage loadingStage = createLoadingStage();
        loadingStage.show();
        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                URL resourceUrl = getClass().getResource("/org/example/complete_ums/" + fxmlPath);
                if (resourceUrl == null) {
                    throw new IOException("Could not find FXML file: " + fxmlPath);
                }
                FXMLLoader loader = new FXMLLoader(resourceUrl);
                return loader.load();
            }
        };

        loadTask.setOnSucceeded(event -> {
            Parent root = loadTask.getValue();
            loadingStage.close();
            if (currentStage != null) {
                currentStage.close();
            }

            boolean isUndecorated =
                    fxmlPath.endsWith("Login.fxml") || fxmlPath.endsWith("changePassword" +
                            ".fxml") || fxmlPath.endsWith("changePasswordWithoutLoggingIn.fxml") || fxmlPath.endsWith(
                            "ForgetPassword.fxml");
            Stage newStage = new Stage();
            newStage.initStyle(isUndecorated ? StageStyle.TRANSPARENT : StageStyle.DECORATED);

            Scene scene = new Scene(root);
            scene.setFill(null);
            Theme_Manager.applyTheme(scene);

            if (!isUndecorated) {
                newStage.setOnCloseRequest(closeEvent -> {
                    closeEvent.consume();
                    confirmAndExitApplication();
                });

                newStage.setMaximized(true);
            }

            newStage.setScene(scene);
            String title = fxmlPath.replace(".fxml", "");
            if (title.contains("/")) {
                title = title.substring(title.lastIndexOf('/') + 1);
            }
            newStage.setTitle(title + " - University App");
            newStage.show();
            this.currentStage = newStage;
            addToHistory(fxmlPath);
        });
        loadTask.setOnFailed(event -> {
            loadingStage.close();
            loadTask.getException().printStackTrace();
        });
        new Thread(loadTask).start();
    }

    public void confirmAndExitApplication() {
        Optional<ButtonType> result = AlertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "Confirm Exit", "Are you sure you want to exit?", "Your session will be ended and the application will close.");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                SessionManager sessionManager = SessionManager.getInstance();
                Connection connection = DatabaseConnection.getConnection();
                if (connection != null && sessionManager.getUserID() != 0) {
                    String query = "UPDATE Users u JOIN Authentication a ON u.User_Id = a.User_Id SET u.User_Status = 'Inactive', a.Last_Login = ? WHERE u.User_Id = ?;";
                    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                        pstmt.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                        pstmt.setInt(2, sessionManager.getUserID());
                        pstmt.executeUpdate();
                        System.out.println("Last login time updated for User ID: " + sessionManager.getUserID());
                    }
                }
            } catch (SQLException e) {
                System.err.println("Failed to update last login time on exit: " + e.getMessage());

            } finally {
                DatabaseConnection.closeConnection();
                SessionManager.getInstance().clearAll();
                Platform.exit();
            }
        }
    }

    private Stage createLoadingStage() {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        if (currentStage != null) {
            stage.initOwner(currentStage);
        }

        ProgressIndicator pIndicator = new ProgressIndicator();
        pIndicator.setPrefSize(80, 80);

        VBox vbox = new VBox(pIndicator);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);");

        Scene scene = new Scene(vbox, 200, 200);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        return stage;
    }

    private void addToHistory(String fxmlPath) {
        if (currentIndex < history.size() - 1) {
            history.subList(currentIndex + 1, history.size()).clear();
        }
        history.add(fxmlPath);
        currentIndex++;
    }

    public void goBack() {
        if (canGoBack()) {
            currentIndex--;
            navigateTo(history.get(currentIndex));
        } else {
            System.out.println("No more history to go back to.");
        }
    }

    public void goForward() {
        if (canGoForward()) {
            currentIndex++;
            navigateTo(history.get(currentIndex));
        } else {
            System.out.println("No more history to go forward to.");
        }
    }

    public void loadNewFrame(Class<?> controllerClass, String fxmlFileName, String title, boolean hideHeader) throws IOException {
        URL resourceUrl = controllerClass.getResource("/org/example/complete_ums/" + fxmlFileName);
        if (resourceUrl == null) {
            System.err.println("Could not find FXML file: " + fxmlFileName);
            return;
        }
        Parent root = FXMLLoader.load(resourceUrl);
        Stage newStage = new Stage();
        if (hideHeader) {
            newStage.initStyle(StageStyle.UNDECORATED);
        } else {
            newStage.initStyle(StageStyle.DECORATED);
            newStage.setTitle(title);
        }
        newStage.setScene(new Scene(root));
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.initOwner(this.currentStage);
        newStage.showAndWait();
    }

    public boolean canGoBack() {
        return currentIndex > 0;
    }

    public boolean canGoForward() {
        return currentIndex < history.size() - 1;
    }
}