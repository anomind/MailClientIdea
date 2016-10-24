package com.ano;

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


}
