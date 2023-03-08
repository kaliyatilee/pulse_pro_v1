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
import com.wosmart.ukprotocollibary.model.sport.SportData;

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
        tvDate.setText(year +"-"+month+"-"+day);

        List<SleepData> sleeps = GlobalGreenDAO.getInstance().loadSleepDataByDate(year, month, day);
        int munites = 0;
        int ndeep = 0;
        int nlight = 0;
        int nuplate = 0;
        if (null != sleeps) {
            for (SleepData item : sleeps) {
                munites = munites + item.getMinutes();
               if(item.getMode() == 1){
                   nlight = nlight + item.getMinutes();

               }
                if(item.getMode() == 2){
                    ndeep = ndeep + item.getMinutes();
                }
                if(item.getMode() == 3){
                    nuplate = nuplate + item.getMinutes();
                }
            }
        }

        long seconds = munites * 60;
        long nlightseconds = nlight * 60;
        long ndeepseconds = ndeep * 60;
        long nuplateseconds = nuplate * 60;
        int percentage = munites / 1440;
        progressBar.setProgress(munites);
        tvTotalDuration.setText(StaticMethods.calculateTime(seconds));
        deep.setText(StaticMethods.calculateTime(ndeepseconds));
        light.setText(StaticMethods.calculateTime(nlightseconds));
        stayUp.setText(StaticMethods.calculateTime(nuplateseconds));
    }
}