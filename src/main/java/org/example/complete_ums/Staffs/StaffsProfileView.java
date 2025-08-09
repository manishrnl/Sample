package org.example.complete_ums.Staffs;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class StaffsProfileView implements Initializable {

    SessionManager sessionManager = SessionManager.getInstance();
    Connection connection = DatabaseConnection.getConnection();

    @FXML
    private ImageView usersImage;
    @FXML
    private Label userIdLabel, roleLabel, statusLabel, joinedOnLabel, updatedOnLabel,
            approvalLabel, fullNameHeader, userNameHeader, idHeader;
    @FXML
    private Label firstNameLabel, lastNameLabel, aadharLabel, panLabel;
    @FXML
    private Label mobileLabel, alternateMobileLabel, emailLabel, genderLabel, dobLabel;
    @FXML
    private Label bloodGroupLabel, maritalStatusLabel, nationalityLabel;
    @FXML
    private Label fatherNameLabel, motherNameLabel, tempAddressLabel, permAddressLabel;
    @FXML
    private Label emergencyNameLabel, emergencyRelationLabel, emergencyMobileLabel;
    @FXML
    private Label registrationLabel, designationLabel, departmentLabel, employmentTypeLabel, experienceLabel, salaryLabel;

    int userIdLoggedIn = sessionManager.getUserID();

    public StaffsProfileView() throws SQLException {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadStaffProfile(userIdLoggedIn);
        loadUsersImage(userIdLoggedIn);
        System.out.println("inside initialised");
    }

    private void loadUsersImage(int userIdLoggedIn) {
        String query = "SELECT Photo_URL FROM Users WHERE User_Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userIdLoggedIn);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                InputStream inputStream = resultSet.getBinaryStream("Photo_URL");

                if (inputStream != null && inputStream.available() > 0) {
                    Image image = new Image(inputStream);
                    usersImage.setImage(image);
                } else {
                    setDefaultImage();
                }
            } else {
                setDefaultImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setDefaultImage(); // Show default image on failure
        }
    }

    // Helper method to set default image
    private void setDefaultImage() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/org/example/complete_ums/Images/UserName.png"));
            usersImage.setImage(defaultImage);
        } catch (Exception ex) {
            System.err.println("Default image could not be loaded.");
            ex.printStackTrace();
        }
    }


    public void loadStaffProfile(int userId) {
        String usersQ = "SELECT * FROM Users WHERE User_Id=?";
        String staffsQ = "SELECT * FROM Staffs WHERE User_Id=?";


        try (PreparedStatement st1 = connection.prepareStatement(usersQ)) {
            st1.setInt(1, userId);
            ResultSet rs1 = st1.executeQuery();
            if (rs1.next()) {
                userIdLabel.setText(String.valueOf(rs1.getInt("User_Id")));
                roleLabel.setText(rs1.getString("Role"));
                statusLabel.setText(rs1.getString("User_Status"));
                joinedOnLabel.setText(rs1.getTimestamp("Joined_On").toString());
                updatedOnLabel.setText(rs1.getTimestamp("Updated_On").toString());
                approvalLabel.setText(rs1.getString("Admin_Approval_Status"));
                firstNameLabel.setText(rs1.getString("First_Name"));
                lastNameLabel.setText(rs1.getString("Last_Name"));
                aadharLabel.setText(rs1.getString("Aadhar"));
                panLabel.setText(rs1.getString("Pan"));
                mobileLabel.setText(rs1.getString("Mobile"));
                alternateMobileLabel.setText(rs1.getString("Alternate_Mobile"));
                emailLabel.setText(rs1.getString("Email"));
                genderLabel.setText(rs1.getString("Gender"));
                dobLabel.setText(rs1.getDate("DOB").toString());
                bloodGroupLabel.setText(rs1.getString("Blood_Group"));
                maritalStatusLabel.setText(rs1.getString("Marital_Status"));
                nationalityLabel.setText(rs1.getString("Nationality"));
                fatherNameLabel.setText(rs1.getString("Fathers_Name"));
                motherNameLabel.setText(rs1.getString("Mothers_Name"));
                tempAddressLabel.setText(rs1.getString("Temporary_Address"));
                permAddressLabel.setText(rs1.getString("Permanent_Address"));
                emergencyNameLabel.setText(rs1.getString("Emergency_Contact_Name"));
                emergencyRelationLabel.setText(rs1.getString("Emergency_Contact_Relationship"));
                emergencyMobileLabel.setText(rs1.getString("Emergency_Contact_Mobile"));


                fullNameHeader.setText("Name : " + sessionManager.getFullName());
                idHeader.setText("User ID: " + userIdLoggedIn);
                userNameHeader.setText("User Name : " + sessionManager.getUserName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement st2 = connection.prepareStatement(staffsQ)) {
            st2.setInt(1, userId);
            ResultSet rs2 = st2.executeQuery();
            if (rs2.next()) {
                registrationLabel.setText(rs2.getString("Registration_Number"));
                designationLabel.setText(rs2.getString("Designation"));
                departmentLabel.setText(rs2.getString("Department_Id"));
                employmentTypeLabel.setText(rs2.getString("Employment_Type"));
                experienceLabel.setText(String.valueOf(rs2.getInt("Work_Experience_Years")));
                salaryLabel.setText(rs2.getBigDecimal("Salary").toPlainString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}


