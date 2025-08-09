package org.example.complete_ums.Librarians;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.complete_ums.Databases.AdminActivityLogs;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddBooks implements Initializable {
    public AddBooks() throws SQLException {
    }

    AdminActivityLogs adminActivityLogs = new AdminActivityLogs();
    Button3DEffect button3DEffect;
    LoadFrame loadFrame;
    SessionManager sessionManager = SessionManager.getInstance();
    Connection connection = DatabaseConnection.getConnection();
    @FXML
    private Button saveButton, cancelButton;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private TextField authorField, genreField, isbnField, locationShelfField,
            publicationYearField, publisherField, titleField, totalCopiesField;
    int userIDLoggedIn = sessionManager.getUserID();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button3DEffect.applyEffect(saveButton, "/sound/hover.mp3");
        button3DEffect.applyEffect(cancelButton, "/sound/hover.mp3");

    }


    @FXML
    void handleCancel(ActionEvent event) {

    }

    @FXML
    void handleSaveBook(ActionEvent event) {
        String numberRegex = "\\d";
        String yearRegex = "\\d{4}";
        errorMessageLabel.setText("");
        String title = titleField.getText();
        String author = authorField.getText();
        String publisher = publisherField.getText();
        String isbn = isbnField.getText();
        String genre = genreField.getText();
        String publicationYearText = publicationYearField.getText();
        String totalCopiesText = totalCopiesField.getText();
        String locationShelf = locationShelfField.getText();
        int publicationYear = 0, totalCopies = 0;
        String description = descriptionArea.getText();

        // Check for empty or null fields
        if (title.isEmpty() || author.isEmpty() || publisher.isEmpty() || isbn.isEmpty() || genre.isEmpty()
                || publicationYearText.isEmpty() || locationShelf.isEmpty() || totalCopiesText.isEmpty()
                || description.isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Please fill in all fields.", "RED");
            return;
        }
        if (!publicationYearField.getText().matches(yearRegex)) {
            loadFrame.setMessage(errorMessageLabel, "Please Enter Numerical Value ( 4 Digits ) in Publication year  Fields", "RED");
            return;
        }
        if (!totalCopiesField.getText().matches(numberRegex)) {
            loadFrame.setMessage(errorMessageLabel, "Please Enter Numerical Value (Digits Only) in Available Copies Fields", "RED");
            return;
        }

        try {
            publicationYear = Integer.parseInt(publicationYearField.getText());
            totalCopies = Integer.parseInt(totalCopiesField.getText());
        } catch (NumberFormatException ex) {
            loadFrame.setMessage(errorMessageLabel, "Only Digits allowed in publication Year and total Copies Field :" + ex.getMessage(), "RED");
        }
        String query = "insert into Books (Title, Author, Publisher, ISBN, Genre, " +
                "Publication_Year, Location_Shelf, Total_Copies, Description," +
                "Available_Copies) values (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, publisher);
            pstmt.setString(4, isbn);
            pstmt.setString(5, genre);
            pstmt.setInt(6, publicationYear);
            pstmt.setString(7, locationShelf);
            pstmt.setInt(8, totalCopies);
            pstmt.setString(9, description);
            pstmt.setInt(10, totalCopies);
            int rs = pstmt.executeUpdate();
            if (rs > 0) {
                adminActivityLogs.insertAdminLogsData(errorMessageLabel, 8,userIDLoggedIn, "INSERT", "Books", "New Books were Added" +
                                " by userID :" + userIDLoggedIn);
                loadFrame.setMessage(errorMessageLabel, "Book added successfully!", "GREEN");
            }
        } catch (Exception e) {
            loadFrame.setMessage(errorMessageLabel, "Some exception occured while saving to " +
                    "database :" + e.getMessage(), "RED");
        }


    }

}
