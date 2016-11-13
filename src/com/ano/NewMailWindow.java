package com.ano;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by ano on 10.11.16.
 */
public class NewMailWindow {
    int messageNumber;

    public NewMailWindow () throws  Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("newMailWindow.fxml"));
        VBox load = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Новое сообщение");
        Scene scene = new Scene(load,750,500);
        stage.setScene(scene);
        stage.show();
    }
}
