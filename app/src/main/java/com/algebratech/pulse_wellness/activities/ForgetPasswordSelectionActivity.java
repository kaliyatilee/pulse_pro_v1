package com.algebratech.pulse_wellness.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.algebratech.pulse_wellness.R;

public class ForgetPasswordSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_selection);


    }

    public void callSms (View view){

        startActivity(new Intent(getApplicationContext(),ForgetPasswordActivity.class));
        finish();

    }

    public void callEmail (View view){

        startActivity(new Intent(getApplicationContext(),ForgetPasswordEmailActivity.class));
        finish();

    }

    public void callLogin (View view){

        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }
}
