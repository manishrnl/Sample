package org.example.complete_ums.Students;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.complete_ums.CommonTable.ViewOrReturnBooksTable;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ViewIssuedBooks implements Initializable {
    SessionManager sessionManager = SessionManager.getInstance();
    Connection connection = DatabaseConnection.getConnection();
    @FXML
    private TableColumn<ViewOrReturnBooksTable, Integer> librariansID, userIdColumn,
            bookIdColumn, borrowIdColumn;
    @FXML
    private TableColumn<ViewOrReturnBooksTable, LocalDate> returnDateColumn, borrowDateColumn, dueDateColumn;
    @FXML
    private TableView<ViewOrReturnBooksTable> viewOrReturnBooksTableTableView;
    @FXML
    private TableColumn<ViewOrReturnBooksTable, Double> fineAmountColumn;
    @FXML
    private TableColumn<ViewOrReturnBooksTable, String> userNameColumn, statusColumn, fullNameColumn;

    @FXML
    private Label errorMessageLabel;


    @FXML
    private ScrollPane mainScrollPane;


    @FXML
    private TextField searchField;

    ObservableList<ViewOrReturnBooksTable> masterData = FXCollections.observableArrayList();
    int userIDLoggedIn = sessionManager.getUserID();

    public ViewIssuedBooks() throws SQLException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userIdColumn.setCellValueFactory(cellData -> cellData.getValue().userIdColumnProperty().asObject());
        bookIdColumn.setCellValueFactory(cellData -> cellData.getValue().bookIdColumnProperty().asObject());
        borrowIdColumn.setCellValueFactory(cellData -> cellData.getValue().borrowIdColumnProperty().asObject());
        librariansID.setCellValueFactory(cellData -> cellData.getValue().librariansIDProperty().asObject());
        returnDateColumn.setCellValueFactory(cellData -> cellData.getValue().returnDateColumnProperty());
        borrowDateColumn.setCellValueFactory(cellData -> cellData.getValue().borrowDateColumnProperty());
        dueDateColumn.setCellValueFactory(cellData -> cellData.getValue().dueDateColumnProperty());
        fineAmountColumn.setCellValueFactory(cellData -> cellData.getValue().fineAmountColumnProperty().asObject());
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().userNameColumnProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusColumnProperty());
        fullNameColumn.setCellValueFactory(cellData -> cellData.getValue().fullNameColumnProperty());
        loadTableValue();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty())
                filterTableByString(newValue);
            else
                viewOrReturnBooksTableTableView.setItems(masterData);

        });
    }


    private void loadTableValue() {
        masterData.clear();
        ObservableList<ViewOrReturnBooksTable> sample = FXCollections.observableArrayList();

        String query = "    SELECT br.Borrow_Id,  br.Book_Id, br.User_Id, br.Librarian_User_Id, br.Borrow_Date, br.Due_Date, br.Return_Date,br.Fine_Amount,br.Status, " +
                "CONCAT(u.First_Name, ' ', u.Last_Name) AS full_Name, a.UserName" +
                " FROM Borrow_Records br " +
                "INNER JOIN Users u ON br.User_Id = u.User_Id " +
                "LEFT JOIN Authentication a ON u.User_Id = a.User_Id where u.User_Id= ? ";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userIDLoggedIn);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int userID = rs.getInt("User_Id");
                int bookID = rs.getInt("Book_Id");
                ViewOrReturnBooksTable data = new ViewOrReturnBooksTable(userID, bookID,
                        rs.getInt("Borrow_Id"),
                        rs.getInt("Librarian_User_Id"),
                        rs.getObject("Return_Date", LocalDate.class),
                        rs.getObject("Borrow_Date", LocalDate.class),
                        rs.getObject("Due_Date", LocalDate.class),
                        rs.getDouble("Fine_Amount"),
                        rs.getString("UserName"),
                        rs.getString("Status"),
                        rs.getString("full_Name"));
                sample.add(data);
            }
            masterData.addAll(sample);
            viewOrReturnBooksTableTableView.setItems(sample);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void filterTableByString(String newValue) {
        ObservableList<ViewOrReturnBooksTable> filtered = FXCollections.observableArrayList();
        for (ViewOrReturnBooksTable records : masterData) {
            String bookId = String.valueOf(records.getBookIdColumn());
            String librariansID = String.valueOf(records.getLibrariansID());
            String userID = String.valueOf(records.getUserIdColumn());
            String borrrowID = String.valueOf(records.getBorrowIdColumn());
            if (records.getFullNameColumn().toLowerCase().contains(newValue.toLowerCase()) || bookId.contains(newValue) || librariansID.contains(newValue) ||
                    userID.contains(newValue) || borrrowID.contains(newValue) ||
                    records.getUserNameColumn().toLowerCase().contains(newValue.toLowerCase()) ||
                    records.getStatusColumn().toLowerCase().contains(newValue.toLowerCase())) {
                filtered.add(records);
            }
        }
        viewOrReturnBooksTableTableView.setItems(filtered);
    }

}
