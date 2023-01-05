package com.algebratech.pulse_wellness.activities;

import static com.inuker.bluetooth.library.BluetoothService.getContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.models.OrganizationModel;
import com.algebratech.pulse_wellness.models.UserModel;
import com.algebratech.pulse_wellness.models.commentModel;
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
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateProfileThreeActivity extends AppCompatActivity {

    TextInputLayout phoneNumber;
    CountryCodePicker countryCodePicker;
    SharedPreferences.Editor myEdit;
    SharedPreferences sharedPreferences;
    String userId, _phoneNo, _country, _firstname, _lastname, _height, _weight, _bmi, _gender, _date, _corporateID;
    private Toolbar toolbarPolicy;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile4);
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        userId = sharedPreferences.getString("userID", null);
        Log.d(Constants.TAG, userId);
        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        setSupportActionBar(toolbarPolicy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        countryCodePicker = findViewById(R.id.country_code_picker);
        phoneNumber = findViewById(R.id.signup_phone_number);

    }

    public void doSubmit(View view) {

        if (!validatePhoneNumber()) {
            return;
        }

        String _getUserEnteredPhoneNumber = phoneNumber.getEditText().getText().toString().trim();

        if (_getUserEnteredPhoneNumber.charAt(0) == '0') {
            _getUserEnteredPhoneNumber = _getUserEnteredPhoneNumber.substring(1);
        }

        _phoneNo = "+" + countryCodePicker.getFullNumber() + _getUserEnteredPhoneNumber;
        _country = countryCodePicker.getSelectedCountryName();

        _firstname = getIntent().getStringExtra("firstname");
        _lastname = getIntent().getStringExtra("lastname");
        _height = getIntent().getStringExtra("height");
        _weight = getIntent().getStringExtra("weight");
        _bmi = getIntent().getStringExtra("bmi");
        _gender = getIntent().getStringExtra("gender");
        _date = getIntent().getStringExtra("dob");
        _corporateID = getIntent().getStringExtra("corporateID");
        Log.e(Constants.TAG + "Country", _country + " : " + _phoneNo + "::" + " : " + _firstname + " : " + _lastname + " : " + _height + " : " + _weight + " : " + _bmi + " : " + _gender + " : " + _date + " : " + _corporateID);
        if (CM.isConnected(CreateProfileThreeActivity.this)) {
            doCreateProfile();
        } else
            Toast.makeText(CreateProfileThreeActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();



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

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), CreateProfileTwoActivity.class);
        startActivity(intent);
        finish();

    }

    private void doCreateProfile() {
        CM.showProgressLoader(CreateProfileThreeActivity.this);
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
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
            object.put("corporate_id", _corporateID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.createprofile, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Constants.TAG, String.valueOf(response));
                        CM.HideProgressLoader();
                        try {
                            JSONObject userData = (JSONObject) response.get("user");

                            if (response.getString("status").equals("success")) {
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
                                myEdit.putBoolean("availableProfile", true);
                                myEdit.putString("profileURL", userData.getString("profileurl"));
                                myEdit.putString("dateCreated", userData.getString("created_at"));
                                myEdit.putString("email", userData.getString("email"));
                                myEdit.apply();

                                Toast.makeText(CreateProfileThreeActivity.this, "Successfully created profile ", Toast.LENGTH_SHORT).show();


                                startActivity(new Intent(getApplicationContext(), SelectDisease.class));
                                finish();

                            } else {

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
                if (error.getMessage().contains(Api.baseurl)) {
                    Toast.makeText(CreateProfileThreeActivity.this, "No internet connection available!!!.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(getApplicationContext(),CreateProfileTwoActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
