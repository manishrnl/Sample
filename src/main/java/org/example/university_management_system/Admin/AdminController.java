package org.example.university_management_system.Admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.university_management_system.Databases.DatabaseConnection;
import org.example.university_management_system.Java_StyleSheet.Button3DEffect;
import org.example.university_management_system.Java_StyleSheet.RoundedImage;
import org.example.university_management_system.Java_StyleSheet.Theme_Manager;
import org.example.university_management_system.ToolsClasses.AlertManager;
import org.example.university_management_system.ToolsClasses.LoadFrame;
import org.example.university_management_system.ToolsClasses.NavigationManager;
import org.example.university_management_system.ToolsClasses.SessionManager;

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
import java.util.*;


public class AdminController implements Initializable {
    public ImageView lightDarkThemeImage;
    Theme_Manager themeManager = new Theme_Manager();
    RoundedImage roundedImage = new RoundedImage();
    Connection connection = DatabaseConnection.getConnection();
    AlertManager alertManager;
    public ScrollPane mainScrollPane;
    LoadFrame loadFrame;
    SessionManager sessionManager = SessionManager.getInstance();
    Button3DEffect button3DEffect;
    NavigationManager navigationManager = NavigationManager.getInstance();
    @FXML
    private Button btnAttendance, btnCourseManagement, btnDashboard, btnEvents, btnExams, btnFacultyManagement, btnFeedback, btnFinance, btnHostelTransport, btnLibrary, btnLogout, btnLogs, btnNotifications, btnSettings, btnStudentManagement;

    @FXML
    private Label ActiveCourses, PendingApprovals, TotalFaculty, TotalStudents, TotalStaff, errorMessageLabel, TotalDepartments, LoginAttempts, Feedback, titleLabel;
    String currentPageFxmlPath = "";
    @FXML
    private AnchorPane contentArea;

    @FXML
    private Menu menuAdminProfile;

    @FXML
    private VBox root;
    @FXML
    private ComboBox comboSearch;
    @FXML
    private HBox titleBar;

    @FXML
    private TextField txtSearch;
    @FXML
    private ImageView BackImage, ForwardImage, AdminImage;
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
        AdminImage.setOnMouseClicked(uploadProfilePhoto -> updateProfilePhoto());
        AdminImage.setStyle("-fx-cursor: hand");
        roundedImage.makeImageViewRounded(lightDarkThemeImage);
        lightDarkThemeImage.setOnMouseClicked(toggleThemeChange());
        loadProfileImage(sessionManager.getUserID());
        loadPageIntoScrollPane("/org/example/university_management_system/Admin/DashboardContent.fxml");

        Platform.runLater(() -> Theme_Manager.applyTheme(root.getScene()));
        apply3DEffectsAndText();
    }

    private EventHandler<? super MouseEvent> toggleThemeChange() {
        return event -> {
            String currentTheme = Theme_Manager.getCurrentTheme();
            if (currentTheme.equals("/org/example/university_management_system/Stylesheet/Dark_Theme.css")) {
                changeTheme("/org/example/university_management_system/Stylesheet/Light-Theme.css");
                // lightDarkThemeImage.setImage(new Image("/org/example/university_management_system/Images/light.png"));
            } else {
                changeTheme("/org/example/university_management_system/Stylesheet/Dark_Theme.css");
                //  lightDarkThemeImage.setImage(new Image("/org/example/university_management_system/Images/dark.png"));
            }
        };

    }

    private void apply3DEffectsAndText() {
        button3DEffect.applyEffect(menuAdminProfile, "/sounds/hover.mp3");
        menuAdminProfile.setText("👤 Welcome , " + sessionManager.getFirstName());
        button3DEffect.applyEffect(BackImage);
        button3DEffect.applyEffect(ForwardImage);
        button3DEffect.applyEffect(btnNotifications, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnSettings, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnAttendance, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnCourseManagement, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnDashboard, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnEvents, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnExams, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnFacultyManagement, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnFeedback, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnFinance, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnHostelTransport, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnLibrary, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnLogout, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnLogs, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnStudentManagement, "/sound/sound2.mp3");
    }


    private void loadProfileImage(int userID) {
        String imageQuery = "SELECT Photo_URL FROM Users WHERE User_Id=?";

        try (PreparedStatement stmt = connection.prepareStatement(imageQuery)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                InputStream is = rs.getBinaryStream("Photo_URL");

                if (is != null) {
                    Image image = new Image(is);
                    AdminImage.setImage(image);
                    roundedImage.makeImageViewRounded(AdminImage); // fixed
                } else {
                    alertManager.showAlert(Alert.AlertType.WARNING, "Image Not Found",
                            "No Profile Image",
                            "No profile image found for the user : " + sessionManager.getFirstName() + " " + sessionManager.getLastName() + " " + "Please upload a profile image quicky under your profile settings.");

                }
            }
        } catch (SQLException e) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load " +
                    "profile image ", "An error occurred while retrieving the profile image " +
                    "from the database.");

        }
    }


    @FXML
    void loadPageIntoScrollPane(String fxmlFile) {
        if (fxmlFile.equals(currentPage)) return;
        try {
            Parent content = FXMLLoader.load(getClass().getResource(fxmlFile));
            mainScrollPane.setContent(content);
            currentPage = fxmlFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMenuOption(ActionEvent event) {
        MenuItem clickedItem = (MenuItem) event.getSource();
        String text = clickedItem.getText();
        Scene scene = root.getScene(); // Get the scene from any Node
        String themePath;
        switch (text) {
            case "Dark Theme":
                changeTheme("/org/example/university_management_system/Stylesheet/Dark_Theme.css");
                break;
            case "Light Theme":
                changeTheme("/org/example/university_management_system/Stylesheet/Light-Theme.css");
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
            AdminImage.setImage(new Image(file.toURI().toString()));
            roundedImage.makeImageViewRounded(AdminImage); // Rounded Corners for the ImageView

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

    public void handleButtonClick(ActionEvent actionEvent) {
        String navigateToPage = "";
        Button current = (Button) actionEvent.getSource();
        String currentButton = current.getText();


        switch (currentButton) {
            case "Dashboard (Home)":
                navigateToPage = "DashboardContent.fxml";
                break;
            case "Student Management":
                navigateToPage = "students.fxml";
                break;
            case "Faculty & Staff Management":
                navigateToPage = "faculty.fxml";
                break;
            case "Course & Department":
                navigateToPage = "courses.fxml";
                break;
            case "Attendance & Timetable":
                navigateToPage = "attendance.fxml";
                break;
            case "Examination & Results":
                navigateToPage = "exams.fxml";
                break;
            case "Library Management":
                navigateToPage = "library.fxml";
                break;
            case "Hostel & Transport":
                navigateToPage = "hostel.fxml";
                break;
            case "Fees & Finance":
                navigateToPage = "finance.fxml";
                break;
            case "Events & Notification_Controller":
                navigateToPage = "events.fxml";
                break;
            case "Feedback & Complaints":
                navigateToPage = "feedback.fxml";
                break;
            case "Logs & Reports":
                navigateToPage = "logs.fxml";
                break;
            case "System Settings":
                navigateToPage = "settings.fxml";
                break;
            default:
                navigateToPage = "DashboardContent.fxml";
                break;
        }
        if (navigateToPage != null) {
            loadPageIntoScrollPane("/org/example/university_management_system/Admin/" + navigateToPage);
        }
    }

    public void handleLogout(ActionEvent actionEvent) {

        Optional<ButtonType> result;
        result = AlertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "QUIT !", "Quitting " +
                "will log you out of the Application", "Are you sure you want to Quit" +
                " . All connections will be closed and you need to login again to access our dashboard");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String query = "UPDATE Authentication SET Last_Login=? WHERE User_Id=?";
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

