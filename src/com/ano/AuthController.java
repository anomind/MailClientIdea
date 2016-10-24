package com.ano;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private CheckBox rememberCheckBox;
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

                if (rememberCheckBox.isSelected()){
                    Preferences preferences = Preferences.userNodeForPackage(Main.class);
                    preferences.put("login",address);
                    preferences.put("password",pass); //TODO Хранить шифрованным
                    preferences.put("imap",imap);
                }
                new MainWindow();

               /* //получаем папку с входящими сообщениями
                Folder inbox = store.getFolder("INBOX");
                //открываем её только для чтения
                inbox.open(Folder.READ_ONLY);
                //получаем последнее сообщение (самое старое будет под номером 1)
                Message m = inbox.getMessage(inbox.getMessageCount());
                Multipart mp = (Multipart) m.getContent();
                BodyPart bp = mp.getBodyPart(0);
                //Выводим содержимое на экран
                System.out.println(bp.getContent()); */

            } catch (Exception e) {
                e.printStackTrace();
                errLabel.setVisible(true);
            }



        }
        //emailField.setText(passwordField.getText());
        //Stage stage = (Stage)loginButton.getScene().getWindow();
        //stage.close();

    }
}
