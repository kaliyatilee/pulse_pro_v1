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
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.DefaultRetryPolicy;
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

public class ForgetPasswordEmailActivity extends AppCompatActivity {

    TextInputLayout etEmail;
    String _email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_email);
        etEmail = findViewById(R.id.etEmail);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForgetPasswordEmailActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void doCheck(View view) {
        _email = etEmail.getEditText().getText().toString().trim();
        if (!validateEmail()) {
            return;
        } else {
            if (CM.isConnected(ForgetPasswordEmailActivity.this)) {
                checkEmail();
            } else
                Toast.makeText(ForgetPasswordEmailActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();

        }

    }

    private void checkEmail() {
        CM.showProgressLoader(ForgetPasswordEmailActivity.this);
        JSONObject object = new JSONObject();
        try {
            object.put("email", _email);
            Log.e("CHECKEMAIL", "CHECKEMAIL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.forgotpass_email, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Constants.TAG, String.valueOf(response));
                        CM.HideProgressLoader();
                        try {
                            if (response.getString("status").equals("success")) {
                                Log.e("email", _email);
                                Log.e("data", response.getString("data"));
                                Intent otpIntent = new Intent(getApplicationContext(), VerityOtpActivity.class);
//                                otpIntent.putExtra("phone", "");
                                otpIntent.putExtra("email", _email);
                                otpIntent.putExtra("id", response.getString("data"));
                                startActivity(otpIntent);
                                finish();
                            } else if (response.equals(500)) {
                                Toast.makeText(getApplicationContext(), "server issue please try again.", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid email, Please enter correct one.", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            CM.HideProgressLoader();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CM.HideProgressLoader();
                VolleyLog.d("Error", "Error: " + error.toString());
                etEmail.setError("server issue please try again");
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }


    private boolean validateEmail() {
        String val = etEmail.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-zA-Z0-9._-]+";


        if (val.isEmpty()) {
            etEmail.setError("Email can not be empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            etEmail.setError("Invalid Email!");
            return false;
        } else {
            etEmail.setError(null);
            etEmail.setErrorEnabled(false);
            return true;
        }
    }
}
