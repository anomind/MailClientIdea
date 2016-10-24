package com.ano;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class Main extends Application {
    private Preferences preferences = Preferences.userNodeForPackage(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
           if (preferences.get("login","1").equals("1")
                || preferences.get("password","1").equals("1")
                || preferences.get("imap","1").equals("1")) {
            Parent root = FXMLLoader.load(getClass().getResource("auth.fxml"));
            primaryStage.setTitle("Mail");
            primaryStage.setScene(new Scene(root, 500, 400));
            primaryStage.show();
        }
        else new MainWindow();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
