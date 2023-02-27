package com.algebratech.pulse_wellness;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.algebratech.pulse_wellness.activities.SyncDataActivity;
import com.wosmart.ukprotocollibary.WristbandManager;
import com.wosmart.ukprotocollibary.WristbandManagerCallback;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerBeginPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerBpListItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerBpListPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerHrpItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerHrpPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerRateItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerRateListPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSleepItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSleepPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSportItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSportPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerStepItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerStepPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerTodaySumSportPacket;
import com.wosmart.ukprotocollibary.model.db.GlobalGreenDAO;
import com.wosmart.ukprotocollibary.model.hrp.HrpData;
import com.wosmart.ukprotocollibary.model.sport.SportData;
import com.wosmart.ukprotocollibary.model.sport.SportSubData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FunctionsActivity extends AppCompatActivity {
    TextView tv1, tv2,tv3,tv4,tv5,tv6,tv7,tv8;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7= (TextView) findViewById(R.id.tv7);
        tv8 = (TextView) findViewById(R.id.tv8);

        tv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FunctionsActivity.this, "  initData();", Toast.LENGTH_SHORT).show();
                initData();
            }
        });

        tv7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FunctionsActivity.this, "startSport", Toast.LENGTH_SHORT).show();
             startSport();
            }
        });
        tv8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FunctionsActivity.this, "stopSport", Toast.LENGTH_SHORT).show();
                stopSport();

            }
        });

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FunctionsActivity.this, "Sync today step to device", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FunctionsActivity.this, SyncDataActivity.class);
                startActivity(intent);

            }
        });
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // WristbandManager.getInstance(FunctionsActivity.this).sendDataRequest();
                Toast.makeText(FunctionsActivity.this, "Read Local", Toast.LENGTH_SHORT).show();
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                List<SportData> sportData = GlobalGreenDAO.getInstance().loadAllSportData();
                if (null != sportData) {
                    for (SportData item : sportData) {
                        Log.d("item",item.toString());
                        System.out.println("+++++++stepsCount"+item.getStepCount());
                        System.out.println("+++++++calory"+item.getCalory());
                        System.out.println("+++++++distance"+item.getDistance());
                        System.out.println("+++++++date"+item.getDate());
                        System.out.println("+++++++day"+item.getDay());
                        System.out.println("+++++++date"+item.getActiveTime());
                    }
                }
             //   Log.d("item",sportData.toString());

//               List<SportData> steps = GlobalGreenDAO.getInstance().loadSportDataByDate(year, month, day);
//                Log.d("item",steps.toString());
//                int stepsCount = 0;
//                int calory = 0;
//                float distance = 0;
//                if (null != steps) {
//                    for (SportData item : steps) {
//                        Log.d("item",item.toString());
//                       stepsCount = stepsCount + item.getStepCount();
//                       calory = calory + item.getCalory();
//                       distance = distance + item.getDistance();
//                    }
//                }
//                Log.d("Step Count",""+stepsCount);
//                Log.d("Step calorie",""+calory);
//                Log.d("Step Distance",""+distance);
            }
        });
        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(FunctionsActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                WristbandManager.getInstance(FunctionsActivity.this).registerCallback(new WristbandManagerCallback() {
                    @Override
                    public void onLoginStateChange(int state) {
                        super.onLoginStateChange(state);
                        if (state == WristbandManager.STATE_WRIST_LOGIN) {
                            Log.i("Lee", "Login State change Success");
                        }
                    }
                });
                WristbandManager.getInstance(FunctionsActivity.this).startLoginProcess("1234567890");

            }
        });


        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initSportData();
            }
        });
    }

    private  void initSportData(){
            WristbandManager.getInstance(this).registerCallback(new WristbandManagerCallback() {
                @Override
                public void onSportRateStatus(int status) {
                    super.onSportRateStatus(status);
                    if (status == 0) {
                        Log.d("Lee" , "hrp detect start");
                    } else if (status == 1) {
                        Log.d("Lee" ,"hrp detect stop");
                    } else if (status == 2) {
                        Log.d("Lee" ,"hrp detect busy");
                    } else if (status == 3) {
                        Log.d("Lee", "hrp detect normal");
                    }
                }

                @Override
                public void onHrpDataReceiveIndication(ApplicationLayerHrpPacket packet) {
                    super.onHrpDataReceiveIndication(packet);
                    Log.d("Lee", "hrp data = " + packet.toString());
                }

                @Override
                public void onDeviceCancelSingleHrpRead() {
                    super.onDeviceCancelSingleHrpRead();
                    Log.d("Lee" ,"cancel sport rate");
                }
            });

    }


    private void checkSport() {
        if (WristbandManager.getInstance(this).checkAppSportRateDetect()) {
            Log.d("Lee", "check sport succcessfull");
        } else {
            Log.d("Lee", "check sport Error");
        }
    }


    private void startSport() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (WristbandManager.getInstance(FunctionsActivity.this).setAppSportRateDetect(true)) {
                    Log.d("Lee", "start sport succcessfull");
                } else {
                    Log.d("Lee", "start sport error");
                }
            }
        });
        thread.start();
    }

    private void stopSport() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (WristbandManager.getInstance(FunctionsActivity.this).setAppSportRateDetect(false)) {
                    Log.d("Lee", "Stop  sport succcessfull");
                } else {
                    Log.d("Lee", "stop sport error");
                }
            }
        });
        thread.start();
    }


    private void initData(){

        WristbandManager.getInstance(FunctionsActivity.this).registerCallback(new WristbandManagerCallback() {

            @Override
            public void onSyncDataBegin(ApplicationLayerBeginPacket packet) {
                super.onSyncDataBegin(packet);
                Log.d("Lee","sync begin");
            }

            @Override
            public void onStepDataReceiveIndication(ApplicationLayerStepPacket packet) {
                super.onStepDataReceiveIndication(packet);
                System.out.println("+++++++++++ onStepDataReceiveIndication");
                for (ApplicationLayerStepItemPacket item : packet.getStepsItems()) {
                    Log.d("Lee", item.toString());
                }
                Log.d("Lee", "size = " + packet.getStepsItems().size());
            }

            @Override
            public void onSleepDataReceiveIndication(ApplicationLayerSleepPacket packet) {
                super.onSleepDataReceiveIndication(packet);
                for (ApplicationLayerSleepItemPacket item : packet.getSleepItems()) {
                    Log.d("Lee",item.toString());
                }
                Log.d("Lee","size = " + packet.getSleepItems().size());
            }

            @Override
            public void onHrpDataReceiveIndication(ApplicationLayerHrpPacket packet) {
                super.onHrpDataReceiveIndication(packet);
                for (ApplicationLayerHrpItemPacket item : packet.getHrpItems()) {
                    Log.d("Lee", item.toString());
                }
                Log.d("Lee", "size = " + packet.getHrpItems().size());
            }

            @Override
            public void onRateList(ApplicationLayerRateListPacket packet) {
                super.onRateList(packet);
                for (ApplicationLayerRateItemPacket item : packet.getRateList()) {
                    Log.d("Lee", item.toString());
                }
                Log.d("Lee","size = " + packet.getRateList().size());
            }

            @Override
            public void onSportDataReceiveIndication(ApplicationLayerSportPacket packet) {
                super.onSportDataReceiveIndication(packet);
                for (ApplicationLayerSportItemPacket item : packet.getSportItems()) {
                    Log.d("Lee", item.toString());
                }
                Log.d("Lee","size = " + packet.getSportItems().size());
            }

//            温度检测回调
//             temperature measure data call back
            @Override
            public void onTemperatureData(ApplicationLayerHrpPacket packet) {
                super.onTemperatureData(packet);

                for (ApplicationLayerHrpItemPacket item : packet.getHrpItems()) {
                    Log.d("Lee","temperature = " + item.toString());
                }
                Log.d("Lee", "temperature size = " + packet.getHrpItems().size());
            }

//            温度历史数据回调
//             temperature history data call back
            @Override
            public void onTemperatureList(ApplicationLayerRateListPacket packet) {
                super.onTemperatureList(packet);
                for (ApplicationLayerRateItemPacket item : packet.getRateList()) {
                    Log.d("Lee", "temperature = " + item.toString());
                }
                Log.d("Lee", "temperature size = " + packet.getRateList().size());
            }

            @Override
            public void onSyncDataEnd(ApplicationLayerTodaySumSportPacket packet) {
                super.onSyncDataEnd(packet);
                System.out.println("++++++++++++++++++onSyncDataEnd");
                Log.d("Lee", "sync end");
            }

            /**
             * 血压自动检测回调
             *
             * bp auto measure callback
             *
             * @param packet
             */
            @Override
            public void onBpList(ApplicationLayerBpListPacket packet) {
                super.onBpList(packet);

                for (ApplicationLayerBpListItemPacket item : packet.getBpListItemPackets()) {
                    Log.d("Lee", "bpItem = " + item.toString());
                }
                Log.d("Lee","bp size = " + packet.getBpListItemPackets().size());
            }
        });
    }

}