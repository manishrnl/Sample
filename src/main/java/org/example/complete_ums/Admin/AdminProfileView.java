package org.example.complete_ums.Admin;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;

public class AdminProfileView {


    public AdminProfileView() throws SQLException {
    }


    SessionManager sessionManager = SessionManager.getInstance();
    Connection connection = DatabaseConnection.getConnection();
    AlertManager alertManager = new AlertManager();
    @FXML
    private TextField aadharField, alternateMobileField, bloodGroupField,
            departmentIdField, designationField, emailField, emergencyContactMobileField,
            emergencyContactNameField, emergencyContactRelField, accessLevelField,
            fathersNameField, firstNameField, lastNameField, maritalStatusField,
            mobileField, mothersNameField, nationalityField, panField,
            referencedViaField, salaryField;

    @FXML
    private ComboBox<String> userStatusComboBox, adminApprovalStatusComboBox, roleComboBox,
            genderComboBox;

    @FXML
    private TextArea permanentAddressArea, temporaryAddressArea;

    @FXML
    private Button updateUsersButton, uploadPhotoButton;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private ImageView usersImage;
    @FXML
    private Label joinedOnLabel, userIdLabel;

    private int userIDLoggedIn = sessionManager.getUserID();
    File selectedImageFile = null;
    Button3DEffect button3DEffect;

    public void initialize() {
        button3DEffect.applyEffect(updateUsersButton, "/sounds/hover.mp3");
        button3DEffect.applyEffect(uploadPhotoButton, "/sounds/hover.mp3");
        userIdLabel.setText(String.valueOf(userIDLoggedIn));
        usersImage.setOnMouseClicked(event -> updateProfilePhoto(new ActionEvent()));
        usersImage.setStyle("-fx-cursor: hand");
        loadProfileImage(userIDLoggedIn);
        populateAccountantsDetails(userIDLoggedIn);
        roleComboBox.setDisable(true);
        userStatusComboBox.setDisable(true);
        adminApprovalStatusComboBox.setDisable(true); // Admin approval status is also typically set by an admin

    }

    public void populateAccountantsDetails(int userIDLoggedIn) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "    SELECT " +
                    "U.User_Id, U.Role, U.User_Status, U.Joined_On, U.First_Name, U.Last_Name, " +
                    "U.Aadhar, U.Pan, U.Mobile, U.Alternate_Mobile, U.Email, U.Gender, U.DOB, " +
                    "U.Blood_Group, U.Marital_Status, U.Nationality, U.Emergency_Contact_Name, " +
                    "U.Emergency_Contact_Relationship, U.Emergency_Contact_Mobile, " +
                    "U.Temporary_Address, U.Permanent_Address, U.Fathers_Name, U.Mothers_Name, " +
                    " U.Referenced_Via, U.Admin_Approval_Status, " +
                    "  A.Designation, A.Department_Id, A.Salary,A.Access_Level " +
                    "FROM Users U " +
                    "JOIN Admins A ON U.User_Id = A.User_Id " +
                    "WHERE U.User_Id = ?";

            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userIDLoggedIn);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Populate Users table fields
                roleComboBox.setValue(rs.getString("Role"));
                userStatusComboBox.setValue(rs.getString("User_Status"));
                joinedOnLabel.setText(rs.getTimestamp("Joined_On") != null ?
                        rs.getTimestamp("Joined_On").toLocalDateTime().toLocalDate().toString() : "");
                firstNameField.setText(rs.getString("First_Name"));
                lastNameField.setText(rs.getString("Last_Name"));
                aadharField.setText(rs.getObject("Aadhar") != null ? String.valueOf(rs.getLong("Aadhar")) : "");
                panField.setText(rs.getString("Pan"));
                mobileField.setText(rs.getObject("Mobile") != null ? String.valueOf(rs.getLong("Mobile")) : "");
                alternateMobileField.setText(rs.getObject("Alternate_Mobile") != null ? String.valueOf(rs.getLong("Alternate_Mobile")) : "");
                emailField.setText(rs.getString("Email"));
                genderComboBox.setValue(rs.getString("Gender"));
                Date dobSql = rs.getDate("DOB");
                dobPicker.setValue(dobSql != null ? dobSql.toLocalDate() : null);
                bloodGroupField.setText(rs.getString("Blood_Group"));
                maritalStatusField.setText(rs.getString("Marital_Status"));
                nationalityField.setText(rs.getString("Nationality"));
                emergencyContactNameField.setText(rs.getString("Emergency_Contact_Name"));
                emergencyContactRelField.setText(rs.getString("Emergency_Contact_Relationship"));
                emergencyContactMobileField.setText(rs.getObject("Emergency_Contact_Mobile") != null ? String.valueOf(rs.getLong("Emergency_Contact_Mobile")) : "");
                temporaryAddressArea.setText(rs.getString("Temporary_Address"));
                permanentAddressArea.setText(rs.getString("Permanent_Address"));
                fathersNameField.setText(rs.getString("Fathers_Name"));
                mothersNameField.setText(rs.getString("Mothers_Name"));

                referencedViaField.setText(rs.getString("Referenced_Via"));
                adminApprovalStatusComboBox.setValue(rs.getString("Admin_Approval_Status"));

                // Populate Accountants table fields
                accessLevelField.setText(rs.getString("Access_Level"));

                designationField.setText(rs.getString("Designation"));
                departmentIdField.setText(rs.getObject("Department_Id") != null ? String.valueOf(rs.getInt("Department_Id")) : "");
                salaryField.setText(rs.getObject("Salary") != null ? String.valueOf(rs.getBigDecimal("Salary")) : "");

            } else {
                System.out.println("No accountant profile found for User ID: " + userIDLoggedIn);
                clearAllFields(); // Clear fields if no data found
            }

        } catch (SQLException e) {
            System.out.println("Failed to load profile: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearAllFields() {
        // Clear User fields
        roleComboBox.getSelectionModel().clearSelection();
        userStatusComboBox.getSelectionModel().clearSelection();
        joinedOnLabel.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        aadharField.setText("");
        panField.setText("");
        mobileField.setText("");
        alternateMobileField.setText("");
        emailField.setText("");
        genderComboBox.getSelectionModel().clearSelection();
        dobPicker.setValue(null);
        bloodGroupField.setText("");
        maritalStatusField.setText("");
        nationalityField.setText("");
        emergencyContactNameField.setText("");
        emergencyContactRelField.setText("");
        emergencyContactMobileField.setText("");
        temporaryAddressArea.setText("");
        permanentAddressArea.setText("");
        fathersNameField.setText("");
        mothersNameField.setText("");
        referencedViaField.setText("");
        adminApprovalStatusComboBox.getSelectionModel().clearSelection();

        accessLevelField.clear();
        designationField.setText("");
        departmentIdField.setText("");
        salaryField.setText("");
    }

    @FXML
    void handleUsersUpdate(ActionEvent event) {

        try {
            connection.setAutoCommit(false);
            String updateUserSQL = "UPDATE Users SET " +
                    "Role = ?, User_Status = ?, First_Name = ?, Last_Name = ?, Aadhar = ?, Pan = ?, " +
                    "Mobile = ?, Alternate_Mobile = ?, Email = ?, Gender = ?, DOB = ?, Blood_Group = ?, " +
                    "Marital_Status = ?, Nationality = ?, Emergency_Contact_Name = ?, " +
                    "Emergency_Contact_Relationship = ?, Emergency_Contact_Mobile = ?, " +
                    "Temporary_Address = ?, Permanent_Address = ?, Fathers_Name = ?, Mothers_Name = ?, " +
                    "Referenced_Via = ?, Admin_Approval_Status = ? " +
                    "WHERE User_Id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(updateUserSQL)) {
                pstmt.setString(1, roleComboBox.getValue());
                pstmt.setString(2, userStatusComboBox.getValue());
                pstmt.setString(3, firstNameField.getText());
                pstmt.setString(4, lastNameField.getText());
                pstmt.setObject(5, parseLongOrNull(aadharField.getText()), java.sql.Types.BIGINT); // Aadhar
                pstmt.setString(6, panField.getText());
                pstmt.setObject(7, parseLongOrNull(mobileField.getText()), java.sql.Types.BIGINT); // Mobile
                pstmt.setObject(8, parseLongOrNull(alternateMobileField.getText()), java.sql.Types.BIGINT); // Alternate_Mobile
                pstmt.setString(9, emailField.getText());
                pstmt.setString(10, genderComboBox.getValue());
                pstmt.setDate(11, (dobPicker.getValue() != null) ? Date.valueOf(dobPicker.getValue()) : null); // DOB
                pstmt.setString(12, bloodGroupField.getText());
                pstmt.setString(13, maritalStatusField.getText());
                pstmt.setString(14, nationalityField.getText());
                pstmt.setString(15, emergencyContactNameField.getText());
                pstmt.setString(16, emergencyContactRelField.getText());
                pstmt.setObject(17, parseLongOrNull(emergencyContactMobileField.getText()), java.sql.Types.BIGINT); // Emergency_Contact_Mobile
                pstmt.setString(18, temporaryAddressArea.getText());
                pstmt.setString(19, permanentAddressArea.getText());
                pstmt.setString(20, fathersNameField.getText());
                pstmt.setString(21, mothersNameField.getText());
                pstmt.setString(22, referencedViaField.getText());
                pstmt.setString(23, adminApprovalStatusComboBox.getValue());
                pstmt.setInt(24, userIDLoggedIn);

                int rowsAffectedUsers = pstmt.executeUpdate();
                System.out.println("Users table rows affected: " + rowsAffectedUsers);
            }

            // 2. Update Accountants Table
            String updateAccountantSQL = "UPDATE Admins SET " +
                    "Designation = ?, Department_Id = ?, Salary = ? ,Access_Level=?" +
                    "WHERE User_Id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(updateAccountantSQL)) {
                pstmt.setString(1, designationField.getText());
                pstmt.setObject(2, parseIntegerOrNull(departmentIdField.getText()),
                        java.sql.Types.INTEGER); // Department_Id
                pstmt.setObject(3, parseDecimalOrNull(salaryField.getText()),
                        java.sql.Types.DECIMAL); // Salary
                pstmt.setString(4, accessLevelField.getText());
                pstmt.setInt(5, userIDLoggedIn);

                int rowsAffectedAccountants = pstmt.executeUpdate();
                System.out.println("Accountants table rows affected: " + rowsAffectedAccountants);
            }

            connection.commit(); // Commit transaction
            alertManager.showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated " +
                    "successfully!", "We have Successfully updated your profile into our " +
                    "database");

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to update profile: ",
                    "We were unable to update profile Details.Try after sometimes or try " +
                            "again after some times" + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter valid" +
                    " Details", "Please enter correct data in number fields for Aadhar, " +
                    "Mobile, " +
                    "Experience, Department" +
                    " ID, and " +
                    "Salary.");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Reset auto-commit

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Long parseLongOrNull(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        return Long.parseLong(text.trim());
    }

    private Integer parseIntegerOrNull(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        return Integer.parseInt(text.trim());
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
                usersImage.setImage(profileImagess);
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
    private void updateProfilePhoto(ActionEvent event) {

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
                pstmt.setInt(2, userIDLoggedIn);

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

    private Double parseDecimalOrNull(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        return Double.parseDouble(text.trim());
    }


}
