package com.algebratech.pulse_wellness.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.RegisterActivity;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.models.GraphModel;
import com.algebratech.pulse_wellness.services.DeviceConnect;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

public class ActivitySummaryFragment extends AppCompatActivity {

    private View root;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;
    private Intent intent;
    private Context mContext;
    private TextView numSteps, numDistance, numKcals, percent;
    private ProgressBar progressBar;
    private Button showdata;
    //private GraphView graph;


    private Toolbar toolbarPolicy;
    CardView day, week, month;
    String graphValue = "daily";
    private String userId;
    private RequestQueue requestQueue;
    private LineChart chart;
    List<GraphModel> graphModelList = new ArrayList<>();
    List<String> dayArray = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_summary);
        dayArray.add("1");
        dayArray.add("9");
        dayArray.add("5");
        dayArray.add("3");
        dayArray.add("5");
        dayArray.add("9");
        dayArray.add("0");
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        userId = sharedPreferences.getString("userID", null);
        intent = new Intent(this, DeviceConnect.class);

        toolbarPolicy = findViewById(R.id.toolbar);
        day = findViewById(R.id.day);
        week = findViewById(R.id.week);
        month = findViewById(R.id.month);
        numSteps = findViewById(R.id.numSteps);
        numDistance = findViewById(R.id.numDistance);
        numKcals = findViewById(R.id.numKcals);
        percent = findViewById(R.id.percent);
        progressBar = findViewById(R.id.progressBar);
        showdata = findViewById(R.id.showdata);
        //graph = findViewById(R.id.graphView);
        chart = findViewById(R.id.chart1);
        setSupportActionBar(toolbarPolicy);
        setTitle("Activity Report");
        getReport();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphValue = "daily";
                day.setCardBackgroundColor(getResources().getColor(R.color.primary));
                week.setCardBackgroundColor(getResources().getColor(R.color.grey_40));
                month.setCardBackgroundColor(getResources().getColor(R.color.grey_40));
                getReport();
            }
        });

        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphValue = "weekly";
                week.setCardBackgroundColor(getResources().getColor(R.color.primary));
                day.setCardBackgroundColor(getResources().getColor(R.color.grey_40));
                month.setCardBackgroundColor(getResources().getColor(R.color.grey_40));
                getReport();
            }
        });

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphValue = "monthly";
                month.setCardBackgroundColor(getResources().getColor(R.color.primary));
                week.setCardBackgroundColor(getResources().getColor(R.color.grey_40));
                day.setCardBackgroundColor(getResources().getColor(R.color.grey_40));
                getReport();
            }
        });


        showdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySummaryFragment.this, ShowDataFragment.class);
                startActivity(intent);
            }
        });


        chart.setBackgroundColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);


        XAxis xAxis;
        {
            xAxis = chart.getXAxis();

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        }

        YAxis yAxis;
        {
            yAxis = chart.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);


        }


    }


    private void getReport() {

        if (CM.isConnected(ActivitySummaryFragment.this)) {

            CM.showProgressLoader(ActivitySummaryFragment.this);

            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);
                object.put("report_type", graphValue);
                Log.e("Reqqq", object.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.getReport, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("ActivitySummaryFragment", response.toString());
                            try {
                                if (response.getString("status").equals("true")) {

                                    JSONObject object = new JSONObject(response.getString("data"));
                                    {
                                        String sum_of_distance = object.getString("sum_of_distance");
                                        String sum_of_steps = object.getString("sum_of_steps");
                                        String sum_of_calories = object.getString("sum_of_calories");
                                        String final_per = object.getString("final_per");

                                        if (!sum_of_steps.isEmpty() && !sum_of_steps.equals(""))
                                            numSteps.setText(sum_of_steps);
                                        else
                                            numSteps.setText("0");

                                        if (!sum_of_distance.isEmpty() && !sum_of_distance.equals(""))
                                            numDistance.setText(sum_of_distance);
                                        else
                                            numDistance.setText("0");

                                        if (!sum_of_calories.isEmpty() && !sum_of_calories.equals(""))
                                            numKcals.setText(sum_of_calories);
                                        else
                                            numKcals.setText("0");

                                        percent.setText("" + final_per + " %");
                                        int i = (int) Math.round(Double.parseDouble(final_per));
                                        progressBar.setProgress(i);
                                    }

                                    JSONArray array = new JSONArray(response.getString("graph_arr"));
                                    graphModelList.clear();
                                    Log.e("Sizeee", String.valueOf(graphModelList.size()));
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject objectArray = array.getJSONObject(i);

                                        GraphModel graphModel = new GraphModel(objectArray.getString("label"), objectArray.getString("distance"), objectArray.getString("steps"), objectArray.getString("calories"));
                                        graphModelList.add(graphModel);

                                    }

                                    GenerateGraph(graphModelList);

                                }
                                CM.HideProgressLoader();
                            } catch (Exception e) {
                                CM.HideProgressLoader();

                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e("ERROR", error.toString());
                            CM.HideProgressLoader();
                        }
                    });

            requestQueue = Volley.newRequestQueue(ActivitySummaryFragment.this);
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(ActivitySummaryFragment.this, R.string.noInternet, Toast.LENGTH_SHORT).show();



    }


    void GenerateGraph(List<GraphModel> graphModelList) {
        XAxis xAxis = chart.getXAxis();
        YAxis leftAxis = chart.getAxisLeft();
//        ValueFormatter xAxisFormatter = new HourAxisFormatterClass(Calendar.getInstance().getTimeInMillis());
        xAxis.setValueFormatter(new ClaimsXAxisValueFormatter(graphValue));
        //  xAxis.setValueFormatter(xAxisFormatter);


        List<Entry> values = new ArrayList<>();

//        if (graphValue.equals("weekly")) {
        for (int i = 0; i < graphModelList.size(); i++) {
            SimpleDateFormat dateFormat;
            if (graphValue.equals("daily")) {
                dateFormat = new SimpleDateFormat("yyy-MM-dd hh:mm:ss");

            } else {
                dateFormat = new SimpleDateFormat("yyy-MM-dd");

            }

            try {
                Date d = dateFormat.parse(graphModelList.get(i).getLabel());
                String Day = (String) DateFormat.format("EEEE", d);
                int valueOfI = -1;

                values.add(new Entry(d.getTime(), Integer.parseInt(graphModelList.get(i).getSteps())));
                Log.e("STEPS=====", graphModelList.get(i).getLabel() + "==" + d + "  ==" + Integer.parseInt(graphModelList.get(i).getSteps()));

            } catch (ParseException e) {
                e.printStackTrace();
            }

//            }
        }

        LineDataSet set1 = new LineDataSet(values, "Steps");


        set1.setDrawValues(true);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        chart.notifyDataSetChanged();
        chart.invalidate();

        LineData data = new LineData(dataSets);
        Log.e("DATAAAAA", String.valueOf(data.getEntryCount()));
        chart.setData(data);
    }
}


class ClaimsXAxisValueFormatter extends ValueFormatter {

    String graphValue;

    public ClaimsXAxisValueFormatter(String graphValue) {
        this.graphValue = graphValue;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {

        Date d = new Date();
        d.setTime((long) value);
        if (graphValue.equals("weekly")) {
            String Day = (String) DateFormat.format("EEE", d);
            Log.e("VALUE", Day);

            return Day;

        } else if (graphValue.equals("monthly")) {
            String Day = (String) DateFormat.format("dd MMM", d);
            Log.e("VALUE", Day);
            return Day;

        } else {
            String Day = (String) DateFormat.format("hh a", d);
            Log.e("VALUE", Day);
            return Day;
        }


//            if (value == 0)
//                return "Mon";
//            else if (value == 1)
//                return "Tue";
//            else if (value == 2)
//                return "Wed";
//            else if (value == 3)
//                return "Thu";
//            else if (value == 4)
//                return "Fri";
//            else if (value == 5)
//                return "Sat";
//            else if (value == 6)
//                return "Sun";
//        }

//
//
//        for (int i = 0; i < day.size(); i++) {
//            return day.get(i).toString();
//        }
//
//        return "";
    }
}




