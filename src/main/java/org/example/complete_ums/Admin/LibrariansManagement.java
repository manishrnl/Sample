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
import org.example.complete_ums.CommonTable.LibrariansTable;
import org.example.complete_ums.CommonTable.Manage_Students_Table;
import org.example.complete_ums.CommonTable.TeacherTable;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.LoadFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class LibrariansManagement implements Initializable {
    public ImageView usersImage;
    Connection connection = DatabaseConnection.getConnection();

    public LibrariansManagement() throws SQLException {
    }

    ObservableList<LibrariansTable> tableData = FXCollections.observableArrayList();
    LoadFrame loadFrame = new LoadFrame();
    AlertManager alertManager = new AlertManager();

    @FXML
    private TableColumn<LibrariansTable, String> colPan, colUserName, colFullName;
    @FXML
    private TableColumn<LibrariansTable, Long> colAadhar, colMobile;
    @FXML
    private TableColumn<LibrariansTable, Integer> colUserId;
    @FXML
    private TableView<LibrariansTable> librariansTable;
    @FXML
    private ComboBox<String> genderCombo;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private TextField departmentIdField, designationField, emailField, emergencyMobileField, emergencyNameField, emergencyRelationField, experienceYearsField, fatherNameField, firstNameField, aadharField, altMobileField, bloodGroupField, certificationField, lastNameField, maritalStatusField, mobileField, motherNameField, nationalityField, panField, permAddressField, qualificationField, referencedViaField, salaryField, searchAnything, tempAddressField;
    ObservableList<LibrariansTable> masterData = FXCollections.observableArrayList();
    ObservableList<LibrariansTable> filtered = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorMessageLabel.setText("");
        usersImage.setOnMouseClicked(mouseEvent -> {
            uploadUsersImage(new ActionEvent());
        });
        colPan.setCellValueFactory(cellData -> cellData.getValue().colPanProperty());
        colFullName.setCellValueFactory(cellData -> cellData.getValue().colFullNameProperty());
        colAadhar.setCellValueFactory(cellData -> cellData.getValue().colAadharProperty().asObject());
        colMobile.setCellValueFactory(cellData -> cellData.getValue().colMobileProperty().asObject());
        colUserId.setCellValueFactory(cellData -> cellData.getValue().colUserIdProperty().asObject());
        colUserName.setCellValueFactory(cellData -> cellData.getValue().colUserNameProperty());
        loadFieldsIntoTable();

        librariansTable.setOnMouseClicked(mouseEvent -> {
            LibrariansTable selectedLibrarians =
                    librariansTable.getSelectionModel().getSelectedItem();
            if (selectedLibrarians != null) {
                int userID = selectedLibrarians.getColUserId();
                loadAllDetails(userID);
                loadUsersImage(userID);
            }
        });

        searchAnything.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                searchByAnything(newValue);
            } else
                librariansTable.setItems(masterData);
        });
    }

    private void searchByAnything(String newValue) {
        filtered.clear();
        String value = newValue.toLowerCase();
        for (LibrariansTable records : masterData) {
            if (records.getColFullName().toLowerCase().contains(value) || records.getColPan().toLowerCase().contains(value) || records.getColRoleType().toLowerCase().contains(value) ||
                    records.getColUserName().toLowerCase().contains(value) || String.valueOf(records.getColUserId()).contains(value) || String.valueOf(records.getColAadhar()).contains(value)) {
                filtered.add(records);
            }
        }
        librariansTable.setItems(filtered);

    }

    private void loadFieldsIntoTable() {
        errorMessageLabel.setText("");
        librariansTable.getItems().clear();
        tableData.clear();

        String query = "SELECT u.User_Id, u.Mobile, u.First_Name, u.Last_Name, u.Pan, u.Aadhar, a.UserName " +
                "FROM Users u " +
                "JOIN Authentication a ON u.User_Id = a.User_Id " +
                "WHERE u.Role = 'Librarian' ";

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

                LibrariansTable table = new LibrariansTable(pan, userName,
                        firstName + " " + lastName, aadhar,
                        mobile, userID, "");
                tableData.add(table);
            }
            masterData.addAll(tableData);
            librariansTable.setItems(tableData);

        } catch (Exception e) {
            alertManager.showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "Error loading fields into table: " + e.getMessage(),
                    "Something went wrong while loading librarian data."
            );
        }
    }

    private void loadAllDetails(int userID) {
        String query = "SELECT u.First_Name, u.Last_Name, u.Aadhar, u.Pan, u.Mobile, u.Alternate_Mobile, " +
                "u.Email, u.Gender, u.DOB, u.Blood_Group, u.Marital_Status, u.Nationality, " +
                "u.Emergency_Contact_Name, u.Emergency_Contact_Relationship, u.Emergency_Contact_Mobile, " +
                "u.Temporary_Address, u.Permanent_Address, u.Fathers_Name, u.Mothers_Name, " +
                "u.Referenced_Via, a.UserName, l.Qualification, l.Certification, l.Experience_Years, " +
                "l.Designation, l.Department_Id, l.Salary " +
                "FROM Users u " +
                "LEFT JOIN Authentication a ON u.User_Id = a.User_Id " +
                "LEFT JOIN Librarians l ON u.User_Id = l.User_Id " +
                "WHERE u.User_Id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // User details
                firstNameField.setText(rs.getString("First_Name"));
                lastNameField.setText(rs.getString("Last_Name"));
                aadharField.setText(String.valueOf(rs.getLong("Aadhar")));
                panField.setText(rs.getString("Pan"));
                mobileField.setText(String.valueOf(rs.getLong("Mobile")));
                altMobileField.setText(String.valueOf(rs.getLong("Alternate_Mobile")));
                emailField.setText(rs.getString("Email"));
                genderCombo.setValue(rs.getString("Gender"));
                dobPicker.setValue(rs.getDate("DOB").toLocalDate());
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

                // Librarian-specific details
                qualificationField.setText(rs.getString("Qualification"));
                certificationField.setText(rs.getString("Certification"));
                experienceYearsField.setText(String.valueOf(rs.getInt("Experience_Years")));
                designationField.setText(rs.getString("Designation"));
                departmentIdField.setText(String.valueOf(rs.getInt("Department_Id")));
                salaryField.setText(String.valueOf(rs.getBigDecimal("Salary")));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // You can log this instead
        }
    }


    @FXML
    void handleDeleteLibrarians(ActionEvent event) {
        LibrariansTable selected = librariansTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select a Librarians to delete.",
                    "RED");
            return;
        }
        Optional<ButtonType> response = alertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "Delete User", "Are you sure you " +
                        "want to delete " + selected.getColFullName() + " ",
                "On clicking OK , " + selected.getColFullName() + " will be deleted from our database");
        if (response.isPresent() && response.get() == ButtonType.CANCEL) {
            loadFrame.setMessage(errorMessageLabel, "Deletion Process Terminated . Users is " +
                    "retained", "GREEN");
            return;
        }
        int userID = selected.getColUserId();
        String deleteLibrarianQuery = "DELETE FROM Librarians WHERE User_Id=?";
        String deleteUserQuery = "DELETE FROM Users WHERE User_Id=?";

        try (PreparedStatement pstmtLib = connection.prepareStatement(deleteLibrarianQuery);
             PreparedStatement pstmtUsers = connection.prepareStatement(deleteUserQuery)) {
            pstmtLib.setInt(1, userID);
            pstmtLib.executeUpdate();
            pstmtUsers.setInt(1, userID);
            pstmtUsers.executeUpdate();
            loadFrame.setMessage(errorMessageLabel, "Librarian deleted successfully.", "GREEN");
            librariansTable.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
            loadFrame.setMessage(errorMessageLabel, "Failed to delete librarian. Try again.", "RED");
        }
    }

    @FXML
    void handleUpdateLibrarians(ActionEvent event) {
        LibrariansTable selected = librariansTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select a user to update.", "RED");
            return;
        }
        Optional<ButtonType> response =
                alertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "Update " +
                                "Librarians",
                        "Are you sure you " +
                                "want to update " + selected.getColFullName() + " ",
                        "On clicking OK , " + selected.getColFullName() + " will be updated in our" +
                                " database");
        if (response.isPresent() && response.get() == ButtonType.CANCEL) {
            loadFrame.setMessage(errorMessageLabel, "Updation Process Terminated . Users is " +
                    "retained", "GREEN");
            return;
        }
        int userID = selected.getColUserId();
        String updateUsersQuery = "UPDATE Users SET First_Name=?, Last_Name=?, Aadhar=?, Pan=?, Mobile=?, " +
                "Alternate_Mobile=?, Email=?, Gender=?, DOB=?, Blood_Group=?, Marital_Status=?, " +
                "Nationality=?, Emergency_Contact_Name=?, Emergency_Contact_Relationship=?, " +
                "Emergency_Contact_Mobile=?, Temporary_Address=?, Permanent_Address=?, Fathers_Name=?, " +
                "Mothers_Name=?, Referenced_Via=? WHERE User_Id=?";

        String updateLibrarianQuery = "UPDATE Librarians SET Qualification=?, Certification=?, Experience_Years=?, " +
                "Designation=?,  Salary=? WHERE User_Id=?";

        try (PreparedStatement pstmtUsers = connection.prepareStatement(updateUsersQuery);
             PreparedStatement pstmtLib = connection.prepareStatement(updateLibrarianQuery)) {
            pstmtUsers.setString(1, firstNameField.getText());
            pstmtUsers.setString(2, lastNameField.getText());
            pstmtUsers.setLong(3, Long.parseLong(aadharField.getText()));
            pstmtUsers.setString(4, panField.getText());
            pstmtUsers.setLong(5, Long.parseLong(mobileField.getText()));
            pstmtUsers.setLong(6, Long.parseLong(altMobileField.getText()));
            pstmtUsers.setString(7, emailField.getText());
            pstmtUsers.setString(8, genderCombo.getValue());
            pstmtUsers.setDate(9, Date.valueOf(dobPicker.getValue()));
            pstmtUsers.setString(10, bloodGroupField.getText());
            pstmtUsers.setString(11, maritalStatusField.getText());
            pstmtUsers.setString(12, nationalityField.getText());
            pstmtUsers.setString(13, emergencyNameField.getText());
            pstmtUsers.setString(14, emergencyRelationField.getText());
            pstmtUsers.setLong(15, Long.parseLong(emergencyMobileField.getText()));
            pstmtUsers.setString(16, tempAddressField.getText());
            pstmtUsers.setString(17, permAddressField.getText());
            pstmtUsers.setString(18, fatherNameField.getText());
            pstmtUsers.setString(19, motherNameField.getText());
            pstmtUsers.setString(20, referencedViaField.getText());
            pstmtUsers.setInt(21, userID);
            pstmtUsers.executeUpdate();

            pstmtLib.setString(1, qualificationField.getText());
            pstmtLib.setString(2, certificationField.getText());
            pstmtLib.setInt(3, Integer.parseInt(experienceYearsField.getText()));
            pstmtLib.setString(4, designationField.getText());
            pstmtLib.setDouble(5, Double.parseDouble(salaryField.getText()));
            pstmtLib.setInt(6, userID);
            pstmtLib.executeUpdate();

            loadFrame.setMessage(errorMessageLabel, "Librarian details updated successfully.", "GREEN");
            librariansTable.refresh();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            loadFrame.setMessage(errorMessageLabel, "Failed to update librarian. Check input values.", "RED");
        }
    }

    private void loadUsersImage(int userID) {
        Task<Image> loadImageTask = new Task<Image>() {
            @Override
            protected Image call() throws Exception {

                String query = "Select Photo_URL from Users where User_Id=" + userID;
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    ResultSet resultSet = pstmt.executeQuery();
                    if (resultSet.next()) {
                        try (InputStream inputStream = resultSet.getBinaryStream("Photo_URL")) {
                            if (inputStream != null) {
                                return new Image(inputStream);
                            }
                        }
                    }
                }
                return null;
            }
        };
        loadImageTask.setOnSucceeded(event -> {
            Image loadProfileImage = loadImageTask.getValue();
            if (loadProfileImage != null) {
                usersImage.setImage(loadProfileImage);

            } else {
                Image defaultImage = new Image(getClass().getResourceAsStream("/org/example" +
                        "/complete_ums/Images/UserName.png"));
                usersImage.setImage(defaultImage);
            }
        });
        loadImageTask.setOnFailed(event -> {
            Throwable throwable = loadImageTask.getException();
            loadFrame.setMessage(errorMessageLabel, "Something weired happened while loading " +
                    "users Image. Contact admin if it appears again and again", "RED");
        });
        new Thread(loadImageTask).start();
    }

    public void uploadUsersImage(ActionEvent actionEvent) {
        errorMessageLabel.setText("");
        LibrariansTable selectedLibrarians =
                librariansTable.getSelectionModel().getSelectedItem();
        if (selectedLibrarians == null) {
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
                    pstmt.setInt(2, selectedLibrarians.getColUserId());
                    int rowsUpdated = pstmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        alertManager.showAlert(Alert.AlertType.INFORMATION, "Photo Upload Successful",
                                "Profile photo uploaded successfully.", "The profile photo has been updated in the database.");
                    } else {
                        alertManager.showAlert(Alert.AlertType.ERROR, "Photo Upload Failed",
                                "Failed to update profile photo.", "Please try again.");
                    }
                } else {
                    alertManager.showAlert(Alert.AlertType.WARNING, "No librarians Selected",
                            "Please select a librarians to upload a photo.", "You must " +
                                    "select a librarians from the table to upload a photo.");
                }
            } catch (Exception e) {
                loadFrame.setMessage(errorMessageLabel, "Something went wrong while uploading " +
                        "the profile photo: " + e.getMessage(), "RED");
            }
        }
    }

}
