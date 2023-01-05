package com.algebratech.pulse_wellness.activities;

import static com.inuker.bluetooth.library.BluetoothService.getContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.utils.Constants;

public class ProfileCreatedView extends AppCompatActivity {

    Button btnGetStated;
    SharedPreferences sharedPreferences;
    TextView welcom_msg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_profile_created_success);
        btnGetStated = findViewById(R.id.getStarted);
        welcom_msg = findViewById(R.id.welcom_msg);
        sharedPreferences =  getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        String useName  = sharedPreferences.getString("firstname", "");

        String message = "Welcome to Pulse Health Community\n"+useName+". We will create a plan for\nyou. Enjoy #PH_Own YourBeat";

        welcom_msg.setText(message);
        btnGetStated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MainActivity.class));
                finishAffinity();
            }
        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
