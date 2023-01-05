package com.algebratech.pulse_wellness.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.models.NewsFeedModel;
import com.algebratech.pulse_wellness.models.OrganizationModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.StaticMethods;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateProfileTwoActivity extends AppCompatActivity {

    TextInputLayout signup_firstname, signup_lastname, signup_height, signup_weight;
    SharedPreferences sharedPreferences;
    String userId;
    List<String> list = new ArrayList<String>();
    List<OrganizationModel> organizationModels = new ArrayList<>();
    String corporateID;
    TextInputEditText test;
    TextInputLayout cor_layout;
    Dialog dialog;
    private Toolbar toolbarPolicy;
    TextInputEditText fname, lname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile3);
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);

        signup_firstname = findViewById(R.id.signup_firstname);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        signup_lastname = findViewById(R.id.signup_lastname);
        signup_height = findViewById(R.id.signup_height);
        signup_weight = findViewById(R.id.signup_weight);
        cor_layout = findViewById(R.id.cor_layout);

        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        setSupportActionBar(toolbarPolicy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        test = findViewById(R.id.testtt);

        signup_firstname.getEditText().addTextChangedListener(textWatcher);
        signup_lastname.getEditText().addTextChangedListener(textWatcher);
        signup_height.getEditText().addTextChangedListener(textWatcher);
        signup_weight.getEditText().addTextChangedListener(textWatcher);

        getOrganization();


        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(CreateProfileTwoActivity.this);
                dialog.setContentView(R.layout.dialog_searchable_spinner);
                ImageView close = dialog.findViewById(R.id.close);
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.60);
                dialog.getWindow().setLayout(width, height);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.hide();
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.show();

                EditText editable = dialog.findViewById(R.id.edt);
                ListView listView = dialog.findViewById(R.id.list_cor);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateProfileTwoActivity.this, android.R.layout.simple_list_item_1, list);
                listView.setAdapter(adapter);

                editable.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        adapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        test.setText(adapter.getItem(i));
                        corporateID = organizationModels.get(i).getId();
                        dialog.dismiss();
                    }
                });
            }
        });

        fname.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        lname.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
    }

    public void callEnterNumber(View view) {


        if (CM.isConnected(CreateProfileTwoActivity.this)) {
            if (!validateFirstname() | !validateLastname() | !validateHeight() | !validateWeight()) {
                return;
            }

            String _firstname = signup_firstname.getEditText().getText().toString().trim();
            String _lastname = signup_lastname.getEditText().getText().toString().trim();
            String _height = signup_height.getEditText().getText().toString().trim();
            String _weight = signup_weight.getEditText().getText().toString().trim();
            String _bmi = String.valueOf(StaticMethods.calculateBmi(Double.valueOf(_weight), Double.valueOf(_height)));
            String _gender = getIntent().getStringExtra("gender");
            String _date = getIntent().getStringExtra("dob");
            Intent intent = new Intent(getApplicationContext(), CreateProfileThreeActivity.class);
            intent.putExtra("firstname", _firstname);
            intent.putExtra("lastname", _lastname);
            intent.putExtra("height", _height);
            intent.putExtra("weight", _weight);
            intent.putExtra("bmi", _bmi);
            intent.putExtra("gender", _gender);
            intent.putExtra("dob", _date);
            intent.putExtra("corporateID", corporateID);
            startActivity(intent);
            finish();
        } else
            Toast.makeText(CreateProfileTwoActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    private boolean validateFirstname() {
        String val = signup_firstname.getEditText().getText().toString().trim();
        String checkspaces = "\\A\\w{1,30}\\z";

        if (val.isEmpty()) {
            signup_firstname.setError("Enter your first name!");
            return false;
        } else if (val.length() > 30) {
            signup_firstname.setError("First name is too large!");
            return false;
        }
        else {
            signup_firstname.setError(null);
            signup_firstname.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateLastname() {
        String val = signup_lastname.getEditText().getText().toString().trim();
        String checkspaces = "\\A\\w{1,30}\\z";

        if (val.isEmpty()) {
            signup_lastname.setError("Enter your last name!");
            return false;
        } else if (val.length() > 30) {
            signup_lastname.setError("Last name is too large!");
            return false;
        } else {
            signup_lastname.setError(null);
            signup_lastname.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateHeight() {
        String val = signup_height.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            signup_height.setError("Enter your height");
            return false;
        } else if (Double.parseDouble(val) > 250) {
            signup_height.setError("Please enter valid height!");
            return false;
        } else {
            signup_height.setError(null);
            signup_height.setErrorEnabled(false);
            return true;
        }


    }

    private boolean validateWeight() {
        String val = signup_weight.getEditText().getText().toString().trim();
        int temp = 0;
        if (!val.isEmpty())
            Double.parseDouble(val);
        if (val.isEmpty()) {
            signup_weight.setError("Enter your weight");
            return false;

        } else if (Double.parseDouble(val) > 500) {
            signup_height.setError("Please enter valid height!");
            return false;
        } else {
            signup_weight.setError(null);
            signup_weight.setErrorEnabled(false);
            return true;
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            signup_firstname.setError(null);
            signup_firstname.setErrorEnabled(false);
            signup_lastname.setError(null);
            signup_lastname.setErrorEnabled(false);
            signup_height.setError(null);
            signup_height.setErrorEnabled(false);
            signup_weight.setError(null);
            signup_weight.setErrorEnabled(false);

        }

        @Override
        public void afterTextChanged(Editable s) {


        }
    };

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), CreateProfileOneActivity.class);
        startActivity(intent);
        finish();

    }

    private void getOrganization() {
        if (CM.isConnected(CreateProfileTwoActivity.this)) {
            JSONObject object = new JSONObject();
            try {
                //input your API parameters
                object.put("id", userId);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.allcorporates, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("status").equals("true")) {
                                    JSONArray array = new JSONArray(response.getString("data"));
                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(i);

                                        String id = object.getString("id");
                                        String name = object.getString("name");

                                        OrganizationModel organizationModel = new OrganizationModel(id, name);
                                        organizationModels.add(organizationModel);

                                    }
                                    if (!organizationModels.isEmpty()) {
                                        for (int i = 0; i < organizationModels.size(); i++) {
                                            list.add(organizationModels.get(i).getName());
                                        }

                                    }

                                }

                            } catch (Exception e) {

                                Log.e(Constants.TAG, e.getMessage());

                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error", error.toString());

                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);


        } else
            Toast.makeText(CreateProfileTwoActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(getApplicationContext(), CreateProfileOneActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

