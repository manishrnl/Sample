package org.example.complete_ums.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.complete_ums.CommonTable.Manage_Students_Table;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.Java_StyleSheet.RoundedImage;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.LoadFrame;

import java.io.File;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminManagement {

    public AdminManagement() throws SQLException {
    }

    AlertManager alertManager = new AlertManager();
    LoadFrame loadFrame = new LoadFrame();
    RoundedImage roundedImage = new RoundedImage();
    Connection connection = DatabaseConnection.getConnection();
    private File selectedImageFile = null;

    @FXML
    private TextField firstNameField, lastNameField, fatherNameField, motherNameField,
            emailField, mobileField, alternateMobileField, searchAdminData,
            aadharField, panField, userNameField, maritalStatusField,
            emergencyContactNameField, emergencyContactRelationField,
            emergencyContactMobileField, designationField;

    @FXML
    private ComboBox<String> userStatusCombo, adminApprovalStatus, genderComboBox,
            bloodGroupCombo, nationalityComboBox, accessLevelComboBox, departmentComboBox;

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
    private Button uploadPhotoButton, updateAdminButton, deleteAdminButton;


    @FXML
    private TextArea temporaryAddressArea, permanentAddressArea;

    @FXML
    private DatePicker dobPicker;
    @FXML
    private Label errorMessageLabel, totalActiveAdminsLabel, superAccessAdminsLabel,
            pendingApprovalAdminsLabel, adminsPerDepartmentLabel;
    @FXML
    private ImageView usersImage;
    ObservableList<Manage_Students_Table> obserVableList = FXCollections.observableArrayList();
    List<Integer> searchResultID = new ArrayList<>();
    Boolean duplicateFound = false;
    String AADHAR_REGEX = "^\\d{12}$";
    Manage_Students_Table selectedStudent;


    ObservableList<Manage_Students_Table> masterData = FXCollections.observableArrayList();
    Button3DEffect button3DEffect = new Button3DEffect();

    @FXML
    void initialize() {
        setAdminTextFieldValue();
        usersImage.setOnMouseClicked(mouseEvent -> {
            uploadUsersImage(new ActionEvent());
        });
        button3DEffect.applyEffect(uploadPhotoButton, "/sounds/hover.mp3");
        button3DEffect.applyEffect(updateAdminButton, "/sounds/hover.mp3");
        button3DEffect.applyEffect(uploadPhotoButton, "/sounds/hover.mp3");
        button3DEffect.applyEffect(deleteAdminButton, "/sounds/hover.mp3");
        errorMessageLabel.setText("");
        loadTableDataForAdmin();

        searchAdminData.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                addFilteredSearchedDataIntoTable(newValue);
            }
        });

        colUserId.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().userIdProperty().asObject());
        colUsername.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().usernameProperty());
        colFirstName.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().firstNameProperty());
        colLastName.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().lastNameProperty());
        colEmail.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().emailProperty());
        colAadhar.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().aadharProperty().asObject());
        colPan.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().panProperty());
        colMobile.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().studentsMobileProperty().asObject());

        studentsTable.setOnMouseClicked(event -> {
            Manage_Students_Table selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
            if (selectedStudent != null) {
                int userID = selectedStudent.getUserId();
                firstNameField.setText(selectedStudent.getFirstName());
                lastNameField.setText(selectedStudent.getLastName());
                emailField.setText(selectedStudent.getEmail());
                mobileField.setText(String.valueOf(selectedStudent.getStudentsMobile()));
                aadharField.setText(String.valueOf(selectedStudent.getAadhar()));
                panField.setText(selectedStudent.getPan());
                aadharField.setText("" + selectedStudent.getAadhar());
                setOtherFields(userID);
                setRoleSpecificDetails(userID);
                loadUsersImage(userID);

            }
        });
    }

    private void setAdminTextFieldValue() {
        int superAccess = 0, pendingApproval = 0, activeAdmins = 0; // Initialize to 0
        String query = "    SELECT a.Access_Level, u.Admin_Approval_Status,u.User_Status FROM Admins a JOIN " +
                "Users u ON a.User_Id = u.User_Id";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                superAccess += rs.getString("Access_Level").equals("Super") ? 1 : 0;
                pendingApproval += !rs.getString("Admin_Approval_Status").equals("Approved") ? 1 : 0;
                activeAdmins += !rs.getString("User_Status").equals("Active") ? 1 : 0;
            }
            superAccessAdminsLabel.setText("" + superAccess);
            pendingApprovalAdminsLabel.setText("" + pendingApproval);
            totalActiveAdminsLabel.setText("" + activeAdmins);
        } catch (Exception e) {
            System.err.println("Error calculating admin statistics: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve admin statistics", e);
        }
    }

    private void setRoleSpecificDetails(int userID) {
        String query = "SELECT a.Designation,a.Access_Level,d.Department_Name FROM Admins a LEFT JOIN Departments d ON a.Department_Id = d.Department_Id WHERE a.User_Id = " + userID;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                designationField.setText(rs.getString("Designation"));
                departmentComboBox.setValue(rs.getString("Department_Name"));
                accessLevelComboBox.setValue(rs.getString("Access_Level"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void addFilteredSearchedDataIntoTable(String value) {
        ObservableList<Manage_Students_Table> filtered = FXCollections.observableArrayList();
        String newValue = value.toLowerCase();
        for (Manage_Students_Table records : masterData) {
            if (records.getEmail().toLowerCase().contains(newValue)
                    || records.getPan().toLowerCase().contains(newValue)
                    || records.getFullName().toLowerCase().contains(newValue)
                    || records.getUsername().toLowerCase().contains(newValue)
                    || String.valueOf(records.getUserId()).contains(newValue)
                    || String.valueOf(records.getAadhar()).contains(newValue)
                    || String.valueOf(records.getStudentsMobile()).contains(newValue)) {
                filtered.add(records);
            }
            studentsTable.setItems(filtered);
        }
    }

    private void loadTableDataForAdmin() {
        ObservableList<Manage_Students_Table> data = FXCollections.observableArrayList();
        String query = "select user.User_Id,user.First_Name,user.Last_Name,user.Email,user" +
                ".PAN,user.Aadhar,user.Mobile ,a.UserName from Users user JOIN Authentication a ON " +
                "user.User_Id=a.User_Id where user.Role='Admin'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {

                Manage_Students_Table tabledata = new Manage_Students_Table(rs.getInt(
                        "User_Id"), rs.getString("UserName"), rs.getString("First_Name"),
                        rs.getString("Last_Name"), rs.getString("Email"), rs.getLong("Mobile"), rs.getLong("Aadhar"), rs.getString("PAN"));
                data.add(tabledata);
            }

            studentsTable.setItems(data);
            masterData.addAll(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                    usersImage.setImage(image);
                } else {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/org" +
                            "/example/complete_ums/Images/UserName.png"));
                    usersImage.setImage(defaultImage);
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
                //    roleComboBox.getSelectionModel().select(resultSet.getString("Role"));
                userNameField.setText(resultSet.getString("UserName"));
                fatherNameField.setText(resultSet.getString("Fathers_Name"));
                motherNameField.setText(resultSet.getString("Mothers_Name"));
                mobileField.setText(resultSet.getString("Alternate_Mobile"));
                userStatusCombo.getSelectionModel().select(resultSet.getString("User_Status"));
                adminApprovalStatus.getSelectionModel().select(resultSet.getString("Admin_Approval_Status"));
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
            loadFrame.setMessage(errorMessageLabel, "You must select a Accountants from the " +
                    "table" +
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
            usersImage.setImage(new Image(file.toURI().toString()));
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

    public void handleDeleteOperation(ActionEvent actionEvent) {
        errorMessageLabel.setText("");
        selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select Accountants from Table to initiate Delete Operations...", "RED");
            return;
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
            if (selectedStudent.getUserId() == 50) {
                loadFrame.setMessage(errorMessageLabel, "Can Not delete Main Admin Account",
                        "RED");
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
        adminApprovalStatus.setValue(null);
        nationalityComboBox.setValue(null);
        maritalStatusField.clear();
        genderComboBox.setValue(null);
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        mobileField.clear();
        alternateMobileField.clear();
        aadharField.clear();
        panField.clear();
        userNameField.clear();
        fatherNameField.clear();
        motherNameField.clear();
        userStatusCombo.getSelectionModel().clearSelection();
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

    public void handleUpdateUsers(ActionEvent actionEvent) {
        errorMessageLabel.setText("");
        String query = "UPDATE Users u JOIN Authentication a ON u.User_Id = a.User_Id SET u.Admin_Approval_Status = ?, a.UserName = ?, u.DOB = ?, u.Emergency_Contact_Name = ?, u.Emergency_Contact_Relationship =?, u.Emergency_Contact_Mobile = ?, u.Gender = ?, u.Marital_Status = ?, u.Nationality = ?, u.Fathers_Name = ?,u.Temporary_Address = ?, u.Permanent_Address = ?, u.Blood_Group = ?, u.Mothers_Name = ?, u.Alternate_Mobile = ?, u.User_Status = ? WHERE u.User_Id = ?";
        selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select Accountants from Table to" +
                    " " +
                    "initiate Update Operations...", "RED");
        }
        if (mobileField.getText() == null || aadharField.getText() == null || panField.getText() == null) {
            loadFrame.setMessage(errorMessageLabel, "All Fields must be filled to proceed. "
                    , "RED");
            return;
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
            loadFrame.setMessage(errorMessageLabel, "Please enter a valid Aadhar Number that " +
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
        if (checkIfDetailsExists("Users", "Mobile", Long.parseLong(mobileField.getText()),
                userID)) {
            loadFrame.setMessage(errorMessageLabel,
                    "A student with the same Mobile Number :  " + mobileField.getText() +
                            "  already exists.Please try with a different Mobile Number " +
                            "again.", "RED");
            return;
        }
        Optional<ButtonType> response = alertManager.showResponseAlert(Alert.AlertType.ERROR,
                "Update Confirmation", "Are you sure you want to update the student with user ID: " + userID, "This action will update the student's " + selectedStudent.getFirstName() + " " + selectedStudent.getLastName() + "'s information in the database.You can re update it whenever you want");

        if (response.isPresent() && response.get() != ButtonType.OK)
            return;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Parameter 1: u.Admin_Approval_Status
            pstmt.setString(1, adminApprovalStatus.getValue());

            // Parameter 2: a.UserName
            pstmt.setString(2, userNameField.getText()); // Corrected: Moved userNameField.getText() here

            // Parameter 3: u.DOB
            pstmt.setDate(3, java.sql.Date.valueOf(dobPicker.getValue()));

            // Parameter 4: u.Emergency_Contact_Name
            pstmt.setString(4, emergencyContactNameField.getText());

            // Parameter 5: u.Emergency_Contact_Relationship
            pstmt.setString(5, emergencyContactRelationField.getText());

            // Parameter 6: u.Emergency_Contact_Mobile
            pstmt.setLong(6, Long.parseLong(emergencyContactMobileField.getText()));

            // Parameter 7: u.Gender
            pstmt.setString(7, genderComboBox.getValue());

            // Parameter 8: u.Marital_Status
            pstmt.setString(8, maritalStatusField.getText());

            // Parameter 9: u.Nationality
            pstmt.setString(9, nationalityComboBox.getValue());

            // Parameter 10: u.Fathers_Name
            pstmt.setString(10, fatherNameField.getText());

            // Parameter 11: u.Temporary_Address
            pstmt.setString(11, temporaryAddressArea.getText());

            // Parameter 12: u.Permanent_Address
            pstmt.setString(12, permanentAddressArea.getText());

            // Parameter 13: u.Blood_Group
            pstmt.setString(13, bloodGroupCombo.getValue());

            // Parameter 14: u.Mothers_Name
            pstmt.setString(14, motherNameField.getText());

            // Parameter 15: u.Alternate_Mobile
            pstmt.setLong(15, Long.parseLong(alternateMobileField.getText()));

            // Parameter 16: u.User_Status
            pstmt.setString(16, userStatusCombo.getValue()); // Corrected: Moved userStatusCombo.getValue() here

            // Parameter 17: u.User_Id (in WHERE clause)
            pstmt.setInt(17, userID);

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