package com.algebratech.pulse_wellness.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.ActivitiesSummaryAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.models.ActivitiesSummaryModel;
import com.algebratech.pulse_wellness.models.TodaysActivityModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailActivitySummary extends AppCompatActivity {
    private Toolbar toolbarPolicy;
    RecyclerView activitiesSummary;
    private ActivitiesSummaryAdapter activitiesSummaryAdapter;
    private RecyclerView.Adapter mAdapter;
    SharedPreferences sharedPreferences;
    String userId;
    List<TodaysActivityModel> todaysActivityModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_summary);

        toolbarPolicy = findViewById(R.id.toolbar);
        activitiesSummary = findViewById(R.id.activitiesSummary);
        setSupportActionBar(toolbarPolicy);
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);
        getTodaysActivities();
        setTitle("Today's Activity");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }


    private void getTodaysActivities() {
        if (CM.isConnected(DetailActivitySummary.this)) {
            CM.showProgressLoader(DetailActivitySummary.this);
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.getActivityDetailsLists, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("activitysportsdata", String.valueOf(response));
                            CM.HideProgressLoader();
                            try {
                                if (response.getString("status").equals("true")) {
                                    todaysActivityModels.clear();

                                    JSONArray array = new JSONArray(response.getString("data"));

                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(i);

                                        String id = object.getString("id");
                                        String user_id = object.getString("user_id");
                                        String act_id = object.getString("act_id");
                                        String time_taken = object.getString("duration");
                                        String distance = object.getString("distance");
                                        String kcals = object.getString("kcals");
                                        String avarage_heart_rate = object.getString("avarage_heart_rate");
                                        String steps = object.getString("steps");
                                        String avg_pace = object.getString("avg_pace");
                                        String camera_file = object.getString("camera_file");
                                        String activity = object.getString("activity");
                                        String created_at = object.getString("created_at");
                                        String updated_at = object.getString("updated_at");
                                        String username = object.getString("username");
                                        String firstname = object.getString("firstname");
                                        String lastname = object.getString("lastname");
                                        String profileurl = object.getString("profileurl");


                                        TodaysActivityModel todaysActivityModel = new TodaysActivityModel(id, user_id, act_id, time_taken, distance, kcals, avarage_heart_rate, steps, avg_pace, camera_file, activity, created_at, updated_at, username, firstname, lastname, profileurl);
                                        todaysActivityModels.add(todaysActivityModel);


                                    }
                                    setdataAdapter();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CM.HideProgressLoader();
                    Log.e("activitysportsdata", error.toString());
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(DetailActivitySummary.this);
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(DetailActivitySummary.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    void setdataAdapter() {
        activitiesSummaryAdapter = new ActivitiesSummaryAdapter(DetailActivitySummary.this, todaysActivityModels);
        mAdapter = activitiesSummaryAdapter;
        activitiesSummary.setLayoutManager(new LinearLayoutManager(DetailActivitySummary.this));
        activitiesSummary.setAdapter(mAdapter);
    }
}