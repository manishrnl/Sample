module org.example.complete_ums {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.desktop;
    requires java.naming;
    requires annotations;
    requires javafx.media;
    requires javafx.base;
    requires java.logging;
    requires com.fasterxml.jackson.annotation;
    requires javafx.graphics; // Add this if you use java.util.logging

    opens org.example.complete_ums to javafx.fxml; // For controllers in the root package (like Login_Controller)
    opens org.example.complete_ums.Teachers to javafx.fxml;
    opens org.example.complete_ums.Admin to javafx.fxml;
    opens org.example.complete_ums.Students to javafx.fxml;
    opens org.example.complete_ums.Staffs to javafx.fxml;
    opens org.example.complete_ums.Accountants to javafx.fxml;
    opens org.example.complete_ums.Librarians to javafx.fxml;
    opens org.example.complete_ums.ToolsClasses to javafx.fxml; // If LoadFrame or SessionManager are used in FXML

    exports org.example.complete_ums;
    exports org.example.complete_ums.ToolsClasses;
    exports org.example.complete_ums.Databases;
    opens org.example.complete_ums.Databases to javafx.fxml;
    opens org.example.complete_ums.CommonTable to javafx.fxml, javafx.base;

}