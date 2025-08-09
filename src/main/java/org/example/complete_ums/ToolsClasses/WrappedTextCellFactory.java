package org.example.complete_ums.ToolsClasses;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

public class WrappedTextCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
    @Override
    public TableCell<S, T> call(TableColumn<S, T> col) {
        return new TableCell<>() {
            private final Text text = new Text();
            private final VBox container = new VBox(text);

            {
                text.setFont(Font.font("System", 14));
                text.setTextAlignment(TextAlignment.CENTER);
                text.wrappingWidthProperty().bind(col.widthProperty().subtract(20));

                container.setPadding(new Insets(8));
                container.setAlignment(Pos.CENTER);
                container.setFillWidth(true);

                setGraphic(container);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    text.setText(null);
                } else {
                    text.setText(item.toString());

                    Platform.runLater(() -> {
                        setPrefHeight(container.prefHeight(-1) + 10);
                    });
                }
            }
        };
    }

}

/*
 1. To use this WrappedTextCellFactory, you can set it to a TableColumn in your JavaFX  application like this:
        tables_Column_Name.setCellFactory(new WrappedTextCellFactory<tables_Name, Data_Type_like_String>());


2.To highlight the particular row based on certain matching value , use code below in
Initialise method()

        NotificationsTable notificationsTable=new NotificationsTable(); //Class level Object Creation

        notificationsTable.setRowFactory(tv -> new TableRow<NotificationsTable>() {
        @Override
        protected void updateItem(NotificationsTable item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setStyle(""); // No style
            } else if (item.getTargetUserId() == sessionManager.getUserID()) Only change
            this line to your desired value
            {
                // Highlight this row
                setStyle("-fx-background-color: #88ea77;");
            } else {
                setStyle(""); // Reset style
            }
        }
    });


 */