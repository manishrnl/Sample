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
import org.example.complete_ums.Databases.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public class AlertManager {

    private static Optional<ButtonType> show(Alert.AlertType alertType, String title, String header, String message) {

        try {
            FXMLLoader loader = new FXMLLoader(AlertManager.class.getResource("/org/example/complete_ums/customAlerts.fxml"));
            Parent root = loader.load();

            CustomAlertController controller = loader.getController();

            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);
            controller.setData(alertType, title,header, message);

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
    public static void confirmAndExit(SessionManager sessionManager) {
        // 1. Show a confirmation dialog
        Optional<ButtonType> result = showResponseAlert(Alert.AlertType.CONFIRMATION, "Confirm Exit",
                "Are you sure you want to exit?",
                "Your session will be ended and the application will close.");

        // 2. Handle the user's response
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User confirmed, so perform logout tasks
            try (Connection connection = DatabaseConnection.getConnection()) {
                if (connection != null && sessionManager.getUserID() != 0) {
                    String query = "UPDATE Authentication SET Last_Login = ? WHERE User_Id = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                        pstmt.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                        pstmt.setInt(2, sessionManager.getUserID());
                        pstmt.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                System.err.println("Failed to update last login time on exit: " + e.getMessage());
            } finally {
                // 3. Clean up and exit the application
                DatabaseConnection.closeConnection();
                sessionManager.clearAll();
                Platform.exit();
            }
        }
        // If the user clicks "Cancel", the dialog closes and nothing happens.
    }
    public static void showAlert(Alert.AlertType alertType, String title, String header, String message) {
        show(alertType, title, header, message);
    }

    public static Optional<ButtonType> showResponseAlert(Alert.AlertType alertType,String title,
                                                         String header,
                                                         String message) {
        return show(alertType, title, header, message);
    }
}
