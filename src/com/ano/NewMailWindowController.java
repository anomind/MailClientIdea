package com.ano;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ano on 10.11.16.
 */
public class NewMailWindowController implements Initializable {

    @FXML
    TextField toField;
    @FXML
    TextField themeField;
    @FXML
    TextArea messageArea;
    @FXML
    Button addButton;
    @FXML
    Button sendButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        sendButton.setOnAction(event -> {
        });
    }
}
