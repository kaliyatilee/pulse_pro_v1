package com.algebratech.pulse_wellness;

import android.text.format.DateFormat;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Locale;

public class Helper {
    public static String  getDate(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000);
        String date = DateFormat.format("MMddyyyy", cal).toString();
        return date;
    }
    public static String  ConvertTimestamp(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000);
        String date = DateFormat.format("MM/dd/yyyy HH:mm:ss", cal).toString();
        return date;
    }
    public static void showNotification(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public static long timestamp = System.currentTimeMillis()/1000;
}
