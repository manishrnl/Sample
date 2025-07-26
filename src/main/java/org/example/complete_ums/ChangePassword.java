package org.example.complete_ums;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.NavigationManager;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChangePassword implements Initializable {
    public Button cancelButton;
    public Button submitButton;
    AlertManager alertManager = new AlertManager();
    SessionManager sessionManager = SessionManager.getInstance();
    LoadFrame loadFrame;
    NavigationManager navigationManager = NavigationManager.getInstance();
    DatabaseConnection databaseConnection;
    Button3DEffect button3DEffect = new Button3DEffect();
    @FXML
    private PasswordField currentPasswordField, newPasswordField, confirmPasswordField;
    @FXML
    private TextField currentShowPasswordField, newShowPasswordField, confirmShowPasswordField;
    @FXML
    private ImageView currentEyeIcon, newEyeIcon, confirmEyeIcon;
    @FXML
    private Label errorLabel;

    String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button3DEffect.applyEffect(cancelButton, "/sound/error.mp3");
        button3DEffect.applyEffect(submitButton, "/sound/sound2.mp3");


    }

    @FXML
    void handleCancel(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();

    }

    @FXML
    void handleChangePassword(ActionEvent event) {
        int userID = sessionManager.getUserID();
        String sessionPassword = sessionManager.getPassword(); // stored password hash
        String userName = sessionManager.getUserName();

        String currentpass = getVisibleText(currentPasswordField, currentShowPasswordField);
        String newPass = getVisibleText(newPasswordField, newShowPasswordField);
        String confirmPass = getVisibleText(confirmPasswordField, confirmShowPasswordField);
        // Validate current password
        if (!sessionPassword.equals(currentpass)) {
            loadFrame.setMessage(errorLabel, "Current password does not match our records for user " + userName, "RED");
            return;
        }

        // Validate new password format
        if (!newPass.matches(PASSWORD_REGEX)) {
            loadFrame.setMessage(errorLabel, "New password must be at least 10 characters long and contain upper, lower, digit, and special character.", "RED");
            return;
        }

        // Confirm password match
        if (!newPass.equals(confirmPass)) {
            loadFrame.setMessage(errorLabel, "New password and confirm password do not match for user " + userName, "RED");
            return;
        }


        // Update in database
        String query = "UPDATE Authentication SET Password_Hash=? WHERE User_Id=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, newPass);
            pstmt.setInt(2, userID);

            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                Optional<ButtonType> response =
                        alertManager.showResponseAlert(Alert.AlertType.INFORMATION,
                                "Password Changed", "Password Updated Successfully",
                                "Password has been updated successfully. You will now be logged out.");

                if (response.isPresent()) {
                    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    currentStage.close();

                    DatabaseConnection.closeConnection();
                    sessionManager.clearAll();
                    navigationManager.navigateTo("Login.fxml");
                }
            } else {
                loadFrame.setMessage(errorLabel, "Failed to update password. Please try again.", "RED");
            }

        } catch (Exception e) {
            loadFrame.setMessage(errorLabel, "An error occurred: " + e.getMessage(), "RED");
            e.printStackTrace();
        }
    }

    @FXML
    void toggleCurrentPasswordVisibility(MouseEvent event) {
        toggleVisibility(currentPasswordField, currentShowPasswordField, currentEyeIcon);
    }

    @FXML
    private void toggleNewPasswordVisibility(MouseEvent event) {
        toggleVisibility(newPasswordField, newShowPasswordField, newEyeIcon);
    }

    @FXML
    private void toggleConfirmPasswordVisibility(MouseEvent event) {
        toggleVisibility(confirmPasswordField, confirmShowPasswordField, confirmEyeIcon);
    }

    private void toggleVisibility(PasswordField pf, TextField tf, ImageView icon) {
        if (tf.isVisible()) {
            pf.setText(tf.getText());
            tf.setVisible(false);
            tf.setManaged(false);
            pf.setVisible(true);
            pf.setManaged(true);
            icon.setImage(new Image(getClass().getResourceAsStream("/Images/ShowPassword.png")));
        } else {
            tf.setText(pf.getText());
            pf.setVisible(false);
            pf.setManaged(false);
            tf.setVisible(true);
            tf.setManaged(true);
            icon.setImage(new Image(getClass().getResourceAsStream("/Images/HidePassword.png")));
        }
    }

    private String getVisibleText(PasswordField pf, TextField tf) {
        return tf.isVisible() ? tf.getText() : pf.getText();
    }


}
