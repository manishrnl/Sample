package org.example.complete_ums.Java_StyleSheet;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class EdgeColorAnimation {

    public static void applyClockwiseEdgeEffect(Button button) {
        // Premium glowing shadows in jewel tones
        DropShadow top = new DropShadow(15, 0, -6, Color.CYAN);
        DropShadow right = new DropShadow(15, 6, 0, Color.DEEPPINK);
        DropShadow bottom = new DropShadow(15, 0, 6, Color.GOLD);
        DropShadow left = new DropShadow(15, -6, 0, Color.ORCHID);

        // Optional: subtle glow effect
        Glow baseGlow = new Glow(0.3);
        Effect topGlow = new javafx.scene.effect.Blend(javafx.scene.effect.BlendMode.SRC_OVER, baseGlow, top);
        Effect rightGlow = new javafx.scene.effect.Blend(javafx.scene.effect.BlendMode.SRC_OVER, baseGlow, right);
        Effect bottomGlow = new javafx.scene.effect.Blend(javafx.scene.effect.BlendMode.SRC_OVER, baseGlow, bottom);
        Effect leftGlow = new javafx.scene.effect.Blend(javafx.scene.effect.BlendMode.SRC_OVER, baseGlow, left);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.0), e -> button.setEffect(topGlow)),
                new KeyFrame(Duration.seconds(0.125), e -> button.setEffect(rightGlow)),
                new KeyFrame(Duration.seconds(0.25), e -> button.setEffect(bottomGlow)),
                new KeyFrame(Duration.seconds(0.375), e -> button.setEffect(leftGlow)),
                new KeyFrame(Duration.seconds(0.50), e -> button.setEffect(topGlow)),
                new KeyFrame(Duration.seconds(0.625), e -> button.setEffect(rightGlow)),
                new KeyFrame(Duration.seconds(0.75), e -> button.setEffect(bottomGlow)),
                new KeyFrame(Duration.seconds(0.875), e -> button.setEffect(leftGlow))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();

        // On hover exit — stop effect
        button.setOnMouseExited(e -> {
            timeline.stop();
            button.setEffect(null);
        });

        button.setOnMouseEntered(e -> {
            timeline.play();
        });
    }
}
