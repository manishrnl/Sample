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

public class UpdateBooks implements Initializable {
    public UpdateBooks() throws SQLException {
    }

    Button3DEffect button3DEffect;
    LoadFrame loadFrame;
    SessionManager sessionManager = SessionManager.getInstance();
    Connection connection = DatabaseConnection.getConnection();
    AdminActivityLogs adminActivityLogs = new AdminActivityLogs();

    @FXML
    private Button updateButton, cancelButton, btnSearch;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField genreField, isbnField, locationShelfField, newCopiesField,
            totalCopiesLeft,
            publicationYearField, publisherField, titleField, authorField, bookIdField;
    @FXML
    private Label errorMessageLabel;
    int userIDLoggedIn = sessionManager.getUserID();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button3DEffect.applyEffect(updateButton, "/sound/hover.mp3");
        button3DEffect.applyEffect(cancelButton, "/sound/hover.mp3");
        button3DEffect.applyEffect(btnSearch, "/sound/hover.mp3");
    }

    @FXML
    void handleCancel(ActionEvent event) {
        bookIdField.setDisable(false);

    }


    public void handleUpdateBook(ActionEvent event) throws SQLException {
        errorMessageLabel.setText("");
        String yearRegex = "\\d{4}";
        String numberRegex = "\\d+";

        String bookIDText = bookIdField.getText();
        String title = titleField.getText();
        String author = authorField.getText();
        String publisher = publisherField.getText();
        String isbn = isbnField.getText();
        String genre = genreField.getText();
        String publicationYearText = publicationYearField.getText();
        String newCopiesText = newCopiesField.getText();
        String locationShelf = locationShelfField.getText();
        String description = descriptionArea.getText();
        String copiesLeftText = totalCopiesLeft.getText();

        if (bookIDText.isEmpty() || title.isEmpty() || author.isEmpty() || publisher.isEmpty()
                || isbn.isEmpty() || genre.isEmpty() || publicationYearText.isEmpty()
                || locationShelf.isEmpty() || newCopiesText.isEmpty() || description.isEmpty()
                || copiesLeftText.isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Please ensure all fields are filled out.", "RED");
            return;
        }

        if (!publicationYearText.matches(yearRegex)) {
            loadFrame.setMessage(errorMessageLabel, "Please enter a 4-digit numerical value for Publication Year.", "RED");
            return;
        }

        if (!newCopiesText.matches(numberRegex)) {
            loadFrame.setMessage(errorMessageLabel, "Please enter a numerical value (digits only) for New Copies.", "RED");
            return;
        }

        if (!copiesLeftText.matches(numberRegex)) {
            loadFrame.setMessage(errorMessageLabel, "Internal Error: Existing copies value is invalid.", "RED");
            return;
        }

        int bookID, publicationYear, newCopies, copiesLeft;
        try {
            bookID = Integer.parseInt(bookIDText);
            publicationYear = Integer.parseInt(publicationYearText);
            newCopies = Integer.parseInt(newCopiesText);
            copiesLeft = Integer.parseInt(copiesLeftText);
        } catch (NumberFormatException ex) {
            loadFrame.setMessage(errorMessageLabel, "Error parsing numerical data. Please ensure all numerical fields contain valid digits.", "RED");
            return;
        }
        int totalCopies = getTotalCopies(bookID);


        String query = "UPDATE Books SET Title=?, Author=?, Publisher=?, ISBN=?, Genre=?, " +
                "Publication_Year=?, Location_Shelf=?, Total_Copies=?, Description=?, Available_Copies=? " +
                "WHERE Book_Id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, publisher);
            pstmt.setString(4, isbn);
            pstmt.setString(5, genre);
            pstmt.setInt(6, publicationYear);
            pstmt.setString(7, locationShelf);
            pstmt.setInt(8, (totalCopies + newCopies));
            pstmt.setString(9, description);
            pstmt.setInt(10, (newCopies + copiesLeft));
            pstmt.setInt(11, bookID);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                adminActivityLogs.insertAdminLogsData(errorMessageLabel, 8, userIDLoggedIn, "UPDATE",
                        "Books", "More Books were Added into Stocks by :" + userIDLoggedIn);
                clearFields(false);
                loadFrame.setMessage(errorMessageLabel, "Book details updated successfully!", "GREEN");
            } else {
                loadFrame.setMessage(errorMessageLabel, "No book found with ID: " + bookID + ". No updates were made.", "YELLOW");
            }
        } catch (SQLException e) {
            loadFrame.setMessage(errorMessageLabel, "Database error: " + e.getMessage(), "RED");
            e.printStackTrace();
        }
    }

    private int getTotalCopies(int bookID) throws SQLException {
        String query = "SELECT Total_Copies from Books where Book_Id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, bookID);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt("Total_Copies");
            }
        }
        return 0;
    }


    public void searchBookID(ActionEvent event) {
        errorMessageLabel.setText("");
        String bookIDText = bookIdField.getText();

        // 1. Validate the book ID input
        if (bookIDText.isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Please Enter Book ID to search.", "RED");
            return;
        }

        int bookID;
        try {
            bookID = Integer.parseInt(bookIDText);
        } catch (NumberFormatException ex) {
            loadFrame.setMessage(errorMessageLabel, "Book ID must be a numerical value only.", "RED");
            return;
        }

        // 2. Use a SELECT statement to retrieve book details
        String query = "SELECT Title, Author, Publisher, ISBN, Genre, Publication_Year, Location_Shelf, Total_Copies, Available_Copies, Description FROM Books WHERE Book_Id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, bookID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    titleField.setText(rs.getString("Title"));
                    authorField.setText(rs.getString("Author"));
                    publisherField.setText(rs.getString("Publisher"));
                    isbnField.setText(rs.getString("ISBN"));
                    genreField.setText(rs.getString("Genre"));
                    publicationYearField.setText(rs.getString("Publication_Year"));
                    locationShelfField.setText(rs.getString("Location_Shelf"));
                    totalCopiesLeft.setText(String.valueOf(rs.getInt("Available_Copies")));
                    descriptionArea.setText(rs.getString("Description"));
                    loadFrame.setMessage(errorMessageLabel, "Book details loaded successfully.", "GREEN");

                } else {
                    // 4. Handle case where no book is found
                    loadFrame.setMessage(errorMessageLabel, "No book found with the provided Book ID.", "RED");
                    clearFields(true);

                }
            }
        } catch (SQLException e) {
            // 5. Handle database-related errors
            loadFrame.setMessage(errorMessageLabel, "Database Error: " + e.getMessage(), "RED");
            e.printStackTrace();
        }
    }

    private void clearFields(boolean DisableBookID) {
        bookIdField.setDisable(DisableBookID);
        newCopiesField.clear();
        titleField.clear();
        authorField.clear();
        publisherField.clear();
        isbnField.clear();
        genreField.clear();
        publicationYearField.clear();
        locationShelfField.clear();
        totalCopiesLeft.setText("");
        descriptionArea.clear();
    }


}
