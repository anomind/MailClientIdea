package com.ano;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import sun.misc.ThreadGroupUtils;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * Created by ano on 02.10.16.
 */
public class MainWindowController implements Initializable{
    @FXML
    public ListView mailList;
    public WebView webBrowser;
    public Button loadMore;
    public Button inboxButton;
    public Button outboxButton;
    public Button newMailButton;
    public Button settingsButton;


    int count =0;
    void updateList () {
        mailList.setCellFactory(param -> new MailListCell());
        ImageView inboxView = new ImageView("com/ano/inbox.png");
        inboxView.setFitWidth(32); inboxView.setFitHeight(32);
        inboxButton.setGraphic(inboxView);
        ImageView outboxView = new ImageView("com/ano/outbox.png");
        outboxView.setFitWidth(32); outboxView.setFitHeight(32);
        outboxButton.setGraphic(outboxView);
        newMailButton.setGraphic(new ImageView("com/ano/newmail.png"));
        settingsButton.setGraphic(new ImageView("com/ano/settings.png"));

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateList();
        WebEngine engine =webBrowser.getEngine();
        Preferences preferences = Preferences.userNodeForPackage(Main.class);

        String address = preferences.get("login","1");
        String imap = preferences.get("imap","1");
        String pass = preferences.get("password","1");
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
            Folder inbox = store.getFolder("INBOX");
            //открываем её только для чтения
            inbox.open(Folder.READ_WRITE);
            ObservableList<Mail> list = FXCollections.observableArrayList();
            FetchProfile fetchProfile = new FetchProfile();
            fetchProfile.add(FetchProfile.Item.CONTENT_INFO);
            for (int i = inbox.getMessageCount(); i > inbox.getMessageCount()-16; i--) {
                int in =i;
                Thread t = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Message m = inbox.getMessage(in);
                            Address[] from = m.getFrom();
                            String fromstr = from[0].toString();
                            fromstr = MimeUtility.decodeText(fromstr);
                            String text = m.getSubject();
                            text=MailUtils.cutSubject(text);
                            store.isConnected();
                            boolean seen;
                            if (m.isSet(Flags.Flag.SEEN)) {
                                seen=true;
                            }
                            else seen=false;
                            Mail mail = new Mail(fromstr, text, in, seen);
                            list.add(mail);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                t.run();
            }
            mailList.setItems(list);
            //LISTENERS
            mailList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Mail>() {
                @Override
                public void changed(ObservableValue <?extends Mail> observable, Mail oldValue, Mail newValue) {
                    try {
                        Message message = inbox.getMessage(newValue.getCount());
                        message.setFlag(Flags.Flag.SEEN,true);

                        if (message.getContentType().contains("text"))
                        engine.loadContent((String)message.getContent());
                        if (message.getContentType().contains("ultipart")){
                            MimeMultipart mp = (MimeMultipart) message.getContent();
                            for (int i = 0; i<mp.getCount(); i++) {
                                MimeBodyPart bp = (MimeBodyPart) mp.getBodyPart(i);
                                if (bp.getContentType().contains("text/html")) {
                                    engine.loadContent((String) bp.getContent());
                                }
                                if (bp.getContentType().contains("application")) {
                                    bp.saveFile("/home/ano/recieve/" + bp.getFileName());
                                }
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            loadMore.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
                @Override
                public void handle(javafx.event.ActionEvent event) {
                    try {
                        count++;
                        for (int i = inbox.getMessageCount()-count*15; i > inbox.getMessageCount()-16-count*15; i--) {
                            int in=i;
                            Thread s = new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        Message m = inbox.getMessage(in);
                                        Address[] from = m.getFrom();
                                        String fromstr = from[0].toString();
                                        fromstr = MimeUtility.decodeText(fromstr);
                                        String text = m.getSubject();
                                        text = MailUtils.cutSubject(text);
                                        boolean seen;
                                        if (m.isSet(Flags.Flag.SEEN)) {
                                            seen = true;
                                        } else seen = false;
                                        Mail mail = new Mail(fromstr, text, in, seen);
                                        list.add(mail);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            };
                            s.run();

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
