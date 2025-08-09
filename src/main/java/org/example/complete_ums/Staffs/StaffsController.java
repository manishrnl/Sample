package org.example.complete_ums.Staffs;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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

public class StaffsController implements Initializable {
    AdminActivityLogs adminActivityLogs = new AdminActivityLogs();
    private final NavigationManager navigationManager = NavigationManager.getInstance();
    @FXML
    public Label errorMessageLabel, usersDetails;

    Connection connection = DatabaseConnection.getConnection();
    RoundedImage roundedImage = new RoundedImage();
    SessionManager sessionManager = SessionManager.getInstance();
    LoadFrame loadFrame;
    AlertManager alertManager;
    Button3DEffect button3DEffect;

    @FXML
    private ImageView studentsImage, lightDarkThemeImage, BackImage, ForwardImage;

    @FXML
    private StackPane contentArea;

    @FXML
    private ScrollPane mainScrollPane;


    @FXML
    private MenuBar menuBarAdmin;
    @FXML
    private ComboBox<String> comboSearch;

    @FXML
    private VBox root;

    @FXML
    private HBox titleBar;


    @FXML
    private TextField txtSearch;

    private String currentPage = "";
    int totalStudents = 0, totalStudents1 = 0, totalStudents2 = 0, totalStudents3 = 0;
    private File selectedImageFile;

    private final ObservableList<String> allCommandsList = FXCollections.observableArrayList();
    private FilteredList<String> filteredCommands;
    private final Map<String, Runnable> functionMap = new LinkedHashMap<>();
    private final Map<String, String> pageMap = new HashMap<>();


    private static StaffsController instance;

    public StaffsController() throws SQLException {
        instance = this;
        this.loadFrame = new LoadFrame();
        this.alertManager = new AlertManager();
        this.button3DEffect = new Button3DEffect();
    }


    public static StaffsController getInstance() {
        return instance;
    }

    private final Map<String, String> fxmlPathMap = new HashMap<>();

    @FXML
    private ComboBox<String> searchComboBox;

    private ChangeListener<String> searchComboBoxTextChangeListener;


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
        usersDetails.setText("👤 Welcome , " + sessionManager.getFirstName());
        loadPageIntoContentArea("/org/example/complete_ums/Staffs/StaffsDashboardContent" +
                ".fxml");
        studentsImage.setOnMouseClicked(uploadProfilePhoto -> updateProfilePhoto());
        studentsImage.setStyle("-fx-cursor: hand");
        roundedImage.makeImageViewRounded(studentsImage);
        roundedImage.makeImageViewRounded(lightDarkThemeImage);
        lightDarkThemeImage.setOnMouseClicked(toggleThemeChange());
        loadProfileImage(sessionManager.getUserID());

        Platform.runLater(() -> Theme_Manager.applyTheme(root.getScene()));

    }

    private void initialisePageMap() {
        pageMap.put("View Attendance", "/org/example/complete_ums/ViewAttendance.fxml");
        pageMap.put("Mark Attendance", "/org/example/complete_ums/MarkAttendance.fxml");
        pageMap.put("View Profile", "/org/example/complete_ums/Staffs/StaffsProfileView.fxml");

        pageMap.put("Home / Dashboard", "/org/example/complete_ums/Staffs" +
                "/StaffsDashboardContent" +
                ".fxml");
        pageMap.put("Events", "/org/example/complete_ums/Events.fxml");
        pageMap.put("Settings", "/org/example/complete_ums/Admin/settings.fxml");
        pageMap.put("View Salary", "/org/example/complete_ums/ViewSalary.fxml");

        pageMap.put("View our Faculty Members", "/org/example/complete_ums/Admin/faculty" +
                ".fxml");


    }

    private EventHandler<? super MouseEvent> toggleThemeChange() {
        return event -> {
            String currentTheme = Theme_Manager.getCurrentTheme();
            if (currentTheme.equals("/org/example/complete_ums/Stylesheet/Dark_Theme.css")) {
                changeTheme("/org/example/complete_ums/Stylesheet/Light-Theme.css");
            } else {
                changeTheme("/org/example/complete_ums/Stylesheet/Dark_Theme.css");
            }
        };
    }

    private void initializeFxmlPathMap() {
        fxmlPathMap.put("btnAttendance", "/org/example/complete_ums/ViewAttendance.fxml");
        fxmlPathMap.put("btnMarkAttendance", "/org/example/complete_ums/MarkAttendance.fxml");
        fxmlPathMap.put("btnViewProfile", "/org/example/complete_ums/Staffs/StaffsProfileView.fxml");

        fxmlPathMap.put("btnDashboard", "/org/example/complete_ums/Staffs/StaffsDashboardContent.fxml");
        fxmlPathMap.put("btnEvents", "/org/example/complete_ums/Events.fxml");
        fxmlPathMap.put("btnSettings", "/org/example/complete_ums/Admin/settings.fxml");
        fxmlPathMap.put("btnViewSalary", "/org/example/complete_ums/ViewSalary.fxml");

        fxmlPathMap.put("btnViewFaculty", "/org/example/complete_ums/Admin/faculty.fxml");
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
                        try (InputStream is = rs.getBinaryStream("Photo_URL")) {
                            if (is != null) {
                                return new Image(is);
                            }
                        }
                    }
                }
                return null;
            }
        };

        loadImageTask.setOnSucceeded(event -> {
            Image profileImage = loadImageTask.getValue();
            if (profileImage != null) {
                studentsImage.setImage(profileImage);
            }
        });

        loadImageTask.setOnFailed(event -> {
            Throwable e = loadImageTask.getException();
            System.err.println("Error loading profile image: " + e.getMessage());
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load profile image",
                    "An error occurred while retrieving the image from the database.");
        });

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

        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                URL resourceUrl = getClass().getResource(fxmlFile);
                if (resourceUrl == null) {
                    throw new FileNotFoundException("FXML file not found at path: " + fxmlFile);
                }
                FXMLLoader loader = new FXMLLoader(resourceUrl);
                return loader.load();
            }
        };

        loadTask.setOnSucceeded(e -> {
            Parent newContent = loadTask.getValue();
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(newContent);
                currentPage = fxmlFile;
            }
        });

        loadTask.setOnFailed(e -> {
            Throwable exception = loadTask.getException();
            StringBuilder detailedMessage = new StringBuilder();

            detailedMessage.append("Error while loading FXML: ").append(fxmlFile).append("\n\n")
                    .append("Exception: ").append(exception.getClass().getName()).append("\n")
                    .append("Message: ").append(exception.getMessage()).append("\n\n");

            // Append cause chain
            Throwable cause = exception.getCause();
            while (cause != null) {
                detailedMessage.append("Caused by: ").append(cause.getClass().getName()).append(" - ")
                        .append(cause.getMessage()).append("\n");
                cause = cause.getCause();
            }

            // Stack trace to console (or write to log file)
            exception.printStackTrace();

            alertManager.showAlert(
                    Alert.AlertType.ERROR,
                    "Page Load Error",
                    "Failed to load: " + fxmlFile,
                    detailedMessage.toString()
            );

            contentArea.getChildren().clear();
        });

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
        String text = clickedItem.getId();
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
               loadPageIntoContentArea("/org/example/complete_ums/Staffs/StaffsProfileView.fxml");
                break;
            case "menuItemUploadPhoto":
                updateProfilePhoto();
                break;
            case "menuLogout":
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
        Optional<ButtonType> result = AlertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "Change " +
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
            studentsImage.setImage(new Image(file.toURI().toString()));
            roundedImage.makeImageViewRounded(studentsImage);

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
    public void handleMenuItemClick(ActionEvent actionEvent) {
        try {
            MenuItem item = (MenuItem) actionEvent.getSource();
            String buttonId = item.getId();
            String fxmlPath = fxmlPathMap.get(buttonId);
            if (fxmlPath != null) {
                loadPageIntoContentArea(fxmlPath);

            }
        } catch (Exception e) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unexpected Error",
                    "An error occurred while processing the menu item: " + e.getMessage());
        }
    }

    public void handleLogout(ActionEvent actionEvent) {
        Optional<ButtonType> result = AlertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "Confirm Logout",
                "Are you sure you want to log out?",
                "Your session will be ended and you will be returned to the login screen.");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String query = "UPDATE Users u JOIN Authentication a ON u.User_Id = a.User_Id SET u.User_Status = 'Inactive', a.Last_Login = ? WHERE u.User_Id = ?;";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                    pstmt.setInt(2, sessionManager.getUserID());
                    pstmt.executeUpdate();
                }
            } catch (Exception e) {
                System.err.println("Failed to update last login time and User Status : " + e.getMessage());
            } finally {
                DatabaseConnection.closeConnection();
                sessionManager.clearAll();
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
}