package com.algebratech.pulse_wellness.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout etemail, etPassword;
    String email, password;
    Button btnLogin;
    TextView redirect;
    SharedPreferences.Editor myEdit;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesToken;
    String refreshedToken = "";
    String fcm_token;
    TextInputEditText pass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        //SharePref
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        sharedPreferencesToken = getSharedPreferences("fcm_token", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();


        //Declare
        redirect = findViewById(R.id.redirect);
        etemail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        pass = findViewById(R.id.pass);

        //Set Event
        btnLogin.setOnClickListener(this);
        redirect.setOnClickListener(this);
        findViewById(R.id.forget_password).setOnClickListener(this);


        if (BuildConfig.DEBUG) {

            etemail.getEditText().setText(Constants.DEBUG_EMAIL);
            etPassword.getEditText().setText(Constants.DEBUG_PASS);

        } else {


            etemail.getEditText().setText("");
            etPassword.getEditText().setText("");

        }
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

        pass.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(20)});

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.redirect) {

            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();

        }
        if (v.getId() == R.id.btnLogin) {
            if (CM.isConnected(LoginActivity.this)) {
                doLogin();
            } else
                Toast.makeText(LoginActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();
        }

        if (v.getId() == R.id.forget_password) {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordEmailActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void doLogin() {
        fcm_token = sharedPreferencesToken.getString("fcm_token", null);
//        Log.e("FCM_TOKEN",fcm_token);
        if (!validateEmail() | !validatePassword()) {
            return;
        }
        email = etemail.getEditText().getText().toString().trim();
        password = etPassword.getEditText().getText().toString().trim();
        CM.showProgressLoader(LoginActivity.this);

        JSONObject object = new JSONObject();
        try {

            object.put("email", email);
            object.put("password", password);
            object.put("fcm_tokken", fcm_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.login, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Constants.TAG, String.valueOf(response));
                        Log.e(Constants.TAG, String.valueOf(response));
                        CM.HideProgressLoader();

                        try {
                            if (response.getString("status").equals("success")) {

                                myEdit.putInt(Constants.PHONE, 0);
                                myEdit.putInt(Constants.SMS, 0);
                                myEdit.putInt(Constants.WHATSAPP, 0);
                                myEdit.putInt(Constants.EMAIL, 0);

                                myEdit.putInt(Constants.FACEBOOK, 0);
                                myEdit.putInt(Constants.INSTA, 0);
                                myEdit.putInt(Constants.TWITTER, 0);
                                myEdit.putInt(Constants.OTHER, 0);

                                JSONObject token = response.optJSONObject("token");
                                if (!token.toString().equals("{}")) {
                                    myEdit.putString("subscriptions_id", token.getString("subscriptions_id"));
                                    myEdit.putInt("plan_id", 0);
                                }


                                JSONObject userData = (JSONObject) response.get("user");
                                JSONObject subToken = (JSONObject) response.get("token");

                                if (userData.getString("status").equals("new")) {
                                    myEdit.putString("userID", userData.getString("id"));
                                    myEdit.putBoolean("isLogin", true);
                                    myEdit.putString("isDatabaseInitialised", "true");
//                                    myEdit.putString("card_status", userData.getString("card_status"));
                                    myEdit.putString("Pulse_Points", userData.getString("loyaltpoints"));
                                    myEdit.putString("macAddress", userData.getString("macAddress"));
                                    myEdit.putBoolean("availableProfile", false);
                                    myEdit.putString("sub_token", String.valueOf(subToken));
                                    Log.e("sub_token", subToken.toString());
                                    myEdit.apply();
                                    String user_id = userData.getString("id");
                                    //addNoticationId(user_id, getApplicationContext());
                                    startActivity(new Intent(getApplicationContext(), CreateProfileOneActivity.class));
                                    finish();

                                } else {
                                    Toast.makeText(getApplicationContext(), "Successful Login", Toast.LENGTH_LONG).show();

                                    myEdit.putString("userID", userData.getString("id"));
                                    myEdit.putBoolean("isLogin", true);
                                    myEdit.putString("isDatabaseInitialised", "true");
                                    myEdit.putString("Pulse_Points", userData.getString("loyaltpoints"));
                                    myEdit.putString("macAddress", userData.getString("macAddress"));
                                    myEdit.putString("fullname", userData.getString("firstname") + " " + userData.getString("lastname"));
                                    myEdit.putString("firstname", userData.getString("firstname"));
                                    myEdit.putString("lastname", userData.getString("lastname"));
                                    myEdit.putString("dob", userData.getString("dob"));
                                    myEdit.putString("bmi", userData.getString("bmi"));
                                    myEdit.putString("weight", userData.getString("weight"));
                                    myEdit.putString("height", userData.getString("height"));
                                    myEdit.putString("gender", userData.getString("gender"));
                                    myEdit.putString("country", userData.getString("country"));
                                    myEdit.putString("phone", userData.getString("phone"));
                                    myEdit.putString("profileURL", userData.getString("profileurl"));
                                    myEdit.putString("dateCreated", userData.getString("created_at"));
                                    myEdit.putString("email", userData.getString("email"));
//                                    myEdit.putString("card_status", userData.getString("card_status"));
                                    myEdit.putString("sub_token", String.valueOf(subToken));
                                    myEdit.putBoolean("availableProfile", true);
                                    myEdit.apply();
//                                    String user_id = userData.getString("id");
//                                    Log.e("SUBTOKEN", String.valueOf(subToken));
                                    //addNoticationId(user_id, getApplicationContext());

//                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }

                            } else {

                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CM.HideProgressLoader();
                VolleyLog.d("Error", "Error: " + error.toString());
                try {
                    if (error.getMessage().contains(Api.baseurl)) {
                        Toast.makeText(LoginActivity.this, "No internet connection available!!!.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.d(Constants.TAG, e.getMessage());
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);


    }

//    private void addNoticationId(String user_id, Context context) {
//
//        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
//            @Override
//            public void onComplete(@NonNull Task<String> task) {
//                if (!task.isSuccessful()) {
//                    Log.w("TAG", "Fetching FCM registration token failed", task.getException());
//                    return;
//                }
//                refreshedToken = task.getResult();
//            }
//        });
//
//
//        Log.e("PULSE_FCM_TOKEN","====>"+refreshedToken);
//
//        JSONObject object = new JSONObject();
//        try {
//            object.put("id", user_id);
//            object.put("fcm_tokken", refreshedToken);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.addfcmtokken, object,
//            new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//
//                    try {
//
//                        if (response.getString("status").equals("success")) {
//                            myEdit.putString("fcmtokken", refreshedToken);
//                            myEdit.apply();
//                        }
//                    } catch (Exception e) {
//
//                    }
//
//                }
//            },
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            });
//
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.add(jsonObjectRequest);
//
//
//    }

    @Override
    public void onBackPressed() {

    }

    private boolean validateEmail() {
        String val = etemail.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-zA-Z0-9._-]+";

        if (val.isEmpty()) {
            etemail.setError("Email can not be empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            etemail.setError("Invalid Email!");
            return false;
        } else {
            etemail.setError(null);
            etemail.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = etPassword.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            etPassword.setError("Password can not be empty");
            return false;
        } else {
            etPassword.setError(null);
            etPassword.setErrorEnabled(false);
            return true;
        }
    }

}
