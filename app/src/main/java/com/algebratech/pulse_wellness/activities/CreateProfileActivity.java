package com.algebratech.pulse_wellness.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.models.UserModel;
import com.algebratech.pulse_wellness.models.WeightRecordModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.StaticMethods;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateProfileActivity extends AppCompatActivity {

    private DatePickerDialog.OnDateSetListener onDateSetListener;
    EditText etlastname, etfirstname, etPhone, etWeight, etHeight;
    Button etDate;
    Spinner etGender;
    String dateOfbirth, firstname, lastname, phoneNumber, gender, height, weight;
    Button btnCreate;
    String email;
    String userId;
    double bmi;
    SharedPreferences.Editor myEdit;
    SharedPreferences sharedPreferences;
    String _phoneNo, _country, _firstname, _lastname, _height, _weight, _bmi, _gender, _date;
    private Toolbar toolbarPolicy;
    String oldWeight = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        oldWeight = sharedPreferences.getString("weight", "");
        userId = sharedPreferences.getString("userID", null);
        Log.d(Constants.TAG, userId);

        etfirstname = findViewById(R.id.etfirstname);
        etlastname = findViewById(R.id.etlastname);
        etPhone = findViewById(R.id.etPhone);
        etGender = findViewById(R.id.etGender);

        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etDate = findViewById(R.id.etDate);
        btnCreate = findViewById(R.id.btnCreate);
        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        setSupportActionBar(toolbarPolicy);
        setTitle("Edit profile");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setData();

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = sharedPreferences.getString("dob", "");
                Log.e("Date Prefernce ", date);
                Date selectedDate = null;
                try {
                    selectedDate = new SimpleDateFormat("yyyy/MM/dd").parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                if (selectedDate != null) {
                    cal.setTime(selectedDate);
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    Log.e("Date", "y=" + year + "m-" + month + "D-" + day);
                    cal.set(year, month, day);
                }


                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateProfileActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth, onDateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();


            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etDate.setText("D.O.B : " + year + "/" + (month + 1) + "/" + dayOfMonth);
                dateOfbirth = year + "/" + (month + 1) + "/" + dayOfMonth;
            }

        };

      //  btnCreate.setText("Hey Hey ");

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weight = etWeight.getText().toString();
                if (weight.isEmpty()) {
                    etWeight.setError("Required");
                    return;
                }
                height = etHeight.getText().toString();
                if (height.isEmpty()) {
                    etHeight.setError("Required");
                    return;
                }
                if (dateOfbirth == null) {
                    etDate.setError("Required");
                    Toast.makeText(CreateProfileActivity.this, "Date of birth required", Toast.LENGTH_SHORT).show();
                    return;
                }
                phoneNumber = etPhone.getText().toString();
                if (phoneNumber.isEmpty()) {
                    etPhone.setError("Required");
                    return;
                }
                if (phoneNumber.length() < 10) {
                    etPhone.setError("Invalid Phone");
                    return;
                }


                gender = etGender.getSelectedItem().toString();
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                String patientId = "ph" + ts;
                double mWeight = Double.valueOf(weight);
                double mHeight = Double.valueOf(height);
                Long timeStamp = Constants.timeStamp;
                bmi = StaticMethods.calculateBmi(mWeight, mHeight);
                WeightRecordModel weightRecord = new WeightRecordModel(timeStamp, mWeight, 0);


                firstname = etfirstname.getText().toString();
                lastname = etlastname.getText().toString();

                _phoneNo = phoneNumber;
                _country = sharedPreferences.getString("country", "");
                _firstname = firstname;
                _lastname = lastname;
                _height = height;
                _weight = weight;
                _bmi = String.valueOf(bmi);
                _gender = gender;
                _date = dateOfbirth;

                UserModel user = new UserModel(firstname, lastname, phoneNumber, dateOfbirth, weight, height, gender, userId, "", "", email, "", "", "https://immedilet-invest.com/wp-content/uploads/2016/01/user-placeholder.jpg", 0, bmi, timeStamp, weightRecord);


                Log.d(Constants.TAG + "Country", _country + " : " + _phoneNo + "::" + " : " + _firstname + " : " + _lastname + " : " + _height + " : " + _weight + " : " + _bmi + " : " + _gender + " : " + _date);
                if (CM.isConnected(CreateProfileActivity.this)) {
                    doCreateProfile();
                } else
                    Toast.makeText(CreateProfileActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


            }
        });

    }

    private void setData() {

        etfirstname.setText(sharedPreferences.getString("firstname", ""));
        etlastname.setText(sharedPreferences.getString("lastname", ""));
        etWeight.setText(sharedPreferences.getString("weight", ""));
        etHeight.setText(sharedPreferences.getString("height", ""));
        etDate.setText("D.O.B : " + sharedPreferences.getString("dob", ""));
        dateOfbirth = sharedPreferences.getString("dob", "");
        etPhone.setText(sharedPreferences.getString("phone", ""));
        String gender = sharedPreferences.getString("gender", "");

        if (gender.equals("Male")) {
            etGender.setSelection(0);
        } else
            etGender.setSelection(1);


    }

    private void doCreateProfile() {
        CM.showProgressLoader(CreateProfileActivity.this);
        JSONObject object = new JSONObject();
        try {
            object.put("firstname", _firstname);
            object.put("lastname", _lastname);
            object.put("dob", _date);
            object.put("gender", _gender);
            object.put("country", _country);
            object.put("phone", _phoneNo);
            object.put("height", _height);
            object.put("weight", _weight);
            object.put("bmi", _bmi);
            object.put("id", userId);
            Log.e("EDITPROFILE", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.createprofile, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Constants.TAG, String.valueOf(response));
                        CM.HideProgressLoader();

                        try {
                            JSONObject userData = (JSONObject) response.get("user");

                            if (response.getString("status").equals("success")) {
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
                                myEdit.apply();

                                if (false) {
                                    regenerateWellnessplan();
                                } else {
                                    Toast.makeText(CreateProfileActivity.this, "Successfully updated profile ", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                }

                            } else {

                                Toast.makeText(CreateProfileActivity.this, "Failed to updated profile , please try again...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CM.HideProgressLoader();
                VolleyLog.d("Error", "Error: " + error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(CreateProfileActivity.this);
        requestQueue.add(jsonObjectRequest);

    }

    void regenerateWellnessplan() {
        System.out.println("Runnning regenerate wellness plam");

        if (CM.isConnected(CreateProfileActivity.this)) {
            CM.showProgressLoader(this);
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);
                System.out.println("+++++++++++++++++++++++");
                System.out.println(userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.generateWellnessPlan, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            CM.HideProgressLoader();
                            System.out.println("+++++++++++++++++++");
                            System.out.println(response);
                            System.out.println("++++++++++++++++++++");


                            try {
                                if (response.getString("status").equals("true")) {
                                    Toast.makeText(CreateProfileActivity.this, "Successfully updated profile ", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
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
            RequestQueue requestQueue = Volley.newRequestQueue(CreateProfileActivity.this);
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(CreateProfileActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

}
