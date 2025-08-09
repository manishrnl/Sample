package org.example.complete_ums.CommonTable;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class BorrowedBooksTable {

    private SimpleObjectProperty<LocalDate> returnDateCol, dueDateCol, borrowDateCol;
    private SimpleDoubleProperty fineCol;
    private SimpleStringProperty statusCol, borrowedRemarksCol, bookNameCOl;
    private SimpleIntegerProperty bookIdCol;

    public BorrowedBooksTable(int bookIdCol, String bookNameCol, LocalDate returnDateCol,
                              LocalDate dueDateCol, LocalDate borrowDateCol, double fineCol,
                              String statusCol, String borrowedRemarksCol) {
        this.bookIdCol = new SimpleIntegerProperty(bookIdCol);
        this.bookNameCOl = new SimpleStringProperty(bookNameCol);
        this.returnDateCol = new SimpleObjectProperty<>(returnDateCol);
        this.dueDateCol = new SimpleObjectProperty<>(dueDateCol);
        this.borrowDateCol = new SimpleObjectProperty<>(borrowDateCol);
        this.fineCol = new SimpleDoubleProperty(fineCol);
        this.statusCol = new SimpleStringProperty(statusCol);
        this.borrowedRemarksCol = new SimpleStringProperty(borrowedRemarksCol);
    }

    public String getBookNameCOl() {
        return bookNameCOl.get();
    }

    public SimpleStringProperty bookNameCOlProperty() {
        return bookNameCOl;
    }

    public int getBookIdCol() {
        return bookIdCol.get();
    }

    public SimpleIntegerProperty bookIdColProperty() {
        return bookIdCol;
    }

    public LocalDate getReturnDateCol() {
        return returnDateCol.get();
    }

    public SimpleObjectProperty<LocalDate> returnDateColProperty() {
        return returnDateCol;
    }

    public LocalDate getDueDateCol() {
        return dueDateCol.get();
    }

    public SimpleObjectProperty<LocalDate> dueDateColProperty() {
        return dueDateCol;
    }

    public LocalDate getBorrowDateCol() {
        return borrowDateCol.get();
    }

    public SimpleObjectProperty<LocalDate> borrowDateColProperty() {
        return borrowDateCol;
    }

    public double getFineCol() {
        return fineCol.get();
    }

    public SimpleDoubleProperty fineColProperty() {
        return fineCol;
    }

    public String getStatusCol() {
        return statusCol.get();
    }

    public SimpleStringProperty statusColProperty() {
        return statusCol;
    }

    public String getBorrowedRemarksCol() {
        return borrowedRemarksCol.get();
    }

    public SimpleStringProperty borrowedRemarksColProperty() {
        return borrowedRemarksCol;
    }
}
