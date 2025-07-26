package org.example.complete_ums;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.NavigationManager;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChangePasswordWithoutLoggingIn implements Initializable {
    public StackPane passwordStack;
    public ImageView eyeIcon;
    LoadFrame loadFrame;
    Button3DEffect button3DEffect;
    AlertManager alertManager = new AlertManager();
    @FXML
    private TextField showPasswordField, confirmPasswordField;
    @FXML
    private PasswordField confirmPasswordHiddenField;
    @FXML
    private ImageView eyeIconConfirm;

    private boolean showPassword = false;
    private boolean showConfirmPassword = false;
    @FXML
    private TextField aadharNumberField, UsernameField, mobileNumberField, panNumberField, registeredEmailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessageLabel, statusMessageLabel;
    @FXML
    private Button BackButton, changeButton, QuitButton;
    NavigationManager navigationManager = NavigationManager.getInstance();
    String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$";
    // Optional<ButtonType> response=null;
    Boolean successStatus = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button3DEffect.applyEffect(BackButton, "/sound/sound2.mp3");
        button3DEffect.applyEffect(changeButton, "/sound/sound2.mp3");
        button3DEffect.applyEffect(QuitButton, "/sound/error.mp3");
        showPasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        confirmPasswordField.textProperty().bindBidirectional(confirmPasswordHiddenField.textProperty());
    }


    public void handleQuit(ActionEvent actionEvent) {
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();
    }

    public void handleBackToLoginButton(ActionEvent actionEvent) {
        navigationManager.navigateTo("Login.fxml");

    }

    @FXML
    private void togglePasswordVisibility() {
        showPassword = !showPassword;
        showPasswordField.setVisible(showPassword);
        showPasswordField.setManaged(showPassword);
        passwordField.setVisible(!showPassword);
        passwordField.setManaged(!showPassword);
    }

    @FXML
    private void toggleConfirmPasswordVisibility() {
        showConfirmPassword = !showConfirmPassword;
        confirmPasswordField.setVisible(showConfirmPassword);
        confirmPasswordField.setManaged(showConfirmPassword);
        confirmPasswordHiddenField.setVisible(!showConfirmPassword);
        confirmPasswordHiddenField.setManaged(!showConfirmPassword);
    }

    public void handleChangePasswordButton(ActionEvent actionEvent) throws IOException {
        Long aadharNumber = Long.valueOf(aadharNumberField.getText());
        String username = UsernameField.getText().trim();
        Long mobileNumber = Long.parseLong(mobileNumberField.getText());
        String panNumber = panNumberField.getText().trim();
        String registeredEmail = registeredEmailField.getText().trim();
        String newPassword = passwordField.getText().trim();
        String confirmPassword = confirmPasswordHiddenField.getText().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty() || aadharNumber == 0 || username.isEmpty() || mobileNumber == 0 || panNumber.isEmpty() || registeredEmail.isEmpty()) {
            errorMessageLabel.setText("Please fill in all fields.");
            return;
        }
        if (!newPassword.matches(PASSWORD_REGEX)) {
            errorMessageLabel.setText("Password must be at least 10 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            errorMessageLabel.setText("Passwords do not match.");
            return;
        }

        String query = "SELECT * FROM Users WHERE Aadhar = ?  AND Mobile = ? AND Pan = ? AND" +
                " Email = ?";
        String fetchUserName = "SELECT UserName FROM Authentication WHERE User_Id = ?";
        String UpdatePasswordQuery = "UPDATE Authentication SET Password_Hash= ? WHERE " +
                "UserName=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query);) {
            pstmt.setLong(1, aadharNumber);
            pstmt.setLong(2, mobileNumber);
            pstmt.setString(3, panNumber);
            pstmt.setString(4, registeredEmail);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                int userID = resultSet.getInt("User_Id");
                try (PreparedStatement pstmt2 = connection.prepareStatement(fetchUserName);) {
                    pstmt2.setInt(1, userID);
                    ResultSet resultSet2 = pstmt2.executeQuery();
                    while (resultSet2.next()) {
                        String fetchedUsername = resultSet2.getString("UserName");
                        if (!fetchedUsername.equals(username)) {
                            errorMessageLabel.setText("Username does not match with the provided details.");
                            return;
                        } else {
                            try (PreparedStatement pstmt4 = connection.prepareStatement(UpdatePasswordQuery)) {
                                pstmt4.setString(1, newPassword);
                                pstmt4.setString(2, fetchedUsername);
                                int resultSet4 = pstmt4.executeUpdate();
                                if (resultSet4 > 0) {
                                   // successStatus = true;
                                    Optional<ButtonType> response = alertManager.showResponseAlert(Alert.AlertType.INFORMATION,
                                            "Success", "Password Changed", "Your password " +
                                                    "has been changed successfully.You will " +
                                                    "now be redirected to the login page.");
                                    if (response.isPresent() && (response.get() == ButtonType.OK || response.get() == ButtonType.CANCEL)) {
                                        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                                        currentStage.close();
                                        DatabaseConnection.closeConnection();
                                        navigationManager.navigateTo("Login.fxml");
                                    }

                                }
                            }
                        }
                    }
                }
            }

        } catch (
                Exception e) {
            loadFrame.setMessage(errorMessageLabel, "Database connection error.: +e.getMessage()",
                    "RED");


        }

    }
}