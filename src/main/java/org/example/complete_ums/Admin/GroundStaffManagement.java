package org.example.complete_ums.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.complete_ums.CommonTable.GroundStaffTable;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.LoadFrame;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class GroundStaffManagement implements Initializable {
    public GroundStaffManagement() throws SQLException {
    }

    AlertManager alertManager = new AlertManager();
    LoadFrame loadFrame = new LoadFrame();
    Connection connection = DatabaseConnection.getConnection();
    @FXML
    private TextField searchByNameField, searchByMobileField, searchByAadharField, searchByUsernameField, searchByEmailField, searchByPanField;
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
    private TableView<GroundStaffTable> staffTable;
    List<Integer> searchResultID = new ArrayList<>();
    ObservableList<GroundStaffTable> obserVableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        handleRefresh(new ActionEvent());
        colUserId.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().userIDProperty().asObject());
        colMobile.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().mobileProperty().asObject());
        colDesignation.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().designationProperty());
        colEmail.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().emailProperty());
        colEmploymentType.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().employmentTypeProperty());
        colFullName.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().fullNameProperty());
        colStatus.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().statusProperty());

        searchByNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                clearTextField(false, true, true, true, true, true);
                String query = "SELECT User_Id FROM Users WHERE CONCAT(First_Name, ' ', " +
                        "Last_Name) LIKE ?";
                List<Integer> resultIDs = SearchData(newValue, query);
                if (resultIDs != null && !resultIDs.isEmpty()) {
                    LoadFieldsIntoTable(resultIDs);
                } else {
                    staffTable.getItems().clear(); // Clear table if no result
                }
            } else {
                staffTable.getItems().clear(); // Clear table when input is empty
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
                    staffTable.getItems().clear(); // Clear table if no result
                }
            } else {
                staffTable.getItems().clear(); // Clear table when input is empty
            }
        });
        searchByEmailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                clearTextField(true, true, true, true, false, true);
                String query = "SELECT * from Users WHERE Email Like ? ";
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

        staffTable.setOnMouseClicked(mouseEvent -> {
            GroundStaffTable selectedStaff = staffTable.getSelectionModel().getSelectedItem();
            if (selectedStaff != null) {
                String fullName = selectedStaff.getFullName();
                int spaceIndex = fullName.indexOf(" ");
                String userID = String.valueOf(selectedStaff.getUserID());
                mobileField.setText(String.valueOf(selectedStaff.getMobile()));
                emailField.setText(selectedStaff.getEmail());
                employmentTypeCombo.setValue(selectedStaff.getEmploymentType());
                designationCombo.setValue(selectedStaff.getDesignation());
                salaryField.setText(getSalaryLoaded(userID));
                firstNameField.setText(fullName.substring(0, spaceIndex));
                lastNameField.setText(fullName.substring(spaceIndex + 1));
            }
        });
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
        staffTable.getItems().clear();
        obserVableList.clear();

        String query = "SELECT u.User_Id,s.Employment_Type, s.Designation,u.First_Name, u" +
                ".Last_Name, u.Email,u.Mobile,u.User_Status FROM Users u JOIN Staffs s ON u" +
                ".User_Id = s.User_Id WHERE u.User_Id = ? AND u.Role='Staff'";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            for (int userId : searchResultID) {
                pstmt.setInt(1, userId);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    while (resultSet.next()) {
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
            }

            staffTable.setItems(obserVableList); // Set once after loop

        } catch (Exception e) {
            alertManager.showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "Error loading fields into table: " + e.getMessage(),
                    "Something went wrong while loading student data."
            );
        }
    }

    private void clearTextField(Boolean clearName, Boolean clearUserName, Boolean clearPan, Boolean clearAadhar, Boolean clearEmail, Boolean clearMobile) {
        errorMessageLabel.setText("");
        if (clearName) searchByNameField.clear();
        if (clearPan) searchByPanField.clear();
        if (clearAadhar) searchByAadharField.clear();
        if (clearEmail) searchByEmailField.clear();
        if (clearMobile) searchByMobileField.clear();
        if (clearUserName) searchByUsernameField.clear();

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
        GroundStaffTable selectedStaff = staffTable.getSelectionModel().getSelectedItem();
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
                    staffTable.getItems().remove(selectedStaff);
                }
            } catch (Exception ex) {
                loadFrame.setMessage(errorMessageLabel, "Something went wrong :" + ex.getMessage(), "RED");
            }
        }
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        staffTable.getItems().clear();
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

            staffTable.setItems(obserVableList); // Set once after loop

        } catch (
                Exception e) {
            loadFrame.setMessage(errorMessageLabel, "Something went wrong " + e.getMessage(), "RED");
        }
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        errorMessageLabel.setText("");
        GroundStaffTable selectedStaff = staffTable.getSelectionModel().getSelectedItem();
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

}
