package org.example.complete_ums.CommonTable;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

import java.time.LocalDate;

public class SalaryTable {

    private SimpleStringProperty nameColumn, roleColumn,transactionIdColumn;
    private SimpleDoubleProperty deductionsColumn, grossSalaryColumn, netSalaryColumn;
    private SimpleIntegerProperty  userIDColumn;
    private SimpleObjectProperty paymentDateColumn;

    public SalaryTable(String transactionIdColumn, int userIDColumn, String nameColumn,
                       String roleColumn,
                       double deductionsColumn, LocalDate paymentDateColumn, double grossSalaryColumn, double netSalaryColumn) {

        this.deductionsColumn = new SimpleDoubleProperty(deductionsColumn);
        this.nameColumn = new SimpleStringProperty(nameColumn);
        this.roleColumn = new SimpleStringProperty(roleColumn);
        this.grossSalaryColumn = new SimpleDoubleProperty(grossSalaryColumn);
        this.netSalaryColumn = new SimpleDoubleProperty(netSalaryColumn);
        this.transactionIdColumn = new SimpleStringProperty(transactionIdColumn);
        this.userIDColumn = new SimpleIntegerProperty(userIDColumn);
        this.paymentDateColumn = new SimpleObjectProperty<>(paymentDateColumn);
    }

    public String getNameColumn() {
        return nameColumn.get();
    }

    public SimpleStringProperty nameColumnProperty() {
        return nameColumn;
    }

    public String getRoleColumn() {
        return roleColumn.get();
    }

    public SimpleStringProperty roleColumnProperty() {
        return roleColumn;
    }

    public double getDeductionsColumn() {
        return deductionsColumn.get();
    }

    public SimpleDoubleProperty deductionsColumnProperty() {
        return deductionsColumn;
    }

    public double getGrossSalaryColumn() {
        return grossSalaryColumn.get();
    }

    public SimpleDoubleProperty grossSalaryColumnProperty() {
        return grossSalaryColumn;
    }

    public double getNetSalaryColumn() {
        return netSalaryColumn.get();
    }

    public SimpleDoubleProperty netSalaryColumnProperty() {
        return netSalaryColumn;
    }

    public String getTransactionIdColumn() {
        return transactionIdColumn.get();
    }

    public SimpleStringProperty transactionIdColumnProperty() {
        return transactionIdColumn;
    }

    public int getUserIDColumn() {
        return userIDColumn.get();
    }

    public SimpleIntegerProperty userIDColumnProperty() {
        return userIDColumn;
    }

    public Object getPaymentDateColumn() {
        return paymentDateColumn.get();
    }

    public SimpleObjectProperty paymentDateColumnProperty() {
        return paymentDateColumn;
    }
}
