package com.algebratech.pulse_wellness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.algebratech.pulse_wellness.activities.DetailActivitySummary;
import com.algebratech.pulse_wellness.activities.FriendDetailScreen;
import com.algebratech.pulse_wellness.activities.RegisterActivity;
import com.algebratech.pulse_wellness.adapters.ActivitiesSummaryAdapter;
import com.algebratech.pulse_wellness.adapters.TransactionAdapter;
import com.algebratech.pulse_wellness.adapters.allActivityAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.models.TodaysActivityModel;
import com.algebratech.pulse_wellness.models.TransactionModel;
import com.algebratech.pulse_wellness.models.allActivityModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivityScreen extends AppCompatActivity {
    private Toolbar toolbarPolicy;
    private RecyclerView recyclerView;
    private String friendId, url, steps, name;
    List<TodaysActivityModel> todaysActivityModels = new ArrayList<>();
    private ActivitiesSummaryAdapter activitiesSummaryAdapter;
    private RecyclerView.Adapter mAdapter;
    CircleImageView profilePic;
    TextView nameTextView, pointsText, no_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity_screen);

        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        recyclerView = findViewById(R.id.recycle_activity_data);
        profilePic = findViewById(R.id.profilePic);
        nameTextView = findViewById(R.id.name);
        pointsText = findViewById(R.id.pointsText);
        no_data = findViewById(R.id.no_data);
        setSupportActionBar(toolbarPolicy);
        setTitle("Detail Activity");

        WristbandManager.getInstance().startScan();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        //TODO here we have to replace steps with points
        steps = intent.getStringExtra("steps");
        name = intent.getStringExtra("name");
        friendId = intent.getStringExtra("friendId");
        Glide.with(DetailActivityScreen.this).load(url).error(R.drawable.placeholder).into(profilePic);
        nameTextView.setText(name);
        pointsText.setText(steps);
        getTodaysActivities();
    }


    private void getTodaysActivities() {
        if (CM.isConnected(DetailActivityScreen.this)) {
            CM.showProgressLoader(DetailActivityScreen.this);
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", friendId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.getActivityDetailsLists, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.d("activitysportsdata", String.valueOf(object));
                            Log.e("===", "===>");
                            Log.d("activitysportsdataRES", response.toString());
                            CM.HideProgressLoader();
                            try {
                                if (response.getString("status").equals("true")) {
                                    todaysActivityModels.clear();

                                    JSONArray array = new JSONArray(response.getString("data"));
                                    JSONObject wellness_plan_data = new JSONObject(response.getString("wellness_plan_data"));

                                    TodaysActivityModel todaysActivityModel1 = new TodaysActivityModel("", "", "", wellness_plan_data.getString("loyalpoints") + " pts", "", "", "", wellness_plan_data.getString("steps"), "", "", "Daily Walking", "", "", "", "", "", "");
                                    todaysActivityModels.add(todaysActivityModel1);
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
                                    if (todaysActivityModels.size() == 0) {
                                        no_data.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    } else
                                        setdataAdapter();

                                }
                            } catch (JSONException e) {
                                Log.e("eroorr", e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CM.HideProgressLoader();
                    no_data.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    Log.e("activitysportsdata", error.toString());
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(DetailActivityScreen.this);
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(DetailActivityScreen.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    void setdataAdapter() {
        activitiesSummaryAdapter = new ActivitiesSummaryAdapter(DetailActivityScreen.this, todaysActivityModels);
        mAdapter = activitiesSummaryAdapter;
        recyclerView.setLayoutManager(new LinearLayoutManager(DetailActivityScreen.this));
        recyclerView.setAdapter(mAdapter);
    }
}
