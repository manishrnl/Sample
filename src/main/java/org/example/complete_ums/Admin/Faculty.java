package org.example.complete_ums.Admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.complete_ums.CommonTable.FacultyMember;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Theme_Manager;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Faculty implements Initializable {
    Theme_Manager themeManager = new Theme_Manager();
    Connection connection = DatabaseConnection.getConnection();
    @FXML
    private GridPane facultyGrid;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortComboBox, filterComboBox;

    private List<FacultyMember> allFacultyMembers;

    public Faculty() throws SQLException {
    }

    String currentTheme = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        allFacultyMembers = new ArrayList<>();
        populateFacultyCards();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAndDisplayUsers(newValue);
        });
        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterAndDisplayUsers(searchField.getText());
        });
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterAndDisplayUsers(searchField.getText());
        });

        // Populate filter and sort combo boxes
        filterComboBox.getItems().addAll("All", "Teacher", "Librarian", "Accountant", "Staff", "Admin");
        filterComboBox.getSelectionModel().select("All");
        sortComboBox.getItems().addAll("Name (A-Z)", "Name (Z-A)", "Joined Date (Newest)", "Joined Date (Oldest)");
        sortComboBox.getSelectionModel().select("Name (A-Z)");
    }

    private void populateFacultyCards() {
        allFacultyMembers = getFacultyMembersFromDatabase();
        displayFacultyCards(allFacultyMembers);
    }

    private List<FacultyMember> getFacultyMembersFromDatabase() {
        String designation = null;
        List<FacultyMember> facultyList = new ArrayList<>();
        String sql = "SELECT u.User_Id, u.First_Name, u.Last_Name, u.Role, u.Photo_URL, t.Designation AS Teacher_Designation, lb.Designation AS Librarian_Designation, a.Designation AS Admin_Designation, s.Designation AS Staff_Designation, ac.Designation AS Accountant_Designation FROM Users u LEFT JOIN Teachers t ON u.User_Id = t.User_Id LEFT JOIN Librarians lb ON u.User_Id = lb.User_Id LEFT JOIN Admins a ON u.User_Id = a.User_Id LEFT JOIN Staffs s ON u.User_Id = s.User_Id LEFT JOIN Accountants ac ON u.User_Id = ac.User_Id WHERE u.Role IN ('Teacher', 'Librarian', 'Accountant', 'Admin', 'Staff')";

        try (
                PreparedStatement pstmt = connection.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                String userId = String.valueOf(rs.getInt("User_Id"));
                String firstName = rs.getString("First_Name");
                String lastName = rs.getString("Last_Name");
                String role = rs.getString("Role");
                if (role.equals("Teacher")) {
                    designation = rs.getString("Teacher_Designation");
                } else if (role.equals("Librarian")) {
                    designation = rs.getString("Librarian_Designation");
                } else if (role.equals("Accountant")) {
                    designation = rs.getString("Accountant_Designation");
                } else if (role.equals("Admin")) {
                    designation = rs.getString("Admin_Designation");
                } else if (role.equals("Staff")) {
                    designation = rs.getString("Staff_Designation");
                }

                String photoUrl = null;
                facultyList.add(new FacultyMember(userId, firstName, lastName, role,
                        designation, photoUrl));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return facultyList;
    }

    private void displayFacultyCards(List<FacultyMember> facultyList) {
        facultyGrid.getChildren().clear(); // Clear existing cards
        int column = 0;
        int row = 0;
        for (FacultyMember member : facultyList) {
            VBox card = createFacultyCard(member);
            facultyGrid.add(card, column, row);
            column++;
            if (column == 6) { // 6 cards per row
                column = 0;
                row++;
            }
        }
    }

    private VBox createFacultyCard(FacultyMember member) {
        VBox card = new VBox(10);
        card.setPrefSize(200, 250);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setAlignment(javafx.geometry.Pos.CENTER);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        byte[] imageData = getPhotoBlob(member.getUserId());
        if (imageData != null) {
            imageView.setImage(new Image(new ByteArrayInputStream(imageData)));
        } else {
            imageView.setImage(new Image(getClass().getResourceAsStream("/org/example/complete_ums/Images/UserName.png")));
        }

        Label nameLabel = new Label(member.getFirstName() + " " + member.getLastName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label roleLabel = new Label("Role: " + member.getRole());
        roleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        Label designationLabel = new Label(member.getDesignation());
        designationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        Label idLabel = new Label("ID: " + member.getUserId());
        idLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        card.getChildren().addAll(imageView, nameLabel, designationLabel, roleLabel, idLabel);
        return card;
    }

    private byte[] getPhotoBlob(String userId) {
        String sql = "SELECT Photo_URL FROM Users WHERE User_Id = ?";
        try (
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Blob blob = rs.getBlob("Photo_URL");
                if (blob != null) {
                    return blob.getBytes(1, (int) blob.length());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching image for User ID " + userId + ": " + e.getMessage());
        }
        return null;
    }

    private void filterAndDisplayUsers(String searchText) {
        String filterType = filterComboBox.getSelectionModel().getSelectedItem();
        String sortType = sortComboBox.getSelectionModel().getSelectedItem();

        // Step 1: Filter
        List<FacultyMember> filteredList = new ArrayList<>();
        for (FacultyMember member : allFacultyMembers) {
            boolean matchesSearch = (searchText == null || searchText.isEmpty() ||
                    member.getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
                    member.getLastName().toLowerCase().contains(searchText.toLowerCase()) ||
                    member.getUserId().toLowerCase().contains(searchText.toLowerCase()));
            boolean matchesFilter = (filterType == null || filterType.equals("All") ||
                    member.getRole().equalsIgnoreCase(filterType));

            if (matchesSearch && matchesFilter) {
                filteredList.add(member);
            }
        }

        // Step 2: Sort
        // Sorting logic based on sortType (e.g., using a Comparator)
        if (sortType != null) {
            switch (sortType) {
                case "Name (A-Z)":
                    filteredList.sort((m1, m2) -> (m1.getFirstName() + m1.getLastName()).compareTo(m2.getFirstName() + m2.getLastName()));
                    break;
                case "Name (Z-A)":
                    filteredList.sort((m1, m2) -> (m2.getFirstName() + m2.getLastName()).compareTo(m1.getFirstName() + m1.getLastName()));
                    break;
                // Add logic for other sorting types like joined date if available in your data model
            }
        }

        // Step 3: Display
        displayFacultyCards(filteredList);
    }
}