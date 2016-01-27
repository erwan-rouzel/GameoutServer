package com.gameout.network;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by erwanrouzel on 26/01/2016.
 */
public class LogHelper {

    public static void log(String label, String message) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        System.out.println("[" + sdf.format(now) + "]-[" + label + "] " + message);
    }

    public static void log(String label, Exception exception) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        System.out.println("[" + sdf.format(now) + "]-[" + label + "] " + exception.toString());
        exception.printStackTrace();
    }
}
