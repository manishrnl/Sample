package org.example.complete_ums.Librarians;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
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
import java.time.LocalDate;
import java.util.ResourceBundle;

public class LibrariansProfileView implements Initializable {
    public LibrariansProfileView() throws SQLException {
    }

    SessionManager sessionManager = SessionManager.getInstance();
    LoadFrame loadFrame;
    Button3DEffect button3DEffect;
    Connection connection = DatabaseConnection.getConnection();
    AlertManager alertManager;

    @FXML
    private Label aadharLabel, departmentIdLabel, errorMessageLabel, joinedOnLabel, panLabel, roleLabel, salaryLabel, userIdLabel, userStatusLabel;

    @FXML
    private TextField alternateMobileTextField, bloodGroupTextField, certificationTextField, designationTextField, emailTextField, experienceYearsTextField, fathersNameTextField, firstNameTextField, lastNameTextField, maritalStatusTextField, mobileTextField, mothersNameTextField, nationalityTextField, qualificationTextField;

    @FXML
    private Button cancelButton, changePhotoButton, saveButton;

    @FXML
    private DatePicker dobDatePicker;

    @FXML
    private ComboBox<String> genderComboBox;

    @FXML
    private TextArea permanentAddressTextArea, temporaryAddressTextArea;

    @FXML
    private ImageView usersImage;

    @FXML
    private AnchorPane rootAnchorPane;

    int userIDLoggedIn = sessionManager.getUserID();
    String roleLoggedIn = sessionManager.getRole();
    File selectedImageFile = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userIdLabel.setText(String.valueOf(userIDLoggedIn));
        roleLabel.setText(roleLoggedIn);
        loadLibrariansDataFromDB(userIDLoggedIn);
        loadUsersImage(userIDLoggedIn);
        button3DEffect.applyEffect(saveButton, "/sound/hover.mp3");
        button3DEffect.applyEffect(cancelButton, "/sound/hover.mp3");
        button3DEffect.applyEffect(changePhotoButton, "/sound/hover.mp3");
    }

    private void loadUsersImage(int userID) {
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
                usersImage.setImage(profileImage);
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

    @FXML
    void handlePhotoChange(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload your Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            usersImage.setImage(new Image(file.toURI().toString()));

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


    private void loadLibrariansDataFromDB(int userIDLoggedIn) {
        String query = "SELECT * FROM Users u JOIN Librarians l ON u.User_Id = l.User_Id WHERE u.User_Id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userIDLoggedIn);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                firstNameTextField.setText(rs.getString("First_Name"));
                lastNameTextField.setText(rs.getString("Last_Name"));
                aadharLabel.setText(rs.getString("Aadhar"));
                panLabel.setText(rs.getString("Pan"));
                mobileTextField.setText(rs.getString("Mobile"));
                alternateMobileTextField.setText(rs.getString("Alternate_Mobile"));
                userStatusLabel.setText(rs.getString("User_Status"));
                joinedOnLabel.setText(rs.getString("Joined_On"));
                emailTextField.setText(rs.getString("Email"));
                genderComboBox.getSelectionModel().select(rs.getString("Gender"));
                dobDatePicker.setValue(LocalDate.parse(rs.getString("DOB")));
                bloodGroupTextField.setText(rs.getString("Blood_Group"));
                maritalStatusTextField.setText(rs.getString("Marital_Status"));
                nationalityTextField.setText(rs.getString("Nationality"));
                fathersNameTextField.setText(rs.getString("Fathers_Name"));
                mothersNameTextField.setText(rs.getString("Mothers_Name"));
                temporaryAddressTextArea.setText(rs.getString("Temporary_Address"));
                permanentAddressTextArea.setText(rs.getString("Permanent_Address"));
                qualificationTextField.setText(rs.getString("Qualification"));
                certificationTextField.setText(rs.getString("Certification"));
                experienceYearsTextField.setText(rs.getString("Experience_Years"));
                designationTextField.setText(rs.getString("Designation"));
                salaryLabel.setText(rs.getString("Salary"));
                departmentIdLabel.setText(rs.getString("Department_Id"));

            }
        } catch (SQLException e) {
            loadFrame.setMessage(errorMessageLabel, "Database error while loading Librarians" +
                    " Data" + e.getMessage(), "RED");
        }
    }

    @FXML
    void handleCancelOperation(ActionEvent event) {
        loadLibrariansDataFromDB(Integer.parseInt(userIdLabel.getText()));
    }

    @FXML
    void handleSaveProfile(ActionEvent event) {
        if (validateInputs()) {
            String updateQuery = "UPDATE Users u JOIN Librarians l ON u.User_Id = l.User_Id " +
                    "SET u.First_Name = ?, u.Last_Name = ?, u.Mobile = ?, " +
                    "u.Alternate_Mobile = ?, u.Email = ?, u.Gender = ?, u.DOB = ?, " +
                    "u.Blood_Group = ?, u.Marital_Status = ?, u.Nationality = ?, " +
                    "u.Fathers_Name = ?, u.Mothers_Name = ?, u.Temporary_Address = ?, " +
                    "u.Permanent_Address = ?, l.Qualification = ?, l.Certification = ?, " +
                    "l.Experience_Years = ?, l.Designation = ? " +
                    "WHERE u.User_Id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                pstmt.setString(1, firstNameTextField.getText());
                pstmt.setString(2, lastNameTextField.getText());
                pstmt.setString(3, mobileTextField.getText());
                pstmt.setString(4, alternateMobileTextField.getText());
                pstmt.setString(5, emailTextField.getText());
                pstmt.setString(6, genderComboBox.getSelectionModel().getSelectedItem());
                pstmt.setString(7, dobDatePicker.getValue().toString());
                pstmt.setString(8, bloodGroupTextField.getText());
                pstmt.setString(9, maritalStatusTextField.getText());
                pstmt.setString(10, nationalityTextField.getText());
                pstmt.setString(11, fathersNameTextField.getText());
                pstmt.setString(12, mothersNameTextField.getText());
                pstmt.setString(13, temporaryAddressTextArea.getText());
                pstmt.setString(14, permanentAddressTextArea.getText());
                pstmt.setString(15, qualificationTextField.getText());
                pstmt.setString(16, certificationTextField.getText());
                pstmt.setString(17, experienceYearsTextField.getText());
                pstmt.setString(18, designationTextField.getText());
                pstmt.setInt(19, Integer.parseInt(userIdLabel.getText()));

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    loadFrame.setMessage(errorMessageLabel, "Profile updated successfully!", "GREEN");
                } else {
                    loadFrame.setMessage(errorMessageLabel, "Profile not found or no changes made.", "RED");
                }

            } catch (SQLException e) {
                loadFrame.setMessage(errorMessageLabel, "Database error: " + e.getMessage(), "RED");
            }
        }
    }

    private boolean validateInputs() {
        errorMessageLabel.setText("");
        if (firstNameTextField.getText().trim().isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "First Name is required.", "RED");
            return false;
        }
        if (lastNameTextField.getText().trim().isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Last Name is required.", "RED");
            return false;
        }
        if (mobileTextField.getText().trim().isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Mobile Number is required.", "RED");
            return false;
        }
        if (emailTextField.getText().trim().isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Email is required.", "RED");
            return false;
        }
        if (fathersNameTextField.getText().trim().isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Father's Name is required.", "RED");
            return false;
        }
        if (mothersNameTextField.getText().trim().isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Mother's Name is required.", "RED");
            return false;
        }
        if (nationalityTextField.getText().trim().isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Nationality is required.", "RED");
            return false;
        }
        if (bloodGroupTextField.getText().trim().isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Blood Group is required.", "RED");
            return false;
        }
        if (qualificationTextField.getText().trim().isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Qualification is required.", "RED");
            return false;
        }
        if (designationTextField.getText().trim().isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Designation is required.", "RED");
            return false;
        }

        if (genderComboBox.getSelectionModel().isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Please select a Gender.", "RED");
            return false;
        }
        if (dobDatePicker.getValue() == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select a Date of Birth.", "RED");
            return false;
        }

        if (temporaryAddressTextArea.getText().trim().isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Temporary Address is required.", "RED");
            return false;
        }
        if (permanentAddressTextArea.getText().trim().isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Permanent Address is required.", "RED");
            return false;
        }
        if (!mobileTextField.getText().trim().matches("\\d{10}")) {
            loadFrame.setMessage(errorMessageLabel, "Mobile Number must be 10 digits.", "RED");
            return false;
        }
        return true;
    }
}
