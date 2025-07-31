package org.example.complete_ums.Students;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.RoundedImage;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StudentsProfile implements Initializable {
    public StudentsProfile() throws SQLException {
    }
    Connection connection = DatabaseConnection.getConnection();
    SessionManager sessionManager = SessionManager.getInstance();
    LoadFrame loadFrame;
    AlertManager alertManager;
    private File selectedImageFile;
    RoundedImage roundedImage = new RoundedImage();

    @FXML
    private ImageView profileImage;
    @FXML
    private Label studentUserNameLabel, aadharLabel, academicYearLabel, altMobileLabel,
            approvalStatusLabel, batchLabel, bloodGroupLabel, courseLabel, departmentLabel, dobLabel,
            emailLabel, emergencyContactMobileLabel, emergencyContactNameLabel, emergencyContactRelationshipLabel, enrollmentDateLabel, fathersNameLabel,
            firstNameLabel, fullNameLabel, genderLabel, joinedOnLabel, lastNameLabel, maritalStatusLabel, mothersNameLabel, nationalityLabel, panLabel,
            permanentAddressLabel, phoneLabel, referencedViaLabel, registrationNumberLabel, rollNumberLabel, school10NameLabel, school10PercentageLabel, school10YearLabel,
            school12NameLabel, school12PercentageLabel, school12YearLabel, semesterLabel, statusLabel, streamLabel, studentIdLabel, temporaryAddressLabel, updatedOnLabel, errorMessageLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorMessageLabel.setText("");
        populateUsersDetails(sessionManager.getUserID());
        populateStudentsDetails(sessionManager.getUserID());
        if (profileImage != null)
            profileImage.setOnMouseClicked(v -> updateProfilePhoto());
        profileImage.setStyle("-fx-cursor: hand");
        roundedImage.makeImageViewRounded(profileImage);
        loadProfileImage(sessionManager.getUserID());
    }

    private void populateUsersDetails(int userID) {
        String query = "SELECT * FROM Users WHERE User_Id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                firstNameLabel.setText(rs.getString("First_Name"));
                lastNameLabel.setText(rs.getString("Last_Name"));
                aadharLabel.setText(String.valueOf(rs.getLong("Aadhar")));
                panLabel.setText(rs.getString("Pan"));
                phoneLabel.setText(String.valueOf(rs.getLong("Mobile")));
                altMobileLabel.setText(String.valueOf(rs.getLong("Alternate_Mobile")));
                emailLabel.setText(rs.getString("Email"));
                genderLabel.setText(rs.getString("Gender"));
                dobLabel.setText(String.valueOf(rs.getDate("DOB")));
                bloodGroupLabel.setText(rs.getString("Blood_Group"));
                maritalStatusLabel.setText(rs.getString("Marital_Status"));
                nationalityLabel.setText(rs.getString("Nationality"));
                emergencyContactNameLabel.setText(rs.getString("Emergency_Contact_Name"));
                emergencyContactRelationshipLabel.setText(rs.getString("Emergency_Contact_Relationship"));
                emergencyContactMobileLabel.setText(String.valueOf(rs.getLong("Emergency_Contact_Mobile")));
                temporaryAddressLabel.setText(rs.getString("Temporary_Address"));
                permanentAddressLabel.setText(rs.getString("Permanent_Address"));
                fathersNameLabel.setText(rs.getString("Fathers_Name"));
                mothersNameLabel.setText(rs.getString("Mothers_Name"));
                referencedViaLabel.setText(rs.getString("Referenced_Via"));
                approvalStatusLabel.setText(rs.getString("Admin_Approval_Status"));
                joinedOnLabel.setText(String.valueOf(rs.getTimestamp("Joined_On")));
                updatedOnLabel.setText(String.valueOf(rs.getTimestamp("Updated_On")));
                statusLabel.setText(rs.getString("User_Status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessageLabel.setText("Error loading user details.");
        }
    }

    private void populateStudentsDetails(int userID) {
        String query = """
        SELECT s.*, d.Department_Name, d.Department_Code, a.UserName
        FROM Students s
        LEFT JOIN Departments d ON s.Department_Id = d.Department_Id
        LEFT JOIN Authentication a ON s.User_Id = a.User_Id
        WHERE s.User_Id = ?
    """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                registrationNumberLabel.setText(rs.getString("Registration_Number"));
                rollNumberLabel.setText(rs.getString("Roll_Number"));
                courseLabel.setText(rs.getString("Course"));
                streamLabel.setText(rs.getString("Stream"));
                batchLabel.setText(rs.getString("Batch"));
                academicYearLabel.setText(String.valueOf(rs.getInt("Current_Academic_Year")));
                semesterLabel.setText(String.valueOf(rs.getInt("Current_Semester")));
                enrollmentDateLabel.setText(String.valueOf(rs.getTimestamp("Enrolled_On")));
                school10NameLabel.setText(rs.getString("School_10_Name"));
                school10YearLabel.setText(String.valueOf(rs.getInt("School_10_Passing_Year")));
                school10PercentageLabel.setText(String.valueOf(rs.getFloat("School_10_Percentage")));
                school12NameLabel.setText(rs.getString("School_12_Name"));
                school12YearLabel.setText(String.valueOf(rs.getInt("School_12_Passing_Year")));
                school12PercentageLabel.setText(String.valueOf(rs.getFloat("School_12_Percentage")));
                departmentLabel.setText(rs.getString("Department_Name") + " (" + rs.getString("Department_Code") + ")");

                // Labels with sessionManager data
                fullNameLabel.setText("Name : " + sessionManager.getFullName());
                studentIdLabel.setText("User ID : " + userID);
                studentUserNameLabel.setText("User Name : " + rs.getString("UserName"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessageLabel.setText("Error loading student details.");
        }
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
                                return new Image(is); // Return the loaded image
                            }
                        }
                    }
                }
                return null; // Return null if no image is found
            }
        };

        loadImageTask.setOnSucceeded(event -> {
            Image profileImagess = loadImageTask.getValue(); // Get the result from the task
            if (profileImagess != null) {
                profileImage.setImage(profileImagess);
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
}
