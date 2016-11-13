package com.ano;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.simplejavamail.email.Email;
import org.simplejavamail.internal.util.ConfigLoader;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.ServerConfig;
import org.simplejavamail.mailer.config.TransportStrategy;

import javax.mail.Message;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * Created by ano on 10.11.16.
 */
public class NewMailWindowController implements Initializable {

    private Preferences preferences = Preferences.userNodeForPackage(Main.class);

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

            ConfigLoader.loadProperties("simplejavamail.properties",true); // optional default
            ConfigLoader.loadProperties("overrides.properties",true); // optional extra
            Email email = new Email();

            email.addRecipient("lol",toField.getText(), Message.RecipientType.TO);
            email.setFromAddress("ya", preferences.get("login","1"));
            email.setSubject(themeField.getText());
            email.setText(messageArea.getText());

            new Mailer(
                    new ServerConfig("smtp.yandex.ru", 465, preferences.get("login","1"), preferences.get("password","1")),
                    TransportStrategy.SMTP_SSL
            ).sendMail(email);
        });
    }
}
