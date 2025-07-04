package org.example.university_management_system.Java_StyleSheet;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class RoundedImage {

    public void makeImageViewRounded(ImageView imageView) {
        double radius = Math.min(imageView.getFitWidth(), imageView.getFitHeight()) / 2;
        Circle clip = new Circle(radius, radius, radius);
        imageView.setClip(clip);
    }

}
