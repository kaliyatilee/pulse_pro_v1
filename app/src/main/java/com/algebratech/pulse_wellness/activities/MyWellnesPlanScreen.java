package com.algebratech.pulse_wellness.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.SlidingAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.models.LikeModel;
import com.algebratech.pulse_wellness.models.SlidingModel;
import com.algebratech.pulse_wellness.models.WellnessPlanModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyWellnesPlanScreen extends AppCompatActivity {
    private Toolbar toolbarPolicy;
    private Button fab;
    private int[] myImageList = new int[]{R.drawable.banner_1, R.drawable.banner_calculator, R.drawable.banner_3};
    SharedPreferences sharedPreferences;
    String userId;
    private List<WellnessPlanModel> wellnessPlanModels = new ArrayList<>();
    TextView steps, distances, calories_burnt, frequency_of_activity, daily_calorie_intake, daily_reminder, participation_in_rigorous_school_sport, recommended_calorie_deficit, duration_of_exercise;
    private TextView no_plan, title;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wellnes_plan_screen);
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);
        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        steps = findViewById(R.id.steps);
        distances = findViewById(R.id.distances);
        calories_burnt = findViewById(R.id.calories_burnt);
        frequency_of_activity = findViewById(R.id.frequency_of_activity);
        daily_calorie_intake = findViewById(R.id.daily_calorie_intake);
        daily_reminder = findViewById(R.id.daily_reminder);
        participation_in_rigorous_school_sport = findViewById(R.id.participation_in_rigorous_school_sport);
        duration_of_exercise = findViewById(R.id.duration_of_exercise);
        recommended_calorie_deficit = findViewById(R.id.recommended_calorie_deficit);
        fab = findViewById(R.id.fab);
        no_plan = findViewById(R.id.no_plan);
        title = findViewById(R.id.title);
        relativeLayout = findViewById(R.id.relativeLayout);
        setSupportActionBar(toolbarPolicy);
        setTitle("My Wellnessplan");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getWellnessPlan();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "Downloading..", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private ArrayList<SlidingModel> populateList() {
        ArrayList<SlidingModel> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            SlidingModel imageModel = new SlidingModel();
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }
        return list;
    }


    private void getWellnessPlan() {

        if (CM.isConnected(MyWellnesPlanScreen.this)) {
            CM.showProgressLoader(MyWellnesPlanScreen.this);
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.getMyWellnessPlan, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(Constants.TAG, String.valueOf(response));
                            no_plan.setVisibility(View.GONE);
                            title.setVisibility(View.VISIBLE);
                            relativeLayout.setVisibility(View.VISIBLE);
                            //fab.setVisibility(View.VISIBLE);

                            try {
                                if (response.getString("status").equals("true")) {
                                    JSONObject object = new JSONObject(response.getString("data"));
                                    {
                                        Log.e("Welness", object.toString());
                                        String plan_name = object.getString("plan_name");
                                        String steps = object.getString("steps");
                                        String daily_distance = object.getString("daily_distance");
                                        String calories_burnt = object.getString("calories_burnt");
                                        String frequency_of_activity = object.getString("frequency_of_activity");
                                        String daily_calorie_intake = object.getString("daily_calorie_intake");
                                        String daily_reminder = object.getString("daily_reminder");
                                        String duration_of_exercise = object.getString("duration_of_exercise");
                                        String recommended_calorie_deficit = object.getString("recommended_calorie_deficit");
                                        String participation_in_rigorous_school_sport = object.getString("participation_in_rigorous_school_sport");
                                        Log.e("User Deatils", plan_name + steps + daily_distance + calories_burnt + frequency_of_activity + daily_calorie_intake + daily_reminder + duration_of_exercise + recommended_calorie_deficit + participation_in_rigorous_school_sport);
                                        WellnessPlanModel wellnessPlanModel = new WellnessPlanModel(plan_name, steps, daily_distance, calories_burnt, frequency_of_activity, daily_calorie_intake, daily_reminder, duration_of_exercise, participation_in_rigorous_school_sport, recommended_calorie_deficit);
                                        wellnessPlanModels.add(wellnessPlanModel);
                                    }

                                    if (!wellnessPlanModels.get(0).getSteps().equals("") && !wellnessPlanModels.get(0).getSteps().equals("null") && wellnessPlanModels.get(0).getSteps() != null)
                                        steps.setText(wellnessPlanModels.get(0).getSteps());
                                    else
                                        steps.setText("-");

                                    if (!wellnessPlanModels.get(0).getDaily_distance().equals("") && !wellnessPlanModels.get(0).getDaily_distance().equals("null") && wellnessPlanModels.get(0).getDaily_distance() != null)
                                        distances.setText(wellnessPlanModels.get(0).getDaily_distance());
                                    else
                                        distances.setText("-");

                                    if (!wellnessPlanModels.get(0).getCalories_burnt().equals("") && !wellnessPlanModels.get(0).getCalories_burnt().equals("null") && wellnessPlanModels.get(0).getCalories_burnt() != null)
                                        calories_burnt.setText(wellnessPlanModels.get(0).getCalories_burnt());
                                    else
                                        calories_burnt.setText("-");

                                    if (!wellnessPlanModels.get(0).getFrequency_of_activity().equals("") && !wellnessPlanModels.get(0).getFrequency_of_activity().equals("null") && wellnessPlanModels.get(0).getFrequency_of_activity() != null)
                                        frequency_of_activity.setText(wellnessPlanModels.get(0).getFrequency_of_activity());
                                    else
                                        frequency_of_activity.setText("-");

                                    if (!wellnessPlanModels.get(0).getDaily_calorie_intake().equals("") && !wellnessPlanModels.get(0).getDaily_calorie_intake().equals("null") && wellnessPlanModels.get(0).getDaily_calorie_intake() != null)
                                        daily_calorie_intake.setText(wellnessPlanModels.get(0).getDaily_calorie_intake());
                                    else
                                        daily_calorie_intake.setText("-");

                                    if (!wellnessPlanModels.get(0).getDaily_reminder().equals("") && !wellnessPlanModels.get(0).getDaily_reminder().equals("null") && wellnessPlanModels.get(0).getDaily_reminder() != null)
                                        daily_reminder.setText(wellnessPlanModels.get(0).getDaily_reminder());
                                    else
                                        daily_reminder.setText("-");


                                    if (wellnessPlanModels.get(0).getParticipation_in_rigorous_school_sport() != null &&
                                            wellnessPlanModels.get(0).getParticipation_in_rigorous_school_sport() != "null" && !wellnessPlanModels.get(0).getParticipation_in_rigorous_school_sport().equals("")) {
                                        participation_in_rigorous_school_sport.setText(wellnessPlanModels.get(0).getParticipation_in_rigorous_school_sport());
                                    } else
                                        participation_in_rigorous_school_sport.setText("-");

                                    if (wellnessPlanModels.get(0).getRecommended_calorie_deficit() != null &&
                                            wellnessPlanModels.get(0).getRecommended_calorie_deficit() != "null" && !wellnessPlanModels.get(0).getRecommended_calorie_deficit().equals("")) {
                                        recommended_calorie_deficit.setText(wellnessPlanModels.get(0).getRecommended_calorie_deficit());
                                    } else
                                        recommended_calorie_deficit.setText("-");

                                    if (wellnessPlanModels.get(0).getDuration_of_exercise() != null &&
                                            wellnessPlanModels.get(0).getDuration_of_exercise() != "null" && !wellnessPlanModels.get(0).getDuration_of_exercise().equals("")) {
                                        duration_of_exercise.setText(wellnessPlanModels.get(0).getDuration_of_exercise());
                                    } else
                                        duration_of_exercise.setText("-");

                                    CM.HideProgressLoader();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                CM.HideProgressLoader();
                            }

                            if (wellnessPlanModels.size() == 0) {
                                no_plan.setVisibility(View.VISIBLE);
                                CM.HideProgressLoader();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.getMessage());
                    Toast.makeText(MyWellnesPlanScreen.this, "No internet connection available!!!.", Toast.LENGTH_SHORT).show();
                    CM.HideProgressLoader();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);

        } else
            Toast.makeText(MyWellnesPlanScreen.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }
}