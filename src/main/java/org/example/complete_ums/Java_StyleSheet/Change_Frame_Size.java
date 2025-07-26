package org.example.complete_ums.Java_StyleSheet;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.scene.Cursor.*;

public class Change_Frame_Size {

    private double xOffset = 0;
    private double yOffset = 0;
    private final int RESIZE_MARGIN = 8;
    private boolean isDragging = false;

    /**
     * Enables dragging the window from a title bar area.
     */
    public void enableWindowDragging(Node draggableNode, Stage stage) {
        draggableNode.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        draggableNode.setOnMouseDragged(event -> {
            if (!isDragging) return;
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        draggableNode.setOnMouseEntered(event -> draggableNode.setCursor(Cursor.DEFAULT));
        draggableNode.setOnMousePressed(event -> isDragging = true);
        draggableNode.setOnMouseReleased(event -> isDragging = false);
    }

    /**
     * Adds resizing support on all 4 borders and corners.
     */
    public void enableResize(Stage stage, Scene scene) {
        scene.setOnMouseMoved(event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            double width = stage.getWidth();
            double height = stage.getHeight();

            boolean left = mouseX < RESIZE_MARGIN;
            boolean right = mouseX > width - RESIZE_MARGIN;
            boolean top = mouseY < RESIZE_MARGIN;
            boolean bottom = mouseY > height - RESIZE_MARGIN;

            if (left && top) {
                scene.setCursor(Cursor.NW_RESIZE);
            } else if (right && top) {
                scene.setCursor(Cursor.NE_RESIZE);
            } else if (left && bottom) {
                scene.setCursor(Cursor.SW_RESIZE);
            } else if (right && bottom) {
                scene.setCursor(Cursor.SE_RESIZE);
            } else if (left) {
                scene.setCursor(Cursor.W_RESIZE);
            } else if (right) {
                scene.setCursor(E_RESIZE);
            } else if (top) {
                scene.setCursor(N_RESIZE);
            } else if (bottom) {
                scene.setCursor(S_RESIZE);
            } else {
                scene.setCursor(Cursor.DEFAULT);
            }
        });

        scene.setOnMouseDragged(event -> {
            Cursor cursor = scene.getCursor();
            if (cursor == Cursor.DEFAULT) return;

            double mouseX = event.getScreenX();
            double mouseY = event.getScreenY();
            double stageX = stage.getX();
            double stageY = stage.getY();
            double stageWidth = stage.getWidth();
            double stageHeight = stage.getHeight();

            if (cursor == NW_RESIZE) {
                double newWidth = stageWidth - (mouseX - stageX);
                double newHeight = stageHeight - (mouseY - stageY);
                if (newWidth > 400) {
                    stage.setX(mouseX);
                    stage.setWidth(newWidth);
                }
                if (newHeight > 300) {
                    stage.setY(mouseY);
                    stage.setHeight(newHeight);
                }
            } else if (cursor == NE_RESIZE) {
                double newWidth = mouseX - stageX;
                double newHeight = stageHeight - (mouseY - stageY);
                if (newWidth > 400) stage.setWidth(newWidth);
                if (newHeight > 300) {
                    stage.setY(mouseY);
                    stage.setHeight(newHeight);
                }
            } else if (cursor == SW_RESIZE) {
                double newWidth = stageWidth - (mouseX - stageX);
                double newHeight = mouseY - stageY;
                if (newWidth > 400) {
                    stage.setX(mouseX);
                    stage.setWidth(newWidth);
                }
                if (newHeight > 300) stage.setHeight(newHeight);
            } else if (cursor == SE_RESIZE) {
                double newWidth = mouseX - stageX;
                double newHeight = mouseY - stageY;
                if (newWidth > 400) stage.setWidth(newWidth);
                if (newHeight > 300) stage.setHeight(newHeight);
            } else if (cursor == W_RESIZE) {
                double newWidth = stageWidth - (mouseX - stageX);
                if (newWidth > 400) {
                    stage.setX(mouseX);
                    stage.setWidth(newWidth);
                }
            } else if (cursor == E_RESIZE) {
                double newWidth = mouseX - stageX;
                if (newWidth > 400) stage.setWidth(newWidth);
            } else if (cursor == N_RESIZE) {
                double newHeight = stageHeight - (mouseY - stageY);
                if (newHeight > 300) {
                    stage.setY(mouseY);
                    stage.setHeight(newHeight);
                }
            } else if (cursor == S_RESIZE) {
                double newHeight = mouseY - stageY;
                if (newHeight > 300) stage.setHeight(newHeight);
            }

        });
    }
}