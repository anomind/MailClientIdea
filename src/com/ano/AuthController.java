package com.ano;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.mail.*;
import java.util.Properties;
import java.util.prefs.Preferences;

public class AuthController {
    @FXML
    private Button loginButton;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private Label errLabel;



    @FXML
    public void onLoginButtonClick(ActionEvent actionEvent) {
        errLabel.setVisible(false);
        if (!AddressChecker.checkAddress(emailField.getText())){
            emailField.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
            errLabel.setVisible(true);
        }

        else {
            String address = emailField.getText();
            String[] temp =address.split("@");
            String imap = "imap."+temp[1];
            String pass = passwordField.getText();
            Properties props = new Properties();
            //включение debug-режима
            props.put("mail.debug", "true");
            //Указываем протокол - IMAP с SSL
            props.put("mail.store.protocol", "imaps");
            Session session = Session.getInstance(props);
            //подключаемся к почтовому серверу
            try {
                Store store = session.getStore();
                store.connect(imap, address, pass);
                store.close();

                    Preferences preferences = Preferences.userNodeForPackage(Main.class);
                    preferences.put("login",address);
                    preferences.put("password",pass); //TODO Хранить шифрованным
                    preferences.put("imap",imap);

                new MainWindow();
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                e.printStackTrace();
                errLabel.setVisible(true);
            }
        }
    }
}
