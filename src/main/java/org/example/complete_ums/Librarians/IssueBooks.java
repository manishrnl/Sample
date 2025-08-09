package org.example.complete_ums.Librarians;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.complete_ums.Databases.AdminActivityLogs;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class IssueBooks implements Initializable {


    public IssueBooks() throws SQLException {
    }

    AdminActivityLogs adminActivityLogs = new AdminActivityLogs();
    LoadFrame loadFrame;
    SessionManager sessionManager = SessionManager.getInstance();
    Connection connection = DatabaseConnection.getConnection();
    int userIDLoggedIn = sessionManager.getUserID();
    @FXML
    private TextField studentIdentifierField, bookIdentifierField, librarianIdField,
            studentsName, bookTitle, availableCopiesField;
    @FXML
    private Button searchButton, issueButton, cancelButton;
    @FXML
    private DatePicker borrowDatePicker, dueDatePicker;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private TextArea remarksArea;
    @FXML
    private ImageView usersImage;

    int availableCopies = 0;
    String numberRegex = "\\d+";
    Boolean foundBooks = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        librarianIdField.setEditable(false);
        librarianIdField.setText(String.valueOf(userIDLoggedIn));
        borrowDatePicker.setValue(LocalDate.now());
        dueDatePicker.setValue(LocalDate.now().plusMonths(1));
        remarksArea.setText("Books issued on " + LocalDate.now() + " in a neat and Clean " +
                "Condition");
        bookIdentifierField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.matches(numberRegex)) {
                int bookID = 0;
                try {
                    bookID = Integer.parseInt(newValue);
                } catch (NumberFormatException e) {
                    loadFrame.setMessage(errorMessageLabel, "Please enter details in number",
                            "RED");
                }
                if (!handleSearchBooks(bookID)) {
                    bookTitle.clear();
                    availableCopiesField.clear();
                    loadFrame.setMessage(errorMessageLabel, "No Books Found with this ID , Please try again", "RED");

                }
            }
        });
        studentIdentifierField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.matches(numberRegex)) {
                int userID = 0;
                try {
                    userID = Integer.parseInt(newValue);
                } catch (NumberFormatException e) {
                    loadFrame.setMessage(errorMessageLabel, "Please enter details in number",
                            "RED");
                }
                if (!handleSearchStudents(userID)) {
                    studentsName.clear();
                    loadFrame.setMessage(errorMessageLabel, "No Users Found with this ID , " +
                            "Please try again", "RED");
                } else
                    loadUsersImage(userID);
            }

        });

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
            loadFrame.setMessage(errorMessageLabel, "Something went wrong while loading the " +
                    "profile image: " + ex.getMessage(), "RED");
        }
    }

    public Boolean handleSearchStudents(int userID) {
        String query = "SELECT CONCAT(First_Name, ' ', Last_Name) AS Full_Name FROM Users " +
                "WHERE User_Id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userID);
            var resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                String fullName = resultSet.getString("Full_Name");
                studentsName.setText(fullName);
                loadFrame.setMessage(errorMessageLabel, "User found: " + fullName, "GREEN");
                return true;
            }
        } catch (SQLException e) {
            loadFrame.setMessage(errorMessageLabel, "Database error: " + e.getMessage(), "RED");
        } catch (Exception e) {
            loadFrame.setMessage(errorMessageLabel, "Something went wrong while fetching  " +
                    "details from Database" + e.getMessage(), "RED");
        }
        return false;
    }


    public Boolean handleSearchBooks(int bookID) {

        String query = "SELECT Available_Copies,Title from Books where Book_Id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, bookID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {

                availableCopies = rs.getInt("Available_Copies");
                availableCopiesField.setText(rs.getString("Available_Copies"));
                bookTitle.setText(rs.getString("Title"));
                loadFrame.setMessage(errorMessageLabel, "Books found: ", "GREEN");
                return true;
            }
        } catch (Exception e) {
            loadFrame.setMessage(errorMessageLabel, "Something went wrong while fetching " +
                    "Book Details" + e.getMessage(), "RED");
        }
        return false;
    }


    @FXML
    void handleCancel(ActionEvent event) {

    }

    @FXML
    void handleIssueBook(ActionEvent event) {
        String name = studentsName.getText();

        String borrowDateText = borrowDatePicker.getValue() != null ? borrowDatePicker.getValue().toString() : "";
        String dueDateText = dueDatePicker.getValue() != null ? dueDatePicker.getValue().toString() : "";
        int librarianId = userIDLoggedIn;
        String studentIdText = studentIdentifierField.getText();
        String bookIdtext = bookIdentifierField.getText();
        int studentId = 0, bookID = 0;

        if (borrowDateText.isEmpty() || dueDateText.isEmpty() || librarianId == 0 || name.isEmpty() || studentIdText.isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Please enter All details to proceed",
                    "RED");
            return;
        }

        try {
            studentId = Integer.parseInt(studentIdText);
            bookID = Integer.parseInt(bookIdtext);
        } catch (NumberFormatException ex) {
            loadFrame.setMessage(errorMessageLabel, "Please enter a valid Book ID ,Users " +
                    "ID in Numbers only.", "RED");
            return;
        }
        if (dueDatePicker.getValue() == null || borrowDatePicker.getValue() == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select both Borrow and Due dates.",
                    "RED");
            return;
        }
        if (availableCopies <= 0) {
            loadFrame.setMessage(errorMessageLabel, "Can not issue books due to its non " +
                    "Availability .", "RED");
            return;
        }

        String booksQuery = "update Books set Available_Copies = Available_Copies - 1 where Book_Id = ?";
        String borrowQuery = "INSERT INTO Borrow_Records (Book_Id, User_Id, Borrow_Date, " +
                "Due_Date ,Return_Date,Librarian_User_Id  ,Remarks ) VALUES (?, ?, ?, ?, NULL, ?, ?)";

        try (PreparedStatement booksPstmt = connection.prepareStatement(booksQuery);
             PreparedStatement borrowPstmt = connection.prepareStatement(borrowQuery)) {

            booksPstmt.setInt(1, bookID);
            int rowsAffected = booksPstmt.executeUpdate();

            borrowPstmt.setInt(1, bookID);
            borrowPstmt.setInt(2, studentId);
            borrowPstmt.setString(3, borrowDateText);
            borrowPstmt.setString(4, dueDateText);
            borrowPstmt.setInt(5, librarianId);
            borrowPstmt.setString(6, remarksArea.getText());
            int borrowRowsAffected = borrowPstmt.executeUpdate();
            if (rowsAffected > 0 && borrowRowsAffected > 0) {

                adminActivityLogs.insertAdminLogsData(errorMessageLabel, 8, userIDLoggedIn, "UPDATE",
                        "Books", "Books were issued by :" + userIDLoggedIn);
                loadFrame.setMessage(errorMessageLabel, "Book issued successfully to " + name, "GREEN");
                availableCopiesField.setText(String.valueOf(availableCopies - 1));
                clearField();
            } else {
                loadFrame.setMessage(errorMessageLabel, "Failed to issue book. Please try again.", "RED");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void clearField() {
        studentIdentifierField.clear();
        bookIdentifierField.clear();
        studentsName.clear();
        availableCopiesField.clear();
        bookTitle.clear();
        usersImage.setImage(new Image(getClass().getResourceAsStream("/org/example/complete_ums/Images/UserName.png")));
    }

}
