package com.algebratech.pulse_wellness.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.utils.Constants;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tgio.rncryptor.RNCryptorNative;


public class SplashActivity extends AppCompatActivity {

    private static final int WRITE_REQUEST_CODE = 1;
    private SharedPreferences.Editor myEdit;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        transparentStatusAndNavigation();

        //startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));

        //SharePref
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat postFormater = new SimpleDateFormat("MMMM dd, yyyy");
        String currentDate = postFormater.format(c);

        String pref_date = sharedPreferences.getString("updated_at","");

//        String fcm_token = sharedPreferences.getString("fcm_token", null);
//        Log.e("FCM_TOKEN",fcm_token);
//        checkUpdate();
        if (!pref_date.equals(currentDate)){
            myEdit.putInt("stepCounter", 0);
            myEdit.putString("updated_at", currentDate);
            myEdit.commit();
        }


        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECORD_AUDIO
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        }

    }


    private void openNext() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPreferences.getBoolean("isLogin",false)){
                    if (sharedPreferences.getBoolean("availableProfile",false)){
                        startActivity( new Intent(getApplicationContext(),MainActivity.class));
                        Toast.makeText(SplashActivity.this, "I am here 1", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(SplashActivity.this, "I am here 2", Toast.LENGTH_SHORT).show();

                        startActivity( new Intent(getApplicationContext(),CreateProfileOneActivity.class));
                        finish();
                    }
                }else {
                    Toast.makeText(SplashActivity.this, "I am here 3", Toast.LENGTH_SHORT).show();

                    startActivity( new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                }


            }
        },5000);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permission granted.
//                    startActivity(new Intent(
//                            "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    openNext();
                }
                else{
                    openNext();
                    System.out.println("Permission denied");
                    //Permission denied.
                }
                break;
        }
    }

    private void transparentStatusAndNavigation() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }



}
