package org.example.complete_ums;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.Java_StyleSheet.EdgeColorAnimation;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.NavigationManager;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Login_Controller implements Initializable {
    private EdgeColorAnimation edgeColorAnimation = new EdgeColorAnimation();
    private Button3DEffect button3DEffect = new Button3DEffect();
    NavigationManager navigationManager = NavigationManager.getInstance();
    LoadFrame loadFrame = new LoadFrame();
    SessionManager sessionManager = SessionManager.getInstance();
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField showPasswordField;
    @FXML
    private ImageView eyeIcon;

    private boolean isPasswordVisible = false;

    @FXML
    private TextField usernameField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button LoginButton, SignupButton, quitButton;

    private static int passwordCount = 5;
    private static final int MAX_ATTEMPTS = 5;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        showPasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        LoadFrame.setMessage(errorLabel, "", "GREEN");
        passwordCount = MAX_ATTEMPTS;
        button3DEffect.applyEffect(quitButton, "/sound/error.mp3");
        button3DEffect.applyEffect(LoginButton, "/sound/sound2.mp3");
        button3DEffect.applyEffect(SignupButton, "/sound/sound2.mp3");
    }


    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        showPasswordField.setVisible(isPasswordVisible);
        showPasswordField.setManaged(isPasswordVisible);
        passwordField.setVisible(!isPasswordVisible);
        passwordField.setManaged(!isPasswordVisible);
    }

    @FXML
    void handleForgotPassword(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigationManager.navigateTo("ForgetPassword.fxml");
    }


    @FXML
    void handleSignupButton(ActionEvent event) {
        navigationManager.navigateTo("Signup.fxml");
    }
    private boolean validateUserName(String userName) throws SQLException {
        // This is a database operation, so it can stay on the background thread.
        String query = "SELECT Lockout_Until FROM Authentication WHERE UserName = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Timestamp lockoutUntil = rs.getTimestamp("Lockout_Until");

                if (lockoutUntil != null && lockoutUntil.toLocalDateTime().isAfter(LocalDateTime.now())) {
                    LocalDateTime lockoutDateTime = lockoutUntil.toLocalDateTime();
                    LocalDateTime now = LocalDateTime.now();
                    Duration duration = Duration.between(now, lockoutDateTime);
                    long minutes = duration.toMinutes();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    String formattedDate = lockoutDateTime.toLocalDate().format(formatter);
                    String errorMessage = "Account is locked for date " + formattedDate +
                            ", Please try again after time - " + minutes + " minutes.";
                    // IMPORTANT: The UI update needs to be on the FX thread.
                    Platform.runLater(() -> LoadFrame.setMessage(errorLabel, errorMessage, "RED"));
                    return false;
                }
                return true;
            } else {
                // IMPORTANT: UI update wrapped in Platform.runLater
                Platform.runLater(() -> LoadFrame.setMessage(errorLabel, "Invalid Username.", "RED"));
                return false;
            }
        } catch (SQLException e) {
            // IMPORTANT: UI update wrapped in Platform.runLater
            Platform.runLater(() -> LoadFrame.setMessage(errorLabel, "Database error during username validation: " + e.getMessage(), "RED"));
            throw e; // Re-throw the exception to be caught by the calling Task.
        }
    }
    private boolean validatePasswordAndManageAttempts(String userName, String password) throws SQLException {
        String query = "SELECT * FROM Authentication WHERE UserName = ? AND Password_Hash = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                passwordCount = MAX_ATTEMPTS;
                clearLockout(userName, connection);
                // UI update
                Platform.runLater(() -> LoadFrame.setMessage(errorLabel, "Initiating Login Process....", "GREEN"));
                return true;
            } else {
                --passwordCount;
                if (passwordCount > 0) {
                    // UI update
                    String message = "Password Incorrect. You have " + passwordCount + " attempts left.";
                    Platform.runLater(() -> LoadFrame.setMessage(errorLabel, message, "RED"));
                } else {
                    setLockout(userName, connection);
                    // UI update
                    String message = "You have exhausted all attempts. Your account is locked for 2 hours. Please contact admin to reset your password or try again later.";
                    Platform.runLater(() -> LoadFrame.setMessage(errorLabel, message, "RED"));
                    return false;
                }
            }
            return false;
        }
    }


    private StackPane createLoadingOverlay() {
        ProgressIndicator pIndicator = new ProgressIndicator();
        pIndicator.setPrefSize(80, 80);
        pIndicator.setMaxSize(80, 80);

        Label startingLabel = new Label("Taking you to your destined Page ... Please wait a moment");
        startingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 17px;");
        startingLabel.setVisible(false);

        VBox content = new VBox(10, pIndicator, startingLabel);
        content.setAlignment(Pos.CENTER);

        StackPane overlay = new StackPane(content);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");

        PauseTransition delay0 = new PauseTransition(javafx.util.Duration.seconds(0));
        delay0.setOnFinished(event -> startingLabel.setVisible(true));
        delay0.play();

        PauseTransition delay1 = new PauseTransition(javafx.util.Duration.seconds(4));
        delay1.setOnFinished(event -> startingLabel.setText("Still loading, please wait .We are trying to Load your page sooner..."));
        delay1.play();

        PauseTransition delay2 = new PauseTransition(javafx.util.Duration.seconds(7));
        delay2.setOnFinished(event -> startingLabel.setText("It's taking longer than usual. It might be slow internet connectivity on your side \n or the database I used is responding very late at present."));
        delay2.play();

        return overlay;
    }
    private void setLockout(String userName, Connection connection) {
        // Schedule UI update on the JavaFX Application Thread
        Platform.runLater(() -> {
            LoadFrame.setMessage(errorLabel, "Locking user account due to multiple failed attempts...", "RED");
        });

        String updateQuery = "UPDATE Authentication SET Lockout_Until = ? WHERE UserName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = LocalDateTime.now().plusHours(2).format(formatter);

            pstmt.setString(1, formattedDateTime);  // bind as String in correct format
            pstmt.setString(2, userName);

            pstmt.executeUpdate();

            // Schedule UI update on the JavaFX Application Thread
            Platform.runLater(() -> {
                LoadFrame.setMessage(errorLabel, "Login_Controller: User '" + userName + "' locked out " +
                        "until " + formattedDateTime, "RED");
            });

        } catch (SQLException ex) {
            // Schedule UI update on the JavaFX Application Thread
            Platform.runLater(() -> {
                LoadFrame.setMessage(errorLabel, "Error while locking user account: " + ex.getMessage(), "RED");
            });
            ex.printStackTrace();
        }
    }

    private void clearLockout(String userName, Connection connection) {
        // Schedule UI update on the JavaFX Application Thread
        Platform.runLater(() -> {
            LoadFrame.setMessage(errorLabel, "Clearing user lockout...", "GREEN");
        });

        String updateQuery = "UPDATE Authentication SET Lockout_Until = NULL WHERE UserName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, userName);
            pstmt.executeUpdate();

        } catch (SQLException ex) {
            // Schedule UI update on the JavaFX Application Thread
            Platform.runLater(() -> {
                LoadFrame.setMessage(errorLabel, "Error while clearing user lockout: " + ex.getMessage(), "RED");
            });
            System.err.println("Login_Controller: Exception while clearing user lockout: " + ex.getMessage());
            ex.printStackTrace();
        }

}
/*
    @FXML
    void handleLoginButton(ActionEvent event) throws SQLException {
        loadFrame.setMessage(errorLabel, "Initiating Login Process... Please wait while we fetch details and validate it for further process....", "GREEN");
        String userName = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (userName.isEmpty() || password.isEmpty()) {
            LoadFrame.setMessage(errorLabel, "All Fields are required.", "RED");
            return;
        }

        if (!validateUserName(userName) || !validatePasswordAndManageAttempts(userName, password)) {
            return;
        }

        String query = """
                SELECT a.User_Id, u.Mobile,u.Permanent_Address,u.DOB,a.Password_Hash, u.Role, u.User_Status, u
                    .Admin_Approval_Status,u.First_Name, u.Aadhar,u.Nationality,u.Last_Name, u.Email, u.Pan FROM 
                                                                                                    Authentication a
                JOIN Users u ON a.User_Id = u.User_Id WHERE a.UserName = ?
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                LoadFrame.setMessage(errorLabel, "Setting Users Details and Logging  you to the main Page", "GREEN");
                int userId = rs.getInt("User_Id");
                String dbPassword = rs.getString("Password_Hash");
                String role = rs.getString("Role");
                String approval = rs.getString("Admin_Approval_Status");

                sessionManager.setUserID(userId);
                sessionManager.setUserName(userName);
                sessionManager.setPassword(dbPassword);
                sessionManager.setFirstName(rs.getString("First_Name"));
                sessionManager.setLastName(rs.getString("Last_Name"));
                sessionManager.setEmail(rs.getString("Email"));
                sessionManager.setPan(rs.getString("Pan"));
                sessionManager.setPhone(String.valueOf(rs.getLong("Mobile")));
                sessionManager.setAddress(rs.getString("Permanent_Address"));
                sessionManager.setDOB(rs.getDate("DOB"));
                sessionManager.setRole(role);
                sessionManager.setAadhar(rs.getString("Aadhar"));
                sessionManager.setCountry(rs.getString("Nationality"));


                switch (approval) {
                    case "Rejected" -> {
                        LoadFrame.setMessage(errorLabel, "Account rejected. Contact admin.", "RED");
                        return;
                    }
                    case "Pending" -> {
                        LoadFrame.setMessage(errorLabel, "Approval pending. Contact admin.", "RED");
                        return;
                    }
                }

                String fxmlPath = switch (role) {
                    case "Student" -> "Students/StudentsDashboard.fxml";
                    case "Admin" -> "Admin/AdminDashboard.fxml";
                    case "Teacher" -> "Teachers/TeachersDashboard.fxml";
                    case "Staff" -> "Staffs/StaffsDashboard.fxml";
                    case "Accountant" -> "Accountants/AccountantsDashboard.fxml";
                    case "Librarian" -> "Librarians/LibrariansDashboard.fxml";
                    default -> {
                        LoadFrame.setMessage(errorLabel, "Unknown role: " + role, "RED");
                        yield null;
                    }
                };

                if (fxmlPath != null) {
                    setUserStatus(userId);
                    navigationManager.navigateTo(fxmlPath);
                }

            } else {
                LoadFrame.setMessage(errorLabel, "Invalid credentials.Check at " +
                        "Login_Controller.handleLoginButton() Manually.", "RED");
            }

        } catch (SQLException e) {
            LoadFrame.setMessage(errorLabel, "SQL error at Login_Controller.handleLoginButton()  " + e.getMessage(), "RED");
        } catch (Exception e) {
            LoadFrame.setMessage(errorLabel, "Unexpected error: " + e.getMessage(), "RED");
        }
    }
*/


    @FXML
    void handleLoginButton(ActionEvent event) {
        String userName = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (userName.isEmpty() || password.isEmpty()) {
            LoadFrame.setMessage(errorLabel, "All Fields are required.", "RED");
            return;
        }

        // Get the root node so we can add/remove overlay
        StackPane rootPane = (StackPane) ((Node) event.getSource()).getScene().getRoot();
        StackPane overlay = createLoadingOverlay();
        rootPane.getChildren().add(overlay);

        Task<Void> loginTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Show first message
                Platform.runLater(() -> LoadFrame.setMessage(errorLabel,
                        "Initiating Login Process... Please wait while we fetch details and validate it for further process....",
                        "GREEN"));

                if (!validateUserName(userName)) return null;

                if (!validatePasswordAndManageAttempts(userName, password)) return null;

                // Step 3: Fetch user details
                String query = """
                            SELECT a.User_Id, u.Mobile,u.Permanent_Address,u.DOB,a.Password_Hash, u.Role, 
                                   u.User_Status, u.Admin_Approval_Status,u.First_Name, u.Aadhar,u.Nationality,
                                   u.Last_Name, u.Email, u.Pan
                            FROM Authentication a
                            JOIN Users u ON a.User_Id = u.User_Id 
                            WHERE a.UserName = ?
                        """;

                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = connection.prepareStatement(query)) {

                    pstmt.setString(1, userName);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        Platform.runLater(() -> LoadFrame.setMessage(errorLabel,
                                "Setting Users Details and Logging you to the main Page", "GREEN"));

                        int userId = rs.getInt("User_Id");
                        String dbPassword = rs.getString("Password_Hash");
                        String role = rs.getString("Role");
                        String approval = rs.getString("Admin_Approval_Status");

                        // Approval check
                        switch (approval) {
                            case "Rejected" -> {
                                Platform.runLater(() -> LoadFrame.setMessage(errorLabel,
                                        "Account rejected. Contact admin.", "RED"));
                                return null;
                            }
                            case "Pending" -> {
                                Platform.runLater(() -> LoadFrame.setMessage(errorLabel,
                                        "Approval pending. Contact admin.", "RED"));
                                return null;
                            }
                        }
                        // Save in session manager
                        sessionManager.setUserID(userId);
                        sessionManager.setUserName(userName);
                        sessionManager.setPassword(dbPassword);
                        sessionManager.setFirstName(rs.getString("First_Name"));
                        sessionManager.setLastName(rs.getString("Last_Name"));
                        sessionManager.setEmail(rs.getString("Email"));
                        sessionManager.setPan(rs.getString("Pan"));
                        sessionManager.setPhone(String.valueOf(rs.getLong("Mobile")));
                        sessionManager.setAddress(rs.getString("Permanent_Address"));
                        sessionManager.setDOB(rs.getDate("DOB"));
                        sessionManager.setRole(role);
                        sessionManager.setAadhar(rs.getString("Aadhar"));
                        sessionManager.setCountry(rs.getString("Nationality"));
                        // Role-based navigation
                        String fxmlPath = switch (role) {
                            case "Student" -> "Students/StudentsDashboard.fxml";
                            case "Admin" -> "Admin/AdminDashboard.fxml";
                            case "Teacher" -> "Teachers/TeachersDashboard.fxml";
                            case "Staff" -> "Staffs/StaffsDashboard.fxml";
                            case "Accountant" -> "Accountants/AccountantsDashboard.fxml";
                            case "Librarian" -> "Librarians/LibrariansDashboard.fxml";
                            default -> null;
                        };

                        if (fxmlPath != null) {
                            setUserStatus(userId);
                            String finalPath = fxmlPath;
                            Platform.runLater(() -> navigationManager.navigateTo(finalPath));
                        }
                    } else {
                        Platform.runLater(() -> LoadFrame.setMessage(errorLabel,
                                "Invalid credentials. Check at Login_Controller.handleLoginButton() manually.", "RED"));
                    }

                } catch (SQLException e) {
                    Platform.runLater(() -> LoadFrame.setMessage(errorLabel,
                            "SQL error at Login_Controller.handleLoginButton(): " + e.getMessage(), "RED"));
                } catch (Exception e) {
                    Platform.runLater(() -> LoadFrame.setMessage(errorLabel,
                            "Unexpected error: " + e.getMessage(), "RED"));
                }
                return null;
            }

            @Override
            protected void succeeded() {
                rootPane.getChildren().remove(overlay); // remove loading screen
            }

            @Override
            protected void failed() {
                rootPane.getChildren().remove(overlay);
                LoadFrame.setMessage(errorLabel, "Login failed. Please try again.", "RED");
            }
        };

        Thread loginThread = new Thread(loginTask);
        loginThread.setDaemon(true);
        loginThread.start();
    }

    private void setUserStatus(int userId) throws SQLException {
        String query = "Update Users set User_Status ='Active' where User_Id=" + userId;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.executeUpdate();

        }
    }

    public void handleQuit(ActionEvent actionEvent) {
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();
    }

    public void handleChangePassword(ActionEvent actionEvent) {
        navigationManager.navigateTo("changePasswordWithoutLoggingIn.fxml");
    }
}

