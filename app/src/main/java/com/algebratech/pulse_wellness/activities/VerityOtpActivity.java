package com.algebratech.pulse_wellness.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.chaos.view.PinView;

import org.json.JSONException;
import org.json.JSONObject;

public class VerityOtpActivity extends AppCompatActivity {

    String _id, _email;
    TextView otp_phone, resend;
    PinView pinFromUser;
    String codeBySystem;
    ImageView edit;
    boolean timeUp = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        pinFromUser = findViewById(R.id.pin_view);
        otp_phone = findViewById(R.id.otp_phone);
        edit = findViewById(R.id.edit);
        resend = findViewById(R.id.resend);
        otp_phone.setText("");
        // _phoneNo = getIntent().getStringExtra("phone");
        _email = getIntent().getStringExtra("email");
        _id = getIntent().getStringExtra("id");
//        if (!_phoneNo.isEmpty()) {
//            otp_phone.setText(_phoneNo);
////            sendVerificationCodeToUser(_phoneNo);
//        } else {
        otp_phone.setText(_email);
//        }


        findViewById(R.id.verify_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // String code = pinFromUser.getText().toString();
//                Log.d(Constants.TAG + "Code:", code);
//                Log.e("phone", _phoneNo);
//                Log.e("email", _email);
//                if (!_phoneNo.isEmpty() || !_email.isEmpty()) {
//                    if (!code.isEmpty()) {
//                        progressBar.setVisibility(View.VISIBLE);
//
//
//                    }
//                } else {
//
//                    Toast.makeText(getApplicationContext(), "Connection Error Please Try Again", Toast.LENGTH_LONG).show();
//
//                }
                if (CM.isConnected(VerityOtpActivity.this)) {
                    checkPhoneNumber();
                } else
                    Toast.makeText(VerityOtpActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerityOtpActivity.this, ForgetPasswordEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                timeUp = true;
                resend.setTextColor(Color.parseColor("#ff0000"));
                Log.e("VALUE", String.valueOf(timeUp));
            }

        }.start();


            Log.e("RESEND", "RESND");
            resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (timeUp == true) {
                        checkEmail();
                    }                }
            });
        }



    public void goToLogin(View view) {
        Intent intent = new Intent(VerityOtpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

//    private void sendVerificationCodeToUser(String phoneNo) {
//
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNo,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
//                mCallbacks);        // OnVerificationStateChangedCallbacks
//
//    }
//
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//        @Override
//        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//            codeBySystem = s;
//        }
//
//        @Override
//        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//            String code = phoneAuthCredential.getSmsCode();
//            if(code!=null){
//                pinFromUser.setText(code);
//                verifyCode(code);
//            }
//
//        }
//
//        @Override
//        public void onVerificationFailed(FirebaseException e) {
//            Toast.makeText(VerityOtpActivity.this,"Server error please try again",Toast.LENGTH_SHORT).show();
//
//        }
//    };
//
//    private void verifyCode(String code) {
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem,code);
//        signInWithPhoneAuthCredential(credential);
//    }
//
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//
//        firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//
//                            Toast.makeText(VerityOtpActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(getApplicationContext(), NewPassword.class);
//                            intent.putExtra("id",_id);
//                            startActivity(intent);
//                            finish();
//
//                        } else {
//
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                Toast.makeText(VerityOtpActivity.this,"Verification not completed! Try again.",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
//    }
//
//    public void callNextScreenFromOTP(View view) {
//        String code = pinFromUser.getText().toString();
//        if (!code.isEmpty()){
//            verifyCode(code);
//        }
//    }
//
//    public void changeNumber(View view)
//    {
//        Intent intent = new Intent(VerityOtpActivity.this, ForgetPasswordActivity.class);
//        startActivity(intent);
//        finish();
//    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(VerityOtpActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkPhoneNumber() {
        CM.showProgressLoader(VerityOtpActivity.this);
        JSONObject object = new JSONObject();
        try {
            object.put("user_id", _id);
            object.put("otp", pinFromUser.getText());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.verify_otp, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Constants.TAG, String.valueOf(response));
                        try {

                            if (response.getString("status").equals("success")) {
                                CM.HideProgressLoader();
                                Intent intent = new Intent(getApplicationContext(), NewPassword.class);
                                intent.putExtra("id", _id);
                                startActivity(intent);
                                finish();
                            } else {
                                CM.HideProgressLoader();
                                Toast.makeText(getApplicationContext(), "Please enter valid otp", Toast.LENGTH_LONG).show();
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

    private void checkEmail() {
        CM.showProgressLoader(VerityOtpActivity.this);
        JSONObject object = new JSONObject();
        try {
            object.put("email", _email);
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
                                Toast.makeText(getApplicationContext(), "Otp sent successfully", Toast.LENGTH_LONG).show();
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
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }
}
