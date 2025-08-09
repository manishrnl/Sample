package org.example.complete_ums.Teachers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.complete_ums.ToolsClasses.NavigationManager;

import javax.swing.text.html.ImageView;
import java.awt.*;

public class TeacherController {

    NavigationManager navigationManager = NavigationManager.getInstance();
    @FXML
    private MenuBar customMenuBar;

    @FXML
    private ImageView navigateBackward;

    @FXML
    private ImageView navigateForward;

    @FXML
    private VBox root;

    @FXML
    private HBox titleBar;



    @FXML
    void navigateBackward(MouseEvent mouseEvent) {
        navigationManager.goBack();

    }

    @FXML
    void navigateForward(MouseEvent mouseEvent) {
        navigationManager.goForward();

    }

    public void handleMenuOption(ActionEvent actionEvent) {
    }

    public void gotoOthersPage(MouseEvent mouseEvent) {
        Stage currentStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        currentStage.close();
        navigationManager.navigateTo("Accountants/AccountantsDashboard.fxml");
    }
}
