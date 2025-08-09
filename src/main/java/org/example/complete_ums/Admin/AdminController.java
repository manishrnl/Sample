package org.example.complete_ums.Admin;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javafx.animation.PauseTransition;
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
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import kotlin.text.UStringsKt;
import org.example.complete_ums.Databases.AdminActivityLogs;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.Java_StyleSheet.RoundedImage;
import org.example.complete_ums.Java_StyleSheet.Theme_Manager;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.NavigationManager;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class AdminController implements Initializable {

    private AdminActivityLogs adminActivityLogs = new AdminActivityLogs();
    private Theme_Manager themeManager = new Theme_Manager();
    private RoundedImage roundedImage = new RoundedImage();
    private Connection connection = DatabaseConnection.getConnection();
    private AlertManager alertManager = new AlertManager();
    private LoadFrame loadFrame = new LoadFrame();
    private SessionManager sessionManager = SessionManager.getInstance();
    private Button3DEffect button3DEffect = new Button3DEffect();
    private NavigationManager navigationManager = NavigationManager.getInstance();
    @FXML
    private ComboBox<String> comboSearch;
    @FXML
    private ImageView lightDarkThemeImage;
    @FXML
    private Button btnNotifications;
    @FXML
    private Label menuAdminProfile, errorMessageLabel;
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox root;
    @FXML
    private ImageView profileImage;
    private String currentPage = "";
    private static AdminController instance;
    private File selectedImageFile;
    private final Map<String, String> fxmlPathMap = new HashMap<>();

    public AdminController() throws SQLException {
        instance = this;
    }

    private final Stack<String> backwardStack = new Stack<>();
    private final Stack<String> forwardStack = new Stack<>();

    String currentTheme = null;
    private final Map<String, String> pageMap = new HashMap<>();

    public static AdminController getInstance() {
        return instance;
    }


    private List<String> navigationArrow = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initialisePageMap();


        ObservableList<String> items = FXCollections.observableArrayList(pageMap.keySet());
        FilteredList<String> filteredItems = new FilteredList<>(items, p -> true);

        comboSearch.setEditable(true);
        comboSearch.setItems(filteredItems);

        // Filter logic while typing
        comboSearch.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = comboSearch.getEditor();
            final String selected = comboSearch.getSelectionModel().getSelectedItem();

            Platform.runLater(() -> {
                if (selected == null || !selected.equals(editor.getText())) {
                    filteredItems.setPredicate(item -> item.toLowerCase().contains(newValue.toLowerCase()));
                    comboSearch.show(); // Show dropdown when typing
                }
            });
        });


        // Handle selection
        comboSearch.setOnAction(event -> {
            String selected = comboSearch.getValue();

            if (pageMap.containsKey(selected)) {
                loadPageIntoContentArea(pageMap.get(selected));
            }
        });


        button3DEffect.applyEffect(btnNotifications, "/sound/hover.mp3");
        initializeFxmlPathMap();

        profileImage.setOnMouseClicked(uploadProfilePhoto -> updateProfilePhoto());
        profileImage.setStyle("-fx-cursor: hand");
        roundedImage.makeImageViewRounded(profileImage);
        roundedImage.makeImageViewRounded(lightDarkThemeImage);
        lightDarkThemeImage.setOnMouseClicked(toggleThemeChange());

        int userID = sessionManager.getUserID();

        loadProfileImage(userID);
        loadPageIntoContentArea("/org/example/complete_ums/Admin/AdminDashboardContent.fxml");

        Platform.runLater(() -> Theme_Manager.applyTheme(root.getScene()));
        menuAdminProfile.setText("👤 Welcome, " + sessionManager.getFirstName());


        currentTheme = themeManager.getCurrentTheme();
        if (currentTheme == null || currentTheme.isEmpty()) {
            currentTheme = "/org/example/complete_ums/Stylesheet/Light-Theme.css"; // Default theme
        } else {
            themeManager.applyTheme(contentArea.getScene(), currentTheme);
            // themeManager.applyTheme(contentArea.getScene(), "org/example/complete_ums/Stylesheet/Dark_Theme.css");
        }


    }

    private void initialisePageMap() {
        pageMap.put("Dashboard", "/org/example/complete_ums/Admin/AdminDashboardContent.fxml");
        pageMap.put("View Profile", "/org/example/complete_ums/Admin/AdminProfileView.fxml");
        pageMap.put("Student Management", "/org/example/complete_ums/Admin" +
                "/StudentsManagement.fxml");
        pageMap.put("Admin Management", "/org/example/complete_ums/Admin/AdminManagement" +
                ".fxml");
        pageMap.put("Teacher Management", "/org/example/complete_ums/Admin" +
                "/TeacherManagement.fxml");
        pageMap.put("Accountant Management", "/org/example/complete_ums/Admin" +
                "/AccountantManagement.fxml");
        pageMap.put("Librarians Management", "/org/example/complete_ums/Admin" +
                "/LibrariansManagement.fxml");
        pageMap.put("Ground Staff Management", "/org/example/complete_ums/Admin" +
                "/GroundStaffManagement.fxml");

        pageMap.put("View Attendance", "/org/example/complete_ums/ViewAttendance.fxml");
        pageMap.put("Mark Attendance", "/org/example/complete_ums/MarkAttendance.fxml");
        pageMap.put("Exams", "/org/example/complete_ums/Students/Exams.fxml");
        pageMap.put("Hostel and Transport", "/org/example/complete_ums/Students" +
                "/HostelTransport.fxml");
        pageMap.put("Check Student's Fees status ", "/org/example/complete_ums/Accountants" +
                "/feesCollection" +
                ".fxml");
        pageMap.put("Events", "/org/example/complete_ums/Events.fxml");
        pageMap.put("Feedback", "/org/example/complete_ums/Admin/feedback.fxml");
        pageMap.put("Logs", "/org/example/complete_ums/Admin/logs.fxml");
        pageMap.put("Settings", "/org/example/complete_ums/Admin/settings.fxml");
        pageMap.put("Initialize Data", "/org/example/complete_ums" +
                "/InsertMissingFieldsIntoSQLTable.fxml");
        pageMap.put("View Salary", "/org/example/complete_ums/ViewSalary.fxml");
        pageMap.put("Salary Distribution", "/org/example/complete_ums/SalaryDistribution" +
                ".fxml");
        pageMap.put("View Our Faculty Members", "/org/example/complete_ums/Admin/faculty" +
                ".fxml");
    }

    private void initializeFxmlPathMap() {
        fxmlPathMap.put("btnDashboard", "/org/example/complete_ums/Admin/AdminDashboardContent.fxml");
        fxmlPathMap.put("btnViewProfile", "/org/example/complete_ums/Admin/AdminProfileView.fxml");
        fxmlPathMap.put("btnStudentManagement", "/org/example/complete_ums/Admin/StudentsManagement.fxml");
        fxmlPathMap.put("btnAdminManagement", "/org/example/complete_ums/Admin/AdminManagement.fxml");
        fxmlPathMap.put("btnTeacherManagement", "/org/example/complete_ums/Admin/TeacherManagement.fxml");
        fxmlPathMap.put("btnAccountantManagement", "/org/example/complete_ums/Admin/AccountantManagement.fxml");
        fxmlPathMap.put("btnLibrariansManagement", "/org/example/complete_ums/Admin/LibrariansManagement.fxml");
        fxmlPathMap.put("btngroundStaffManagement", "/org/example/complete_ums/Admin/GroundStaffManagement.fxml");

        fxmlPathMap.put("btnAttendance", "/org/example/complete_ums/ViewAttendance.fxml");
        fxmlPathMap.put("btnMarkAttendance", "/org/example/complete_ums/MarkAttendance.fxml");
        fxmlPathMap.put("btnExams", "/org/example/complete_ums/Students/Exams.fxml");
        fxmlPathMap.put("btnHostelTransport", "/org/example/complete_ums/Students/HostelTransport.fxml");
        fxmlPathMap.put("btnFinance", "/org/example/complete_ums/Accountants/feesCollection" +
                ".fxml");
        fxmlPathMap.put("btnEvents", "/org/example/complete_ums/Events.fxml");
        fxmlPathMap.put("btnFeedback", "/org/example/complete_ums/Admin/feedback.fxml");
        fxmlPathMap.put("btnLogs", "/org/example/complete_ums/Admin/logs.fxml");
        fxmlPathMap.put("btnSettings", "/org/example/complete_ums/Admin/settings.fxml");
        fxmlPathMap.put("btnInitializeData", "/org/example/complete_ums/InsertMissingFieldsIntoSQLTable.fxml");
        fxmlPathMap.put("btnViewSalary", "/org/example/complete_ums/ViewSalary.fxml");
        fxmlPathMap.put("btnSalaryDistribution", "/org/example/complete_ums/SalaryDistribution.fxml");
        fxmlPathMap.put("btnFaculty", "/org/example/complete_ums/Admin/faculty.fxml");
    }


    @FXML
    private void handleMenuOption(ActionEvent event) {
        MenuItem clickedItem = (MenuItem) event.getSource();
        String text = clickedItem.getText();
        switch (text) {
            case "menuItemDarkTheme":
                changeTheme("/org/example/complete_ums/Stylesheet/Dark_Theme.css");
                break;
            case "menuItemLightTheme":
                changeTheme("/org/example/complete_ums/Stylesheet/Light-Theme.css");
                break;
            case "menuItemChangePassword":
                changePassword();
                break;
            case "My Profile":
                loadPageIntoContentArea("/org/example/complete_ums/Admin/AdminProfileView.fxml");
                break;
            case "menuItemUploadPhoto":
                updateProfilePhoto();
                break;

            default:
                System.err.println("Unknown menu option: " + text);
                break;
        }
    }

    private EventHandler<? super MouseEvent> toggleThemeChange() {
        return event -> {
            String currentTheme = Theme_Manager.getCurrentTheme();
            String newTheme = currentTheme.equals("/org/example/complete_ums/Stylesheet/Dark_Theme.css")
                    ? "/org/example/complete_ums/Stylesheet/Light-Theme.css"
                    : "/org/example/complete_ums/Stylesheet/Dark_Theme.css";
            changeTheme(newTheme);
        };
    }

    private void loadProfileImage(int userID) {
        String imageQuery = "SELECT Photo_URL FROM Users WHERE User_Id=?";
        try (PreparedStatement stmt = connection.prepareStatement(imageQuery)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                try (InputStream is = rs.getBinaryStream("Photo_URL")) {
                    if (is != null) {
                        Image profileImage1 = new Image(is);
                        profileImage.setImage(profileImage1);
                    }
                }
            }
        } catch (SQLException | IOException e) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load profile image",
                    "An error occurred while retrieving the image: " + e.getMessage());
        }
    }

    private void loadPageIntoContentArea(String fxmlFile) {
        if (fxmlFile == null) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Page Load Error", "Invalid Path",
                    "The requested page path is null.");
            return;
        }

        StackPane overlay = createLoadingOverlay();
        contentArea.getChildren().clear();
        contentArea.getChildren().add(overlay);

        // 2. Create a Task to handle the background loading
        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                // This code runs on a background thread
                URL resourceUrl = getClass().getResource(fxmlFile);
                if (resourceUrl == null) {
                    throw new FileNotFoundException("FXML file not found at path: " + fxmlFile);
                }
                FXMLLoader loader = new FXMLLoader(resourceUrl);
                Parent newContent = loader.load();
                return newContent;
            }
        };

        // 3. Set up event handlers for the Task
        loadTask.setOnSucceeded(e -> {
            // This code runs on the JavaFX Application Thread
            Parent newContent = loadTask.getValue();
            if (contentArea != null) {
                contentArea.getChildren().clear(); // Clear the overlay
                contentArea.getChildren().add(newContent); // Add the new content
                currentPage = fxmlFile;

            }
        });

        loadTask.setOnFailed(e -> {
            // This code runs on the JavaFX Application Thread in case of failure
            Throwable exception = loadTask.getException();
            if (exception instanceof FileNotFoundException) {
                alertManager.showAlert(Alert.AlertType.ERROR, "Page Load Error", "File Not Found",
                        "The requested page could not be found: " + fxmlFile);
            } else if (exception instanceof IOException) {
                alertManager.showAlert(Alert.AlertType.ERROR, "Page Load Error", "Loading Failed",
                        "An error occurred while loading the page: " + exception.getMessage());
            } else {
                alertManager.showAlert(Alert.AlertType.ERROR, "Page Load Error", "Unexpected Error",
                        "An unexpected error occurred: " + exception.getMessage());
            }
            // In case of failure, you might want to show a message or go back to a default view
            contentArea.getChildren().clear(); // Clear the overlay
        });

        // 4. Start the Task on a new thread
        new Thread(loadTask).start();
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

        PauseTransition delay0 = new PauseTransition(Duration.seconds(0));
        delay0.setOnFinished(event -> startingLabel.setVisible(true));
        delay0.play();

        PauseTransition delay1 = new PauseTransition(Duration.seconds(4));
        delay1.setOnFinished(event -> startingLabel.setText("Still loading, please wait .We are trying to Load your page sooner..."));
        delay1.play();

        PauseTransition delay2 = new PauseTransition(Duration.seconds(7));
        delay2.setOnFinished(event -> startingLabel.setText("It's taking longer than usual. It might be slow internet connectivity on your side \n or the database I used is responding very late at present."));
        delay2.play();

        return overlay;
    }

    private void changeTheme(String themePath) {
        if (root.getScene() != null) {
            Theme_Manager.setCurrentTheme(themePath);
            Theme_Manager.applyTheme(root.getScene());
            if (adminActivityLogs != null && errorMessageLabel != null) {
                try {
                    adminActivityLogs.insertAdminLogsData(
                            errorMessageLabel,
                            sessionManager.getUserID(),
                            sessionManager.getUserID(),
                            "UPDATE",
                            "Theme",
                            "Changed to " + themePath
                    );
                } catch (Exception e) {
                    System.err.println("Error logging theme change: " + e.getMessage());
                }
            }
        } else {
            alertManager.showAlert(Alert.AlertType.ERROR, "Theme Error", "Failed to Change Theme",
                    "The scene is not initialized.");
        }
    }

    private void changePassword() {
        Optional<ButtonType> result = alertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "Change Password",
                "Are you sure you want to change Password?",
                "Your password must contain a combination of uppercase, lowercase, digits, and a special character, " +
                        "and be at least 10 characters long.");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            navigationManager.navigateTo("changePassword.fxml");
            if (adminActivityLogs != null && errorMessageLabel != null) {
                try {
                    adminActivityLogs.insertAdminLogsData(
                            errorMessageLabel,
                            sessionManager.getUserID(),
                            sessionManager.getUserID(),
                            "NAVIGATION",
                            "Password",
                            "Navigated to change password"
                    );
                } catch (Exception e) {
                    System.err.println("Error logging password change navigation: " + e.getMessage());
                }
            }
        }
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
            roundedImage.makeImageViewRounded(profileImage);
            String updateQuery = "UPDATE Users SET Photo_URL=? WHERE User_Id=?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                pstmt.setBinaryStream(1, new FileInputStream(selectedImageFile));
                pstmt.setInt(2, sessionManager.getUserID());
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    if (adminActivityLogs != null && errorMessageLabel != null) {
                        try {
                            adminActivityLogs.insertAdminLogsData(
                                    errorMessageLabel,
                                    sessionManager.getUserID(),
                                    sessionManager.getUserID(),
                                    "UPDATE",
                                    "Users",
                                    "Profile Photo Updated"
                            );
                        } catch (Exception e) {
                            System.err.println("Error logging profile photo update: " + e.getMessage());
                        }
                    }
                    alertManager.showAlert(Alert.AlertType.INFORMATION, "Success", "Profile Image Updated",
                            "Your profile image has been updated successfully.");
                } else {
                    alertManager.showAlert(Alert.AlertType.ERROR, "Error", "Profile Image Update Failed",
                            "No user found with the specified ID.");
                }
            } catch (Exception e) {
                alertManager.showAlert(Alert.AlertType.ERROR, "Error", "Profile Image Update Failed",
                        "An error occurred while updating your profile image: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleMenuItemClicked(ActionEvent actionEvent) {
        try {
            MenuItem item = (MenuItem) actionEvent.getSource();
            String buttonId = item.getId();
            String fxmlPath = fxmlPathMap.get(buttonId);
            if (fxmlPath != null) {
                loadPageIntoContentArea(fxmlPath);
                if (adminActivityLogs != null && errorMessageLabel != null) {
                    try {
                        adminActivityLogs.insertAdminLogsData(
                                errorMessageLabel,
                                sessionManager.getUserID(),
                                sessionManager.getUserID(),
                                "NAVIGATION",
                                "MenuItem",
                                "Clicked " + item.getText()
                        );
                    } catch (Exception e) {
                        System.err.println("Error logging menu item click: " + e.getMessage());
                    }
                }
            } else {
                alertManager.showAlert(Alert.AlertType.ERROR, "Navigation Error", "Page Not Found",
                        "No page is configured for the selected menu item: " + buttonId);
            }
        } catch (Exception e) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unexpected Error",
                    "An error occurred while processing the menu item: " + e.getMessage());
        }
    }

    @FXML
    public void handleAbout(ActionEvent event) {
        alertManager.showAlert(Alert.AlertType.INFORMATION, "About", "About the Application",
                "University Management System v1.0");

        if (adminActivityLogs != null && errorMessageLabel != null) {
            try {
                adminActivityLogs.insertAdminLogsData(
                        errorMessageLabel,
                        sessionManager.getUserID(),
                        sessionManager.getUserID(),
                        "VIEW",
                        "About",
                        "Displayed About dialog"
                );
            } catch (Exception e) {
                System.err.println("Error logging about dialog: " + e.getMessage());
            }
        }
    }

    public void handleLogout(ActionEvent actionEvent) {
        Optional<ButtonType> result = alertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "QUIT!",
                "Quitting will log you out of the Application",
                "Are you sure you want to Quit? All connections will be closed, and you need to login again.");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String query = "UPDATE Users u JOIN Authentication a ON u.User_Id = a.User_Id " +
                    "SET u.User_Status = 'Inactive', a.Last_Login = ? WHERE u.User_Id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, String.valueOf(LocalDateTime.now()));
                pstmt.setInt(2, sessionManager.getUserID());
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    if (adminActivityLogs != null && errorMessageLabel != null) {
                        try {
                            adminActivityLogs.insertAdminLogsData(
                                    errorMessageLabel,
                                    sessionManager.getUserID(),
                                    sessionManager.getUserID(),
                                    "UPDATE",
                                    "Users",
                                    "Logged out"
                            );
                        } catch (Exception e) {
                            System.err.println("Error logging logout: " + e.getMessage());
                        }
                    }
                } else {
                    System.err.println("No record updated. User ID might be invalid: " + sessionManager.getUserID());
                }
            } catch (SQLException e) {
                alertManager.showAlert(Alert.AlertType.ERROR, "Logout Error", "Database Update Failed",
                        "Failed to update login status: " + e.getMessage());
            }
            DatabaseConnection.closeConnection();
            sessionManager.clearAll();
            Stage currentStage = (Stage) contentArea.getScene().getWindow();
            currentStage.close();
            navigationManager.navigateTo("Login.fxml");

        }
    }

    public void handleNotification(ActionEvent actionEvent) throws IOException {

        loadPageIntoContentArea("/org/example/complete_ums/Notifications.fxml");
        if (adminActivityLogs != null && errorMessageLabel != null) {
            try {
                adminActivityLogs.insertAdminLogsData(
                        errorMessageLabel,
                        sessionManager.getUserID(),
                        sessionManager.getUserID(),
                        "NAVIGATION",
                        "Notifications",
                        "Opened Notifications"
                );
            } catch (Exception e) {
                System.err.println("Error logging notification open: " + e.getMessage());
            }
        }
    }
}