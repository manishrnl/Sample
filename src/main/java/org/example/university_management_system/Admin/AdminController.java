package org.example.university_management_system.Admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.university_management_system.Databases.DatabaseConnection;
import org.example.university_management_system.Java_StyleSheet.Button3DEffect;
import org.example.university_management_system.ToolsClasses.AlertManager;
import org.example.university_management_system.ToolsClasses.LoadFrame;
import org.example.university_management_system.ToolsClasses.NavigationManager;
import org.example.university_management_system.ToolsClasses.SessionManager;


import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;


public class AdminController implements Initializable {
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

    @FXML
    private AnchorPane contentArea;

    @FXML
    private MenuButton menuAdminProfile;

    @FXML
    private VBox root;

    @FXML
    private HBox titleBar;

    @FXML
    private TextField txtSearch;
    @FXML
    private ImageView BackImage, ForwardImage;
    private String currentPage = ""; // add this at the class level
    int totalStudents = 0, totalStudents1 = 0, totalStudents2 = 0, totalStudents3 = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPageIntoScrollPane("/org/example/university_management_system/Admin/DashboardContent.fxml");

        menuAdminProfile.setText("👤 Welcome , " + sessionManager.getFirstName());
        button3DEffect.applyEffect(BackImage);
        button3DEffect.applyEffect(ForwardImage);
        button3DEffect.applyEffect(menuAdminProfile);
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

    @FXML
    private void loadPageIntoScrollPane(String fxmlFile) {
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
    private void handleMenuOption(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) menuAdminProfile.getScene().getWindow();
        MenuItem menuItem = (MenuItem) event.getSource();  // ✅ correct cast
        String option = menuItem.getText();

        switch (option) {


            case "Change Password":
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
                break;

            case "My Profile":
                System.out.println("Logging out...");
                // handleLogout();
                break;
            case "Logout":
                System.out.println("Logout....");
                break;
            default:
                System.out.println("Unknown option selected: " + option);
                break;
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
            case "Events & Notifications":
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
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

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
            navigationManager.navigateTo("Login.fxml");
        }
    }

}
