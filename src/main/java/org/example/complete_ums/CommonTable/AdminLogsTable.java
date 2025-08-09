package org.example.complete_ums.CommonTable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;

public class AdminLogsTable {
    private SimpleStringProperty actionTypeColumn, targetTableColumn, actionDetailsColumn, ipAddressColumn;
    private SimpleObjectProperty<LocalDateTime> actionTimestampColumn;
    private SimpleIntegerProperty logIdColumn, targetRecordIdColumn, adminUserIdColumn;


    public AdminLogsTable(int logId, int adminUserId, String actionType, String targetTable,
                          int targetRecordId, String actionDetails, LocalDateTime actionTimestamp,
                          String ipAddress) {
        this.logIdColumn = new SimpleIntegerProperty(logId);
        this.adminUserIdColumn = new SimpleIntegerProperty(adminUserId);
        this.actionTypeColumn = new SimpleStringProperty(actionType);
        this.targetTableColumn = new SimpleStringProperty(targetTable);
        this.targetRecordIdColumn = new SimpleIntegerProperty(targetRecordId);
        this.actionDetailsColumn = new SimpleStringProperty(actionDetails);
        this.actionTimestampColumn = new SimpleObjectProperty<>(actionTimestamp);
        this.ipAddressColumn = new SimpleStringProperty(ipAddress);
    }

    // --- Getters for properties ---

    public int getLogIdColumn() {
        return logIdColumn.get();
    }

    public SimpleIntegerProperty logIdColumnProperty() {
        return logIdColumn;
    }

    public int getAdminUserIdColumn() {
        return adminUserIdColumn.get();
    }

    public SimpleIntegerProperty adminUserIdColumnProperty() {
        return adminUserIdColumn;
    }

    public String getActionTypeColumn() {
        return actionTypeColumn.get();
    }

    public SimpleStringProperty actionTypeColumnProperty() {
        return actionTypeColumn;
    }

    public String getTargetTableColumn() {
        return targetTableColumn.get();
    }

    public SimpleStringProperty targetTableColumnProperty() {
        return targetTableColumn;
    }

    public int getTargetRecordIdColumn() {
        return targetRecordIdColumn.get();
    }

    public SimpleIntegerProperty targetRecordIdColumnProperty() {
        return targetRecordIdColumn;
    }

    public String getActionDetailsColumn() {
        return actionDetailsColumn.get();
    }

    public SimpleStringProperty actionDetailsColumnProperty() {
        return actionDetailsColumn;
    }

    public LocalDateTime getActionTimestampColumn() {
        return actionTimestampColumn.get();
    }

    public SimpleObjectProperty<LocalDateTime> actionTimestampColumnProperty() {
        return actionTimestampColumn;
    }

    public String getIpAddressColumn() {
        return ipAddressColumn.get();
    }

    public SimpleStringProperty ipAddressColumnProperty() {
        return ipAddressColumn;
    }
}
