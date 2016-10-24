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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by ano on 02.10.16.
 */
public class MainWindowController implements Initializable{
    @FXML
    public ListView mailList;
    public ListView folderList;
    public WebView webBrowser;
    public Button loadMore;

    int count =0;
    void updateList () {
        MenuList inbox = new MenuList("Inbox", new Image("com/ano/inbox.png"));
        MenuList outbox = new MenuList("Outbox", new Image("com/ano/outbox.png"));
        MenuList spam = new MenuList("Spam", new Image("com/ano/spam.png"));
        folderList.setCellFactory(param -> new MenuListCell());
        folderList.getItems().addAll(inbox,outbox,spam);
        mailList.setCellFactory(param -> new MailListCell());
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateList();
        WebEngine engine =webBrowser.getEngine();
        String address = "anomind@ya.ru";
        String[] temp =address.split("@");
        String imap = "imap."+temp[1];
        String pass = "222222";
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
            //получаем последнее сообщение (самое старое будет под номером 1)
            for (int i = inbox.getMessageCount(); i > inbox.getMessageCount()-16; i--) {
                Message m = inbox.getMessage(i);
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
                Mail mail = new Mail(fromstr, text, i, seen);
                list.add(mail);
            }
            mailList.setItems(list);

            mailList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Mail>() {
                @Override
                public void changed(ObservableValue <?extends Mail> observable, Mail oldValue, Mail newValue) {
                    try {
                        Message message = inbox.getMessage(newValue.getCount());
                        message.setFlag(Flags.Flag.SEEN,true);

                        if (message.getContentType().contains("text"))
                        engine.loadContent((String)message.getContent());
                        if (message.getContentType().contains("ultipart")){
                            Multipart mp = (Multipart) message.getContent();
                            BodyPart bp = mp.getBodyPart(mp.getCount()-1);
                            engine.loadContent((String)bp.getContent());
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
                            Message m = inbox.getMessage(i);
                            Address[] from = m.getFrom();
                            String fromstr = from[0].toString();
                            fromstr = MimeUtility.decodeText(fromstr);
                            String text = m.getSubject();
                            text = MailUtils.cutSubject(text);
                            boolean seen;
                            if (m.isSet(Flags.Flag.SEEN)) {
                                seen = true;
                            } else seen = false;
                            Mail mail = new Mail(fromstr, text, i, seen);
                            list.add(mail);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            //store.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
