package com.algebratech.pulse_wellness.activities;

import static com.veepoo.protocol.model.enums.EFunctionStatus.SUPPORT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.algebratech.pulse_wellness.DeviceCompare;
import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.BleScanViewAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.interfaces.OnRecycleViewClickCallback;
import com.algebratech.pulse_wellness.models.EnableNotificationModel;
import com.algebratech.pulse_wellness.services.DeviceConnect;
import com.algebratech.pulse_wellness.utils.CM;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IABleConnectStatusListener;
import com.veepoo.protocol.listener.base.IABluetoothStateListener;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.base.IConnectResponse;
import com.veepoo.protocol.listener.base.INotifyResponse;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.listener.data.ISportDataListener;
import com.veepoo.protocol.model.datas.FunctionDeviceSupportData;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.CustomSettingData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnRecycleViewClickCallback {

    private static final String LISTENER_PATH = "com.algebratech.pulse_wellness";
    private final static String TAG = "Trynos : ";
    Context mContext = ScanActivity.this;
    private final int REQUEST_CODE = 1;
    private final int REQUEST_CODE_NOTIFY = 147;
    List<SearchResult> mListData = new ArrayList<>();
    List<String> mListAddress = new ArrayList<>();
    SwipeRefreshLayout mSwipeRefreshLayout;
    BleScanViewAdapter bleConnectAdatpter;
    WriteResponse writeResponse = new WriteResponse();
    private BluetoothManager mBManager;
    private BluetoothAdapter mBAdapter;
    private BluetoothLeScanner mBScanner;
    final static int MY_PERMISSIONS_REQUEST_BLUETOOTH = 0x55;
    RecyclerView mRecyclerView;
    TextView mTitleTextView, devicename, macaddress;
    VPOperateManager mVpoperateManager;
    private boolean mIsOadModel;

    boolean is24Hourmodel = false;
    int watchDataDay = 3;
    int contactMsgLength = 0;
    int allMsgLenght = 4;
    private int deviceNumber = -1;
    private String deviceVersion;
    private String deviceTestVersion;
    boolean isNewSportCalc = false;
    boolean isSleepPrecision = false;
    SharedPreferences.Editor myEdit;
    SharedPreferences sharedPreferences;
    String userId, old_deviceMac, device_type;
    private Intent intent;
    private CardView mydevice;
    private TextView search_device;
    private TextView add_more_device;
    private FloatingActionButton devicesettings;
    private ImageView img_watch;
    private Switch switch_noti;
    private Toolbar toolbarPolicy;
    private String single_choice_selected;
    private static final String[] RINGTONE = new String[]{
            "None", "Callisto", "Ganymede", "Luna"
    };

    private boolean[] clicked_colors = new boolean[COLORS.length];
    private static final String[] COLORS = new String[]{
            "Calls", "Sms", "Whatsapp", "Facebook", "Instagram", "Twitter", "Email", "Others"
    };

    List<String> deviceTypeList = new ArrayList<String>();

    boolean oldDevice = false;
    List<EnableNotificationModel> enableNotificationModel;
    int whatsApp;
    int sms;
    int phone;
    int email;
    int facebook;
    int instagram;
    int twitter;
    int other;

    String notificationListeners = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Log.d(TAG, "onSearchStarted");

        deviceTypeList.add("V19");
        deviceTypeList.add("GT2");
        deviceTypeList.add("Pulse F");
        deviceTypeList.add("Pulse S");

        toolbarPolicy = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarPolicy);
        setTitle("Devices");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        mVpoperateManager = mVpoperateManager.getMangerInstance(mContext.getApplicationContext());
        sharedPreferences = getSharedPreferences(com.algebratech.pulse_wellness.utils.Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        iniVar();
        userId = sharedPreferences.getString("userID", null);
        old_deviceMac = sharedPreferences.getString("macAddress", null);
        device_type = sharedPreferences.getString("deviceName", null);
        Log.e("DEVICE CONNECTED ADRS", "====" + old_deviceMac);
        Log.e("DEVICE CONNECTED TYPE", "====" + device_type);
        if (device_type != null)
            if (old_deviceMac != null) {
                Log.e("DEVICE CONNECTED", "IS NOT NULL ==" + old_deviceMac);
                try {
                    myDeviceInfo();
                } catch (Exception e) {
                    Log.e(com.algebratech.pulse_wellness.utils.Constants.TAG + "Error :", e.getMessage());
                }
            } else {
                Log.e("DEVICE CONNECTED", "IS NULL");
            }

        initRecyleView();
        checkPermission();
        registerBluetoothStateListener();
        if (BluetoothUtils.isBluetoothEnabled() && !oldDevice) {
            Log.e("SCAN DEVICE", "BLE ENABLE");
            scanDevice();
        } else {
            Log.e("SCAN DEVICE", "BLE NOT ENABLE");
        }

//        for (int i = 0; i < COLORS.length; i++) {
//            enableNotificationModel.add(new EnableNotificationModel(COLORS[i], false));
//        }
    }

    private void iniVar() {

        mydevice = findViewById(R.id.mydevice);
        search_device = findViewById(R.id.search_device);
        add_more_device = findViewById(R.id.add_more_device);
        mydevice.setVisibility(View.GONE);
        devicename = findViewById(R.id.tv);
        macaddress = findViewById(R.id.device);
        devicesettings = findViewById(R.id.devicesettings);
        img_watch = findViewById(R.id.img_watch);
        switch_noti = findViewById(R.id.switch_noti);
        switch_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationListeners != null && notificationListeners.contains(LISTENER_PATH)) {
                    Log.e("ENABLE_LISTENER_PATH","YES");
                } else {
                    Log.e("ENABLE_LISTENER_PATH","NO");
                }
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        });
        switch_noti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                startActivityForResult(
//                        new Intent("android.settings.NOTIFICATION_LISTENER_SETTINGS"), REQUEST_CODE_NOTIFY);
                if (!isChecked) {

                }
            }
        });

    }

    private void myDeviceInfo() {
        CM.showProgressLoader(ScanActivity.this);
        mydevice.setVisibility(View.VISIBLE);
        macaddress.setText(old_deviceMac);
        notificationListeners = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        Log.e("nameeeeeee", device_type);
        if (!device_type.isEmpty()) {
            if (device_type.equals("GT2")) {
                devicename.setText("Pulse Force");
                Glide.with(mContext).load(R.drawable.pulse_force).error(R.drawable.pulse_force).into(img_watch);

            }
            if (device_type.equals("Pulse F")) {
                devicename.setText("Pulse Force");
                Glide.with(mContext).load(R.drawable.pulse_force).error(R.drawable.pulse_force).into(img_watch);
                img_watch.setImageResource(R.drawable.pulse_force);
            }
            if (device_type.equals("Pulse S")) {
                devicename.setText("Pulse Spirit");
                Glide.with(mContext).load(R.drawable.watchimage).error(R.drawable.watchimage).into(img_watch);

            }
            if (device_type.equals("V19")) {
                devicename.setText("Pulse Spirit");
                Glide.with(mContext).load(R.drawable.watchimage).error(R.drawable.watchimage).into(img_watch);
            }
        }

        if (notificationListeners != null && notificationListeners.contains(LISTENER_PATH)) {
            switch_noti.setChecked(true);
            devicesettings.setVisibility(View.VISIBLE);

        } else {
            switch_noti.setChecked(false);
            devicesettings.setVisibility(View.GONE);
        }


        CM.HideProgressLoader();
        oldDevice = true;
        devicesettings.setVisibility(View.VISIBLE);
        add_more_device.setVisibility(View.VISIBLE);
        devicesettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showMultiChoiceDialog();
                checkBoxDialog();
            }
        });


    }

//    private void showMultiChoiceDialog() {
//        single_choice_selected = RINGTONE[0];
//        clicked_colors = new boolean[COLORS.length];
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Enable Notifications For");
//        builder.setMultiChoiceItems(COLORS, clicked_colors, new DialogInterface.OnMultiChoiceClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
//                clicked_colors[i] = b;
//            }
//        });
//        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                for (int z = 0; z < clicked_colors.length; z++) {
//                    Log.d(com.algebratech.pulse_wellness.utils.Constants.TAG + "Picker : ", String.valueOf(clicked_colors.equals(z)));
//                }
//
//                for (int j = 0; j < COLORS.length; j++) {
//                    if (clicked_colors[j]) {
//                        Log.d("SOHAN", COLORS[j] + " checked!");
//                    }
//                }
//            }
//        });
//        builder.setNegativeButton("Cancel", null);
//        builder.show();
//    }

    private void checkBoxDialog() {
        whatsApp = sharedPreferences.getInt(com.algebratech.pulse_wellness.utils.Constants.WHATSAPP, -1);
        sms = sharedPreferences.getInt(com.algebratech.pulse_wellness.utils.Constants.SMS, -1);
        phone = sharedPreferences.getInt(com.algebratech.pulse_wellness.utils.Constants.PHONE, -1);
        email = sharedPreferences.getInt(com.algebratech.pulse_wellness.utils.Constants.EMAIL, -1);

        facebook = sharedPreferences.getInt(com.algebratech.pulse_wellness.utils.Constants.FACEBOOK, -1);
        instagram = sharedPreferences.getInt(com.algebratech.pulse_wellness.utils.Constants.INSTA, -1);
        twitter = sharedPreferences.getInt(com.algebratech.pulse_wellness.utils.Constants.TWITTER, -1);
        other = sharedPreferences.getInt(com.algebratech.pulse_wellness.utils.Constants.OTHER, -1);

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
        LayoutInflater factory = LayoutInflater.from(ScanActivity.this);
        final View v = factory.inflate(R.layout.check_box_dialog, null);
        builder.setView(v);
        AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView cancel = v.findViewById(R.id.cancel);
        TextView enable = v.findViewById(R.id.enable);
        CheckBox Calls, Sms, Whatsapp, Facebook, Instagram, Twitter, Email, Others;
        Calls = v.findViewById(R.id.Calls);
        Sms = v.findViewById(R.id.Sms);
        Whatsapp = v.findViewById(R.id.Whatsapp);
        Email = v.findViewById(R.id.Email);
        Facebook = v.findViewById(R.id.Facebook);
        Instagram = v.findViewById(R.id.Instagram);
        Twitter = v.findViewById(R.id.Twitter);
        Others = v.findViewById(R.id.Others);
        if (whatsApp == 1) {
            Whatsapp.setChecked(true);
        }
        if (phone == 1) {
            Calls.setChecked(true);
        }
        if (sms == 1) {
            Sms.setChecked(true);
        }
        if (email == 1) {
            Email.setChecked(true);
        }

        if (facebook == 1) {
            Facebook.setChecked(true);
        }
        if (instagram == 1) {
            Instagram.setChecked(true);
        }
        if (twitter == 1) {
            Twitter.setChecked(true);
        }
        if (other == 1) {
            Others.setChecked(true);
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
        alert.show();
        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isWhatsapp = Whatsapp.isChecked();
                boolean isPhone = Calls.isChecked();
                boolean isSms = Sms.isChecked();
                boolean isEmail = Email.isChecked();

                boolean isFacebook = Facebook.isChecked();
                boolean isInsta = Instagram.isChecked();
                boolean isTwitter = Twitter.isChecked();
                boolean isOther = Others.isChecked();

                if (isWhatsapp) {
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.WHATSAPP, 1);
                } else
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.WHATSAPP, 0);
                if (isPhone) {
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.PHONE, 1);
                } else
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.PHONE, 0);
                if (isSms) {
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.SMS, 1);
                } else
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.SMS, 0);
                if (isEmail) {
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.EMAIL, 1);
                } else
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.EMAIL, 0);


                if (isFacebook) {
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.FACEBOOK, 1);
                } else
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.FACEBOOK, 0);

                if (isInsta) {
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.INSTA, 1);
                } else
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.INSTA, 0);
                if (isTwitter) {
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.TWITTER, 1);
                } else
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.TWITTER, 0);
                if (isOther) {
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.OTHER, 1);
                } else
                    myEdit.putInt(com.algebratech.pulse_wellness.utils.Constants.OTHER, 0);


                myEdit.apply();
                alert.dismiss();
            }
        });
    }

    private void initRecyleView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) super.findViewById(R.id.mian_swipeRefreshLayout);
        mRecyclerView = (RecyclerView) super.findViewById(R.id.main_recylerlist);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bleConnectAdatpter = new BleScanViewAdapter(this, mListData);
        mRecyclerView.setAdapter(bleConnectAdatpter);
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        bleConnectAdatpter.setBleItemOnclick(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);


    }


    private void checkPermission() {
        Log.d(TAG, "Build.VERSION.SDK_INT =" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT <= 22) {
            initBLE();
            return;
        }

        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission,PERMISSION_GRANTED");
            initBLE();
        } else if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            requestPermission();
            Log.d(TAG, "checkPermission,PERMISSION_DENIED");
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ScanActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Log.d(TAG, "requestPermission,shouldShowRequestPermissionRationale");

            } else {
                Log.d(TAG, "requestPermission,shouldShowRequestPermissionRationale else");
                ActivityCompat.requestPermissions(ScanActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_BLUETOOTH);
            }
        } else {
            Log.d(TAG, "requestPermission,shouldShowRequestPermissionRationale hehe");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_BLUETOOTH: {
                Log.d(TAG, "onRequestPermissionsResult,MY_PERMISSIONS_REQUEST_BLUETOOTH ");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initBLE();
                } else {
                }
                return;
            }
        }
    }


    private boolean scanDevice() {
        if (!mListAddress.isEmpty()) {
            mListAddress.clear();
        }
        if (!mListData.isEmpty()) {
            mListData.clear();
            bleConnectAdatpter.notifyDataSetChanged();
        }

        if (!BluetoothUtils.isBluetoothEnabled()) {
            Toast.makeText(mContext, "bluetoothIsNotTurnedOn", Toast.LENGTH_SHORT).show();
            return true;
        }
        mVpoperateManager.startScanDevice(mSearchResponse);
        return false;
    }


    private void connectDevice(final String mac, final String deviceName) {

        Log.d("Trynos Decive Info", mac + " | " + deviceName);

        mVpoperateManager.registerConnectStatusListener(mac, mBleConnectStatusListener);

        mVpoperateManager.connectDevice(mac, deviceName, new IConnectResponse() {

            @Override
            public void connectState(int code, BleGattProfile profile, boolean isoadModel) {
                if (code == Code.REQUEST_SUCCESS) {
                    //bluetoothAndDeviceConnectionStatus
                    Log.d(TAG, "connectionSucceeded");
                    Log.d(TAG, "whetherItIsFirmwareUpgradeMode=" + isoadModel);
                    mIsOadModel = isoadModel;
                } else {
                    Log.d(TAG, "connectionFailed");
                }
            }
        }, new INotifyResponse() {
            @Override
            public void notifyState(int state) {
                if (state == Code.REQUEST_SUCCESS) {
                    //bluetoothAndDeviceConnectionStatus
                    Log.d(TAG, "Successful monitoring-other operations can be performed");

//                    Intent intent = new Intent(mContext, OperaterActivity.class);
//                    intent.putExtra("isoadmodel", mIsOadModel);
//                    intent.putExtra("deviceaddress", mac);
//                    startActivity(intent);
                    afterconnection();
                } else {
                    Log.d(TAG, "monitorFailed，reconnect");
                }

            }
        });
    }

    private void afterconnection() {
        Log.d(TAG, "After Connection");

        VPOperateManager.getMangerInstance(mContext).confirmDevicePwd(writeResponse, new IPwdDataListener() {
            @Override
            public void onPwdDataChange(PwdData pwdData) {
                String message = "PwdData:\n" + pwdData.toString();
                Log.d(TAG, message);

                deviceNumber = pwdData.getDeviceNumber();
                deviceVersion = pwdData.getDeviceVersion();
                deviceTestVersion = pwdData.getDeviceTestVersion();

            }
        }, new IDeviceFuctionDataListener() {
            @Override
            public void onFunctionSupportDataChange(FunctionDeviceSupportData functionSupport) {
                String message = "FunctionDeviceSupportData:\n" + functionSupport.toString();
                Log.d(TAG, message);
                //  sendMsg(message, 2);
                EFunctionStatus newCalcSport = functionSupport.getNewCalcSport();
                if (newCalcSport != null && newCalcSport.equals(SUPPORT)) {
                    isNewSportCalc = true;
                } else {
                    isNewSportCalc = false;
                }
                watchDataDay = functionSupport.getWathcDay();
                contactMsgLength = functionSupport.getContactMsgLength();
                allMsgLenght = functionSupport.getAllMsgLength();
                isSleepPrecision = functionSupport.getPrecisionSleep() == SUPPORT;
            }
        }, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {
                String message = "FunctionSocailMsgData:\n" + functionSocailMsgData.toString();
                Log.d(TAG, message);
                //  sendMsg(message, 3);
            }
        }, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                String message = "FunctionCustomSettingData:\n" + customSettingData.toString();
                Log.d(TAG, message);
                //   sendMsg(message, 4);
            }
        }, "0000", is24Hourmodel);

        VPOperateManager.getMangerInstance(mContext).readSportStep(writeResponse, new ISportDataListener() {
            @Override
            public void onSportDataChange(SportData sportData) {

                Log.e("IDIGIT_21_07_RED_SCAN", "READ SPORT STEPS");

                String message = "Current step count: " + sportData.toString();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                Log.d(TAG, message);

                //sendMsg(message, 1);
            }
        });
    }


    /**
     * bluetoothOnOrOff
     */
    private void registerBluetoothStateListener() {
        mVpoperateManager.registerBluetoothStateListener(mBluetoothStateListener);
    }


    /**
     * Monitor the callback status of the system's Bluetooth on and off
     */
    private final IABleConnectStatusListener mBleConnectStatusListener = new IABleConnectStatusListener() {

        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if (status == Constants.STATUS_CONNECTED) {
                Log.d(TAG, "STATUS_CONNECTED");
            } else if (status == Constants.STATUS_DISCONNECTED) {
                Log.d(TAG, "STATUS_DISCONNECTED");
            }
        }
    };

    /**
     * Monitor the callback status between Bluetooth and the device
     */
    private final IABluetoothStateListener mBluetoothStateListener = new IABluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            Log.d(TAG, "open=" + openOrClosed);
        }
    };


    /**
     * scannedCallback
     */
    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            CM.showProgressLoader(ScanActivity.this);
            Log.d(TAG, "onSearchStarted");
        }

        @Override
        public void onDeviceFounded(final SearchResult device) {
            //  Log.d(TAG,String.format("device for %s-%s-%d", device.getName(), device.getAddress(), device.rssi));


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(device.getName()!=null &&
                        !device.getName().isEmpty() &&
                        deviceTypeList.contains(device.getName())) {
                        if (!mListAddress.contains(device.getAddress())) {
                            mListData.add(device);
                            mListAddress.add(device.getAddress());
                        }
                    }
                    CM.HideProgressLoader();
                    Collections.sort(mListData, new DeviceCompare());
                    bleConnectAdatpter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onSearchStopped() {
            if (mListData.size() <= 0) {
                Log.e("NULL", "NULL");
                search_device.setVisibility(View.VISIBLE);
            } else
                search_device.setVisibility(View.GONE);

            CM.HideProgressLoader();
            refreshStop();
            Log.d(TAG, "onSearchStopped");
        }

        @Override
        public void onSearchCanceled() {
            refreshStop();
            CM.HideProgressLoader();
            Log.d(TAG, "onSearchCanceled");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (BluetoothUtils.isBluetoothEnabled()) {
                scanDevice();
            } else {
                refreshStop();
            }
        }

        if (requestCode == REQUEST_CODE_NOTIFY) {

            checkEnabled();

        }

    }

    private void checkEnabled() {
        String listeners = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        if (listeners != null && listeners.contains(LISTENER_PATH)) {
            switch_noti.setChecked(true);
            devicesettings.setVisibility(View.VISIBLE);

        } else {
            switch_noti.setChecked(false);
            devicesettings.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        Log.d(TAG, "onRefresh");
        search_device.setVisibility(View.GONE);
        add_more_device.setVisibility(View.GONE);
        if (checkBLE()) {
            scanDevice();
        }
    }

    @SuppressLint("NewApi")
    private void initBLE() {
        mBManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (null != mBManager) {
            mBAdapter = mBManager.getAdapter();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBScanner = mBAdapter.getBluetoothLeScanner();
        }
        checkBLE();

    }

    /**
     * checkIfTheBluetoothDeviceIsTurnedOn
     *
     * @return
     */
    private boolean checkBLE() {
        if (!BluetoothUtils.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    /**
     * endRefresh
     */
    void refreshStop() {
        Log.d(TAG, "refreshComlete");
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void OnRecycleViewClick(int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "connecting，please Wait...", Toast.LENGTH_SHORT).show();
            }
        });

        final SearchResult searchResult = mListData.get(position);


        if (old_deviceMac != null) {

            mVpoperateManager.disconnectWatch(writeResponse);

        }

        saveMacOnline(searchResult.getAddress());

        myEdit.putString("macAddress", searchResult.getAddress());
        myEdit.putString("deviceName", searchResult.getName());
        myEdit.apply();

        intent = new Intent(ScanActivity.this, DeviceConnect.class);
        stopService(intent);


        // startService(new Intent(getApplicationContext(), DeviceConnect.class));
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();

        //connectDevice(searchResult.getAddress(), searchResult.getName());
    }

    private void saveMacOnline(String address) {

        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("macAddress", address);
            object.put("id", userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.addmac, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(com.algebratech.pulse_wellness.utils.Constants.TAG, String.valueOf(response));
                        //progressBar.setVisibility(View.GONE);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // progressBar.setVisibility(View.GONE);
//                VolleyLog.d("Error", "Error: " + error.getMessage());
//                if (error.getMessage().contains(Api.baseurl)){
//                   // Toast.makeText(CreateProfileActivity.this, "No internet connection available!!!.", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

    }


    class WriteResponse implements IBleWriteResponse {

        @SuppressLint("LongLogTag")
        @Override
        public void onResponse(int code) {
            Log.d("Trynos write cmd status: ", String.valueOf(code));

        }
    }
}
