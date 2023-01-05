package com.algebratech.pulse_wellness.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.interfaces.DialogClickListener;
import com.algebratech.pulse_wellness.models.GraphModel;
import com.algebratech.pulse_wellness.models.NewsFeedModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendDetailScreen extends AppCompatActivity {
    private Toolbar toolbarPolicy;
    TextView tvFullname, friendPoints, steps, dis, cal;
    public LinearLayoutManager mLayoutManager;
    private String userId;
    CircleImageView profilePic;
    private int requestCountmain = 1;
    private String friendId;
    private RequestQueue requestQueue;
    List<GraphModel> graphModelList = new ArrayList<>();
    private LineChart chart;
    Button unfriend;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_detail_screen);
        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        setSupportActionBar(toolbarPolicy);
        setTitle("Friend Detail Screen");
        chart = findViewById(R.id.friend_chart);
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();

        friendId = intent.getStringExtra("friendId");


        tvFullname = findViewById(R.id.tvFullname);
        friendPoints = findViewById(R.id.friendPoints);
        profilePic = findViewById(R.id.profilePic);
        steps = findViewById(R.id.steps);
        dis = findViewById(R.id.dis);
        cal = findViewById(R.id.cal);
        unfriend = findViewById(R.id.unfriend);

        getFriendDetail();
        unfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CM.ShowDialogueWithCustomAction(
                        FriendDetailScreen.this,
                        "Are you sure, you want to unfriend this user?",
                        "Yes", "No", true,
                        new DialogClickListener() {
                            @Override
                            public void onPositiveClick() {
                                unfriend();
                            }

                            @Override
                            public void onNegativeClick() {

                            }
                        }
                );
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    void getFriendDetail() {


        if (CM.isConnected(FriendDetailScreen.this)) {
            CM.showProgressLoader(FriendDetailScreen.this);
            JSONObject object = new JSONObject();
            try {

                object.put("user_id", friendId);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.friendDetails, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("Friend Details", String.valueOf(response));


                            try {

                                if (response.getString("status").equals("true")) {


                                    JSONObject data = new JSONObject(response.getString("data"));
                                    JSONObject users = new JSONObject(data.getString("users"));
                                    {
                                        String id = users.getString("id");
                                        String firstname = users.getString("firstname");
                                        String lastname = users.getString("lastname");
                                        String profileurl = users.getString("profileurl");
                                        String bmii = users.getString("bmi");
                                        String heightt = users.getString("height");
                                        String weightt = users.getString("weight");
                                        String loyaltpoints = users.getString("loyaltpoints");
                                        String distance = users.getString("distance");
                                        String stepss = users.getString("steps");
                                        String calories = users.getString("calories");


                                        tvFullname.setText(firstname + " " + lastname);
                                        Glide.with(FriendDetailScreen.this).load(profileurl).error(R.drawable.placeholder).into(profilePic);
                                        friendPoints.setText(loyaltpoints + " pts");
                                        if (!stepss.isEmpty())
                                            steps.setText(stepss);
                                        else
                                            steps.setText("0");

                                        if (!distance.isEmpty())
                                            dis.setText(distance + " km");
                                        else
                                            dis.setText("0 km");

                                        if (!calories.isEmpty())
                                            cal.setText(calories+" kcal");
                                        else
                                            cal.setText("0 kcal");

                                    }

                                    JSONArray daily_arr = new JSONArray(data.getString("daily_arr"));
                                    {
                                        for (int i = 0; i < daily_arr.length(); i++) {
                                            JSONObject objectArray = daily_arr.getJSONObject(i);

                                            GraphModel graphModel = new GraphModel(objectArray.getString("label"), objectArray.getString("distance"), objectArray.getString("steps"), objectArray.getString("calories"));
                                            graphModelList.add(graphModel);
                                        }
                                        GenerateGraph(graphModelList);
                                    }

                                    CM.HideProgressLoader();

                                } else {
                                    CM.HideProgressLoader();
                                }

                            } catch (Exception e) {

                                Log.e(Constants.TAG, e.getMessage());
                                CM.HideProgressLoader();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CM.HideProgressLoader();
                }
            });

            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
            requestCountmain++;
        } else
            Toast.makeText(FriendDetailScreen.this, R.string.noInternet, Toast.LENGTH_SHORT).show();

    }


    void GenerateGraph(List<GraphModel> graphModelList) {
        XAxis xAxis = chart.getXAxis();
        YAxis leftAxis = chart.getAxisLeft();
        chart.setBackgroundColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        xAxis = chart.getXAxis();

        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis;
        yAxis = chart.getAxisLeft();
        chart.getAxisRight().setEnabled(false);


        xAxis.setValueFormatter(new ClaimsXAxisValueFormatter1());


        List<Entry> values = new ArrayList<>();

        for (int i = 0; i < graphModelList.size(); i++) {
            SimpleDateFormat dateFormat;
            dateFormat = new SimpleDateFormat("yyy-MM-dd hh:mm:ss");


            try {
                Date d = dateFormat.parse(graphModelList.get(i).getLabel());
                String Day = (String) DateFormat.format("EEEE", d);
                int valueOfI = -1;

                values.add(new Entry(d.getTime(), Integer.parseInt(graphModelList.get(i).getSteps())));

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        LineDataSet set1 = new LineDataSet(values, "Steps");


        set1.setDrawValues(true);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        chart.notifyDataSetChanged();
        chart.invalidate();

        LineData data = new LineData(dataSets);
        chart.setData(data);
    }

    void unfriend() {
        if (CM.isConnected(FriendDetailScreen.this)) {
            CM.showProgressLoader(FriendDetailScreen.this);
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);
                object.put("friend_id", friendId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.unfriend, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                if (response.getString("status").equals("true")) {

                                    try {
                                        onBackPressed();
                                    } catch (Exception e) {

                                    }


                                } else {
                                    Toast.makeText(FriendDetailScreen.this, response.getString("message"), Toast.LENGTH_LONG).show();

                                }

                                CM.HideProgressLoader();
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
            requestQueue = Volley.newRequestQueue(FriendDetailScreen.this);
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(FriendDetailScreen.this, R.string.noInternet, Toast.LENGTH_SHORT).show();

    }

}

class ClaimsXAxisValueFormatter1 extends ValueFormatter {


    @Override
    public String getAxisLabel(float value, AxisBase axis) {

        Date d = new Date();
        d.setTime((long) value);
        String Day = (String) DateFormat.format("hh a", d);
        Log.e("VALUE", Day);
        return Day;


    }
}
