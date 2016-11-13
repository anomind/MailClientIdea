package com.ano;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.IOException;

/**
 * Created by ano on 03.10.16.
 */
public class MailUtils {
    public static String cutSubject (String raw){
        String[] temp =raw.split(" ");
        StringBuilder res = new StringBuilder();
        if (temp.length>3) {
        for (int i = 0; i < 4; i++) {
            if (res.length()+temp[i].length()<30) {
                res.append(temp[i]);
                res.append(' ');
            }
        }
        res.append("...");
        return res.toString();}
        else return raw;
    }


    /**
     * Return the primary text content of the message.
     */
    public static String getText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
           // textIsHtml = p.isMimeType("text/html");
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return null;
    }


}
