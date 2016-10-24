package com.ano;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by ano on 12.10.16.
 */
public class MenuList {
    private Image icon;
    private String name;

    MenuList(String name,Image icon){
        this.name=name;
        this.icon=icon;
    }

    public Image getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
}
