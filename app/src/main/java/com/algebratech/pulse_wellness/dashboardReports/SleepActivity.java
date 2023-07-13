package com.algebratech.pulse_wellness.dashboardReports;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.algebratech.pulse_wellness.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.wosmart.ukprotocollibary.model.db.GlobalGreenDAO;
import com.wosmart.ukprotocollibary.model.sleep.SleepData;
import com.wosmart.ukprotocollibary.model.sleep.SleepSubData;
import com.wosmart.ukprotocollibary.model.sport.SportData;
import com.wosmart.ukprotocollibary.util.WristbandCalculator;

import java.util.Calendar;
import java.util.List;

public class SleepActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView tvTotalDuration,tvDate,deep,light,stayUp;
    Button showData;
    GraphView graphView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        progressBar = findViewById(R.id.progressBar);
        tvTotalDuration = findViewById(R.id.tvTotalDuration);
        graphView = findViewById(R.id.graphSleepView);
        showData = findViewById(R.id.showData);
        tvDate = findViewById(R.id.tvDate);
        deep = findViewById(R.id.deep);
        light = findViewById(R.id.light);
        stayUp = findViewById(R.id.stayUp);
        progressBar.setMax(1440);
//        progressBar.setProgress(30);
        initData();

        showData.setVisibility(View.GONE);
        showData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SleepActivity.this, "No Data", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

//        day = 9;
//        tvDate.setText(year +"-"+month+"-"+day);

       // List<SleepData> sleepData = GlobalGreenDAO.getInstance().loadAllSleepData();

//        List<SleepData> sleepData = GlobalGreenDAO.getInstance().loadSleepDataByDate(year,month,day);
        List<SleepData> sleepData = GlobalGreenDAO.getInstance().loadAllSleepData();
        SleepData lastItem = sleepData.get(sleepData.size() - 1);
        tvDate.setText(lastItem.getYear() +"-"+lastItem.getMonth()+"-"+ lastItem.getDay());
        System.out.println("+++++sleepData+++++++++++++"+lastItem.toString());
        int y = lastItem.getYear();
        int m = lastItem.getMonth();
        int d = lastItem.getDay();
        SleepSubData sleepSubData = WristbandCalculator.sumOfSleepDataByDate(y,m,d,sleepData);
        System.out.println("+++++sleepSubData++++++++++"+sleepSubData);

        DataPoint[] dataPoints = new DataPoint[sleepData.size()];
        DataPoint[] dataPoints1 = new DataPoint[sleepData.size()];
        int i = 0;

        for (SleepData item : sleepData) {
            dataPoints[i] = new DataPoint(i, item.getDay());
            int hour = item.getMinutes() / 60;
            dataPoints1[i] =new DataPoint(i,hour);
            i = i +1;
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints1);
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>(dataPoints);

        series1.setBackgroundColor(R.color.primary);
        series1.setBackgroundColor(R.color.red);

        graphView.addSeries(series);
        graphView.addSeries(series1);

        try {
            progressBar.setProgress(sleepSubData.getTotalSleepTime());
            tvTotalDuration.setText(""+sleepSubData.getTotalSleepTime());
            deep.setText(""+sleepSubData.getDeepSleepTime());
            light.setText(""+sleepSubData.getLightSleepTime());
            stayUp.setText(""+sleepSubData.getAwakeTimes());
        }catch (Exception e){
            System.out.println("++++++++++Exception"+e);
        }
    }
}