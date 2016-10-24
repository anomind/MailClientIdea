package com.ano;

import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;


/**
 * Created by ano on 12.10.16.
 */
public class MenuListCell extends ListCell <MenuList> {
    @Override
    protected void updateItem(MenuList item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item == null) {

            setText(null);
            setGraphic(null);
        } else {
            ImageView icon = new ImageView(item.getIcon());
            icon.setFitHeight(32); icon.setFitWidth(32);
            setGraphic(icon);
        }
}
}

