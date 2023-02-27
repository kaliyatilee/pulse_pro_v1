package com.algebratech.pulse_wellness.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.inuker.bluetooth.library.utils.BluetoothUtils;

public class DeviceSyncService extends Service {
    public static boolean IsRunning = false;
    public static boolean IsWearableConnected = false;
    private static final String TAG = "CONNECT SERVICE===>";
    public static final String BROADCAST_ACTION = "com.algebratech.pulse_wellness.DeviceConnectData";
    private final Handler handler = new Handler();
    Intent intent;
    Context mContext = DeviceSyncService.this;
    private String tag = "SyncDataActivity";
    String deviceMac;
    SharedPreferences.Editor myEdit;
    SharedPreferences sharedPreferences;
    private BluetoothManager mBManager;
    private BluetoothAdapter mBAdapter;
    private BluetoothLeScanner mBScanner;
    private String user_id;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(com.algebratech.pulse_wellness.utils.Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        deviceMac = sharedPreferences.getString("macAddress", null);
        user_id = sharedPreferences.getString("userID", "");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initBLE();

        return START_NOT_STICKY;

    }

    private void initBLE(){
        mBManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (null != mBManager) {
            mBAdapter = mBManager.getAdapter();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBScanner = mBAdapter.getBluetoothLeScanner();
        }

    }

    private void scanDevice() {

    }
}
