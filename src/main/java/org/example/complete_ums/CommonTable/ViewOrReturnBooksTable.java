package org.example.complete_ums.CommonTable;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class ViewOrReturnBooksTable {
    private SimpleIntegerProperty userIdColumn, bookIdColumn, borrowIdColumn, librariansID;
    private SimpleObjectProperty<LocalDate> returnDateColumn, borrowDateColumn, dueDateColumn;
    private SimpleDoubleProperty fineAmountColumn;
    private SimpleStringProperty userNameColumn, statusColumn, fullNameColumn;

    public ViewOrReturnBooksTable(int userIdColumn, int bookIdColumn, int borrowIdColumn,
                                  int librariansID,
                                  LocalDate returnDateColumn, LocalDate borrowDateColumn, LocalDate
                                          dueDateColumn, double fineAmountColumn, String userNameColumn, String statusColumn, String fullNameColumn) {
        this.userIdColumn = new SimpleIntegerProperty(userIdColumn);
        this.bookIdColumn = new SimpleIntegerProperty(bookIdColumn);
        this.borrowIdColumn = new SimpleIntegerProperty(borrowIdColumn);
        this.librariansID =new SimpleIntegerProperty(librariansID);
        this.returnDateColumn = new SimpleObjectProperty<>(returnDateColumn);
        this.borrowDateColumn = new SimpleObjectProperty<>(borrowDateColumn);
        this.dueDateColumn = new SimpleObjectProperty<>(dueDateColumn);
        this.fineAmountColumn = new SimpleDoubleProperty(fineAmountColumn);
        this.userNameColumn = new SimpleStringProperty(userNameColumn);
        this.statusColumn = new SimpleStringProperty(statusColumn);
        this.fullNameColumn = new SimpleStringProperty(fullNameColumn);
    }

    public int getLibrariansID() {
        return librariansID.get();
    }

    public SimpleIntegerProperty librariansIDProperty() {
        return librariansID;
    }

    public int getUserIdColumn() {
        return userIdColumn.get();
    }

    public SimpleIntegerProperty userIdColumnProperty() {
        return userIdColumn;
    }

    public int getBookIdColumn() {
        return bookIdColumn.get();
    }

    public SimpleIntegerProperty bookIdColumnProperty() {
        return bookIdColumn;
    }

    public int getBorrowIdColumn() {
        return borrowIdColumn.get();
    }

    public SimpleIntegerProperty borrowIdColumnProperty() {
        return borrowIdColumn;
    }

    public LocalDate getReturnDateColumn() {
        return returnDateColumn.get();
    }

    public SimpleObjectProperty<LocalDate> returnDateColumnProperty() {
        return returnDateColumn;
    }

    public LocalDate getBorrowDateColumn() {
        return borrowDateColumn.get();
    }

    public SimpleObjectProperty<LocalDate> borrowDateColumnProperty() {
        return borrowDateColumn;
    }

    public LocalDate getDueDateColumn() {
        return dueDateColumn.get();
    }

    public SimpleObjectProperty<LocalDate> dueDateColumnProperty() {
        return dueDateColumn;
    }

    public double getFineAmountColumn() {
        return fineAmountColumn.get();
    }

    public SimpleDoubleProperty fineAmountColumnProperty() {
        return fineAmountColumn;
    }

    public String getUserNameColumn() {
        return userNameColumn.get();
    }

    public SimpleStringProperty userNameColumnProperty() {
        return userNameColumn;
    }

    public String getStatusColumn() {
        return statusColumn.get();
    }

    public SimpleStringProperty statusColumnProperty() {
        return statusColumn;
    }

    public String getFullNameColumn() {
        return fullNameColumn.get();
    }

    public SimpleStringProperty fullNameColumnProperty() {
        return fullNameColumn;
    }
}
