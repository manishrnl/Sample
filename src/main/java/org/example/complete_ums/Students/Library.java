package org.example.complete_ums.Students;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.complete_ums.CommonTable.BorrowedBooksTable;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Library implements Initializable {
    LoadFrame loadFrame = new LoadFrame();
    SessionManager sessionManager = SessionManager.getInstance();
    Connection connection = DatabaseConnection.getConnection();
    @FXML
    private TableColumn<BorrowedBooksTable, LocalDate> returnDateCol, dueDateCol, borrowDateCol;
    @FXML
    private TableColumn<BorrowedBooksTable, Double> fineCol;
    @FXML
    private TableView<BorrowedBooksTable> borrowedBooksTable;
    @FXML
    private TableColumn<BorrowedBooksTable, Integer> bookIdCol;
    @FXML
    private TableColumn<BorrowedBooksTable, String> statusCol, bookNameCol, borrowedRemarkseCol;

    @FXML
    private Label errorMessageLabel;
    @FXML
    private TextField searchField;
    private final int userIdLoggedIn = sessionManager.getUserID();
    ObservableList<BorrowedBooksTable> masterData = FXCollections.observableArrayList();

    public Library() throws SQLException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bookIdCol.setCellValueFactory(cellData -> cellData.getValue().bookIdColProperty().asObject());
        bookNameCol.setCellValueFactory(cellData -> cellData.getValue().bookNameCOlProperty());
        borrowedRemarkseCol.setCellValueFactory(cellData -> cellData.getValue().borrowedRemarksColProperty());
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusColProperty());
        returnDateCol.setCellValueFactory(cellData -> cellData.getValue().returnDateColProperty());
        dueDateCol.setCellValueFactory(cellData -> cellData.getValue().dueDateColProperty());
        borrowDateCol.setCellValueFactory(cellData -> cellData.getValue().borrowDateColProperty());
        fineCol.setCellValueFactory(cellData -> cellData.getValue().fineColProperty().asObject());

        getBorrowedBooksLoaded(userIdLoggedIn);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                handleSearch(newValue);
        });
    }

    private void getBorrowedBooksLoaded(int userIdLoggedIn) {
        ObservableList<BorrowedBooksTable> data = FXCollections.observableArrayList();
        String query = "select b.Book_Id, b.Title, br.Borrow_Date, br.Due_Date, br" +
                ".Return_Date, br.Fine_Amount,br.Status, br.Remarks from Books b JOIN " +
                "Borrow_Records br ON b.Book_Id=br.Book_Id where br.User_Id=" + userIdLoggedIn;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                // Retrieve the java.sql.Date objects first
                java.sql.Date sqlDueDate = resultSet.getDate("Due_Date");
                java.sql.Date sqlReturnDate = resultSet.getDate("Return_Date");
                java.sql.Date sqlBorrowDate = resultSet.getDate("Borrow_Date");

                // Convert to LocalDate only if the SQL date is not null
                LocalDate dueDate = (sqlDueDate != null) ? sqlDueDate.toLocalDate() : null;
                LocalDate returnDate = (sqlReturnDate != null) ? sqlReturnDate.toLocalDate() : null;
                LocalDate borrowDate = (sqlBorrowDate != null) ? sqlBorrowDate.toLocalDate() : null;

                // Create the object with the potentially null LocalDate values
                BorrowedBooksTable table =
                        new BorrowedBooksTable(resultSet.getInt("Book_Id"), resultSet.getString("Title"), returnDate, dueDate, borrowDate, resultSet.getDouble("Fine_Amount"),
                                resultSet.getString("Status"), resultSet.getString("Remarks"));
                data.add(table);
            }
            masterData.setAll(data);
            borrowedBooksTable.setItems(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleSearch(String newValue) {
        String searchTerm = searchField.getText();
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            borrowedBooksTable.setItems(masterData);
            return;
        }
        String lowerCaseSearchTerm = searchTerm.toLowerCase();
        ObservableList<BorrowedBooksTable> filteredData = FXCollections.observableArrayList();
        for (BorrowedBooksTable record : masterData) {
            if (String.valueOf(record.getBookIdCol()).contains(lowerCaseSearchTerm) ||
                    record.getBookNameCOl().toLowerCase().contains(lowerCaseSearchTerm) ||
                    record.getStatusCol().toLowerCase().contains(lowerCaseSearchTerm)) {
                filteredData.add(record);
            }
        }
        borrowedBooksTable.setItems(filteredData);
    }

}