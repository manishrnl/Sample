package org.example.university_management_system.ToolsClasses;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.university_management_system.Java_StyleSheet.Theme_Manager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NavigationManager {
    private static final NavigationManager instance = new NavigationManager();
    private Stage currentStage;
    private final List<String> history = new ArrayList<>();
    private int currentIndex = -1;

    private NavigationManager() {
    }

    public static NavigationManager getInstance() {
        return instance;
    }

    public void setPrimaryStage(Stage initialStage) {
        this.currentStage = initialStage;
    }

    public void navigateTo(String fxmlPath) {
        if (currentStage == null) {
            System.err.println("Error: Primary stage has not been set. Call setPrimaryStage() first.");
            return;
        }
        try {
            currentStage.close(); // Close current window

            URL resourceUrl = getClass().getResource("/org/example/university_management_system/" + fxmlPath);
            if (resourceUrl == null) {
                System.err.println("Could not find FXML file: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            // Decide if headerless frame is needed
            boolean isUndecorated = fxmlPath.endsWith("Login.fxml") ||
                    fxmlPath.endsWith("ForgetPassword.fxml") ||
                    fxmlPath.endsWith("changePasswordWithoutLoggingIn.fxml") ||
                    fxmlPath.endsWith("changePassword.fxml");

            Stage newStage = new Stage();
            newStage.initStyle(isUndecorated ? StageStyle.TRANSPARENT : StageStyle.DECORATED);

            Scene scene = new Scene(root);
            scene.setFill(null); // Allow transparent background if needed

            // ✅ Apply theme
            Theme_Manager.applyTheme(scene);

            newStage.setScene(scene);

            // Title formatting
            String title = fxmlPath.replace(".fxml", "");
            if (title.contains("/")) {
                title = title.substring(title.lastIndexOf('/') + 1);
            }
            newStage.setTitle(title + " - University App");

            newStage.show();
            this.currentStage = newStage;

            addToHistory(fxmlPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addToHistory(String fxmlPath) {
        // Clear "forward" history if we are navigating from a "back" point
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
        URL resourceUrl = controllerClass.getResource("/org/example/university_management_system/" + fxmlFileName);
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
        newStage.initOwner(this.currentStage); // Set the owner to the current main window
        newStage.showAndWait();
    }

    public boolean canGoBack() {
        return currentIndex > 0;
    }

    public boolean canGoForward() {
        return currentIndex < history.size() - 1;
    }
}