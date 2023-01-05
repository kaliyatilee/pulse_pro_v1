package com.algebratech.pulse_wellness.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.interfaces.DialogClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CM {


    static CustomDialogClass customDialogClass = null;

    public static void showProgressLoader(Activity activity) {
        if (customDialogClass == null) {
            Log.e("MY_ANDROID_APP", "CM NOT NULL");
            customDialogClass = new CustomDialogClass(activity);
            customDialogClass.setCancelable(false);
            customDialogClass.show();
        } else {
            Log.e("MY_ANDROID_APP", "CM NOT NULL");
        }

    }

    public static void HideProgressLoader() {
        if (customDialogClass != null && customDialogClass.isShowing()) {
            customDialogClass.dismiss();
            customDialogClass = null;
            Log.e("MY_ANDROID_APP", "dismiss");
        } else
            Log.e("MY_ANDROID_APP", "not dismiss");

    }

    public static void ShowDialogueWithCustomAction(
            final Activity activity,
            String message,
            String positiveBtnText,
            String negativeBtnText,
            boolean shouldHaveTwoButton,
            DialogClickListener clickListener
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View commonCustomDlg = LayoutInflater.from(activity).inflate(R.layout.dialog_common_custom_msg, null);
        Button btnOk = commonCustomDlg.findViewById(R.id.btnOk);
        Button btnNo = commonCustomDlg.findViewById(R.id.btnNo);
        Button btnyes = commonCustomDlg.findViewById(R.id.btnYes);
        TextView txtMsg = commonCustomDlg.findViewById(R.id.cDialog_txtMsg);
        builder.setView(commonCustomDlg);
        builder.setCancelable(false);
        btnOk.setVisibility(View.GONE);
        if (shouldHaveTwoButton) {
            btnyes.setText(positiveBtnText);
            btnNo.setText(negativeBtnText);
            btnyes.setVisibility(View.VISIBLE);
            btnNo.setVisibility(View.VISIBLE);
        } else {
            btnyes.setVisibility(View.VISIBLE);
            btnNo.setVisibility(View.GONE);
        }

        final AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();

        txtMsg.setText(message);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alert != null && alert.isShowing()) {
                    alert.dismiss();
                }
                clickListener.onNegativeClick();
            }
        });

        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alert != null && alert.isShowing()) {
                    alert.dismiss();
                }
                clickListener.onPositiveClick();
//                activity.finish();
            }
        });
    }

    public static boolean isConnected(Activity activity) {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;

    }

}
