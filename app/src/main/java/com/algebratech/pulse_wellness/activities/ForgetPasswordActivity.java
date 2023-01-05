package com.algebratech.pulse_wellness.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgetPasswordActivity extends AppCompatActivity {

    TextInputLayout phoneNumber;
    CountryCodePicker countryCodePicker;
    RelativeLayout progressBar;
    String _phoneNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_sms);


        progressBar = findViewById(R.id.progressBar);
        countryCodePicker = findViewById(R.id.country_code_picker);
        phoneNumber = findViewById(R.id.phone_number);

        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForgetPasswordActivity.this, ForgetPasswordSelectionActivity.class);
        startActivity(intent);
        finish();
    }

    public void doCheck(View view) {

        if (!validatePhoneNumber()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String _getUserEnteredPhoneNumber = phoneNumber.getEditText().getText().toString().trim();

        if (_getUserEnteredPhoneNumber.charAt(0) == '0') {
            _getUserEnteredPhoneNumber = _getUserEnteredPhoneNumber.substring(1);
        }

        _phoneNo = "+"+countryCodePicker.getFullNumber()+_getUserEnteredPhoneNumber;


        checkPhoneNumber();


    }

    private void checkPhoneNumber() {
        Log.e("phone number",_phoneNo);
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("phone", _phoneNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.forgotpass_phone, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Constants.TAG, String.valueOf(response));
                        progressBar.setVisibility(View.GONE);

                        try {

                            if (response.getString("status").equals("success")) {

                                Log.e("forgot pass","done");
                                Intent otpIntent = new Intent(getApplicationContext(),VerityOtpActivity.class);
                                otpIntent.putExtra("phone",_phoneNo);
                                otpIntent.putExtra("email","");
                                otpIntent.putExtra("id",response.getString("data"));
                                startActivity(otpIntent);
                                finish();

                            } else {

                                Toast.makeText(getApplicationContext(),"Invalid phone number, Please enter correct one.",Toast.LENGTH_LONG).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                VolleyLog.d("Error", "Error: " + error.toString());
                try {
//                    if (error.getMessage().contains(Api.baseurl)) {
//                        Toast.makeText(LoginActivity.this, "No internet connection available!!!.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(LoginActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                    }

                } catch (Exception e) {
                    Log.d(Constants.TAG, e.getMessage());
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }

    private boolean validatePhoneNumber() {
        String val = phoneNumber.getEditText().getText().toString().trim();
        String checkspaces = "\\A\\w{1,20}\\z";
        if (val.isEmpty()) {
            phoneNumber.setError("Enter valid phone number");
            return false;
        } else if (!val.matches(checkspaces)) {
            phoneNumber.setError("No White spaces are allowed!");
            return false;
        } else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            return true;
        }
    }
}
