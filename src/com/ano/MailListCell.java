package com.ano;

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Created by ano on 02.10.16.
 */
public class MailListCell extends ListCell <Mail>{
    @Override
    protected void updateItem(Mail item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item == null) {

            setText(null);
            setGraphic(null);
        } else {
            Label from = new Label(item.getFrom());
            Label text = new Label(item.getText());
            if (!item.isSeen()){
                from.setFont(Font.font("Verdana",FontWeight.BOLD, 14));
            }
            ImageView icon =new ImageView("com/ano/icon.png");
            icon.setFitHeight(32); icon.setFitWidth(32);
            setGraphic(new HBox(icon, new VBox(from,text)));
        }
    }
}
