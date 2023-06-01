package com.algebratech.pulse_wellness.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class NewPassword extends AppCompatActivity {

    String _id, _password;
    TextInputLayout etConfirmPassword, etPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        _id = getIntent().getStringExtra("id");

        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CM.isConnected(NewPassword.this)) {
                    changePassword();
                } else
                    Toast.makeText(NewPassword.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


            }
        });

    }

    private void changePassword() {

        if (!validatePassword() | !validateConfirmPassword()) {
            return;
        }

        _password = etPassword.getEditText().getText().toString().trim();

        CM.showProgressLoader(NewPassword.this);
        JSONObject object = new JSONObject();
        try {
            object.put("user_id", _id);
            object.put("password", _password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.change_password, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Constants.TAG, String.valueOf(response));

                        try {

                            if (response.getString("status").equals("success")) {

                                CM.HideProgressLoader();
                                startActivity(new Intent(getApplicationContext(), ChangePasswordDoneActivity.class));
                                finish();

                            } else {

                                Toast.makeText(getApplicationContext(), "Failed to change password , Please try again.", Toast.LENGTH_LONG).show();

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
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }

    private boolean validatePassword() {
        String val = etPassword.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            etPassword.setError("Password can not be empty");
            return false;
        } else if (val.length() < 6) {
            etPassword.setError("Password should contain 6 characters!");
            return false;
        } else {
            etPassword.setError(null);
            etPassword.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String val = etConfirmPassword.getEditText().getText().toString().trim();
        String valConfirm = etPassword.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            etConfirmPassword.setError("Confirm Password can not be empty");
            return false;
        } else if (valConfirm.length() < 6) {
            etPassword.setError("Password should contain 6 characters!");
            return false;
        } else if (!val.equals(valConfirm)) {
            etConfirmPassword.setError("Password doesn't match!");
            return false;
        } else {
            etPassword.setError(null);
            etPassword.setErrorEnabled(false);
            etConfirmPassword.setError(null);
            etConfirmPassword.setErrorEnabled(false);
            return true;
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


            etPassword.setError(null);
            etPassword.setErrorEnabled(false);
            etConfirmPassword.setError(null);
            etConfirmPassword.setErrorEnabled(false);

        }

        @Override
        public void afterTextChanged(Editable s) {


        }
    };

    @Override
    public void onBackPressed() {

    }
}
