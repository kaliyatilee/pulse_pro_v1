package com.algebratech.pulse_wellness.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.algebratech.pulse_wellness.R;
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

public class SyncDataActivity extends AppCompatActivity {
    private final String tag = "SyncDataActivity";
    private Button btn_sync_data;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);

        btn_sync_data = findViewById(R.id.btn_sync_data);
        initData();

        if (WristbandManager.getInstance(SyncDataActivity.this).sendDataRequest()) {
            // Toast.makeText(SyncDataActivity.this, "Success", Toast.LENGTH_SHORT).show();
            Log.d("Success","Send Data Request");
        } else {
            Log.d("Error","Send Data Request");
            //  Toast.makeText(SyncDataActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
        btn_sync_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (WristbandManager.getInstance(SyncDataActivity.this).sendDataRequest()) {
                           // Toast.makeText(SyncDataActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            Log.d("Success","Send Data Request");
                        } else {
                            Log.d("Error","Send Data Request");
                          //  Toast.makeText(SyncDataActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                thread.start();
            }
        });
    }

    private void initData() {
       // handler = new MyHandler();
        WristbandManager.getInstance(this).registerCallback(new WristbandManagerCallback() {

            @Override
            public void onSyncDataBegin(ApplicationLayerBeginPacket packet) {
                super.onSyncDataBegin(packet);
                Log.i(tag, "sync begin");
            }

            @Override
            public void onStepDataReceiveIndication(ApplicationLayerStepPacket packet) {
                super.onStepDataReceiveIndication(packet);
                for (ApplicationLayerStepItemPacket item : packet.getStepsItems()) {
                    Log.i(tag, item.toString());
                }
                Log.i(tag, "size = " + packet.getStepsItems().size());
            }

            @Override
            public void onSleepDataReceiveIndication(ApplicationLayerSleepPacket packet) {
                super.onSleepDataReceiveIndication(packet);
                for (ApplicationLayerSleepItemPacket item : packet.getSleepItems()) {
                    Log.i(tag, item.toString());
                }
                Log.i(tag, "size = " + packet.getSleepItems().size());
            }

            @Override
            public void onHrpDataReceiveIndication(ApplicationLayerHrpPacket packet) {
                super.onHrpDataReceiveIndication(packet);
                for (ApplicationLayerHrpItemPacket item : packet.getHrpItems()) {
                    Log.i(tag, item.toString());
                }
                Log.i(tag, "size = " + packet.getHrpItems().size());
            }

            @Override
            public void onRateList(ApplicationLayerRateListPacket packet) {
                super.onRateList(packet);
                for (ApplicationLayerRateItemPacket item : packet.getRateList()) {
                    Log.i(tag, item.toString());
                }
                Log.i(tag, "size = " + packet.getRateList().size());
            }

            @Override
            public void onSportDataReceiveIndication(ApplicationLayerSportPacket packet) {
                super.onSportDataReceiveIndication(packet);
                for (ApplicationLayerSportItemPacket item : packet.getSportItems()) {
                    Log.i(tag, item.toString());
                }
                Log.i(tag, "size = " + packet.getSportItems().size());
            }

            //温度检测回调
            // temperature measure data call back
            @Override
            public void onTemperatureData(ApplicationLayerHrpPacket packet) {
                super.onTemperatureData(packet);

                for (ApplicationLayerHrpItemPacket item : packet.getHrpItems()) {
                    Log.i(tag, "temperature = " + item.toString());
                }
                Log.i(tag, "temperature size = " + packet.getHrpItems().size());
            }

            //温度历史数据回调
            // temperature history data call back
            @Override
            public void onTemperatureList(ApplicationLayerRateListPacket packet) {
                super.onTemperatureList(packet);
                for (ApplicationLayerRateItemPacket item : packet.getRateList()) {
                    Log.i(tag, "temperature = " + item.toString());
                }
                Log.i(tag, "temperature size = " + packet.getRateList().size());
            }

            @Override
            public void onSyncDataEnd(ApplicationLayerTodaySumSportPacket packet) {
                super.onSyncDataEnd(packet);
                Log.i(tag, "sync end");
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
                    Log.i(tag, "bpItem = " + item.toString());
                }
                Log.i(tag, "bp size = " + packet.getBpListItemPackets().size());
            }
        });
    }




}