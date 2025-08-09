package org.example.complete_ums.CommonTable;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class FinanceFeeTable {
    private SimpleIntegerProperty academicYearCol;
    private SimpleDoubleProperty amountPaidCol, amountDueCol;
    private SimpleObjectProperty paymentDateCol, dueDateCol;
    private SimpleStringProperty receiptNumberCol, statusCol, feeTypeCol;

    public FinanceFeeTable(String receiptNumberCol, int academicYearCol, double amountPaidCol,
                           double amountDueCol, LocalDate paymentDateCol,
                           LocalDate dueDateCol, String statusCol, String feeTypeCol) {
        this.receiptNumberCol = new SimpleStringProperty(receiptNumberCol);
        this.academicYearCol = new SimpleIntegerProperty(academicYearCol);
        this.amountPaidCol = new SimpleDoubleProperty(amountPaidCol);
        this.amountDueCol = new SimpleDoubleProperty(amountDueCol);
        this.paymentDateCol = new SimpleObjectProperty(paymentDateCol);
        this.dueDateCol = new SimpleObjectProperty(dueDateCol);
        this.statusCol = new SimpleStringProperty(statusCol);
        this.feeTypeCol = new SimpleStringProperty(feeTypeCol);
    }

    public String getReceiptNumberCol() {
        return receiptNumberCol.get();
    }

    public SimpleStringProperty receiptNumberColProperty() {
        return receiptNumberCol;
    }

    public int getAcademicYearCol() {
        return academicYearCol.get();
    }

    public SimpleIntegerProperty academicYearColProperty() {
        return academicYearCol;
    }

    public double getAmountPaidCol() {
        return amountPaidCol.get();
    }

    public SimpleDoubleProperty amountPaidColProperty() {
        return amountPaidCol;
    }

    public double getAmountDueCol() {
        return amountDueCol.get();
    }

    public SimpleDoubleProperty amountDueColProperty() {
        return amountDueCol;
    }

    public Object getPaymentDateCol() {
        return paymentDateCol.get();
    }

    public SimpleObjectProperty paymentDateColProperty() {
        return paymentDateCol;
    }

    public Object getDueDateCol() {
        return dueDateCol.get();
    }

    public SimpleObjectProperty dueDateColProperty() {
        return dueDateCol;
    }

    public String getStatusCol() {
        return statusCol.get();
    }

    public SimpleStringProperty statusColProperty() {
        return statusCol;
    }

    public String getFeeTypeCol() {
        return feeTypeCol.get();
    }

    public SimpleStringProperty feeTypeColProperty() {
        return feeTypeCol;
    }
}
