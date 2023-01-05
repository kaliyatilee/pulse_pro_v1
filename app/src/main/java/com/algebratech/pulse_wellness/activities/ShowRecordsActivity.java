package com.algebratech.pulse_wellness.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.ActivitiesSummaryAdapter;
import com.algebratech.pulse_wellness.adapters.ShowDataAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.db.DBHelper;
import com.algebratech.pulse_wellness.models.ActivityListModel;
import com.algebratech.pulse_wellness.models.TodaysActivityModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowRecordsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private DBHelper db;
    private String type;
    private String activityName;
    private SharedPreferences sharedPreferences;
    private int user_id;
    TextView noData,activityLog;
    LinearLayout activityData;
    private Toolbar toolbarPolicy;
    List<TodaysActivityModel> todaysActivityModels = new ArrayList<>();

    ArrayList<ActivityListModel> activityListModels = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_records);

        activityName = getIntent().getStringExtra("title");
        type = getIntent().getStringExtra("type");
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        user_id = Integer.parseInt(sharedPreferences.getString("userID", null));
        SQLiteDatabase.loadLibs(ShowRecordsActivity.this);
        db = new DBHelper(ShowRecordsActivity.this);


        toolbarPolicy = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarPolicy);
        setTitle(activityName);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        activityData = findViewById(R.id.activityData);
        noData = findViewById(R.id.noData);
        activityLog = findViewById(R.id.activityLog);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext()) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 100;
            }
        };
        recyclerView.setLayoutManager(linearLayoutManager);
        getTodaysActivities();

    }

    private void getActivities() {

        if (activityListModels.isEmpty() || activityListModels == null) {
            activityData.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            activityLog.setVisibility(View.VISIBLE);
            mAdapter = new ShowDataAdapter(ShowRecordsActivity.this, activityListModels);
            recyclerView.setAdapter(mAdapter);
        }


    }


    private void getTodaysActivities() {
        if (CM.isConnected(ShowRecordsActivity.this)) {
            CM.showProgressLoader(ShowRecordsActivity.this);
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", user_id);
                object.put("activity", activityName);

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


                                        ActivityListModel activityListModel = new ActivityListModel(activityName, username, time_taken, Integer.parseInt(steps), distance, avg_pace, avarage_heart_rate, kcals, created_at, "", "", "", false);
                                        activityListModels.add(activityListModel);


                                    }
                                    getActivities();

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
            RequestQueue requestQueue = Volley.newRequestQueue(ShowRecordsActivity.this);
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(ShowRecordsActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }


}
