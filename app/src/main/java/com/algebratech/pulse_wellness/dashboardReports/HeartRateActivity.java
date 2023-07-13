package com.algebratech.pulse_wellness.dashboardReports;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.StaticMethods;
import com.algebratech.pulse_wellness.activities.ScanActivity;
import com.algebratech.pulse_wellness.utils.CM;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.wosmart.ukprotocollibary.WristbandManager;
import com.wosmart.ukprotocollibary.WristbandManagerCallback;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerHrpItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerHrpPacket;
import com.wosmart.ukprotocollibary.model.db.GlobalGreenDAO;
import com.wosmart.ukprotocollibary.model.hrp.HrpData;
//import com.wosmart.ukprotocollibary.model.sleep.hrpData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HeartRateActivity extends AppCompatActivity {
    TextView reading,averageValue,lowestValue,highestValue,tvDate;
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    private final String tag = "HrActivity";
    List<Integer> hr;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;
    GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        CM.showProgressLoader(HeartRateActivity.this);
        graphView = findViewById(R.id.graphSleepView2);

        sharedPreferences = getSharedPreferences(com.algebratech.pulse_wellness.utils.Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        reading = findViewById(R.id.reading);
        tvDate = findViewById(R.id.tvDate);
        highestValue = findViewById(R.id.highestValue);
        averageValue = findViewById(R.id.averageHeart);
        lowestValue = findViewById(R.id.lowestValue);


//         graph = (GraphView) findViewById(R.id.graph);
        hr = new ArrayList<Integer>();
        initHrData();
        startMeasure();
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        stopMeasure();
                        calculate();
                    }
                },
                20000
        );
    }


    @Override
    public void onBackPressed() {
        stopMeasure();

        // Call super.onBackPressed() to allow the default back button behavior (navigating back)
        super.onBackPressed();
    }

    private void calculate() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                int highest = 0;
                int lowest = 0;
                int total = 0 ;
                int average = 0;

                for (int i = 0; i < hr.size(); i++) {
                    total = total + hr.get(i);
                    if (hr.get(i) > highest)
                        highest = hr.get(i);
                }


                for (int i = 0; i < hr.size(); i++){
                    if (hr.get(i) < lowest)
                        lowest = hr.get(i);
                    if(i == 0){
                        lowest = hr.get(0);
                    }
                }
                average = total / hr.size();

                myEdit.putString("latestHr",String.valueOf(average));
                myEdit.apply();

                highestValue.setText(String.valueOf(highest));
                lowestValue.setText(String.valueOf(lowest));
                averageValue.setText(String.valueOf(average));
                reading.setText(average +" BPS");
            }
        });

    }

    private void initData() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        tvDate.setText(year +"-"+month+"-"+day);

       List<HrpData> hrpData = GlobalGreenDAO.getInstance().loadHrpDataByDate(year,month,day);

        DataPoint[] dataPoints = new DataPoint[hrpData.size()];
        DataPoint[] dataPoints1 = new DataPoint[hrpData.size()];
        int i = 0;

        for (HrpData item : hrpData) {
            dataPoints[i] = new DataPoint(i, item.getDay());
            int hour = item.getValue();
            dataPoints1[i] =new DataPoint(i,hour);
            i = i +1;
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints1);
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>(dataPoints);

        series1.setBackgroundColor(R.color.primary);
        series1.setBackgroundColor(R.color.red);

        graphView.addSeries(series);
        graphView.addSeries(series1);

//        try {
//            progressBar.setProgress(sleepSubData.getTotalSleepTime());
//            tvTotalDuration.setText(""+sleepSubData.getTotalSleepTime());
//            deep.setText(""+sleepSubData.getDeepSleepTime());
//            light.setText(""+sleepSubData.getLightSleepTime());
//            stayUp.setText(""+sleepSubData.getAwakeTimes());
//        }catch (Exception e){
//            System.out.println("++++++++++Exception"+e);
//        }
       // List<HrpData> hrpData = GlobalGreenDAO.getInstance().loadAllHrpData();



//        series= new LineGraphSeries<DataPoint>(new DataPoint[] {
//        new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//        graph.addSeries(series);

    }

    private void initHrData() {
        WristbandManager.getInstance(this).registerCallback(new WristbandManagerCallback() {
            @Override
            public void onHrpDataReceiveIndication(ApplicationLayerHrpPacket packet) {
                super.onHrpDataReceiveIndication(packet);
                for (ApplicationLayerHrpItemPacket item : packet.getHrpItems()) {
                    Log.i(tag, "hr value :" + item.getValue());
                    hr.add(item.getValue());

                }
            }

            @Override
            public void onDeviceCancelSingleHrpRead() {
                super.onDeviceCancelSingleHrpRead();
                Log.i(tag, "stop measure hr ");
            }
        });
    }
    private void startMeasure() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (WristbandManager.getInstance(HeartRateActivity.this).readHrpValue()) {
                    Log.i(tag, "hr value : success");
                } else {
                    Log.i(tag, "hr value : failed");
                }
            }
        });
        thread.start();
    }

    private void stopMeasure() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (WristbandManager.getInstance(HeartRateActivity.this).stopReadHrpValue()) {
                    Log.i(tag, "hr value : success");
                    CM.HideProgressLoader();
                } else {
                    Log.i(tag, "hr value : failed");
                }
            }
        });
        thread.start();
    }


}