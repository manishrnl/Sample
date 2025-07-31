package org.example.complete_ums.Admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.Java_StyleSheet.RoundedImage;
import org.example.complete_ums.Java_StyleSheet.Theme_Manager;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.NavigationManager;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;


public class AdminController implements Initializable {

    Theme_Manager themeManager = new Theme_Manager();
    RoundedImage roundedImage = new RoundedImage();
    Connection connection = DatabaseConnection.getConnection();
    AlertManager alertManager;
    LoadFrame loadFrame;
    SessionManager sessionManager = SessionManager.getInstance();
    Button3DEffect button3DEffect;
    NavigationManager navigationManager = NavigationManager.getInstance();

    @FXML
    private ScrollPane mainScrollPane;
    @FXML
    private ImageView lightDarkThemeImage;
    @FXML
    private Button btnMarkAttendance, btnAttendance, btnCourseManagement, btnDashboard,
            btnEvents, btnExams,
            btnFeedback, btnFinance, btnHostelTransport, btnLogout, btnLogs, btnNotifications, btnSettings, btnStudentManagement,
            btnAdminManagement, btnTeacherManagement, btnAccountantManagement,
            btnLibrariansManagement, btngroundStaffManagement;

    @FXML
    private Label ActiveCourses, PendingApprovals, TotalFaculty, TotalStudents, TotalStaff, errorMessageLabel, TotalDepartments, LoginAttempts, Feedback, titleLabel;
    String currentPageFxmlPath = "";
    @FXML
    private StackPane contentArea;

    @FXML
    private MenuButton menuAdminProfile;

    @FXML
    private VBox root;
    @FXML
    private ComboBox comboSearch;
    @FXML
    private HBox titleBar;

    @FXML
    private TextField txtSearch;
    @FXML
    private ImageView BackImage, ForwardImage, profileImage;
    private String currentPage = ""; // add this at the class level
    int totalStudents = 0, totalStudents1 = 0, totalStudents2 = 0, totalStudents3 = 0;
    private static AdminController instance;
    private File selectedImageFile;  // Class-level variable

    private final ObservableList<String> allCommandsList = FXCollections.observableArrayList();
    private FilteredList<String> filteredCommands;

    private final Map<String, Runnable> functionMap = new LinkedHashMap<>();
    private final Map<String, String> pageMap = new LinkedHashMap<>();


    public AdminController() throws SQLException {
        instance = this;
    }

    public static AdminController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        profileImage.setOnMouseClicked(uploadProfilePhoto -> updateProfilePhoto());
        profileImage.setStyle("-fx-cursor: hand");
        roundedImage.makeImageViewRounded(profileImage);
        roundedImage.makeImageViewRounded(lightDarkThemeImage);
        lightDarkThemeImage.setOnMouseClicked(toggleThemeChange());
        loadProfileImage(sessionManager.getUserID());
        loadPageIntoScrollPane("/org/example/complete_ums/Admin/AdminDashboardContent.fxml");

        Platform.runLater(() -> Theme_Manager.applyTheme(root.getScene()));
        apply3DEffectsAndText();
    }

    private EventHandler<? super MouseEvent> toggleThemeChange() {
        return event -> {
            String currentTheme = Theme_Manager.getCurrentTheme();
            if (currentTheme.equals("/org/example/complete_ums/Stylesheet/Dark_Theme.css")) {
                changeTheme("/org/example/complete_ums/Stylesheet/Light-Theme.css");
                // lightDarkThemeImage.setImage(new Image("/org/example/complete_ums/Images/light.png"));
            } else {
                changeTheme("/org/example/complete_ums/Stylesheet/Dark_Theme.css");
                //  lightDarkThemeImage.setImage(new Image("/org/example/complete_ums/Images/dark.png"));
            }
        };

    }

    private void apply3DEffectsAndText() {
        button3DEffect.applyEffect(menuAdminProfile, "/sounds/hover.mp3");
        menuAdminProfile.setText("👤 Welcome , " + sessionManager.getFirstName());

        button3DEffect.applyEffect(btnAdminManagement, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnAccountantManagement, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnLibrariansManagement, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btngroundStaffManagement, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnTeacherManagement, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnAdminManagement, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnNotifications, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnSettings, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnAttendance, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnMarkAttendance, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnCourseManagement, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnDashboard, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnEvents, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnExams, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnFeedback, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnFinance, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnHostelTransport, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnLogout, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnLogs, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnStudentManagement, "/sound/sound2.mp3");
    }

    private void loadProfileImage(int userID) {
        // Create a background task to avoid freezing the UI
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                String imageQuery = "SELECT Photo_URL FROM Users WHERE User_Id=?";
                try (PreparedStatement stmt = connection.prepareStatement(imageQuery)) {
                    stmt.setInt(1, userID);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        // Use try-with-resources to automatically close the InputStream
                        try (InputStream is = rs.getBinaryStream("Photo_URL")) {
                            if (is != null) {
                                return new Image(is); // Return the loaded image
                            }
                        }
                    }
                }
                return null; // Return null if no image is found
            }
        };

        // This code runs on the JavaFX thread AFTER the background task succeeds
        loadImageTask.setOnSucceeded(event -> {
            Image profileImage1 = loadImageTask.getValue(); // Get the result from the task
            if (profileImage != null) {
                profileImage.setImage(profileImage1);
                // Assuming roundedImage is an instance of a class you wrote
                // roundedImage.makeImageViewRounded(AdminImage);
            }
        });

        // This code runs on the JavaFX thread IF the background task fails
        loadImageTask.setOnFailed(event -> {
            Throwable e = loadImageTask.getException();
            System.err.println("Error loading profile image: " + e.getMessage());
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load profile image",
                    "An error occurred while retrieving the image from the database.");
        });

        // Start the background task
        new Thread(loadImageTask).start();
    }

    @FXML
    void loadPageIntoScrollPane(String fxmlFile) {
        if (fxmlFile.equals(currentPage)) {
            return;
        }
        StackPane loadingOverlay = createLoadingOverlay();
        StackPane parentContainer = (StackPane) mainScrollPane.getParent();
        parentContainer.getChildren().add(loadingOverlay);

        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                URL resourceUrl = getClass().getResource(fxmlFile);

                if (resourceUrl == null) {
                    throw new java.io.FileNotFoundException("FXML file not found at path: " + fxmlFile);
                }
                return FXMLLoader.load(resourceUrl);
            }
        };

        loadTask.setOnSucceeded(event -> {
            Parent content = loadTask.getValue();
            mainScrollPane.setContent(content);
            currentPage = fxmlFile;
            parentContainer.getChildren().remove(loadingOverlay);
        });

        loadTask.setOnFailed(event -> {
            parentContainer.getChildren().remove(loadingOverlay);
            loadTask.getException().printStackTrace();
        });
        new Thread(loadTask).start();
    }

    private StackPane createLoadingOverlay() {
        ProgressIndicator pIndicator = new ProgressIndicator();
        pIndicator.setPrefSize(80, 80);
        pIndicator.setMaxSize(80, 80);
        StackPane overlay = new StackPane(pIndicator);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7);"); // White, 70% transparent
        return overlay;
    }

    @FXML
    private void handleMenuOption(ActionEvent event) {
        MenuItem clickedItem = (MenuItem) event.getSource();
        String text = clickedItem.getText();
        Scene scene = root.getScene(); // Get the scene from any Node
        String themePath;
        switch (text) {
            case "Dark Theme":
                changeTheme("/org/example/complete_ums/Stylesheet/Dark_Theme.css");
                break;
            case "Light Theme":
                changeTheme("/org/example/complete_ums/Stylesheet/Light-Theme.css");
                break;
            case "Change Password":
                changePassword();
                break;
            case "My Profile":
                viewProfile();
                break;
            case "Upload Profile Photo":
                updateProfilePhoto();
                break;
            case "Logout":
                handleLogout(event);
                break;
            default:
                System.out.println("Unknown option selected: " + text);
                break;
        }

    }

    private void changeTheme(String themePath) {
        if (root.getScene() != null) {
            Theme_Manager.setCurrentTheme(themePath);
            Theme_Manager.applyTheme(root.getScene());
        }
    }

    private void changePassword() {
        Optional<ButtonType> result =
                AlertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "Change " +
                        "Password", "Are you sure you want to change Password ?", "Your " +
                        "password will be updated to the desired value that must fullfill " +
                        "password criteria E.g., It must contains a combination of Upper " +
                        "case,Lower case , digits and a special character .\n\n And " +
                        "Password's length must be atleast 10 digits long" +
                        ".");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            navigationManager.navigateTo("changePassword.fxml");
        }
    }

    private void viewProfile() {
        alertManager.showAlert(Alert.AlertType.INFORMATION, "Profile Information",
                "User Profile", "Name: " + sessionManager.getFirstName() + " " +
                        sessionManager.getLastName() + "\nEmail: " + sessionManager.getEmail() +
                        "\nRole: " + sessionManager.getRole() + "\nUser ID: " + sessionManager.getUserID()
                        + "\n PAN : " + sessionManager.getPan() + "\n Aadhar : " + sessionManager.getAadhar() + "\n Mobile : " + sessionManager.getPhone() + "\n Address : " + sessionManager.getAddress() + "\n Date of Birth : " + sessionManager.getDOB());
    }

    private void updateProfilePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload your Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            profileImage.setImage(new Image(file.toURI().toString()));
            roundedImage.makeImageViewRounded(profileImage); // Rounded Corners for the ImageView

            String updateQuery = "UPDATE Users SET Photo_URL=? WHERE User_Id=?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                pstmt.setBinaryStream(1, new FileInputStream(selectedImageFile));
                pstmt.setInt(2, sessionManager.getUserID());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Profile image updated successfully.");
                    AlertManager.showAlert(Alert.AlertType.INFORMATION, "Success", "Profile Image Updated",
                            "Your profile image has been updated successfully.");
                } else {
                    System.out.println("No record updated. User ID might be invalid.");
                    AlertManager.showAlert(Alert.AlertType.ERROR, "Error", "Profile Image Update Failed",
                            "An error occurred while updating your profile image. Please try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertManager.showAlert(Alert.AlertType.ERROR, "Error", "Profile Image Update Failed",
                        "An error occurred while updating your profile image. Please try again.");
            }
        } else {
            System.out.println("No image selected.");
        }


    }

    @FXML
    void navigateBackward(MouseEvent event) {
        navigationManager.goBack();
    }

    @FXML
    void navigateForward(MouseEvent event) {
        navigationManager.goForward();
    }

    public void handleButtonClick(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        String navigateToPage = "";
        Button current = (Button) actionEvent.getSource();
        String currentButton = current.getId();
        System.out.println("AdminController.handleButtonClick" + currentButton);

        switch (currentButton) {
            case "btnDashboard":
                navigateToPage = "AdminDashboardContent.fxml";
                break;
            case "btnStudentManagement":
                navigateToPage = "StudentsManagement.fxml";
                break;
            case "btnAdminManagement":
                navigateToPage = "AdminManagement.fxml";
                break;
            case "btnTeacherManagement":
                navigateToPage = "TeacherManagement.fxml";
                break;
            case "btnAccountantManagement":
                navigateToPage = "AccountantManagement.fxml";
                break;
            case "btnLibrariansManagement":
                navigateToPage = "LibrariansManagement.fxml";
                break;
            case "btngroundStaffManagement":
                navigateToPage = "GroundStaffManagement.fxml";
                break;
            case "btnCourseManagement":
                navigateToPage = "courses.fxml";
                break;
            case "btnAttendance":
                loadPageIntoScrollPane("/org/example/complete_ums/ViewAttendance.fxml");
                break;
            case "btnMarkAttendance":
                loadPageIntoScrollPane("/org/example/complete_ums/MarkAttendance.fxml");
                break;
            case "btnExams":
                loadPageIntoScrollPane("/org/example/complete_ums/Students/Exams.fxml");
                break;
            case "btnHostelTransport":
                loadPageIntoScrollPane("/org/example/complete_ums/Students/HostelTransport.fxml");
                break;
            case "btnFinance":
                navigateToPage = "finance.fxml";
                break;
            case "btnEvents":
                loadPageIntoScrollPane("/org/example/complete_ums/Events.fxml");
                break;
            case "btnFeedback":
                navigateToPage = "feedback.fxml";
                break;
            case "btnLogs":
                navigateToPage = "logs.fxml";
                break;
            case "btnSettings":
                navigateToPage = "settings.fxml";
                break;
            default:
                System.out.println("Invalid response..");
                break;
        }
        if (navigateToPage != null) {
            loadPageIntoScrollPane("/org/example/complete_ums/Admin/" + navigateToPage);
        }
    }

    public void handleLogout(ActionEvent actionEvent) {

        Optional<ButtonType> result;
        result = AlertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "QUIT !", "Quitting " +
                "will log you out of the Application", "Are you sure you want to Quit" +
                " . All connections will be closed and you need to login again to access our dashboard");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String query = "UPDATE Users u JOIN Authentication a ON u.User_Id = a.User_Id SET u.User_Status = 'Inactive', a.Last_Login = ? WHERE u.User_Id = ?;";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {

                pstmt.setString(1, String.valueOf(LocalDateTime.now()));
                pstmt.setString(2, String.valueOf(sessionManager.getUserID()));

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Last Login Updated");

                } else {
                    System.out.println("No record updated. User ID might be invalid.");
                }
            } catch (Exception e) {
                throw new RuntimeException("Error updating last login", e);
            }

            DatabaseConnection.closeConnection();
            sessionManager.clearAll();
            Stage currentStage = (Stage) btnLogout.getScene().getWindow();
            currentStage.close();
            navigationManager.navigateTo("Login.fxml");
        }
    }

    public void handleNotification(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) btnNotifications.getScene().getWindow();
        loadFrame.addNewFrame(currentStage, getClass(), "Notifications.fxml",
                "Notification_Controller " +
                        "Panel", false);

    }
}
