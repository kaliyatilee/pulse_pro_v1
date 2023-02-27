package com.algebratech.pulse_wellness.dashboardReports;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.algebratech.pulse_wellness.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.wosmart.ukprotocollibary.model.db.GlobalGreenDAO;
import com.wosmart.ukprotocollibary.model.sport.SportData;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class StepsActivity extends AppCompatActivity {
    TextView numSteps,numDistance,numKcals,percent;
    String steps;
    String distance;
    String kcals;
    ProgressBar progressBar;
    GraphView graphView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);
        Intent intent = getIntent();
        steps = intent.getStringExtra("today_steps");
        kcals = intent.getStringExtra("today_kcals");
        distance = intent.getStringExtra("today_distance");


        numSteps = findViewById(R.id.numSteps);
        numDistance = findViewById(R.id.numDistance);
        numKcals = findViewById(R.id.numKcals);
        percent = findViewById(R.id.percent);
        progressBar = findViewById(R.id.progressBar);
        graphView = findViewById(R.id.graphView);

        numDistance.setText(distance);
        numSteps.setText(steps);
        numKcals.setText(kcals);

        double percentage = 0;

        System.out.println("++++++++++++++++steps"+steps);
        if(steps != null){
            double totalSteps = Double.parseDouble(steps);
            percentage = totalSteps / 10000;
            percentage = percentage *100;
        }

        percent.setText(String.valueOf(percentage));
        progressBar.setMax(100);
        progressBar.setProgress((int) percentage);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int stepcount = 0;
        int calories = 0;
        int distance  = 0;
        List<SportData> steps = GlobalGreenDAO.getInstance().loadSportDataByDate(year,month,23);
       // List<SportData> steps = GlobalGreenDAO.getInstance().loadAllSportData();
        System.out.println("++++++++++++++++++"+steps);
        DataPoint[] dataPoints = new DataPoint[steps.size()];
        DataPoint[] dataPoints1 = new DataPoint[steps.size()];
        int i = 0;
        if (null != steps) {
            for (SportData item : steps) {
                dataPoints[i] = new DataPoint(i, item.getStepCount());
                dataPoints1[i] =new DataPoint(i,item.getCalory());
                i = i +1;
            }

            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
            LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>(dataPoints1);

            series1.setBackgroundColor(R.color.primary);
            series1.setBackgroundColor(R.color.red);

            graphView.addSeries(series);
            graphView.addSeries(series1);
        }



    }
}