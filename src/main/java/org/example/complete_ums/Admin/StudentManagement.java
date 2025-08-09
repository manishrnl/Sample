package org.example.complete_ums.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.complete_ums.CommonTable.Manage_Students_Table;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.RoundedImage;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.LoadFrame;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class StudentManagement implements Initializable {

    AlertManager alertManager = new AlertManager();
    LoadFrame loadFrame = new LoadFrame();
    RoundedImage roundedImage = new RoundedImage();
    Connection connection = DatabaseConnection.getConnection();
    private File selectedImageFile = null;
    @FXML
    private TextField searchAnything, firstNameField, lastNameField, fatherNameField, motherNameField, emailField, studentMobileField, parentsMobileField,
            aadharField, panField, userNameField, maritalStatusField, emergencyContactNameField, emergencyContactRelationField, emergencyContactMobileField;

    @FXML
    private ComboBox<String> userStatusCombo, AdminApprovalStatus, genderComboBox,
            roleComboBox, bloodGroupCombo, nationalityComboBox;

    @FXML
    private TableView<Manage_Students_Table> studentsTable;
    @FXML
    private TableColumn<Manage_Students_Table, Integer> colUserId;
    @FXML
    private TableColumn<Manage_Students_Table, Long> colAadhar, colMobile;
    @FXML
    private TableColumn<Manage_Students_Table, String> colUsername, colFirstName,
            colLastName, colEmail, colPan;

    @FXML
    private Button uploadPhotoButton, updateButton, deleteButton, clearButton, cancelButton;

    @FXML
    private GridPane userFormGrid;

    @FXML
    private TextArea temporaryAddressArea, permanentAddressArea;

    @FXML
    private DatePicker dobPicker;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private ImageView profileImageView;


    String AADHAR_REGEX = "^\\d{12}$";
    Manage_Students_Table selectedStudent;

    public StudentManagement() throws SQLException {
    }

    ObservableList<Manage_Students_Table> masterData = FXCollections.observableArrayList();
    ObservableList<Manage_Students_Table> filtered = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorMessageLabel.setText("");
        roleComboBox.setDisable(true);

        colUserId.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().userIdProperty().asObject());
        colUsername.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().usernameProperty());
        colFirstName.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().firstNameProperty());
        colLastName.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().lastNameProperty());
        colEmail.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().emailProperty());
        colAadhar.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().aadharProperty().asObject());
        colPan.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().panProperty());
        colMobile.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().studentsMobileProperty().asObject());


        loadFieldsIntoTable();
        studentsTable.setOnMouseClicked(event -> {
            Manage_Students_Table selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
            if (selectedStudent != null) {
                int userID = selectedStudent.getUserId();
                firstNameField.setText(selectedStudent.getFirstName());
                lastNameField.setText(selectedStudent.getLastName());
                emailField.setText(selectedStudent.getEmail());
                studentMobileField.setText(String.valueOf(selectedStudent.getStudentsMobile()));
                aadharField.setText(String.valueOf(selectedStudent.getAadhar()));
                panField.setText(selectedStudent.getPan());
                setOtherFields(userID);
                profileImageView.setImage(null);  // Clears the previous Image if it does
                loadUsersImage(userID);
            }
        });
        searchAnything.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                searchByAnything(newValue);
            } else
                studentsTable.setItems(masterData);
        });
    }

    private void searchByAnything(String newValue) {
        filtered.clear();
        String value = newValue.toLowerCase();
        for (Manage_Students_Table recors : masterData) {
            if (recors.getUsername().toLowerCase().contains(value) || recors.getFullName().toLowerCase().contains(value) || recors.getEmail().toLowerCase().contains(value) ||
                    recors.getPan().toLowerCase().contains(value) || String.valueOf(recors.getUserId()).contains(value) || String.valueOf(recors.getStudentsMobile()).contains(value)
                    || String.valueOf(recors.getAadhar()).contains(value)) {
                filtered.add(recors);
            }
        }
        studentsTable.setItems(filtered);

    }


    private void loadFieldsIntoTable() {
        errorMessageLabel.setText("");
        studentsTable.getItems().clear();
        filtered.clear(); // Clear the observable list as well

        String query = "SELECT u.User_Id, a.UserName, u.First_Name, u.Last_Name, u.Email, u.Mobile, u.Aadhar, u.Pan FROM Users u " +
                "JOIN Authentication a ON u.User_Id = a.User_Id WHERE u.Role='Student'";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String firstName = resultSet.getString("First_Name");
                String lastName = resultSet.getString("Last_Name");
                long mobile = resultSet.getLong("Mobile");
                long aadhar = resultSet.getLong("Aadhar");
                String email = resultSet.getString("Email");
                String pan = resultSet.getString("Pan");
                String userName = resultSet.getString("UserName");

                Manage_Students_Table student = new Manage_Students_Table(resultSet.getInt(
                        "User_Id"), userName, firstName, lastName, email, mobile, aadhar, pan);
                filtered.add(student);
            }
            masterData.addAll(filtered);
            studentsTable.setItems(filtered); // Set once after loop
        } catch (SQLException e) {
            loadFrame.setMessage(errorMessageLabel, "Database Error " + e.getMessage(), "RED");
        }
    }

    private void loadUsersImage(int userId) {
        errorMessageLabel.setText("");
        String query = "SELECT Photo_URL FROM Users WHERE User_Id = ?";
        try (PreparedStatement pstmt5 = connection.prepareStatement(query)) {
            pstmt5.setInt(1, userId);

            ResultSet rs = pstmt5.executeQuery();
            while (rs.next()) {
                InputStream inputStream = rs.getBinaryStream("Photo_URL");
                if (inputStream != null) {
                    Image image = new Image(inputStream);
                    profileImageView.setImage(image);
                }
            }
        } catch (Exception ex) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Error loading profile image.",
                    "Something went wrong while loading the profile image: " + ex.getMessage());
        }
    }

    private void setOtherFields(int userId) {
        String query = "SELECT u.User_Id,u.Admin_Approval_Status, u.Role,a.UserName,u.DOB, u.Emergency_Contact_Name,u.Emergency_Contact_Relationship,u.Emergency_Contact_Mobile,u.Gender,u.Marital_Status,u.Nationality,u.Fathers_Name, u.Temporary_Address , u.Permanent_Address , u.Blood_Group,u.Mothers_Name,u.Alternate_Mobile, u.User_Status FROM Users u JOIN Authentication a ON u.User_Id = a.User_Id WHERE u.User_Id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                permanentAddressArea.setText(resultSet.getString("Permanent_Address"));
                temporaryAddressArea.setText(resultSet.getString("Temporary_Address"));
                emergencyContactMobileField.setText(resultSet.getString("Emergency_Contact_Mobile"));
                emergencyContactRelationField.setText(resultSet.getString("Emergency_Contact_Relationship"));
                emergencyContactNameField.setText(resultSet.getString("emergency_contact_name"));
                nationalityComboBox.getSelectionModel().select(resultSet.getString("Nationality"));
                genderComboBox.setValue(resultSet.getString("Gender"));
                maritalStatusField.setText(resultSet.getString("Marital_Status"));
                bloodGroupCombo.setValue(resultSet.getString("Blood_Group"));
                dobPicker.setValue(resultSet.getDate("DOB").toLocalDate());
                roleComboBox.getSelectionModel().select(resultSet.getString("Role"));
                userNameField.setText(resultSet.getString("UserName"));
                fatherNameField.setText(resultSet.getString("Fathers_Name"));
                motherNameField.setText(resultSet.getString("Mothers_Name"));
                parentsMobileField.setText(resultSet.getString("Alternate_Mobile"));
                userStatusCombo.getSelectionModel().select(resultSet.getString("User_Status"));
                AdminApprovalStatus.getSelectionModel().select(resultSet.getString("Admin_Approval_Status"));
            } else {
                loadFrame.setMessage(errorMessageLabel, "No user found with ID: " + userId + " " +
                        "Please check the user ID and try again.", "RED");
            }
        } catch (SQLException e) {
            loadFrame.setMessage(errorMessageLabel, "Something went wrong while fetching user" +
                    " data: " + e.getMessage(), "RED");
        }
    }

    public void uploadUsersImage(ActionEvent actionEvent) {
        errorMessageLabel.setText("");
        selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            loadFrame.setMessage(errorMessageLabel, "You must select a student from the table" +
                    " to upload a photo.", "RED");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            selectedImageFile = file;
            profileImageView.setImage(new Image(file.toURI().toString()));
            String photoQuery = "UPDATE Users SET Photo_URL = ? WHERE User_Id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(photoQuery)) {
                pstmt.setBinaryStream(1, new java.io.FileInputStream(file));

                if (selectedStudent != null) {
                    pstmt.setInt(2, selectedStudent.getUserId());
                    int rowsUpdated = pstmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        alertManager.showAlert(Alert.AlertType.INFORMATION, "Photo Upload Successful",
                                "Profile photo uploaded successfully.", "The profile photo has been updated in the database.");
                    } else {
                        alertManager.showAlert(Alert.AlertType.ERROR, "Photo Upload Failed",
                                "Failed to update profile photo.", "Please try again.");
                    }
                } else {
                    alertManager.showAlert(Alert.AlertType.WARNING, "No Student Selected",
                            "Please select a student to upload a photo.", "You must select a student from the table to upload a photo.");
                }
            } catch (Exception e) {
                loadFrame.setMessage(errorMessageLabel, "Something went wrong while uploading " +
                        "the profile photo: " + e.getMessage(), "RED");
            }
        }
    }

    public void handleCancelOperation(ActionEvent actionEvent) {
        errorMessageLabel.setText("");
        loadFrame.loadNewFrame((Stage) cancelButton.getScene().getWindow(),
                AdminController.class, "Admin/MainDashboard.fxml", "Admin Dashboard");
    }

    public void handleDeleteOperation(ActionEvent actionEvent) {
        errorMessageLabel.setText("");
        selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select Students from Table to " +
                    "initiate Delete Operations...", "RED");
        }
        try (PreparedStatement pstmt = connection.prepareStatement(
                "DELETE u, a FROM Users u " +
                        "JOIN Authentication a ON u.User_Id = a.User_Id " +
                        "WHERE u.User_Id = ?")) {

            Manage_Students_Table selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
            if (selectedStudent == null) {
                loadFrame.setMessage(errorMessageLabel, "You must select a student from the " +
                        "table to delete.", "RED");
                return;
            }

            Optional<ButtonType> response = alertManager.showResponseAlert(Alert.AlertType.CONFIRMATION,
                    "Delete Confirmation", "Are you sure you want to delete student with user ID: " + selectedStudent.getUserId(),
                    "This action cannot be undone.");

            if (response.isPresent() && response.get() != ButtonType.OK) {
                return;
            }

            pstmt.setInt(1, selectedStudent.getUserId());
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                loadFrame.setMessage(errorMessageLabel, "The student has been deleted from the" +
                        " database.", "GREEN");
                studentsTable.getItems().remove(selectedStudent);
                handleClearOperation(actionEvent);
            } else {
                loadFrame.setMessage(errorMessageLabel, "Please check the student ID and try " +
                        "again.", "RED");
            }

        } catch (Exception e) {
            loadFrame.setMessage(errorMessageLabel, "Something went wrong while deleting the " +
                    "student: " + e.getMessage(), "RED");
        }

    }

    public void handleClearOperation(ActionEvent actionEvent) {
        errorMessageLabel.setText("");
        temporaryAddressArea.clear();
        permanentAddressArea.clear();
        emergencyContactNameField.clear();
        emergencyContactRelationField.clear();
        emergencyContactMobileField.clear();
        dobPicker.setValue(null);
        bloodGroupCombo.setValue(null);
        AdminApprovalStatus.setValue(null);
        nationalityComboBox.setValue(null);
        roleComboBox.setValue(null);
        maritalStatusField.clear();
        genderComboBox.setValue(null);
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        studentMobileField.clear();
        parentsMobileField.clear();
        aadharField.clear();
        panField.clear();
        userNameField.clear();
        fatherNameField.clear();
        motherNameField.clear();
        userStatusCombo.getSelectionModel().clearSelection();
        profileImageView.setImage(null); // Clear the profile image
        studentsTable.getSelectionModel().clearSelection(); // Clear the selection in the table

    }

    private boolean checkIfDetailsExists(String tableName, String columnName, String columnValue, int userID) {
        errorMessageLabel.setText("");
        String query = "SELECT 1 FROM " + tableName + " WHERE " + columnName + " = ? AND User_Id != ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, columnValue);
            pstmt.setInt(2, userID);
            return pstmt.executeQuery().next(); // true if a row exists
        } catch (Exception e) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Error checking for duplicate entries: " + e.getMessage(),
                    "While checking " + columnName + " in " + tableName);
            return false;
        }
    }

    private boolean checkIfDetailsExists(String tableName, String columnName, long columnValue, int userID) {
        errorMessageLabel.setText("");
        String query = "SELECT 1 FROM " + tableName + " WHERE " + columnName + " = ? AND User_Id != ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, columnValue);
            pstmt.setInt(2, userID);
            return pstmt.executeQuery().next(); // true if a row exists
        } catch (Exception e) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Error checking for duplicate entries: " + e.getMessage(),
                    "While checking " + columnName + " in " + tableName);
            return false;
        }
    }

    public void handleUpdateOperation(ActionEvent actionEvent) throws SQLException {
        errorMessageLabel.setText("");
        String query = "UPDATE Users u " +
                "JOIN Authentication a ON u.User_Id = a.User_Id " +
                "SET u.Admin_Approval_Status = ?, u.Role = ?, a.UserName = ?, u.DOB = ?, " +
                "u.Emergency_Contact_Name = ?, u.Emergency_Contact_Relationship = ?, u.Emergency_Contact_Mobile = ?, " +
                "u.Gender = ?, u.Marital_Status = ?, u.Nationality = ?, u.Fathers_Name = ?, " +
                "u.Temporary_Address = ?, u.Permanent_Address = ?, u.Blood_Group = ?, " +
                "u.Mothers_Name = ?, u.Alternate_Mobile = ?, u.User_Status = ? " +
                "WHERE u.User_Id = ?";
        selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select Students from Table to " +
                    "initiate Update Operations...", "RED");
        }
        int userID = selectedStudent.getUserId();
        if (checkIfDetailsExists("Authentication", "UserName", userNameField.getText(), userID)) {
            loadFrame.setMessage(errorMessageLabel, "A student with the same User Name : " + userNameField.getText() + " already " +
                    "exists.Please try with a different user Name again.", "RED");
            return;
        }
        if (checkIfDetailsExists("Users", "Pan", panField.getText(), userID)) {
            loadFrame.setMessage(errorMessageLabel, "A student with the same PAN Number : " + panField.getText() + " already" +
                    " exists.Please try with a different PAN Number again.", "RED");
            return;
        }
        if (!aadharField.getText().matches(AADHAR_REGEX)) {
            loadFrame.setMessage(errorMessageLabel, "Invalid Aadhar Number . Aadhar Number " +
                    "must be 12 digits long.Please enter a valid Aadhar Number that " +
                    "must be 12 digits long. No character except digits are allowed.\n" +
                    "Your current Aadhar length is :" + aadharField.getText().length(), "RED");
            return;
        }
        if (checkIfDetailsExists("Users", "Aadhar", Long.parseLong(aadharField.getText()), userID)) {
            loadFrame.setMessage(errorMessageLabel, "A student with the same Aadhar Number : " + Long.parseLong(aadharField.getText()) + " already " +
                    "exists.Please try with a different Aadhar again.", "RED");
            return;
        }
        if (checkIfDetailsExists("Users", "Email", emailField.getText(), userID)) {
            loadFrame.setMessage(errorMessageLabel,
                    "A student with the same Email ID : " + emailField.getText() + "  " +
                            "already " +
                            "exists.Please try with a different Email again.", "RED");
            return;
        }
        if (checkIfDetailsExists("Users", "Mobile", Long.parseLong(studentMobileField.getText()),
                userID)) {
            loadFrame.setMessage(errorMessageLabel,
                    "A student with the same Mobile Number :  " + studentMobileField.getText() +
                            "  already exists.Please try with a different Mobile Number " +
                            "again.", "RED");
            return;
        }
        Optional<ButtonType> response = alertManager.showResponseAlert(Alert.AlertType.ERROR,
                "Update Confirmation", "Are you sure you want to update the student with user" +
                        " ID: " + userID,
                "This action will update the student's " + selectedStudent.getFirstName() + " " + selectedStudent.getLastName() + "'s information in the database.You can re update it whenever you want");

        if (response.isPresent() && response.get() != ButtonType.OK)
            return;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, AdminApprovalStatus.getValue());
            pstmt.setString(2, roleComboBox.getValue());
            pstmt.setString(3, userNameField.getText());
            pstmt.setDate(4, java.sql.Date.valueOf(dobPicker.getValue()));
            pstmt.setString(5, emergencyContactNameField.getText());
            pstmt.setString(6, emergencyContactRelationField.getText());
            pstmt.setLong(7, Long.parseLong(emergencyContactMobileField.getText()));
            pstmt.setString(8, genderComboBox.getValue());
            pstmt.setString(9, maritalStatusField.getText());
            pstmt.setString(10, nationalityComboBox.getValue());
            pstmt.setString(11, fatherNameField.getText());
            pstmt.setString(12, temporaryAddressArea.getText());
            pstmt.setString(13, permanentAddressArea.getText());
            pstmt.setString(14, bloodGroupCombo.getValue());
            pstmt.setString(15, motherNameField.getText());
            pstmt.setLong(16, Long.parseLong(parentsMobileField.getText()));
            pstmt.setString(17, userStatusCombo.getValue());
            pstmt.setInt(18, userID);

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                alertManager.showAlert(Alert.AlertType.INFORMATION, "Update Successful",
                        "Student data updated successfully!", "Changes have been saved to the database.");
            } else {
                alertManager.showAlert(Alert.AlertType.WARNING, "No Update Performed",
                        "No matching student record found.", "Please check the user ID and try again.");
            }

        } catch (Exception e) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Update Error", "Failed to update student data.",
                    "Something went wrong while updating the student data: " + e.getMessage());
        }
    }


}