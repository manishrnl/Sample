module org.example.university_management_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics; // Often good to include explicitly if not pulled by controls/fxml

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires jdk.dynalink;
    requires java.desktop;
    requires java.naming;
    requires mysql.connector.java;
    requires javafx.media; // This is a JDK internal module, sometimes needed for reflection.

    // Open packages containing FXML controllers to javafx.fxml
    // This allows FXMLLoader to create instances of your controllers via reflection.
    opens org.example.university_management_system to javafx.fxml; // For controllers in the root package (like Login_Controller)
    opens org.example.university_management_system.Teachers to javafx.fxml;
    opens org.example.university_management_system.Admin to javafx.fxml;
    opens org.example.university_management_system.Students to javafx.fxml;
    opens org.example.university_management_system.Staffs to javafx.fxml;
    opens org.example.university_management_system.Accountants to javafx.fxml;
    opens org.example.university_management_system.Librarians to javafx.fxml;
    opens org.example.university_management_system.ToolsClasses to javafx.fxml; // If LoadFrame or SessionManager are used in FXML

    // Export packages containing public APIs that other *modules* might directly use.
    // In this case, your main application module exports its main package
    // and potentially other packages if they provide APIs to other (non-JavaFX runtime) modules.
    exports org.example.university_management_system;
    exports org.example.university_management_system.ToolsClasses;
    exports org.example.university_management_system.Databases;
    opens org.example.university_management_system.Databases to javafx.fxml; // If you call LoadFrame/SessionManager from another module
    // You might also export other core logical packages if they are meant to be consumed by other modules
}
