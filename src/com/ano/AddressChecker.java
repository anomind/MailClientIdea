package com.ano;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ano on 25.09.16.
 */
public class AddressChecker {

    public static boolean checkAddress(String address){
        if (!address.contains("@")) return false;
        if (!address.contains(".")) return false;
        if (address.contains(" ")) return false;
        return true;
    }
}
