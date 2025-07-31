// In org.example.complete_ums.CommonTable.Fee_Collection_Table.java

package org.example.complete_ums.CommonTable;

import javafx.beans.property.*;

import java.sql.Date;

public class Fee_Collection_Table {
    private final IntegerProperty colAcademicYear;
    private final ObjectProperty<Integer> colFeeMonth; // Use ObjectProperty for nullable Integer
    private final IntegerProperty colFeeRecordId;
    private final StringProperty colSemester; // Changed to StringProperty
    private final ObjectProperty<Date> colDueDate;
    private final ObjectProperty<Date> colPaymentDate;
    private final DoubleProperty colAmountDue;
    private final DoubleProperty colAmountPaid;
    private final StringProperty colRemarks;
    private final StringProperty colReceiptNumber;
    private final StringProperty colPaymentStatus;
    private final StringProperty colFeeType;
    private final StringProperty colTransactionId;

    public Fee_Collection_Table(int colAcademicYear, Integer colFeeMonth, int colFeeRecordId, String colSemester,
                                Date colDueDate, Date colPaymentDate, double colAmountDue, double colAmountPaid,
                                String colRemarks, String colReceiptNumber, String colPaymentStatus,
                                String colFeeType, String colTransactionId) {
        this.colAcademicYear = new SimpleIntegerProperty(colAcademicYear);
        this.colFeeMonth = new SimpleObjectProperty<>(colFeeMonth);
        this.colFeeRecordId = new SimpleIntegerProperty(colFeeRecordId);
        this.colSemester = new SimpleStringProperty(colSemester); // Changed to SimpleStringProperty
        this.colDueDate = new SimpleObjectProperty<>(colDueDate);
        this.colPaymentDate = new SimpleObjectProperty<>(colPaymentDate);
        this.colAmountDue = new SimpleDoubleProperty(colAmountDue);
        this.colAmountPaid = new SimpleDoubleProperty(colAmountPaid);
        this.colRemarks = new SimpleStringProperty(colRemarks);
        this.colReceiptNumber = new SimpleStringProperty(colReceiptNumber);
        this.colPaymentStatus = new SimpleStringProperty(colPaymentStatus);
        this.colFeeType = new SimpleStringProperty(colFeeType);
        this.colTransactionId = new SimpleStringProperty(colTransactionId);
    }

    // --- Getters and Property Getters ---

    public int getColAcademicYear() {
        return colAcademicYear.get();
    }

    public IntegerProperty colAcademicYearProperty() {
        return colAcademicYear;
    }

    public Integer getColFeeMonth() {
        return colFeeMonth.get();
    }

    public ObjectProperty<Integer> colFeeMonthProperty() {
        return colFeeMonth;
    }

    public int getColFeeRecordId() {
        return colFeeRecordId.get();
    }

    public IntegerProperty colFeeRecordIdProperty() {
        return colFeeRecordId;
    }

    public String getColSemester() { // Changed return type
        return colSemester.get();
    }

    public StringProperty colSemesterProperty() { // Changed return type
        return colSemester;
    }

    public Date getColDueDate() {
        return colDueDate.get();
    }

    public ObjectProperty<Date> colDueDateProperty() {
        return colDueDate;
    }

    public Date getColPaymentDate() {
        return colPaymentDate.get();
    }

    public ObjectProperty<Date> colPaymentDateProperty() {
        return colPaymentDate;
    }

    public double getColAmountDue() {
        return colAmountDue.get();
    }

    public DoubleProperty colAmountDueProperty() {
        return colAmountDue;
    }

    public double getColAmountPaid() {
        return colAmountPaid.get();
    }

    public DoubleProperty colAmountPaidProperty() {
        return colAmountPaid;
    }

    public String getColRemarks() {
        return colRemarks.get();
    }

    public StringProperty colRemarksProperty() {
        return colRemarks;
    }

    public String getColReceiptNumber() {
        return colReceiptNumber.get();
    }

    public StringProperty colReceiptNumberProperty() {
        return colReceiptNumber;
    }

    public String getColPaymentStatus() {
        return colPaymentStatus.get();
    }

    public StringProperty colPaymentStatusProperty() {
        return colPaymentStatus;
    }

    public String getColFeeType() {
        return colFeeType.get();
    }

    public StringProperty colFeeTypeProperty() {
        return colFeeType;
    }

    public String getColTransactionId() {
        return colTransactionId.get();
    }

    public StringProperty colTransactionIdProperty() {
        return colTransactionId;
    }
}