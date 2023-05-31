package com.algebratech.pulse_wellness.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.ScanWearableAdapter;
import com.algebratech.pulse_wellness.services.SyncWearableService;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.SharedPreferenceUtil;
import com.wosmart.ukprotocollibary.WristbandManager;
import com.wosmart.ukprotocollibary.WristbandScanCallback;

import com.algebratech.pulse_wellness.models.SearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScanWearableActivity extends BaseActivity {
    private final String tag = "ScanActivity";

    private SwipeRefreshLayout srl_search;

    private List<SearchResult> devices;

    private ScanWearableAdapter adapter;

    private SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_wearable);
        sharedPreferenceUtil = new SharedPreferenceUtil(this);

        srl_search = findViewById(R.id.srl_search);

        srl_search.setOnRefreshListener(() -> {
            devices.clear();
            adapter.notifyDataSetChanged();
            startScan();
        });

        RecyclerView rcy_device = findViewById(R.id.rcy_device);
        rcy_device.setLayoutManager(new LinearLayoutManager(this));
        devices = new ArrayList<>();
        adapter = new ScanWearableAdapter(this, devices);
        adapter.setListener(pos -> {
            stopScan();
            showProgress();
            SearchResult result = devices.get(pos);
            sharedPreferenceUtil.setStringPreference(Constants.DEVICE_MAC,result.getAddress());
            Intent serviceIntent = new Intent(ScanWearableActivity.this, SyncWearableService.class);
            serviceIntent.putExtra(Constants.START_SERVICE,true);
            ContextCompat.startForegroundService(ScanWearableActivity.this, serviceIntent);
            Intent intent = new Intent();
            setResult(2, intent);
            ScanWearableActivity.this.finish();
        });
        rcy_device.setAdapter(adapter);
        startScan();
    }


    private void startScan() {
        WristbandManager.getInstance(this).startScan(true, new WristbandScanCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onWristbandDeviceFind(BluetoothDevice device, int rssi, byte[] scanRecord) {
                super.onWristbandDeviceFind(device, rssi, scanRecord);
                SearchResult result = new SearchResult(device, rssi, scanRecord);
                Log.d(tag+"1",result.toString());
                if (!devices.contains(result)) {
                    devices.add(result);
                    Collections.sort(devices, new RssiComparable());
                    adapter.notifyDataSetChanged();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onWristbandDeviceFind(BluetoothDevice device, int rssi, ScanRecord scanRecord) {
                super.onWristbandDeviceFind(device, rssi, scanRecord);
                SearchResult result = new SearchResult(device, rssi, null);
                Log.d(tag+"2",result.toString());
                if (!devices.contains(result)) {
                    devices.add(result);
                    Collections.sort(devices, new RssiComparable());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLeScanEnable(boolean enable) {
                super.onLeScanEnable(enable);
                if (!enable) {
                    srl_search.setRefreshing(false);
                }
            }

            @Override
            public void onWristbandLoginStateChange(boolean connected) {
                super.onWristbandLoginStateChange(connected);
            }

            @Override
            public void onStartLeScan() {
                super.onStartLeScan();
            }

            @Override
            public void onStopLeScan() {
                super.onStopLeScan();
            }

            @Override
            public void onCancelLeScan() {
                super.onCancelLeScan();
            }
        });
    }

    private void stopScan() {
        WristbandManager.getInstance(this).stopScan();
    }


    private static class RssiComparable implements Comparator<SearchResult> {
        @Override
        public int compare(SearchResult o1, SearchResult o2) {
            return Integer.compare(o2.rssi, o1.rssi);
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }
    }
