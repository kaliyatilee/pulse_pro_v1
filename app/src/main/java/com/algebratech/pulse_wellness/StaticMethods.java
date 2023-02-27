package com.algebratech.pulse_wellness;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StaticMethods {
    public static String  TimestampTodate(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000);
        String date = DateFormat.format("HH:mm:ss", cal).toString();
        return date;
    }

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

    public static  String getTime(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000);
        String date = DateFormat.format("HH:mm", cal).toString();
        return date;
    }

    public static String calculateTime(long seconds) {
        int day = (int)TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);

        long minute = TimeUnit.SECONDS.toMinutes(seconds) -
                (TimeUnit.SECONDS.toHours(seconds)* 60);

        long second = TimeUnit.SECONDS.toSeconds(seconds) -
                (TimeUnit.SECONDS.toMinutes(seconds) *60);

        String hr = hours + " Hrs " + minute + " min ";

        return  hr;
    }

    public static int findMax(int[] array) {
        if (array == null || array.length < 1)
            return 0;
        int min = array[0];
        int max = array[0];

        for (int i = 1; i <= array.length - 1; i++) {
            if (max < array[i]) {
                max = array[i];
            }

            if (min > array[i]) {
                min = array[i];
            }
        }
       return max;
    }

    public static int findMin(int[] array) {
        if (array == null || array.length < 1)
            return 0;
        int min = array[0];
        int max = array[0];

        for (int i = 1; i <= array.length - 1; i++) {
            if (max < array[i]) {
                max = array[i];
            }

            if (min > array[i]) {
                min = array[i];
            }
        }
      return  min;
    }

}
