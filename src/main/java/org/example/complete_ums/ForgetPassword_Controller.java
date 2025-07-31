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
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ForgetPassword_Controller implements Initializable {

    public StackPane passwordStack;
    public ImageView eyeIcon;
    LoadFrame loadFrame;
    Button3DEffect button3DEffect;
    AlertManager alertManager = new AlertManager();

    @FXML
    private ImageView eyeIconConfirm;


    @FXML
    private TextField aadharNumberField, UsernameField, mobileNumberField, panNumberField, registeredEmailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessageLabel, statusMessageLabel;
    @FXML
    private Button BackButton, forgetButton, QuitButton;
    NavigationManager navigationManager = NavigationManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button3DEffect.applyEffect(QuitButton, "/sound/error.mp3");
        button3DEffect.applyEffect(forgetButton, "/sound/sound2.mp3");
        button3DEffect.applyEffect(BackButton, "/sound/sound2.mp3");

    }

    @FXML
    void handleBackToLoginButton(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigationManager.navigateTo("Login.fxml");
    }

    @FXML
    void handleForgetPasswordButton(ActionEvent event) throws SQLException {
        Boolean validUser = false;
        statusMessageLabel.setText("");
        errorMessageLabel.setText("");

        String pan = panNumberField.getText().trim();
        String username = UsernameField.getText().trim();
        String email = registeredEmailField.getText().trim();
        String mobileStr = mobileNumberField.getText().trim();
        String aadharStr = aadharNumberField.getText().trim();

        long mobile;
        long aadhar;
        if (pan.isEmpty() || email.isEmpty() || username.isEmpty() || mobileStr.isEmpty() || aadharStr.isEmpty()) {
            errorMessageLabel.setText("All fields are required.");
            return;
        }

        try {
            aadhar = Long.parseLong(aadharStr);
            mobile = Long.parseLong(mobileStr);
        } catch (NumberFormatException e) {
            errorMessageLabel.setText("Aadhar and Mobile Number must be numeric.");
            return;
        }

        String verifyQuery = "SELECT User_Id FROM Users WHERE Pan = ? AND Email = ? AND Aadhar = ? AND Mobile = ?";
        String fetchUserName = "SELECT UserName FROM Authentication WHERE User_Id=?";
        String fetchPasswordQuery = "SELECT Password_Hash FROM Authentication WHERE User_Id = ?"; // Assuming UserID is primary key

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(verifyQuery)) {

            pstmt.setString(1, pan);
            pstmt.setString(2, email);
            pstmt.setLong(3, aadhar);
            pstmt.setLong(4, mobile);

            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                validUser = true;
                int userId = resultSet.getInt("User_Id");
                try (PreparedStatement pstmt3 = connection.prepareStatement(fetchUserName)) {

                    pstmt3.setInt(1, userId);
                    ResultSet resultSet3 = pstmt3.executeQuery();
                    while (resultSet3.next()) {
                        String fetchedUsername = resultSet3.getString("UserName");
                        if (!fetchedUsername.equals(username)) {
                            validUser = false;

                        } else {
                            try (PreparedStatement pstmt4 = connection.prepareStatement(fetchPasswordQuery)) {
                                pstmt4.setInt(1, userId);
                                ResultSet resultSet4 = pstmt4.executeQuery();
                                while (resultSet4.next()) {
                                    String newPassword = resultSet4.getString("Password_Hash");
                                    alertManager.showAlert(Alert.AlertType.CONFIRMATION, "User Found", "We have " +
                                            "Found user with above credentials", "You are a valid user and we " +
                                            "have fetched your password. Please note it somewhere.\nYour " +
                                            "password is : " + newPassword);
                                }
                            }
                        }
                    }


                }
            }
            if (!validUser)
                statusMessageLabel.setText("Not a valid User. Double Check that you have " +
                        "entered correct data");

        }


    }


    public void handleQuit(ActionEvent actionEvent) {
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();
    }
}


