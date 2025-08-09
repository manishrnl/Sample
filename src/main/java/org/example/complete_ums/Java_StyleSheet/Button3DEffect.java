package org.example.complete_ums.Java_StyleSheet;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.net.URL;

public class Button3DEffect {

    public static void playSound(String soundFilePath) {
        if (soundFilePath == null || soundFilePath.isEmpty()) {
            return;
        }
        try {
            URL resource = Button3DEffect.class.getResource(soundFilePath);
            if (resource != null) {
//               Media sound = new Media(resource.toString());
//                MediaPlayer mediaPlayer = new MediaPlayer(sound);
//                mediaPlayer.setOnEndOfMedia(mediaPlayer::dispose); // Clean up after playback
//                mediaPlayer.play();
            } else {

            }
        } catch (Exception e) {
            System.err.println("Error playing audio file: " + soundFilePath);
            e.printStackTrace();
        }
    }

    private static void apply3DHoverAnimation(Node node) {
        Rotate rotate = new Rotate(0, Rotate.X_AXIS);

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

        node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            hoverOut.stop();
            hoverIn.play();
        });

        node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            hoverIn.stop();
            hoverOut.play();
        });
    }

    public static void applyEffect(Button button, String soundFilePath) {
        apply3DHoverAnimation(button);
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> playSound(soundFilePath));
    }

    public static void applyEffect(MenuItem menuItem, String soundFilePath) {
        if (soundFilePath != null && !soundFilePath.isEmpty()) {
            menuItem.setOnAction(event -> playSound(soundFilePath));
        }
    }

    public static void applyEffect(MenuItem menuItem) {
        // No visual 3D effect can be applied directly to MenuItem.
        // Leave empty or use for logging/styling.
    }


    public static void applyEffect(MenuButton menuButton) {
        apply3DHoverAnimation(menuButton);
    }

    public static void applyEffect(MenuButton menuButton, String soundFilePath) {
        apply3DHoverAnimation(menuButton);
        menuButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> playSound(soundFilePath));
    }

    public static void applyEffect(ImageView imageView, String soundFilePath) {
        apply3DHoverAnimation(imageView);
        imageView.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> playSound(soundFilePath));
    }

    public static void applyEffect(ImageView imageView) {
        apply3DHoverAnimation(imageView);
    }

    public static void applyEffect(Menu menu, String soundFilePath) {
        if (soundFilePath != null && !soundFilePath.isEmpty()) {
            menu.setOnShowing(event -> Button3DEffect.playSound(soundFilePath));
        }
        menu.getStyleClass().add("menu-3d-hover");

    }
}