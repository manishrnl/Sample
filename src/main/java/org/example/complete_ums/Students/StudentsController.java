package org.example.complete_ums.Students;

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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
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

public class StudentsController implements Initializable {

    private final NavigationManager navigationManager = NavigationManager.getInstance();
    AdminActivityLogs adminActivityLogs = new AdminActivityLogs();

    Connection connection = DatabaseConnection.getConnection();
    RoundedImage roundedImage = new RoundedImage();
    SessionManager sessionManager = SessionManager.getInstance();
    LoadFrame loadFrame;
    AlertManager alertManager;
    Button3DEffect button3DEffect;
    @FXML
    private Label errorMessageLabel, userDetails;
    @FXML
    private ImageView usersImage, lightDarkThemeImage;
    @FXML
    public StackPane contentArea;
    @FXML
    private ComboBox<String> comboSearch;

    @FXML
    private VBox root;


    private String currentPage = ""; // add this at the class level
    int totalStudents = 0, totalStudents1 = 0, totalStudents2 = 0, totalStudents3 = 0;
    private static StudentsController instance;
    private File selectedImageFile;  // Class-level variable
    private final Map<String, String> fxmlPathMap = new HashMap<>();
    private final Map<String, Runnable> functionMap = new LinkedHashMap<>();
    private final Map<String, String> pageMap = new HashMap<>();


    public StudentsController() throws SQLException {
        instance = this;
    }

    public static StudentsController getInstance() {
        return instance;
    }

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



        initializeFxmlPathMap();
        if (usersImage != null)
            usersImage.setOnMouseClicked(uploadProfilePhoto -> updateProfilePhoto());
        usersImage.setStyle("-fx-cursor: hand");
        lightDarkThemeImage.setStyle("-fx-cursor: hand");

        roundedImage.makeImageViewRounded(usersImage);
        roundedImage.makeImageViewRounded(lightDarkThemeImage);
        lightDarkThemeImage.setOnMouseClicked(toggleThemeChange());
        loadProfileImage(sessionManager.getUserID());
        loadPageIntoContentArea("/org/example/complete_ums/Students/StudentsDashboardContent.fxml");

        Platform.runLater(() -> Theme_Manager.applyTheme(root.getScene()));
        userDetails.setText("👤 Welcome, " + sessionManager.getFirstName());

    }

    private void initialisePageMap() {
        pageMap.put("Home / Dashboard", "/org/example/complete_ums/Students" +
                "/StudentsDashboardContent" +
                ".fxml");
        pageMap.put("View Profile", "/org/example/complete_ums/Students/StudentsProfile" +
                ".fxml");
        pageMap.put("Events", "/org/example/complete_ums/Events.fxml");
        pageMap.put("TimeTable", "/org/example/complete_ums/Students/TimeTable.fxml");
        pageMap.put("View Attendance", "/org/example/complete_ums/ViewAttendance.fxml");
        pageMap.put("Exams", "/org/example/complete_ums/Students/Exams.fxml");
        pageMap.put("Hostel and Transport", "/org/example/complete_ums/Students" +
                "/HostelTransport" +
                ".fxml");
        pageMap.put("View Fees paid", "/org/example/complete_ums/Students/Finance.fxml");
        pageMap.put("View Books taken", "/org/example/complete_ums/Students/viewIssuedBooks" +
                ".fxml");
        pageMap.put("Know your Faculty", "/org/example/complete_ums/Admin/faculty.fxml");
        pageMap.put("Settings", "/org/example/complete_ums/Admin/settings.fxml");

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


    private void initializeFxmlPathMap() {
        fxmlPathMap.put("btnDashboard", "/org/example/complete_ums/Students/StudentsDashboardContent.fxml");
        fxmlPathMap.put("btnViewProfile", "/org/example/complete_ums/Students/StudentsProfile.fxml");
        fxmlPathMap.put("btnEvents", "/org/example/complete_ums/Events.fxml");
        fxmlPathMap.put("btnTimeTable", "/org/example/complete_ums/Students/TimeTable.fxml");
        fxmlPathMap.put("btnAttendance", "/org/example/complete_ums/ViewAttendance.fxml");
        fxmlPathMap.put("btnExams", "/org/example/complete_ums/Students/Exams.fxml");
        fxmlPathMap.put("btnHostelTransport", "/org/example/complete_ums/Students/HostelTransport.fxml");
        fxmlPathMap.put("btnFinance", "/org/example/complete_ums/Students/Finance.fxml");
        fxmlPathMap.put("btnViewBooks", "/org/example/complete_ums/Students/viewIssuedBooks.fxml");
        fxmlPathMap.put("btnViewFaculty", "/org/example/complete_ums/Admin/faculty.fxml");
        fxmlPathMap.put("btnSettings", "/org/example/complete_ums/Admin/settings.fxml");

    }

    private void loadProfileImage(int userID) {
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
            Image profileImagess = loadImageTask.getValue(); // Get the result from the task
            if (profileImagess != null) {
                usersImage.setImage(profileImagess);
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
    @FXML
    private void handleMenuOption(ActionEvent event) {
        MenuItem clickedItem = (MenuItem) event.getSource();
        String text = clickedItem.getText();
        Scene scene = root.getScene(); // Get the scene from any Node
        String themePath;
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
            case "menuItemProfile":
               loadPageIntoContentArea("/org/example/complete_ums/Students/StudentsProfile.fxml");
                break;
            case "menuItemUploadPhoto":
                updateProfilePhoto();
                break;
            case "btnLogout":
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



    private void updateProfilePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload your Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            usersImage.setImage(new Image(file.toURI().toString()));
            roundedImage.makeImageViewRounded(usersImage); // Rounded Corners for the ImageView

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
        Optional<ButtonType> result = AlertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "Confirm Logout",
                "Are you sure you want to log out?",
                "Your session will be ended and you will be returned to the login screen.");

        // Proceed only if the user confirms
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Use the correct data types for the PreparedStatement
                String query = "UPDATE Users u JOIN Authentication a ON u.User_Id = a.User_Id SET u.User_Status = 'Inactive', a.Last_Login = ? WHERE u.User_Id = ?;";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    // Use setTimestamp for date-time objects
                    pstmt.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                    // Use setInt for integer IDs
                    pstmt.setInt(2, sessionManager.getUserID());
                    pstmt.executeUpdate();
                }
            } catch (Exception e) {
                System.err.println("Failed to update last login time: and User Status :" + e.getMessage());
                // Optionally show an alert, but don't stop the logout process.
            } finally {
                // This block ALWAYS executes, ensuring proper cleanup
                DatabaseConnection.closeConnection();
                sessionManager.clearAll();

                // --- CHANGE ---
                // The two lines for closing the stage have been removed from here.
                // Let the NavigationManager handle closing the old stage and opening the new one.
                navigationManager.navigateTo("Login.fxml");
            }
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



























