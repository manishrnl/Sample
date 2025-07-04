package org.example.university_management_system.Java_StyleSheet;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.net.URL;

/**
 * A utility class for applying common animations and sound effects to JavaFX nodes.
 */
public class Button3DEffect {

    // =================================================================================
    //  1. Reusable Sound Player
    // =================================================================================

    /**
     * Plays a sound effect from a given resource path.
     * The sound is loaded and played immediately.
     *
     * @param soundFilePath The path to the sound file (e.g., "/sounds/hover.wav").
     */
    public static void playSound(String soundFilePath) {
        if (soundFilePath == null || soundFilePath.isEmpty()) {
            return;
        }
        try {
            URL resource = Button3DEffect.class.getResource(soundFilePath);
            if (resource != null) {
                //   new AudioClip(resource.toExternalForm()).play();
            } else {
                System.err.println("Audio file not found at: " + soundFilePath);
            }
        } catch (Exception e) {
            System.err.println("Error playing audio file: " + soundFilePath);
            e.printStackTrace();
        }
    }

    // =================================================================================
    //  2. Reusable 3D Animation Logic (Private Helper)
    // =================================================================================

    /**
     * Private helper method that contains the core 3D hover animation logic.
     * It handles different node types (Button vs. ImageView).
     *
     * @param node The node to apply the animation to.
     */
    private static void apply3DHoverAnimation(Node node) {
        Rotate rotate = new Rotate(0, Rotate.X_AXIS);

        // Handle different ways of getting node size for pivot point
        if (node instanceof Control) { // For Button, MenuButton, etc.
            Control control = (Control) node;
            rotate.pivotXProperty().bind(control.widthProperty().divide(2));
            rotate.pivotYProperty().bind(control.heightProperty().divide(2));
        } else if (node instanceof ImageView) { // For ImageView
            ImageView imageView = (ImageView) node;
            rotate.pivotXProperty().bind(imageView.fitWidthProperty().divide(2));
            rotate.pivotYProperty().bind(imageView.fitHeightProperty().divide(2));
        }

        node.getTransforms().add(rotate);

        // Define "in" and "out" transitions
        Timeline rotateIn = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(rotate.angleProperty(), -10)));
        TranslateTransition translateIn = new TranslateTransition(Duration.millis(200), node);
        translateIn.setByY(-5);
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), node);
        scaleIn.setToX(1.1);
        scaleIn.setToY(1.1);
        ParallelTransition hoverIn = new ParallelTransition(rotateIn, translateIn, scaleIn);

        Timeline rotateOut = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(rotate.angleProperty(), 0)));
        TranslateTransition translateOut = new TranslateTransition(Duration.millis(200), node);
        translateOut.setToY(0);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), node);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);
        ParallelTransition hoverOut = new ParallelTransition(rotateOut, translateOut, scaleOut);

        // Attach event handlers
        node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            hoverOut.stop();
            hoverIn.play();
        });

        node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            hoverIn.stop();
            hoverOut.play();
        });
    }


    // =================================================================================
    //  3. Public Methods to Combine Animation and Sound
    // =================================================================================

    /**
     * Applies the 3D hover animation and a sound effect to a Button.
     *
     * @param button        The button to animate.
     * @param soundFilePath The path to the sound file to play on hover.
     */
    public static void applyEffect(Button button, String soundFilePath) {
        apply3DHoverAnimation(button);
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> playSound(soundFilePath));
    }

    /**
     * Applies the 3D hover animation (without sound) to a MenuButton.
     *
     * @param menuButton The MenuButton to animate.
     */
    public static void applyEffect(MenuButton menuButton) {
        apply3DHoverAnimation(menuButton);
    }

    /**
     * Applies the 3D hover animation (without sound) to an ImageView.
     *
     * @param imageView The ImageView to animate.
     */
    public static void applyEffect(ImageView imageView) {
        apply3DHoverAnimation(imageView);
    }


    /**
     * Applies a pseudo hover effect and optional sound to a Menu (note: no real 3D animation possible).
     *
     * @param menu          The Menu to style.
     * @param soundFilePath Optional sound file path to play on hover/show.
     */
    public static void applyEffect(Menu menu, String soundFilePath) {
        if (soundFilePath != null && !soundFilePath.isEmpty()) {
            menu.setOnShowing(event -> Button3DEffect.playSound(soundFilePath));
        }
        // Add custom style class if needed
        menu.getStyleClass().add("menu-3d-hover");

    }
}