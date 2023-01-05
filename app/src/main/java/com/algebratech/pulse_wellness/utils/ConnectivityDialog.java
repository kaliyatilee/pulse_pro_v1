package com.algebratech.pulse_wellness.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;

import com.algebratech.pulse_wellness.R;

public class ConnectivityDialog {

    public void showDialog(Activity activity, String msg) {

        final Dialog dialog = new Dialog(activity,R.style.ConnectivityDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_connectivity);

        dialog.show();

    }
}
