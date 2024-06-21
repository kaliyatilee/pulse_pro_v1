package com.algebratech.pulse_wellness.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import com.algebratech.pulse_wellness.BuildConfig;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout etConfirmPassword, etPassword, etEmail;
    Button btnRegister;
    String email, password, confirmPassword;
    TextView redirect;
    TextInputEditText pass, cpass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        btnRegister = findViewById(R.id.btnRegister);
        redirect = findViewById(R.id.redirect);
        pass = findViewById(R.id.pass);
        cpass = findViewById(R.id.cpass);


        btnRegister.setOnClickListener(this);
        redirect.setOnClickListener(this);

//        if (BuildConfig.DEBUG) {
//
//            etEmail.getEditText().setText(Constants.DEBUG_EMAIL);
//            etPassword.getEditText().setText(Constants.DEBUG_PASS);
//            etConfirmPassword.getEditText().setText(Constants.DEBUG_PASS);
//
//        } else {

            etEmail.getEditText().setText("");
            etPassword.getEditText().setText("");
            etConfirmPassword.getEditText().setText("");

//        }

        etEmail.getEditText().addTextChangedListener(textWatcher);
        etPassword.getEditText().addTextChangedListener(textWatcher);
        etConfirmPassword.getEditText().addTextChangedListener(textWatcher);


        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };

        pass.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(20)});
        cpass.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(20)});

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.redirect) {

            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }
        if (v.getId() == R.id.btnRegister) {
            if (CM.isConnected(RegisterActivity.this)) {
                doReg();
            } else
                Toast.makeText(RegisterActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onBackPressed() {

    }

    private void doReg() {

        if (!validateEmail() | !validatePassword() | !validateConfirmPassword()) {
            return;
        }

        email = etEmail.getEditText().getText().toString().trim();
        password = etPassword.getEditText().getText().toString();
        CM.showProgressLoader(RegisterActivity.this);


        JSONObject object = new JSONObject();
        try {
            object.put("roleId", 3);
            object.put("email", email);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.register, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.e(Constants.TAG, String.valueOf(response));
                            Log.e(Constants.TAG, String.valueOf(email));
                            Log.d(Constants.TAG, String.valueOf(password));
                            CM.HideProgressLoader();

                            if (response.getString("status").equals("success")) {

                                Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();

                            } else {

                                if (response.getString("message").contains("The email has already been taken.") || response.getString("message").contains("User already exist")) {

                                    Toast.makeText(getApplicationContext(), "User already exist , Go to login.", Toast.LENGTH_LONG).show();
                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            CM.HideProgressLoader();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CM.HideProgressLoader();
                VolleyLog.d("Error", "Error: " + error.getMessage());
                try {
                    if (error.getMessage().contains(Api.baseurl)) {
                        Toast.makeText(RegisterActivity.this, "No internet connection available!!!.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.d(Constants.TAG, e.getMessage());
                }

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.getCache().clear();
        jsonObjectRequest.setShouldCache(false);
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

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            etEmail.setError(null);
            etEmail.setErrorEnabled(false);
            etPassword.setError(null);
            etPassword.setErrorEnabled(false);
            etConfirmPassword.setError(null);
            etConfirmPassword.setErrorEnabled(false);

        }

        @Override
        public void afterTextChanged(Editable s) {


        }
    };
}
