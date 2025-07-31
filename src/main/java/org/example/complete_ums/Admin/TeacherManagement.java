package org.example.complete_ums.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.complete_ums.CommonTable.TeacherTable;
import org.example.complete_ums.Databases.DatabaseConnection;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class TeacherManagement implements Initializable {
    Connection connection = DatabaseConnection.getConnection();

    public TeacherManagement() throws SQLException {
    }

    ObservableList<TeacherTable> lists = FXCollections.observableArrayList();
    LoadFrame loadFrame = new LoadFrame();
    AlertManager alertManager = new AlertManager();
    SessionManager sessionManager = SessionManager.getInstance();
    @FXML
    private ComboBox<String> genderCombo, employmentTypeCombo;
    @FXML
    private TableColumn<TeacherTable, Long> colAadhar, colMobile;
    @FXML
    private TableColumn<TeacherTable, String> colUserName, colPan, colFullName;
    @FXML
    private TableColumn<TeacherTable, Integer> colUserId;
    @FXML
    private TableView<TeacherTable> teacherTable;
    @FXML
    private TextField aadharField, altMobileField, bloodGroupField, departmentIdField, designationField, emailField, emergencyMobileField, emergencyNameField, emergencyRelationField, experienceYearsField, fatherNameField, firstNameField, lastNameField, maritalStatusField, mobileField, motherNameField, nationalityField, panField, permAddressField, qualificationField, referencedViaField, salaryField, searchByAadharField, searchByFullNameField, searchByMobileField, searchByPanField, searchByUserIDField, searchByUsernameField, specialisationField, tempAddressField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private ImageView usersImage;
    List<Integer> searchResultID = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorMessageLabel.setText("");
        loadteacherTables();
        usersImage.setOnMouseClicked(mouseEvent -> {
            uploadProfilePic(new ActionEvent());
        });
        colPan.setCellValueFactory(cellData -> cellData.getValue().colPanProperty());
        colFullName.setCellValueFactory(cellData -> cellData.getValue().colFullNameProperty());
        colAadhar.setCellValueFactory(cellData -> cellData.getValue().colAadharProperty().asObject());
        colMobile.setCellValueFactory(cellData -> cellData.getValue().colMobileProperty().asObject());
        colUserId.setCellValueFactory(cellData -> cellData.getValue().colUserIdProperty().asObject());
        colUserName.setCellValueFactory(cellData -> cellData.getValue().colUserNameProperty());

        searchByFullNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null || !newValue.isEmpty()) {
                clearTextField(false, true, true, true, true, true);
                String query = "SELECT User_Id FROM Users WHERE CONCAT(First_Name, ' ', " +
                        "Last_Name) LIKE ?";
                List<Integer> resultIDs = SearchData(newValue, query);
                if (resultIDs != null && !resultIDs.isEmpty()) {
                    LoadFieldsIntoTable(resultIDs);
                } else {
                    teacherTable.getItems().clear(); // Clear table if no result
                }
            } else {
                teacherTable.getItems().clear(); // Clear table when input is empty
            }
        });
        searchByUsernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null || !newValue.isEmpty()) {
                clearTextField(true, false, true, true, true, true);
                String query = "SELECT * FROM Authentication WHERE UserName LIKE ? ";
                List<Integer> resultIDs = SearchData(newValue, query);
                if (resultIDs != null && !resultIDs.isEmpty()) {
                    LoadFieldsIntoTable(resultIDs);
                } else {
                    teacherTable.getItems().clear(); // Clear table if no result
                }
            } else {
                teacherTable.getItems().clear(); // Clear table when input is empty
            }
        });
        searchByUserIDField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                clearTextField(true, true, true, true, false, true);
                String query = "SELECT * from Users WHERE User_Id Like ? ";
                searchResultID = SearchData(newValue, query);
                if (searchResultID != null) {
                    LoadFieldsIntoTable(searchResultID);
                }
            }
        });
        searchByMobileField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                clearTextField(true, true, true, true, true, false);
                String query = "SELECT * FROM Users WHERE Mobile LIKE ?  ";
                searchResultID = SearchData(newValue, query);
                if (searchResultID != null) {
                    LoadFieldsIntoTable(searchResultID);
                }
            }
        });
        searchByAadharField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                clearTextField(true, true, true, false, true, true);
                String query = "SELECT * FROM Users WHERE Aadhar LIKE ?  ";
                searchResultID = SearchData(newValue, query);
                if (searchResultID != null) {
                    LoadFieldsIntoTable(searchResultID);
                }
            }
        });
        searchByPanField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                clearTextField(true, true, false, true, true, true);
                String query = "SELECT * FROM Users WHERE Pan LIKE ? ";
                searchResultID = SearchData(newValue, query);
                if (searchResultID != null) {
                    LoadFieldsIntoTable(searchResultID);
                }
            }
        });

        teacherTable.setOnMouseClicked(mouseEvent -> {
            TeacherTable selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();
            if (selectedTeacher != null) {
                int userID = selectedTeacher.getColUserId();
                AddDetailsInTextField(userID);
                loadUsersImage(userID);
            }
        });
    }


    private void clearTextField(Boolean clearName, Boolean clearUserName, Boolean clearPan, Boolean clearAadhar, Boolean clearUserID, Boolean clearMobile) {
        errorMessageLabel.setText("");
        if (clearName) searchByFullNameField.clear();
        if (clearPan) searchByPanField.clear();
        if (clearAadhar) searchByAadharField.clear();
        if (clearUserID) searchByUserIDField.clear();
        if (clearMobile) searchByMobileField.clear();
        if (clearUserName) searchByUsernameField.clear();

    }

    private List<Integer> SearchData(String newValue, String query) {
        errorMessageLabel.setText("");
        List<Integer> userIDs = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "%" + newValue + "%");
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                userIDs.add(resultSet.getInt("User_Id"));

            }
        } catch (Exception e) {
            loadFrame.setMessage(errorMessageLabel,
                    "Error executing search query: " + e.getMessage() +
                            " -> Something went wrong while searching for '" + newValue + "' in Users",
                    "RED");
            return null;
        }

        return userIDs;
    }

    private void LoadFieldsIntoTable(List<Integer> searchResultID) {
        errorMessageLabel.setText("");
        teacherTable.getItems().clear();
        lists.clear();

        if (searchResultID == null || searchResultID.isEmpty()) {
            return;
        }

        String placeholders = String.join(",", java.util.Collections.nCopies(searchResultID.size(), "?"));

        String query = "SELECT u.User_Id, u.Mobile, u.First_Name, u.Last_Name, u.Pan, u.Aadhar, a.UserName " +
                "FROM Users u " +
                "JOIN Authentication a ON u.User_Id = a.User_Id " +
                "WHERE u.Role = 'Teacher' AND u.User_Id IN (" + placeholders + ")";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            for (int i = 0; i < searchResultID.size(); i++) {
                pstmt.setInt(i + 1, searchResultID.get(i));
            }

            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String firstName = resultSet.getString("First_Name");
                String lastName = resultSet.getString("Last_Name");

                int userID = resultSet.getInt("User_Id");
                long mobile = resultSet.getLong("Mobile");
                String pan = resultSet.getString("Pan");
                long aadhar = resultSet.getLong("Aadhar");
                String userName = resultSet.getString("UserName");

                TeacherTable table = new TeacherTable(pan, userName,
                        firstName + " " + lastName, aadhar,
                        mobile, userID);
                lists.add(table);
            }

            teacherTable.setItems(lists);

        } catch (Exception e) {
            alertManager.showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "Error loading fields into table: " + e.getMessage(),
                    "Something went wrong while loading librarian data."
            );
        }
    }

    private void loadteacherTables() {

        String query = "SELECT u.User_Id, u.Mobile, u.Pan, u.Aadhar, a.UserName ,u" +
                ".First_Name,u.Last_Name  FROM Users u " +
                "JOIN Authentication a ON u.User_Id = a.User_Id " +
                "WHERE u.Role = 'Teacher'";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String firstName = resultSet.getString("First_Name");
                String lastName = resultSet.getString("Last_Name");
                int userID = resultSet.getInt("User_Id");
                long mobile = resultSet.getLong("Mobile");
                String pan = resultSet.getString("Pan");
                long aadhar = resultSet.getLong("Aadhar");
                String userName = resultSet.getString("UserName");

                TeacherTable table = new TeacherTable(pan, userName,
                        firstName + " " + lastName, aadhar, mobile, userID);
                lists.add(table);
            }
            teacherTable.setItems(lists);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading librarian data", e);
        }
    }

    private void AddDetailsInTextField(int userID) {
        errorMessageLabel.setText("");
        String query = "SELECT u.*, t.* FROM Users u JOIN Teachers t ON u.User_Id = t.User_Id WHERE u.User_Id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                firstNameField.setText(rs.getString("First_Name"));
                lastNameField.setText(rs.getString("Last_Name"));
                aadharField.setText(String.valueOf(rs.getLong("Aadhar")));
                panField.setText(rs.getString("Pan"));
                mobileField.setText(String.valueOf(rs.getLong("Mobile")));
                altMobileField.setText(String.valueOf(rs.getObject("Alternate_Mobile") != null ? rs.getLong("Alternate_Mobile") : ""));
                emailField.setText(rs.getString("Email"));
                genderCombo.setValue(rs.getString("Gender"));
                if (rs.getDate("DOB") != null) {
                    dobPicker.setValue(rs.getDate("DOB").toLocalDate());
                } else {
                    dobPicker.setValue(null);
                }

                bloodGroupField.setText(rs.getString("Blood_Group"));
                maritalStatusField.setText(rs.getString("Marital_Status"));
                nationalityField.setText(rs.getString("Nationality"));
                emergencyNameField.setText(rs.getString("Emergency_Contact_Name"));
                emergencyRelationField.setText(rs.getString("Emergency_Contact_Relationship"));
                emergencyMobileField.setText(String.valueOf(rs.getLong("Emergency_Contact_Mobile")));
                tempAddressField.setText(rs.getString("Temporary_Address"));
                permAddressField.setText(rs.getString("Permanent_Address"));
                fatherNameField.setText(rs.getString("Fathers_Name"));
                motherNameField.setText(rs.getString("Mothers_Name"));
                referencedViaField.setText(rs.getString("Referenced_Via"));

                designationField.setText(rs.getString("Designation"));
                departmentIdField.setText(String.valueOf(rs.getInt("Department_Id")));
                qualificationField.setText(rs.getString("Qualification"));
                specialisationField.setText(rs.getString("Specialisation"));
                employmentTypeCombo.setValue(rs.getString("Employment_Type"));
                experienceYearsField.setText(String.valueOf(rs.getInt("Teaching_Experience_Years")));
                salaryField.setText(String.valueOf(rs.getBigDecimal("Salary")));

                errorMessageLabel.setText("");

            }
        } catch (SQLException e) {
            loadFrame.setMessage(errorMessageLabel, "Error loading teacher details: " + e.getMessage(), "RED");
        }
    }

    @FXML
    void handleDeleteTeachers(ActionEvent event) {
        errorMessageLabel.setText("");
        TeacherTable selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();

        if (selectedTeacher == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select a teacher from the table " +
                    "to delete.", "RED");
            return;
        }

        int selectedTeacherId = selectedTeacher.getColUserId();
        String teacherName = selectedTeacher.getColFullName();
        Optional<ButtonType> response = alertManager.showResponseAlert(
                Alert.AlertType.CONFIRMATION, "Confirm Deletion", "Are you sure you want to delete " + teacherName + "?",
                "This will permanently remove the teacher and ALL notifications they created. This action cannot be undone."
        );

        if (response.isPresent() && response.get() == ButtonType.OK) {
            String deleteNotificationsSQL = "DELETE FROM Notifications WHERE Created_By_User_Id = ?";
            String deleteUserSQL = "DELETE FROM Users WHERE User_Id = ?";
            try {
                connection.setAutoCommit(false);
                try (PreparedStatement pstmtDeleteNotifications = connection.prepareStatement(deleteNotificationsSQL)) {
                    pstmtDeleteNotifications.setInt(1, selectedTeacherId);
                    pstmtDeleteNotifications.executeUpdate();
                }
                int affectedRows;
                try (PreparedStatement pstmtDeleteUser = connection.prepareStatement(deleteUserSQL)) {
                    pstmtDeleteUser.setInt(1, selectedTeacherId);
                    affectedRows = pstmtDeleteUser.executeUpdate();
                }
                connection.commit();
                if (affectedRows > 0) {
                    loadFrame.setMessage(errorMessageLabel, "Teacher '" + teacherName + "' was successfully deleted.", "GREEN");
                    lists.removeIf(teacher -> teacher.getColUserId() == selectedTeacherId);
                    clearFormFieldsAndSelection();
                } else {
                    alertManager.showAlert(Alert.AlertType.ERROR, "Deletion Failed", "No teacher was deleted.", "The teacher may have already been removed.");
                }
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during database transaction rollback: " + rollbackEx.getMessage());
                }
                loadFrame.setMessage(errorMessageLabel, "Database Error: " + e.getMessage(), "RED");
                System.err.println("SQL Error during deletion: " + e.getMessage());

            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException finalEx) {
                    System.err.println("Error resetting auto-commit: " + finalEx.getMessage());
                }
            }
        }
    }

    @FXML
    void handleUpdateTeachers(ActionEvent event) {
        TeacherTable selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Selection Error", "No Teacher Selected", "Please select a teacher from the table to update.");
            return;
        }
        if (firstNameField.getText().isBlank() || lastNameField.getText().isBlank() || emailField.getText().isBlank() || mobileField.getText().isBlank() || aadharField.getText().isBlank() || panField.getText().isBlank()) {
            loadFrame.setMessage(errorMessageLabel, "Error: Name, Email, Mobile, Aadhar, and PAN cannot be empty.", "RED");
            return;
        }
        try {
            Long.parseLong(mobileField.getText());
            Long.parseLong(aadharField.getText());
            if (!salaryField.getText().isBlank()) Double.parseDouble(salaryField.getText());
            if (!experienceYearsField.getText().isBlank())
                Integer.parseInt(experienceYearsField.getText());
        } catch (NumberFormatException e) {
            errorMessageLabel.setText("Error: Mobile, Aadhar, Salary, and Experience must be valid numbers.");
            return;
        }
        String updateUserSQL = "UPDATE Users SET First_Name=?, Last_Name=?, Aadhar=?, Pan=?, Mobile=?, Alternate_Mobile=?, Email=?, Gender=?, DOB=?, Blood_Group=?, Marital_Status=?, Nationality=?, Emergency_Contact_Name=?, Emergency_Contact_Relationship=?, Emergency_Contact_Mobile=?, Temporary_Address=?, Permanent_Address=?, Fathers_Name=?, Mothers_Name=?, Referenced_Via=? WHERE User_Id=?";
        String updateTeacherSQL = "UPDATE Teachers SET Designation=?, Department_Id=?, Qualification=?, Specialisation=?, Employment_Type=?, Teaching_Experience_Years=?, Salary=? WHERE User_Id=?";
        try {
            int selectedTeacherId = selectedTeacher.getColUserId();
            connection.setAutoCommit(false);
            try (PreparedStatement userPstmt = connection.prepareStatement(updateUserSQL)) {
                userPstmt.setString(1, firstNameField.getText());
                userPstmt.setString(2, lastNameField.getText());
                userPstmt.setLong(3, Long.parseLong(aadharField.getText()));
                userPstmt.setString(4, panField.getText());
                userPstmt.setLong(5, Long.parseLong(mobileField.getText()));
                userPstmt.setString(6, altMobileField.getText());
                userPstmt.setString(7, emailField.getText());
                userPstmt.setString(8, genderCombo.getValue());
                userPstmt.setDate(9, dobPicker.getValue() != null ? java.sql.Date.valueOf(dobPicker.getValue()) : null);
                userPstmt.setString(10, bloodGroupField.getText());
                userPstmt.setString(11, maritalStatusField.getText());
                userPstmt.setString(12, nationalityField.getText());
                userPstmt.setString(13, emergencyNameField.getText());
                userPstmt.setString(14, emergencyRelationField.getText());
                userPstmt.setLong(15, Long.parseLong(emergencyMobileField.getText()));
                userPstmt.setString(16, tempAddressField.getText());
                userPstmt.setString(17, permAddressField.getText());
                userPstmt.setString(18, fatherNameField.getText());
                userPstmt.setString(19, motherNameField.getText());
                userPstmt.setString(20, referencedViaField.getText());
                userPstmt.setInt(21, selectedTeacherId);
                userPstmt.executeUpdate();
            }
            try (PreparedStatement teacherPstmt = connection.prepareStatement(updateTeacherSQL)) {
                teacherPstmt.setString(1, designationField.getText());
                teacherPstmt.setInt(2, Integer.parseInt(departmentIdField.getText()));
                teacherPstmt.setString(3, qualificationField.getText());
                teacherPstmt.setString(4, specialisationField.getText());
                teacherPstmt.setString(5, employmentTypeCombo.getValue());
                teacherPstmt.setInt(6, Integer.parseInt(experienceYearsField.getText()));
                teacherPstmt.setDouble(7, Double.parseDouble(salaryField.getText()));
                teacherPstmt.setInt(8, selectedTeacherId);
                teacherPstmt.executeUpdate();
            }
            connection.commit(); // Commit the transaction
            loadFrame.setMessage(errorMessageLabel, "Teacher details have been updated Successfully", "GREEN");
            lists.clear();
            teacherTable.getItems().clear();
            loadteacherTables();
            clearFormFieldsAndSelection();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
            }
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Update Failed", "An error occurred: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
            }
        }
    }

    private void clearFormFieldsAndSelection() {
        firstNameField.clear();
        lastNameField.clear();
        aadharField.clear();
        panField.clear();
        mobileField.clear();
        altMobileField.clear();
        emailField.clear();
        bloodGroupField.clear();
        maritalStatusField.clear();
        nationalityField.clear();
        emergencyNameField.clear();
        emergencyRelationField.clear();
        emergencyMobileField.clear();
        tempAddressField.clear();
        permAddressField.clear();
        fatherNameField.clear();
        motherNameField.clear();
        referencedViaField.clear();
        designationField.clear();
        departmentIdField.clear();
        qualificationField.clear();
        specialisationField.clear();
        experienceYearsField.clear();
        salaryField.clear();
        genderCombo.setValue(null);
        employmentTypeCombo.setValue(null);
        dobPicker.setValue(null);
        errorMessageLabel.setText("");
        teacherTable.getSelectionModel().clearSelection();
    }

    public void uploadProfilePic(ActionEvent actionEvent) {
        errorMessageLabel.setText("");
        TeacherTable selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null) {
            loadFrame.setMessage(errorMessageLabel, "You must select a Teacher from the " +
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
            File selectedImageFile = file;
            usersImage.setImage(new Image(file.toURI().toString()));
            String photoQuery = "UPDATE Users SET Photo_URL = ? WHERE User_Id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(photoQuery)) {
                pstmt.setBinaryStream(1, new FileInputStream(file));

                if (selectedImageFile != null) {
                    pstmt.setInt(2, selectedTeacher.getColUserId());
                    int rowsUpdated = pstmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        alertManager.showAlert(Alert.AlertType.INFORMATION, "Photo Upload Successful",
                                "Profile photo uploaded successfully.", "The profile photo has been updated in the database.");
                    } else {
                        alertManager.showAlert(Alert.AlertType.ERROR, "Photo Upload Failed",
                                "Failed to update profile photo.", "Please try again.");
                    }
                } else {
                    alertManager.showAlert(Alert.AlertType.WARNING, "No teacher Selected",
                            "Please select a teacher to upload a photo.", "You must select a" +
                                    " " +
                                    "teacher from the table to upload a photo.");
                }
            } catch (Exception e) {
                loadFrame.setMessage(errorMessageLabel, "Something went wrong while uploading " +
                        "the profile photo: " + e.getMessage(), "RED");
            }
        }
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
                                return new Image(is); // Return the loaded image
                            }
                        }
                    }
                }
                return null;
            }
        };
        loadImageTask.setOnSucceeded(event -> {
            Image profileImage1 = loadImageTask.getValue(); // Get the result from the task

            if (profileImage1 != null) {
                usersImage.setImage(profileImage1);
            } else {
                Image defaultImage = new Image(getClass().getResourceAsStream("/org/example/complete_ums/Images/UserName.png"));
                usersImage.setImage(defaultImage);
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
}

