package com.ano;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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
    public Button attachmentsButton;
    public Button replyButton;
    private ProgressIndicator indicator = new ProgressIndicator();


    int count =0;
    boolean isOutbox = false;
    void updateList () {
        mailList.setCellFactory(param -> new MailListCell());
        ImageView inboxView = new ImageView("com/ano/inbox.png");
        inboxView.setFitWidth(32); inboxView.setFitHeight(32);
        inboxButton.setGraphic(inboxView);
        ImageView outboxView = new ImageView("com/ano/outbox.png");
        outboxView.setFitWidth(32); outboxView.setFitHeight(32);
        outboxButton.setGraphic(outboxView);
        newMailButton.setGraphic(new ImageView("com/ano/newmail.png"));
        settingsButton.setGraphic(new ImageView("com/ano/delete.png"));
        indicator.setVisible(false);
        indicator.setStyle("-fx-progress-color: white");
        indicator.setMaxSize(25,25);
        loadMore.setGraphic(indicator);

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateList();
        WebEngine engine =webBrowser.getEngine();
        engine.loadContent("<style>" +
                "body {\n" +
                "    background-image: url(https://raw.githubusercontent.com/anomind/MailClientIdea/master/src/com/ano/at.png); /* Путь к фоновому изображению */\n"+
                "   }\n" +
                "  </style>");
        Preferences preferences = Preferences.userNodeForPackage(Main.class);
        preferences.put("mesSub","1");
        preferences.put("mesRep","1");
        preferences.put("message","-1");
        preferences.putInt("listIndex",-1);
        String address = preferences.get("login","1");
        String imap = preferences.get("imap","1");
        String pass = preferences.get("password","1");
        Properties props = new Properties();
        //включение debug-режима
        props.put("mail.debug", "true");
        //Указываем протокол - IMAP с SSL
        props.put("mail.store.protocol", "imaps");
        Session session = Session.getInstance(props);
        try {
            Store store = session.getStore();
            store.connect(imap, address, pass);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            Folder outbox = store.getFolder("Отправленные");
            outbox.open(Folder.READ_WRITE);
            ObservableList<Mail> list = FXCollections.observableArrayList();
           // FetchProfile fetchProfile = new FetchProfile();
           // fetchProfile.add(FetchProfile.Item.CONTENT_INFO);

                Thread t = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            for (int i = inbox.getMessageCount(); i > inbox.getMessageCount()-16; i--) {
                                Message m = inbox.getMessage(i);
                                Address[] from = m.getFrom();
                                String fromstr = from[0].toString();
                                fromstr = MimeUtility.decodeText(fromstr);
                                String temp[] = fromstr.split("<");
                                fromstr = temp[0];
                                String text = m.getSubject();
                                text = MailUtils.cutSubject(text);
                                //store.isConnected();
                                boolean seen;
                                seen = m.isSet(Flags.Flag.SEEN);
                                Mail mail = new Mail(fromstr, text, i, seen);
                                list.add(mail);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                t.start();

            mailList.setItems(list);
            //LISTENERS
            mailList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Mail>() {
                @Override
                public void changed(ObservableValue <?extends Mail> observable, Mail oldValue, Mail newValue) {
                    try {
                        Message message;
                        if (isOutbox)
                           message = outbox.getMessage(newValue.getCount());
                        else
                            message = inbox.getMessage(newValue.getCount());
                        message.setFlag(Flags.Flag.SEEN,true);
                        preferences.put("message",String.valueOf(message.getMessageNumber()));
                        preferences.putInt("listIndex",list.indexOf(newValue));
                        if (message.getContentType().contains("text"))
                        engine.loadContent((String)message.getContent());
                        if (message.getContentType().contains("ultipart")){
                            MimeMultipart mp = (MimeMultipart) message.getContent();
                            engine.loadContent("<style>" +
                                    "body {\n" +
                                    "    background-image: url(https://raw.githubusercontent.com/anomind/MailClientIdea/master/src/com/ano/at.png); /* Путь к фоновому изображению */\n"+
                                    "   }\n" +
                                    "  </style>");
                            for (int i = 0; i<mp.getCount(); i++) {
                                MimeBodyPart bp = (MimeBodyPart) mp.getBodyPart(i);
                                if (bp.getContentType().contains("text/html")) {
                                    engine.loadContent((String) bp.getContent());
                                }
                                if (!bp.getContentType().contains("application"))
                                    attachmentsButton.setVisible(false);
                                if ((bp.getContentType().contains("application")||bp.getContentType().contains("image"))
                                        &&bp.getDisposition().equalsIgnoreCase("attachment")) {
                                    attachmentsButton.setVisible(true);
                                    Thread thread = new Thread(){
                                        @Override
                                        public void run() {
                                            try {
                                                if (!bp.getFileName().isEmpty()) {
                                                    super.run();
                                                    new File("/tmp/" + newValue.getCount()).mkdirs();
                                                    bp.saveFile("/tmp/" + newValue.getCount() + "/" + bp.getFileName());
                                                    String dir = "/tmp/" + newValue.getCount();
                                                    props.put("attachdir", dir);
                                                }
                                            } catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    thread.start();
                                }
                                engine.loadContent(MailUtils.getText(message));
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
                        count++;
                            Thread s = new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        Platform.runLater(()-> indicator.setVisible(true));
                                        for (int i = inbox.getMessageCount()-count*15; i > inbox.getMessageCount()-16-count*15; i--) {

                                            Message m = inbox.getMessage(i);
                                             Address[] from = m.getFrom();
                                            String fromstr = from[0].toString();
                                            fromstr = MimeUtility.decodeText(fromstr);
                                            fromstr = fromstr.split("<")[0];
                                            String text = m.getSubject();
                                            text = MailUtils.cutSubject(text);
                                            boolean seen;
                                            seen = m.isSet(Flags.Flag.SEEN);
                                            Mail mail = new Mail(fromstr, text, i, seen);
                                            list.add(mail);
                                            }
                                        Platform.runLater(()-> indicator.setVisible(false));
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            };
                            s.start();
                }
            });
            attachmentsButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        Runtime.getRuntime().exec("nautilus "+props.getProperty("attachdir"));
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            newMailButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        preferences.put("mesSub","1");
                        preferences.put("mesRep","1");
                        new NewMailWindow();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            outboxButton.setOnAction(event -> {
                Thread out = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            isOutbox=true;
                            loadMore.setVisible(false);
                            list.clear();
                            for (int i = outbox.getMessageCount(); i > outbox.getMessageCount()-16; i--) {
                                Message m = outbox.getMessage(i);
                                Address[] from = m.getRecipients(Message.RecipientType.TO);
                                String fromstr = from[0].toString();
                                fromstr = MimeUtility.decodeText(fromstr);
                                String temp[] = fromstr.split("<");
                                fromstr = temp[0];
                                String text = m.getSubject();
                                text = MailUtils.cutSubject(text);
                                //store.isConnected();
                                boolean seen;
                                seen = m.isSet(Flags.Flag.SEEN);
                                Mail mail = new Mail(fromstr, text, i, seen);
                                list.add(mail);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                out.start();
            });
            inboxButton.setOnAction(event -> {
                Thread in = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            isOutbox=false;
                            loadMore.setVisible(true);
                            list.clear();
                            count=0;
                            for (int i = inbox.getMessageCount(); i > inbox.getMessageCount()-16; i--) {
                                Message m = inbox.getMessage(i);
                                Address[] from = m.getReplyTo();
                                String fromstr = from[0].toString();
                                fromstr = MimeUtility.decodeText(fromstr);
                                String temp[] = fromstr.split("<");
                                fromstr = temp[0];
                                String text = m.getSubject();
                                text = MailUtils.cutSubject(text);
                                //store.isConnected();
                                boolean seen;
                                seen = m.isSet(Flags.Flag.SEEN);
                                Mail mail = new Mail(fromstr, text, i, seen);
                                list.add(mail);
                            }
                        }catch (Exception e){
                         e.printStackTrace();
                        }
                    }
                };
                in.start();
            });
            replyButton.setOnAction(event ->{
                int mesNumber = Integer.parseInt(preferences.get("message","-1"));
                try {
                if (mesNumber>0) {
                    Message m =inbox.getMessage(mesNumber);
                    preferences.put("mesSub", m.getSubject());
                    Address[] from = m.getReplyTo();
                    String fromstr = from[0].toString();
                    fromstr = MimeUtility.decodeText(fromstr);
                    String temp[] = fromstr.split("<");
                    fromstr = temp[1];
                    temp=fromstr.split(">");
                    fromstr = temp[0];
                    preferences.put("mesRep", fromstr);
                }
                    new NewMailWindow();
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
            settingsButton.setOnAction(event -> {
                int mesNumber = Integer.parseInt(preferences.get("message","-1"));
                if (mesNumber>0){
                    try {
                        Message m = inbox.getMessage(mesNumber);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Удаление");
                        alert.setHeaderText("Вы действительно хотите удалить сообщение?");
                        //alert.setContentText("Are you ok with this?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            // ... user chose OK
                            m.setFlag(Flags.Flag.DELETED, true);
                            inbox.close(true);
                            inbox.open(Folder.READ_WRITE);
                            list.remove(preferences.getInt("listIndex", -1));
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
