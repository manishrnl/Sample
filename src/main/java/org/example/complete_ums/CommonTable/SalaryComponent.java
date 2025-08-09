package org.example.complete_ums.CommonTable;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SalaryComponent {
    private final StringProperty componentName = new SimpleStringProperty();
    private final StringProperty componentType = new SimpleStringProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();

    public SalaryComponent(String componentName, String componentType, double amount) {
        this.componentName.set(componentName);
        this.componentType.set(componentType);
        this.amount.set(amount);
    }

    // Property getters for TableView
    public StringProperty componentNameProperty() { return componentName; }
    public StringProperty componentTypeProperty() { return componentType; }
    public DoubleProperty amountProperty() { return amount; }

    // Standard getters and setters
    public String getComponentName() { return componentName.get(); }
    public void setComponentName(String componentName) { this.componentName.set(componentName); }

    public String getComponentType() { return componentType.get(); }
    public void setComponentType(String componentType) { this.componentType.set(componentType); }

    public double getAmount() { return amount.get(); }
    public void setAmount(double amount) { this.amount.set(amount); }
}