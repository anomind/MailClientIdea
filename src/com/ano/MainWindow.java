package com.ano;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Created by ano on 26.09.16.
 */
public class MainWindow {
    public MainWindow ()throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
        HBox load = (HBox) loader.load();
        Stage stage = new Stage();
        stage.setTitle("Mail");
        Scene scene = new Scene(load,1000,600);
        stage.setScene(scene);
        stage.show();
    }
}
