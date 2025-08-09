package org.example.complete_ums.Librarians;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.complete_ums.CommonTable.ViewOrReturnBooksTable;
import org.example.complete_ums.Databases.AdminActivityLogs;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.ToolsClasses.AlertManager;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewOrReturnBooks implements Initializable {
    public ViewOrReturnBooks() throws SQLException {
    }

    AdminActivityLogs adminActivityLogs = new AdminActivityLogs();
    Button3DEffect button3DEffect;
    AlertManager alertManager;
    SessionManager sessionManager = SessionManager.getInstance();
    Connection connection = DatabaseConnection.getConnection();
    LoadFrame loadFrame;
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
    private Button returnButton, markAsLostButton;
    @FXML
    private TextField searchField, finetoLevied;
    ObservableList<ViewOrReturnBooksTable> masterData = FXCollections.observableArrayList();
    int userIDLoggedIn = sessionManager.getUserID();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button3DEffect.applyEffect(markAsLostButton, "/sounds/hover.mp3");
        button3DEffect.applyEffect(returnButton, "/sounds/hover.mp3");
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

    private void loadTableValue() {
        masterData.clear();
        ObservableList<ViewOrReturnBooksTable> sample = FXCollections.observableArrayList();

        String query = "SELECT br.Borrow_Id,  br.Book_Id, br.User_Id, br.Librarian_User_Id, br.Borrow_Date, br.Due_Date, br.Return_Date,br.Fine_Amount,br.Status, " +
                "CONCAT(u.First_Name, ' ', u.Last_Name) AS full_Name, a.UserName" +
                " FROM Borrow_Records br " +
                "INNER JOIN Users u ON br.User_Id = u.User_Id " +
                "LEFT JOIN Authentication a ON u.User_Id = a.User_Id;";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
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

    @FXML
    void handleReturnBook(ActionEvent event) {
        ViewOrReturnBooksTable selected = viewOrReturnBooksTableTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {

            if (selected.getStatusColumn().equals("Lost")) {
                Optional<ButtonType> response = alertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "Return and" +
                                " Delete Records", "Following data will be deleted permanently " +
                                "if Fine Amount is 0",
                        " Selected data : " + selected.getFullNameColumn() +
                                " will be Mark as Returned and will be deleted permanently and you will not be able to undo it.Click CANCEL to quit deleting Process");
                if (response.isPresent() && response.get() == ButtonType.OK) {
                    loadFrame.setMessage(errorMessageLabel, "Initiating Returning and Deleting " +
                            "the records....", "GREEN");

                    String query = "UPDATE Borrow_Records SET Status = 'Returned', Return_Date = CURRENT_DATE, Fine_Amount = ? WHERE Borrow_Id = ? ";

                    try (PreparedStatement pstmt = connection.prepareStatement(query)) {

                        LocalDate dueDate = selected.getDueDateColumn();
                        LocalDate returnedDate = LocalDate.now();
                        double newFineAmount = selected.getFineAmountColumn();

                        if (returnedDate.isAfter(dueDate)) {
                            long difference = ChronoUnit.DAYS.between(dueDate, returnedDate);
                            newFineAmount = difference * 10.0;

                            loadFrame.setMessage(errorMessageLabel, "You returned the book " + difference + " Days late. " +
                                    "New Fine Amount: " + newFineAmount, "GREEN");
                        }
                        pstmt.setDouble(1, newFineAmount);
                        pstmt.setInt(2, selected.getBorrowIdColumn());
                        int rs = pstmt.executeUpdate();

                        if (rs > 0) {
                            updateAvailableCopies(selected.getBookIdColumn());
                            updateTotalCopies(selected.getBookIdColumn(), +1);
                            if (newFineAmount > 0) {
                                adminActivityLogs.insertAdminLogsData(errorMessageLabel, 8, userIDLoggedIn, "UPDATE",
                                        "Borrow_Records",
                                        "Books were returned and updated to Stocks by :" + userIDLoggedIn);
                                viewOrReturnBooksTableTableView.getItems().clear();
                                loadTableValue();
                                loadFrame.setMessage(errorMessageLabel, "Could not delete " +
                                                "records as user is Fined for paying late. This " +
                                                "record will be used later On while calculating fees.",
                                        "GREEN");

                            } else {
                                loadFrame.setMessage(errorMessageLabel, "Deleting records as " +
                                        "Amount is 0", "RED");
                                deleteBookIssuedRecords(selected.getBorrowIdColumn());
                            }
                        }
                    } catch (Exception e) {
                        loadFrame.setMessage(errorMessageLabel, "Database error occured :" + e.getMessage(),
                                "RED");
                    }
                }
            } else {
                Optional<ButtonType> response =
                        alertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "Return and" +
                                        " Delete Records", "Following data will be deleted permanently " +
                                        "if Fine Amount is 0",
                                " Selected data : " + selected.getFullNameColumn() +
                                        " will be Mark as Returned and will be deleted permanently and you will not be able to undo it.Click CANCEL to quit deleting Process");

                if (response.isPresent() && response.get() == ButtonType.OK) {
                    loadFrame.setMessage(errorMessageLabel, "Initiating Returning and Deleting " +
                            "the records....", "GREEN");

                    String query = "UPDATE Borrow_Records SET Status = 'Returned', Return_Date = CURRENT_DATE, Fine_Amount = ? WHERE Borrow_Id = ? ";

                    try (PreparedStatement pstmt = connection.prepareStatement(query)) {

                        LocalDate dueDate = selected.getDueDateColumn();
                        LocalDate returnedDate = LocalDate.now();
                        double newFineAmount = selected.getFineAmountColumn();

                        if (returnedDate.isAfter(dueDate)) {
                            long difference = ChronoUnit.DAYS.between(dueDate, returnedDate);
                            // Assuming 10 is the fine per day
                            newFineAmount = difference * 10.0;

                            loadFrame.setMessage(errorMessageLabel, "You returned the book " + difference + " Days late. " +
                                    "New Fine Amount: " + newFineAmount, "GREEN");
                        }
                        pstmt.setDouble(1, newFineAmount);
                        pstmt.setInt(2, selected.getBorrowIdColumn());
                        int rs = pstmt.executeUpdate();

                        if (rs > 0) {
                            adminActivityLogs.insertAdminLogsData(errorMessageLabel, 8, userIDLoggedIn, "UPDATE",
                                    "Borrow_Records",
                                    "Books were returned and updated to Stocks by :" + userIDLoggedIn);
                            updateAvailableCopies(selected.getBookIdColumn());
                            if (newFineAmount > 0) {
                                viewOrReturnBooksTableTableView.getItems().clear();
                                loadTableValue();
                                loadFrame.setMessage(errorMessageLabel, "Could not delete " +
                                                "records as user is Fined for paying late. This " +
                                                "record will be used later On while calculating fees.",
                                        "GREEN");

                            } else {
                                loadFrame.setMessage(errorMessageLabel, "Deleting records as " +
                                        "Amount is 0", "RED");
                                deleteBookIssuedRecords(selected.getBorrowIdColumn());
                            }
                        }
                    } catch (Exception e) {
                        loadFrame.setMessage(errorMessageLabel, "Database error occured :" + e.getMessage(),
                                "RED");
                    }
                }
            }
        } else {
            loadFrame.setMessage(errorMessageLabel, "Please select Students from table to " +
                    "return its book", "RED");
        }
    }

    private void updateAvailableCopies(int bookIdColumn) throws SQLException {
        int availableCopies = 0;
        String getAvailableCopies = "select Available_Copies from Books where Book_Id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(getAvailableCopies)) {
            pstmt.setInt(1, bookIdColumn);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                availableCopies = resultSet.getInt("Available_Copies");
            }
        }

        String query = "Update Books set Available_Copies=? where Book_Id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, (availableCopies + 1));
            pstmt.setInt(2, bookIdColumn);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Available copies updated");
                loadFrame.setMessage(errorMessageLabel, "Total Available Copies Updated", "GREEN");
            }
        }
    }

    private void updateTotalCopies(int bookIdColumn, int value) throws SQLException {
        int totalCopies = 0;
        String getAvailableCopies = "select Total_Copies from Books where Book_Id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(getAvailableCopies)) {
            pstmt.setInt(1, bookIdColumn);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                totalCopies = resultSet.getInt("Total_Copies");
            }
        }

        String query = "Update Books set Total_Copies=? where Book_Id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, (totalCopies + value));
            pstmt.setInt(2, bookIdColumn);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("total copies updated");
                loadFrame.setMessage(errorMessageLabel, "Total Copies Updated", "GREEN");
            }
        }
    }

    private void deleteBookIssuedRecords(int borrowId) {
        String query = "DELETE from Borrow_Records where Borrow_Id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, borrowId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {

                adminActivityLogs.insertAdminLogsData(errorMessageLabel, 8, userIDLoggedIn,
                        "DELETE", "Borrow_Records", "Users Table were deleted as they returned the books and fine amount" +
                                "  = 0 : by userID : " + userIDLoggedIn);

                loadFrame.setMessage(errorMessageLabel, "Successfully deleted Records as " +
                        "Fine Amount was 0", "RED");
                viewOrReturnBooksTableTableView.getItems().clear();
                loadTableValue();
            }
        } catch (Exception e) {
            loadFrame.setMessage(errorMessageLabel, "Database error occured while deleting " +
                    "records :", "RED");
        }

    }

    public void handleMarkAsLost(ActionEvent event) {
        LocalDate returnedDate = LocalDate.now();
        String fineText = finetoLevied.getText();
        Double fine = 0.0;
        if (fineText.isEmpty() || fineText.equals(null)) {
            loadFrame.setMessage(errorMessageLabel, "Please Enter Fine to Add", "RED");
            return;
        }
        try {
            fine = Double.parseDouble(fineText);
        } catch (NumberFormatException ex) {
            loadFrame.setMessage(errorMessageLabel, "Enter digits only in Fine field",
                    "RED");
            return;
        }
        ViewOrReturnBooksTable selected = viewOrReturnBooksTableTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Optional<ButtonType> response =
                    alertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "Mark As " +
                                    "Lost", "Selected Books will be marked as Lost and fine " +
                                    "will be added for books",
                            " Selected data : " + selected.getFullNameColumn() +
                                    " will be Mark as Lost and you will be fined for losing " +
                                    "University's resources" +
                                    ".Click CANCEL to quit Process");

            if (response.isPresent() && response.get() != ButtonType.OK) {
                loadFrame.setMessage(errorMessageLabel, "Operation cancelled as you clicked CANCEL button. " +
                        "All transactions is rolled back to previous state.", "GREEN");
                return;
            } else if (response.isPresent() && response.get() == ButtonType.OK) {
                String query = "UPDATE Borrow_Records SET Status = 'Lost', Fine_Amount = ? " +
                        "WHERE Borrow_Id = ? ";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setDouble(1, fine);
                    pstmt.setInt(2, selected.getBorrowIdColumn());
                    int rs = pstmt.executeUpdate();
                    if (rs > 0) {

                        adminActivityLogs.insertAdminLogsData(errorMessageLabel, 8, userIDLoggedIn, "UPDATE",
                                "Borrow_Records","Books were marked as Lost and is updated by :" + userIDLoggedIn);

                        updateTotalCopies(selected.getBookIdColumn(), -1);
                        loadFrame.setMessage(errorMessageLabel, "Added fine of " + fine + " " +
                                "and marked as Lost as well", "GREEN");
                        viewOrReturnBooksTableTableView.getItems().clear();
                        loadTableValue();
                    }
                } catch (Exception e) {
                    loadFrame.setMessage(errorMessageLabel, "Database error occured :" + e.getMessage(),
                            "RED");
                }
            }
        } else {
            loadFrame.setMessage(errorMessageLabel, "Please select Students from table to " +
                    "return its book", "RED");
        }
    }
}
