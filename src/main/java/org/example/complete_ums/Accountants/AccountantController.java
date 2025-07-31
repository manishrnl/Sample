package org.example.complete_ums.Accountants;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList; // Not used currently, but kept if you intended to use it elsewhere
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
import javafx.beans.value.ChangeListener; // Import ChangeListener for explicit listener management

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
import java.util.stream.Collectors;

public class AccountantController implements Initializable {

    private final NavigationManager navigationManager = NavigationManager.getInstance();

    // Ensure connection is obtained via singleton method
    // This should be safe now with the updated DatabaseConnection class
    Connection connection = DatabaseConnection.getConnection();
    RoundedImage roundedImage = new RoundedImage();
    SessionManager sessionManager = SessionManager.getInstance();
    LoadFrame loadFrame;
    AlertManager alertManager;
    Button3DEffect button3DEffect;

    @FXML
    private ImageView studentsImage, lightDarkThemeImage, BackImage, ForwardImage;

    @FXML
    private StackPane contentArea; // Make sure this is linked in your FXML if you intend to use it as a root for mainScrollPane

    @FXML
    private ScrollPane mainScrollPane;

    @FXML
    private Menu menuStudentsProfile, menuAdminProfile2;

    @FXML
    private MenuBar menuBarAdmin;

    @FXML
    private MenuItem menuItemChangePassword, menuItemChangePassword2, menuItemLogout, menuItemProfile, menuItemProfile2, menuItemUploadPhoto, menuItemUploadPhoto2;

    @FXML
    private VBox root;

    @FXML
    private HBox titleBar;

    @FXML
    private Button btnDashboard, btnViewProfile, btnViewAttendance, btnMarkAttendance,
            btnFinance, btnNotification, btnEvents, btnLogout;

    @FXML
    private TextField txtSearch; // If this is used for general search and not the ComboBox editor

    private String currentPage = "";
    // These specific student counters might not be directly relevant to the ComboBox issue, but kept for context.
    int totalStudents = 0, totalStudents1 = 0, totalStudents2 = 0, totalStudents3 = 0;
    private File selectedImageFile;

    // These fields are not currently used in the provided code, but were in a previous snippet.
    // Keeping them for now, but consider removing if truly unused.
    private final ObservableList<String> allCommandsList = FXCollections.observableArrayList();
    private FilteredList<String> filteredCommands;
    private final Map<String, Runnable> functionMap = new LinkedHashMap<>();
    private final Map<String, String> pageMap = new LinkedHashMap<>();


    private static AccountantController instance;

    public AccountantController() throws SQLException {
        instance = this; // Initialize singleton instance
        // Initialize utility classes
        this.loadFrame = new LoadFrame();
        this.alertManager = new AlertManager();
        this.button3DEffect = new Button3DEffect();
    }

    public static AccountantController getInstance() {
        return instance;
    }

    // Map to store view names and their FXML paths
    private Map<String, String> fxmlPathMap = new HashMap<>();

    @FXML
    private ComboBox<String> searchComboBox;
    private ObservableList<String> allComboBoxItems; // Stores all keys for filtering

    // Declare the ChangeListener as a field so we can remove and re-add it
    private ChangeListener<String> searchComboBoxTextChangeListener;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // --- ComboBox and Page Loading Setup ---

        // 1. Populate the map with your view names and their corresponding FXML paths
        fxmlPathMap.put("View Attendance", "/org/example/complete_ums/ViewAttendance.fxml");
        fxmlPathMap.put("Mark Attendance", "/org/example/complete_ums/MarkAttendance.fxml");
        fxmlPathMap.put("Profile View", "/org/example/complete_ums/Accountants/accountantsProfileView.fxml");
        fxmlPathMap.put("Home", "/org/example/complete_ums/Accountants/AccountantsDashboardContent.fxml");
        fxmlPathMap.put("Notifications", "/org/example/complete_ums/Notifications.fxml");
        // Add more mappings as needed for other pages

        // 2. Initialize the master list of all ComboBox items from the map keys
        allComboBoxItems = FXCollections.observableArrayList(fxmlPathMap.keySet());

        // 3. Set the ComboBox to be editable
        searchComboBox.setEditable(true);
        searchComboBox.setItems(allComboBoxItems); // Initially populate with all items
        searchComboBox.setPromptText("Search or Select View..."); // Set a helpful prompt text

        // 4. Initialize the text change listener FIRST
        // This listener is responsible for filtering the ComboBox items as the user types.
        searchComboBoxTextChangeListener = (observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                // If text is cleared, show all items
                searchComboBox.setItems(allComboBoxItems);
            } else {
                // Filter the master list based on the typed text (case-insensitive)
                ObservableList<String> filteredList = allComboBoxItems.stream()
                        .filter(item -> item.toLowerCase().contains(newValue.toLowerCase()))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
                searchComboBox.setItems(filteredList);

                // Ensure the dropdown is shown while typing, especially if it was hidden
                Platform.runLater(() -> {
                    if (!searchComboBox.isShowing() && !filteredList.isEmpty()) {
                        searchComboBox.show();
                    } else if (filteredList.isEmpty() && searchComboBox.isShowing()) {
                        // If no matches, hide the dropdown to prevent IndexOutOfBounds issues
                        searchComboBox.hide();
                    }
                });
            }
        };

        // 5. Attach the text change listener to the ComboBox editor's text property
        searchComboBox.getEditor().textProperty().addListener(searchComboBoxTextChangeListener);


        // 6. Listener for when a user *selects* an item from the dropdown list
        searchComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                // CRITICAL FIX: Temporarily remove the text listener
                // This prevents the setText() call below from triggering the listener recursively.
                searchComboBox.getEditor().textProperty().removeListener(searchComboBoxTextChangeListener);

                // Load the corresponding page into the ScrollPane
                loadPageIntoScrollPane(fxmlPathMap.get(newValue));

                // Set the editor's text to the selected value for visual clarity
                searchComboBox.getEditor().setText(newValue);

                // CRITICAL FIX: Clear the selection model *after* setting the text and *before* re-adding the listener.
                // This often resolves IndexOutOfBoundsExceptions related to internal ListView/SelectionModel synchronization.
                searchComboBox.getSelectionModel().clearSelection();

                // Re-attach the text listener after all programmatic modifications are complete
                searchComboBox.getEditor().textProperty().addListener(searchComboBoxTextChangeListener);
            }
        });

        // 7. Optional: Handle action when user presses ENTER after typing
        searchComboBox.getEditor().setOnAction(event -> {
            String typedText = searchComboBox.getEditor().getText();
            if (fxmlPathMap.containsKey(typedText)) {
                loadPageIntoScrollPane(fxmlPathMap.get(typedText));
            } else {
                System.out.println("No view found for: \"" + typedText + "\""); // Use loadFrame.setMessage here too
                // loadFrame.setMessage(null, "No view found for: \"" + typedText + "\"", "RED");
            }
        });

        // 8. Load a default page when the application starts
        loadPageIntoScrollPane(fxmlPathMap.get("Home"));

        // --- Existing Initialization Code ---
        studentsImage.setOnMouseClicked(uploadProfilePhoto -> updateProfilePhoto());
        studentsImage.setStyle("-fx-cursor: hand");
        roundedImage.makeImageViewRounded(studentsImage);
        roundedImage.makeImageViewRounded(lightDarkThemeImage);
        lightDarkThemeImage.setOnMouseClicked(toggleThemeChange());
        loadProfileImage(sessionManager.getUserID());
        // The line below should now be redundant if "Home" is loaded by searchComboBox init
        // loadPageIntoScrollPane("/org/example/complete_ums/Accountants/AccountantsDashboardContent.fxml");


        Platform.runLater(() -> Theme_Manager.applyTheme(root.getScene()));
        apply3DEffectsAndText();
    }

    // This method seems redundant as its logic is now within initialize directly
    // If you plan to call it from other places, keep it, but ensure it doesn't cause issues.
    /*
    private void loadAndDisplayPage(String viewName) {
        String fxmlFilePath = fxmlPathMap.get(viewName);
        System.out.println("viewName = " + viewName);
        System.out.println(fxmlFilePath);
        loadPageIntoScrollPane(fxmlFilePath);
    }
    */

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

    private void apply3DEffectsAndText() {
        button3DEffect.applyEffect(menuStudentsProfile, "/sounds/hover.mp3");
        menuStudentsProfile.setText("👤 Welcome , " + sessionManager.getFirstName());
        button3DEffect.applyEffect(BackImage);
        button3DEffect.applyEffect(ForwardImage);
        button3DEffect.applyEffect(btnNotification, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnViewProfile, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnViewAttendance, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnMarkAttendance, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnDashboard, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnEvents, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnFinance, "/sound/sound2.mp3");
        button3DEffect.applyEffect(btnLogout, "/sound/sound2.mp3");
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

    // Your existing loadPageIntoScrollPane method (remains the same as it's functional)
    @FXML
    void loadPageIntoScrollPane(String fxmlFile) {
        if (fxmlFile.equals(currentPage)) {
            System.out.println("Page already loaded: " + fxmlFile);
            return;
        }
        StackPane loadingOverlay = createLoadingOverlay();
        // The cast to StackPane should be safe if your FXML structure is correct
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
            // Optionally, add a success message here if desired
        });

        loadTask.setOnFailed(event -> {
            parentContainer.getChildren().remove(loadingOverlay);
            loadTask.getException().printStackTrace();
            // Show error message to user
            Platform.runLater(() -> {
                String errorMessage = "Failed to load page: " + (loadTask.getException() != null ? loadTask.getException().getMessage() : "Unknown error.");
                // Use alertManager or loadFrame.setMessage for user feedback
                // loadFrame.setMessage(errorMessageLabel, errorMessage, "RED");
                System.err.println(errorMessage);
            });
        });
        new Thread(loadTask).start();
    }

    private StackPane createLoadingOverlay() {
        ProgressIndicator pIndicator = new ProgressIndicator();
        pIndicator.setPrefSize(80, 80);
        pIndicator.setMaxSize(80, 80);
        StackPane overlay = new StackPane(pIndicator);
        overlay.setAlignment(Pos.CENTER);
        // Using a slightly darker overlay for better visibility on potentially mixed backgrounds
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);"); // Darker, 40% transparent
        return overlay;
    }

    @FXML
    private void handleMenuOption(ActionEvent event) {
        MenuItem clickedItem = (MenuItem) event.getSource();
        String text = clickedItem.getText();
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
                // If you have a profile FXML, load it via loadPageIntoScrollPane
                // loadPageIntoScrollPane(fxmlPathMap.get("Profile View"));
                viewProfile(); // Current logic shows alert
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
    void navigateBackward(MouseEvent event) {
        navigationManager.goBack();
    }

    @FXML
    void navigateForward(MouseEvent event) {
        navigationManager.goForward();
    }

    public void handleButtonClick(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();


        Button current = (Button) actionEvent.getSource();

        String currentId = current.getId();
        String navigateToPage = null; // Initialize to null


        switch (currentId) {

            case "btnDashboard":

                navigateToPage = "AccountantsDashboardContent.fxml";

                break;

            case "btnViewProfile":

                navigateToPage = "accountantsProfileView.fxml";

                break;

            case "btnViewAttendance":

                loadPageIntoScrollPane("/org/example/complete_ums/ViewAttendance.fxml");

                break;

            case "btnMarkAttendance":

                loadPageIntoScrollPane("/org/example/complete_ums/MarkAttendance.fxml");

                break;

            case "btnFinance":

                navigateToPage = "feesCollection.fxml";

                break;

            case "btnEvents":

                loadPageIntoScrollPane("/org/example/complete_ums/Events.fxml");

                break;

            case "btnNotification":

                loadFrame.addNewFrame(currentStage, getClass(), "Notifications.fxml",

                        "Notification Panel", false);

                break;

        }





    // Only call loadPageIntoScrollPane if a page was determined
        if (navigateToPage != null) {
            // Need to get the FXML path from the map, not just use the key if it's not a path
            String fxmlPath = fxmlPathMap.get(navigateToPage);
            if (fxmlPath != null) {
                loadPageIntoScrollPane(fxmlPath);
            } else if (navigateToPage.endsWith(".fxml")) {
                // Fallback for direct FXML names not in map, but better to put them in map
                loadPageIntoScrollPane("/org/example/complete_ums/Accountants/" + navigateToPage);
            }
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
        Stage currentStage = (Stage) btnNotification.getScene().getWindow();
        loadFrame.addNewFrame(currentStage, getClass(), "Notifications.fxml", "Notification_Controller Panel", false);
    }

    // Helper method to get the user-friendly name from the FXML path
    private String getPageNameFromPath(String fxmlPath) {
        return fxmlPathMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(fxmlPath))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Unknown Page");
    }
}