package com.algebratech.pulse_wellness.dashboardReports;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.StaticMethods;
import com.wosmart.ukprotocollibary.WristbandManager;
import com.wosmart.ukprotocollibary.WristbandManagerCallback;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerBeginPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSleepItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSleepPacket;
import com.wosmart.ukprotocollibary.model.db.GlobalGreenDAO;
import com.wosmart.ukprotocollibary.model.sleep.SleepData;
import com.wosmart.ukprotocollibary.model.sleep.SleepSubData;
import com.wosmart.ukprotocollibary.model.sleep.filter.SleepFilterData;
import com.wosmart.ukprotocollibary.model.sport.SportData;
import com.wosmart.ukprotocollibary.util.WristbandCalculator;

import java.util.Calendar;
import java.util.List;

public class SleepActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView tvTotalDuration,tvDate,deep,light,stayUp;
    Button showData;
    private String tag = "SyncDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        progressBar = findViewById(R.id.progressBar);
        tvTotalDuration = findViewById(R.id.tvTotalDuration);
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

    private void initData() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        day = 9;
        tvDate.setText(year +"-"+month+"-"+day);

       // List<SleepData> sleepData = GlobalGreenDAO.getInstance().loadAllSleepData();

        List<SleepData> sleepData = GlobalGreenDAO.getInstance().loadSleepDataByDate(year,month,day);
        System.out.println("+++++sleepData+++++++++++++"+sleepData.toString());
        SleepSubData sleepSubData = WristbandCalculator.sumOfSleepDataByDate(year,month,day,sleepData);
        System.out.println("+++++sleepSubData++++++++++"+sleepSubData);

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