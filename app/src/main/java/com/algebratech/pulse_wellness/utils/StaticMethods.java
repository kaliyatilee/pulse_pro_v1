package com.algebratech.pulse_wellness.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class StaticMethods {
    Context context;

    public static Double StepsToCurrency(double steps, double pointValue) {
        double pulsePoints = steps * pointValue;
        return pulsePoints;
    }

    public static double calculateAverage(List<Integer> marks) {
        Integer sum = 0;
        if (!marks.isEmpty()) {
            for (Integer mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }


    public static String getDate(long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000);
        String date = DateFormat.format("MMddyyyy", cal).toString();
        return date;
    }

    public static String ConvertTimestamp(long timestamp) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000);
        String date = DateFormat.format("MM/dd/yyyy HH:mm:ss", cal).toString();
        return date;
    }

    public static String TimestampTodate(long timestamp) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000);
        String date = DateFormat.format("MM/dd/yyyy", cal).toString();
        return date;
    }

    public static String correctDate(String date,String org_format,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(org_format);
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String newFormat = formatter.format(testDate);
       return newFormat;
    }

    public static String GetFullname(String userid) {
        String fullname = null;
        return fullname;
    }

    public static double calculateBmi(double mW, double mH) {
        double mBmi = (mW / (Math.pow(mH, 2))) * 10000;
        //double finalBmi = Double.parseDouble(String.format("%.2f", mBmi));
        BigDecimal round = new BigDecimal(mBmi).setScale(2, RoundingMode.HALF_UP);
        double finalresult = round.doubleValue();
        return finalresult;
    }

    public static double hoursToseconds(int hours) {
        double seconds = 0;

        try {
            seconds = hours * 60 * 60;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seconds;
    }

    public static double munitesToseconds(int munites) {
        double seconds = 0;

        try {
            seconds = munites * 60;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seconds;

    }

    public static double avaragePace(double distance, double time) {
        double pace = 0.0;
        int hour = 3600;
        if (distance != 0.0) {
            pace = (time / 60) / distance;
        }


        return pace;
    }

    public static double roundTwoDecimals(double d) {
        // DecimalFormat twoDForm = new DecimalFormat("#.##");
        BigDecimal round = new BigDecimal(d);
        double finalresult = round.doubleValue();
        return finalresult;
    }

    public static Integer roundWholeNumber(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#");
        return Integer.valueOf(twoDForm.format(d));
    }

    public static void showNotification(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public static void AlertMessage(String message, Context context, final DialogSingleButtonListener dialogSingleButtonListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialogSingleButtonListener.onButtonClicked(dialog);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        alertDialogBuilder.show();

    }

    public static void errorNotification(String message, Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public static interface DialogSingleButtonListener {
        public abstract void onButtonClicked(DialogInterface dialog);
    }

    public static LinearLayoutManager initializeSingleRecyclerviewLayouts(RecyclerView recyclerViews, int layout, Context mContext) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, layout, false);
        recyclerViews.setLayoutManager(layoutManager);
        recyclerViews.setHasFixedSize(true);

        return layoutManager;
    }


    public static Double CalAvarageSpeed(double distance, double time) {
        double avarageSpeed = distance / time;
        return avarageSpeed;
    }

    public static Double CalDistance(double Lati, double Longi, double finalLati, double finalLongi) {
        Location location1 = new Location(LocationManager.GPS_PROVIDER);
        Location location2 = new Location(LocationManager.GPS_PROVIDER);
        location1.setLatitude(Lati);
        location1.setLongitude(Longi);
        location2.setLatitude(finalLati);
        location2.setLongitude(finalLongi);
        double distanceTravelled = location1.distanceTo(location2) / 1000;
        return roundTwoDecimals(distanceTravelled);
    }


    public static Double calPulseCoins(int Steps) {
        double coins = Steps * Constants.POINTSRATE;

        return coins;
    }

    public static double calculatePoint(int Steps) {
        double points = Steps * Constants.POINTSRATE;
        return points;
    }

    public static boolean CheckConnection(Context context) {

        boolean isAvailable;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                isAvailable = true;
            } else {
                isAvailable = false;
            }
        } else {

            isAvailable = false;
        }

        return isAvailable;
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static String fileToBase64(String filePath){
        String result ;
        File imgFile = new File(filePath);
        if (imgFile.exists() && imgFile.length() > 0) {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            result = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT);
            Log.d(Constants.TAG+"FileSize", String.valueOf(imgFile.length() / 1024000) + "MB");
        }else {
            result = "none";
        }
        return  result;
    }


    public static boolean sizeLimitUpload(String filePath){
        boolean result ;
        File imgFile = new File(filePath);
        if (imgFile.exists() && imgFile.length()/1024000 < 150) {

            result = false;
        }else {
            result = true;
        }
        return  result;
    }
}






