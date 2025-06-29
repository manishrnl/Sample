package org.example.university_management_system.Students;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.university_management_system.ToolsClasses.NavigationManager;

import java.io.IOException;

public class StudentsController {
    NavigationManager navigationManager = NavigationManager.getInstance();
    @FXML
    private VBox root;

    @FXML
    private HBox titleBar;

    @FXML
    void gotoOthersPage(MouseEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
        navigationManager.navigateTo("Librarians/LibrariansDashboard.fxml");
    }

    @FXML
    void handleMenuOption(ActionEvent event) {
        System.out.println("AdminController.handleMenuOption");

    }

    @FXML
    void navigateBackward(MouseEvent event) {
        navigationManager.goBack();
        // Optionally, you can disable the backward button if there's no history left
        // navigateBackward.setDisable(navigationManager.getCurrentIndex() <= 0);
    }

    @FXML
    void navigateForward(MouseEvent event) {
        navigationManager.goForward();
    }

}
