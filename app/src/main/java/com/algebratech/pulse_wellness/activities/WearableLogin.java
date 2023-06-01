package com.algebratech.pulse_wellness.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.algebratech.pulse_wellness.R;
import com.wosmart.ukprotocollibary.WristbandManager;
import com.wosmart.ukprotocollibary.WristbandManagerCallback;

public class WearableLogin extends AppCompatActivity {
    private final String tag = "LoginActivity";
    Button btn_login;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btn_login = findViewById(R.id.btn_login);
        setContentView(R.layout.activity_wearable_login);
         btn_login.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                     WristbandManager.getInstance(WearableLogin.this).startLoginProcess("1234567890");

             }
         });
    }

    private void initData() {
        WristbandManager.getInstance(this).registerCallback(new WristbandManagerCallback() {
            @Override
            public void onLoginStateChange(int state) {
                super.onLoginStateChange(state);
                if (state == WristbandManager.STATE_WRIST_LOGIN) {
                    Log.i(tag, "Login Success");
                }
            }
        });
    }
}