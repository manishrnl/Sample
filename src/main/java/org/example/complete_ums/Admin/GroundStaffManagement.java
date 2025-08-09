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
import org.example.complete_ums.CommonTable.GroundStaffTable;
import org.example.complete_ums.CommonTable.LibrariansTable;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class GroundStaffManagement implements Initializable {
    public ImageView usersImage;

    public GroundStaffManagement() throws SQLException {
    }

    SessionManager sessionManager = SessionManager.getInstance();
    AlertManager alertManager = new AlertManager();
    LoadFrame loadFrame = new LoadFrame();
    Connection connection = DatabaseConnection.getConnection();
    @FXML
    private TextField SearchAnything;
    @FXML
    private TableColumn<GroundStaffTable, String> colDesignation, colEmail,
            colEmploymentType, colFullName, colStatus;
    @FXML
    private TableColumn<GroundStaffTable, Integer> colUserId;
    @FXML
    private TableColumn<GroundStaffTable, Long> colMobile;

    @FXML
    private TextField emailField, firstNameField, lastNameField, mobileField, salaryField, searchField;
    @FXML
    private ComboBox<String> employmentTypeCombo, designationCombo;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private TableView<GroundStaffTable> groundStaffTable;
    List<Integer> searchResultID = new ArrayList<>();
    ObservableList<GroundStaffTable> obserVableList = FXCollections.observableArrayList();
    int userIdLoggedIn = sessionManager.getUserID();
    private File selectedImageFile;
    ObservableList<GroundStaffTable> masterData = FXCollections.observableArrayList();
    ObservableList<GroundStaffTable> filtered = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usersImage.setOnMouseClicked(mouseEvent -> {
            uploadUsersImage(new ActionEvent());
        });
        handleRefresh(new ActionEvent());
        colUserId.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().userIDProperty().asObject());
        colMobile.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().mobileProperty().asObject());
        colDesignation.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().designationProperty());
        colEmail.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().emailProperty());
        colEmploymentType.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().employmentTypeProperty());
        colFullName.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().fullNameProperty());
        colStatus.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().statusProperty());

        loadFieldsIntoTable();

        SearchAnything.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                SearchAnything(newValue);
            } else
                groundStaffTable.setItems(masterData);
        });

        groundStaffTable.setOnMouseClicked(mouseEvent -> {
            GroundStaffTable selectedStaff = groundStaffTable.getSelectionModel().getSelectedItem();
            if (selectedStaff != null) {
                String fullName = selectedStaff.getFullName();
                int spaceIndex = fullName.indexOf(" ");
                int currentUserId = selectedStaff.getUserID();
                mobileField.setText(String.valueOf(selectedStaff.getMobile()));
                emailField.setText(selectedStaff.getEmail());
                employmentTypeCombo.setValue(selectedStaff.getEmploymentType());
                designationCombo.setValue(selectedStaff.getDesignation());
                salaryField.setText(getSalaryLoaded("" + currentUserId));
                firstNameField.setText(fullName.substring(0, spaceIndex));
                lastNameField.setText(fullName.substring(spaceIndex + 1));
                loadUsersImage(currentUserId);
            }
        });
    }


    private void SearchAnything(String newValue) {
        filtered.clear();
        String value = newValue.toLowerCase();
        for (GroundStaffTable records : masterData) {
            if (records.getFullName().toLowerCase().contains(value) || records.getStatus().toLowerCase().contains(value) || records.getEmploymentType().toLowerCase().contains(value) ||records.getEmail().toLowerCase().contains(value)||
                    records.getDesignation().toLowerCase().contains(value) || String.valueOf(records.getMobile()).contains(value) || String.valueOf(records.getUserID()).contains(value)) {
                filtered.add(records);
            }
        }
        groundStaffTable.setItems(filtered);

    }

    public String getSalaryLoaded(String userID) {
        String query = "SELECT Salary from Staffs where User_Id=" + userID;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString("Salary");
            }
        } catch (Exception e) {
            loadFrame.setMessage(errorMessageLabel, "Something went wrong : " + e.getMessage(),
                    "RED");
        }
        return "No Salary Assigned";
    }

    private void loadFieldsIntoTable() {
        errorMessageLabel.setText("");
        groundStaffTable.getItems().clear();
        obserVableList.clear();

        String query = "SELECT u.User_Id,s.Employment_Type, s.Designation,u.First_Name, u" +
                ".Last_Name, u.Email,u.Mobile,u.User_Status FROM Users u JOIN Staffs s ON u" +
                ".User_Id = s.User_Id WHERE u.Role='Staff'";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                long mobile = resultSet.getLong("Mobile");
                String employementType = resultSet.getString("Employment_Type");
                String email = resultSet.getString("Email");
                String Designation = resultSet.getString("Designation");
                String firstName = resultSet.getString("First_Name");
                String lastName = resultSet.getString("Last_Name");
                String userStatus = resultSet.getString("User_Status");

                GroundStaffTable groundStaff = new GroundStaffTable(resultSet.getInt(
                        "User_Id"), mobile,
                        Designation, email, employementType, firstName +
                        " " + lastName, userStatus);

                obserVableList.add(groundStaff);
            }
            groundStaffTable.setItems(obserVableList); // Set once after loop
        } catch (Exception e) {
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading fields into table: " + e.getMessage(),
                    "Something went wrong while loading student data.");
        }
    }

    @FXML
    void handleClear(ActionEvent event) {
        firstNameField.setText("");
        lastNameField.setText("");
        mobileField.setText("");
        emailField.setText("");
        salaryField.setText("");
        employmentTypeCombo.setValue(null);
        designationCombo.setValue(null);

        employmentTypeCombo.setPromptText("Select Employment type");
        designationCombo.setPromptText("Select Designation");

    }

    @FXML
    void handleDelete(ActionEvent event) {
        errorMessageLabel.setText("");
        GroundStaffTable selectedStaff = groundStaffTable.getSelectionModel().getSelectedItem();
        int userID = selectedStaff.getUserID();
        String query = "DELETE u, s FROM Users u JOIN Staffs s ON u.User_Id = s.User_Id WHERE s.User_Id = ?";
        if (userID == 0) {
            loadFrame.setMessage(errorMessageLabel, "Please selects Staffs to delete from " +
                    "table", "RED");
            return;
        }

        Optional<ButtonType> response = alertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "Delete ", "Do you wish " +
                "to delete Staff", "Selecting OK will delete the staffs from ther records . " +
                "Do you wish to continue");
        if (response.isPresent() || response.get() == ButtonType.OK) {
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userID);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    loadFrame.setMessage(errorMessageLabel, "Records deleted successfully", "GREEN");
                    groundStaffTable.getItems().remove(selectedStaff);
                }
            } catch (Exception ex) {
                loadFrame.setMessage(errorMessageLabel, "Something went wrong :" + ex.getMessage(), "RED");
            }
        }
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        groundStaffTable.getItems().clear();
        String query = "SELECT * FROM Users u JOIN Staffs s ON s.User_Id=u.User_Id where u.Role='Staff'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet resultSet = pstmt.executeQuery();
            {
                while (resultSet.next()) {
                    int userId = resultSet.getInt("User_Id");
                    long mobile = resultSet.getLong("Mobile");
                    String employementType = resultSet.getString("Employment_Type");
                    String email = resultSet.getString("Email");
                    String Designation = resultSet.getString("Designation");
                    String firstName = resultSet.getString("First_Name");
                    String lastName = resultSet.getString("Last_Name");
                    String userStatus = resultSet.getString("User_Status");

                    GroundStaffTable groundStaff = new GroundStaffTable(userId, mobile,
                            Designation, email, employementType, firstName +
                            " " + lastName, userStatus);

                    obserVableList.add(groundStaff);
                }

            }

            groundStaffTable.setItems(obserVableList); // Set once after loop

        } catch (
                Exception e) {
            loadFrame.setMessage(errorMessageLabel, "Something went wrong " + e.getMessage(), "RED");
        }
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        errorMessageLabel.setText("");
        GroundStaffTable selectedStaff = groundStaffTable.getSelectionModel().getSelectedItem();
        if (selectedStaff == null) {
            loadFrame.setMessage(errorMessageLabel, "Select a data from the table to update.", "RED");
            return;
        }
        String userID = String.valueOf(selectedStaff.getUserID());
        String query = "UPDATE Users u JOIN Staffs s ON u.User_Id = s.User_Id SET u.First_Name = ?, u.Last_Name = ?, u.Mobile = ?, u.Email = ?, " +
                "s.Salary = ?, s.Designation = ?, s.Employment_Type = ? WHERE u.User_Id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, firstNameField.getText());
            pstmt.setString(2, lastNameField.getText());
            pstmt.setString(3, mobileField.getText());
            pstmt.setString(4, emailField.getText());
            pstmt.setDouble(5, Double.parseDouble(salaryField.getText()));  // or pstmt.setString if it's a string
            pstmt.setString(6, designationCombo.getValue());
            pstmt.setString(7, employmentTypeCombo.getValue());
            pstmt.setString(8, userID);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                handleRefresh(event);
                loadFrame.setMessage(errorMessageLabel, "Updated Successfully", "GREEN");
            } else {
                loadFrame.setMessage(errorMessageLabel, "No rows were updated.", "ORANGE");
            }
        } catch (Exception e) {
            loadFrame.setMessage(errorMessageLabel, "Update failed: " + e.getMessage(), "RED");
            e.printStackTrace();
        }
    }

    private void loadUsersImage(int currentUserId) {
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                String imageQuery = "SELECT Photo_URL FROM Users WHERE User_Id=?";
                try (PreparedStatement stmt = connection.prepareStatement(imageQuery)) {
                    stmt.setInt(1, currentUserId);
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
            } else {
                Image defaultImage = new Image(getClass().getResourceAsStream("/org/example/complete_ums/Images/UserName.png"));
                usersImage.setImage(defaultImage);
            }
        });

        loadImageTask.setOnFailed(event -> {
            Throwable e = loadImageTask.getException();
            alertManager.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load profile image",
                    "An error occurred while retrieving the image from the database." + e.getMessage());
        });

        new Thread(loadImageTask).start();
    }

    public void uploadUsersImage(ActionEvent actionEvent) {
        GroundStaffTable selectedStaf = groundStaffTable.getSelectionModel().getSelectedItem();
        if (selectedStaf == null) {
            loadFrame.setMessage(errorMessageLabel, "Please Select Staff from Table to " +
                    "initiate Uploading photos for Staff", "RED");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload your Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            usersImage.setImage(new Image(file.toURI().toString()));
            int userID = selectedStaf.getUserID();
            String updateQuery = "UPDATE Users SET Photo_URL=? WHERE User_Id=?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                pstmt.setBinaryStream(1, new FileInputStream(selectedImageFile));
                pstmt.setInt(2, userID);

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
