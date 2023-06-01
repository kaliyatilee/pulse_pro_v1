package com.algebratech.pulse_wellness.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.interfaces.DialogClickListener;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AddUserGoals extends AppCompatActivity {
    private Toolbar toolbarPolicy;
    private SeekBar seekBar1, seekBar2, seekBar3, seekBar4;
    private TextView lose_weight, running_distance, calories_burn, steps;
    private Button set_goal;
    int runningDistance, caloriesBurn, stepss;
    float loseWeight;
    int i = 0;
    private SharedPreferences sharedPreferences;
    private String userId;
    private final int requestCountmain = 1;
    private RequestQueue requestQueue;
    String gender = "";
    ImageView userIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_goals);

        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);
        seekBar1 = findViewById(R.id.seekBar1);
        seekBar2 = findViewById(R.id.seekBar2);
        seekBar3 = findViewById(R.id.seekBar3);
        seekBar4 = findViewById(R.id.seekBar4);
        lose_weight = findViewById(R.id.lose_weight);
        running_distance = findViewById(R.id.running_distance);
        calories_burn = findViewById(R.id.calories_burn);
        steps = findViewById(R.id.steps);
        set_goal = findViewById(R.id.set_goal);
        userIcon = findViewById(R.id.userIcon);
        setSupportActionBar(toolbarPolicy);
        setTitle("Set Activity Goals");
        gender = sharedPreferences.getString("gender", null);
        Log.e("gender", gender);
        if (gender.equals("Male"))
            userIcon.setImageResource(R.drawable.yoga_men);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar1.setMin(0);
            seekBar1.setMax(5000);
        }
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                float decimalProgress = (float) i / 10;
                lose_weight.setText(decimalProgress + " gm");
                loseWeight = decimalProgress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar2.setMin(0);
            seekBar2.setMax(200);
        }
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                running_distance.setText(i + " km");
                runningDistance = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar3.setMin(0);
            seekBar3.setMax(20000);
        }
        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                calories_burn.setText(i + " kcal");
                caloriesBurn = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar4.setMin(0);
            seekBar4.setMax(150000);
        }
        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                steps.setText(i + " steps");
                stepss = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        set_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Lose weight", String.valueOf(loseWeight));
                Log.e("Running Distance", String.valueOf(runningDistance));
                Log.e("Calories Burn", String.valueOf(caloriesBurn));
                Log.e("Steps", String.valueOf(stepss));

                if (CM.isConnected(AddUserGoals.this)) {
                    if (loseWeight == 0 && runningDistance == 0 && caloriesBurn == 0 && stepss == 0) {
                        Toast.makeText(getApplicationContext(), "Please select at least one goal", Toast.LENGTH_LONG).show();

                    } else {
                        CM.showProgressLoader(AddUserGoals.this);
                        JSONObject object = new JSONObject();
                        try {
                            object.put("user_id", userId);
                            object.put("weight", loseWeight);
                            object.put("steps", stepss);
                            object.put("calories", caloriesBurn);
                            object.put("running_distance", runningDistance);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.setgoal, object,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d(Constants.TAG, String.valueOf(response));
                                        CM.HideProgressLoader();
                                        try {

                                            if (response.getString("status").equals("success")) {
//                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AddUserGoals.this);
//                                        builder1.setMessage("User goal added");
//                                        builder1.setCancelable(true);
//
//                                        builder1.setPositiveButton(
//                                                "Ok",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(DialogInterface dialog, int id) {
//                                                        dialog.cancel();
//                                                        finish();
//                                                    }
//                                                });
//
//                                        AlertDialog alert11 = builder1.create();
//                                        alert11.show();

                                                CM.ShowDialogueWithCustomAction(
                                                        AddUserGoals.this,
                                                        "User goal added",
                                                        "Ok", "", false,
                                                        new DialogClickListener() {
                                                            @Override
                                                            public void onPositiveClick() {
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onNegativeClick() {

                                                            }
                                                        }
                                                );


                                            } else if (response.getString("status").equals("error")) {
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(AddUserGoals.this);
                                                builder1.setMessage("User goal already added");
                                                builder1.setCancelable(true);

                                                builder1.setPositiveButton(
                                                        "Ok",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                                finish();
                                                            }
                                                        });

                                                AlertDialog alert11 = builder1.create();
                                                alert11.show();

                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.d("Error", "Error: " + error.toString());
                                CM.HideProgressLoader();
                            }
                        });
                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(jsonObjectRequest);

                    }
                } else
                    Toast.makeText(AddUserGoals.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


            }
        });
    }

}