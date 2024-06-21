package com.algebratech.pulse_wellness.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.utils.CM;

import java.util.Calendar;

public class CreateProfileOneActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton selectedGender;
    DatePicker datePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile2);

        radioGroup = findViewById(R.id.radio_group);
        datePicker = findViewById(R.id.age_picker);

    }

    public void calltwoSigupScreen(View view) {

        if (validateAge()) {
            selectedGender = findViewById(radioGroup.getCheckedRadioButtonId());
            String _gender = selectedGender.getText().toString();

            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();

            String _date = year + "/" + month + "/" + day;

            if (CM.isConnected(CreateProfileOneActivity.this)) {
                Intent intent = new Intent(getApplicationContext(), CreateProfileTwoActivity.class);
                intent.putExtra("gender", _gender);
                intent.putExtra("dob", _date);
                startActivity(intent);
                finish();
            } else
                Toast.makeText(CreateProfileOneActivity.this,  R.string.noInternet, Toast.LENGTH_SHORT).show();
        }


    }

//    private boolean validateGender() {
//        if (radioGroup.getCheckedRadioButtonId() == -1) {
//            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
//            return false;
//        } else {
//            return true;
//        }
//    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isAgeValid = currentYear - userAge;

//        if (isAgeValid < 14) {
//            Toast.makeText(this, "You are not eligible to apply", Toast.LENGTH_SHORT).show();
//            return false;
//        } else
//            return true;

        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(CreateProfileOneActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else if (isAgeValid < 14) {
            Toast.makeText(CreateProfileOneActivity.this, "You are not eligible to apply as you are below 14 years", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
}

