package com.algebratech.pulse_wellness.services;

import static android.bluetooth.BluetoothProfile.GATT;
import static com.veepoo.protocol.model.enums.EFunctionStatus.SUPPORT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanRecord;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.algebratech.pulse_wellness.FunctionsActivity;
import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.AddUserGoals;
import com.algebratech.pulse_wellness.activities.MainActivity;
import com.algebratech.pulse_wellness.activities.ScanActivity;
import com.algebratech.pulse_wellness.activities.SyncDataActivity;
import com.algebratech.pulse_wellness.adapters.BleScanViewAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.db.DBHelper;
import com.algebratech.pulse_wellness.models.ActivityListModel;
import com.algebratech.pulse_wellness.models.DailyReads;
import com.algebratech.pulse_wellness.utils.StaticMethods;
import com.algebratech.pulse_wellness.utils.WearableNotification;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.veepoo.protocol.listener.data.IFindDeviceDatalistener;
import com.veepoo.protocol.listener.data.IFindPhonelistener;
import com.veepoo.protocol.listener.data.IHeartDataListener;
import com.veepoo.protocol.listener.data.IPersonInfoDataListener;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.listener.data.ISportDataListener;
import com.veepoo.protocol.listener.data.ISportModelStateListener;
import com.veepoo.protocol.model.datas.FindDeviceData;
import com.veepoo.protocol.model.datas.FunctionDeviceSupportData;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.datas.HeartData;
import com.veepoo.protocol.model.datas.PersonInfoData;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.datas.SportModelStateData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.EOprateStauts;
import com.veepoo.protocol.model.enums.ESex;
import com.veepoo.protocol.model.enums.ESocailMsg;
import com.veepoo.protocol.model.settings.ContentPhoneSetting;
import com.veepoo.protocol.model.settings.ContentSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.wosmart.ukprotocollibary.WristbandManager;
import com.wosmart.ukprotocollibary.WristbandManagerCallback;
import com.wosmart.ukprotocollibary.WristbandScanCallback;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerBeginPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerBpListItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerBpListPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerHrpItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerHrpPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerPrivateBpPacket;
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
import com.wosmart.ukprotocollibary.model.sleep.SleepData;
import com.wosmart.ukprotocollibary.model.sport.SportData;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceConnect extends Service implements IBleWriteResponse, ISportModelStateListener {

    public static boolean IsRunning = false;
    public static boolean IsWearableConnected = false;
    //    private static final String TAG = com.algebratech.pulse_wellness.utils.Constants.TAG;
    private static final String TAG = "CONNECT SERVICE===>";
    public static final String BROADCAST_ACTION = "com.algebratech.pulse_wellness.DeviceConnectData";
    private final Handler handler = new Handler();
    Intent intent;
    int counter = 0;

    int stepCounter;

    //BLE
    Context mContext = DeviceConnect.this;
    private final int REQUEST_CODE = 1;
    List<SearchResult> mListData = new ArrayList<>();
    List<String> mListAddress = new ArrayList<>();
    private BleScanViewAdapter bleConnectAdatpter;
    private BluetoothManager mBManager;
    private BluetoothAdapter mBAdapter;
    private BluetoothLeScanner mBScanner;
    final static int MY_PERMISSIONS_REQUEST_BLUETOOTH = 0x55;
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
    boolean stopService = false;
    String deviceMac;
    SharedPreferences.Editor myEdit;
    SharedPreferences sharedPreferences;
    private String CHANNEL_ID;
    private DBHelper db;
    private String user_id;
    private String tag = "SyncDataActivity";
    int STEPS = 0;
    double KCAL = 0;
    double DISS = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        IsRunning = true;
        sharedPreferences = getSharedPreferences(com.algebratech.pulse_wellness.utils.Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        deviceMac = sharedPreferences.getString("macAddress", null);
        user_id = sharedPreferences.getString("userID", "");
        SQLiteDatabase.loadLibs(getApplicationContext());
        db = new DBHelper(getApplicationContext());


        intent = new Intent(BROADCAST_ACTION);
        mVpoperateManager = mVpoperateManager.getMangerInstance(mContext.getApplicationContext());
        //getNotification();
        //registerBluetoothStateListener();
        //circleImageView.setBorderColor(ResourcesCompat.getColor(mContext.getResources(), R.color.orange, null));
        //initBLE();
        //scanDevice();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("DEVICE_CONNECT_CLASS", "ON_START_CONNECT");
//        noSoundNotification("", "");
        registerBluetoothStateListener();
        //circleImageView.setBorderColor(ResourcesCompat.getColor(mContext.getResources(), R.color.orange, null));
        initBLE();
        scanDevice();
        return START_NOT_STICKY;
    }

    @SuppressLint("WrongConstant")
//    private void noSoundNotification(String contentTitle, String contentText) {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            pendingIntent = PendingIntent.getActivity(this,
//                    0, notificationIntent,
//                    0 | PendingIntent.FLAG_MUTABLE);
//        } else {
//            pendingIntent = PendingIntent.getActivity(this,
//                    0, notificationIntent, 0);
//        }
//
//        Notification notification = new NotificationCompat.Builder(this, com.algebratech.pulse_wellness.utils.Constants.CHANNEL_ID_NO_SOUND)
//                .setContentTitle(contentTitle)
//                .setContentText(contentText)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setSound(null)
//                .setSilent(true)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        startForeground(1, notification);
//    }
//
//    private void soundNotification(String contentText) {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, 0);
//
//        Notification notification = new NotificationCompat.Builder(this, com.algebratech.pulse_wellness.utils.Constants.CHANNEL_ID_SOUND)
//                .setContentTitle("Example Service")
//                .setContentText(contentText)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        startForeground(1, notification);
//    }

    @Override
    public void onStart(Intent intent, int startId) {
        // handler.removeCallbacks(sendUpdatesToUI);
        // handler.postDelayed(sendUpdatesToUI, 400); // 1 second

//        TelephonyManager telephony = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE); // TelephonyManager
//        // object
//        CallListener listener = new CallListener();
//        telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

       // mVpoperateManager.readSportModelState(DeviceConnect.this, iSportModelStateListener);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        IsRunning = false;
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
            return true;
        }

//        searchDevice();

        return false;
    }

    private void searchDevice() {
        System.out.println("++++++++++++Search Device");
        WristbandManager.getInstance(this).startScan(true, new WristbandScanCallback() {
            @Override
            public void onWristbandDeviceFind(BluetoothDevice device, int rssi, ScanRecord scanRecord) {
                super.onWristbandDeviceFind(device, rssi, scanRecord);
                SearchResult result = new SearchResult(device, rssi, null);
                Log.d("DeviceResults 2", result.getName());
                if (!mListAddress.contains(device.getAddress())) {
                    mListAddress.add(device.getAddress());
                    if (result.getAddress().equals(deviceMac)) {
//                        WristbandManager.getInstance(DeviceConnect.this).stopScan();
                      //  connectDevice(result.getAddress(), result.getName());
                    }
                }

            }

            @Override
            public void onLeScanEnable(boolean enable) {
                super.onLeScanEnable(enable);
                if (!enable) {
//                    srl_search.setRefreshing(false);
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


    /**
     * bluetoothOnOrOff
     */
    private void registerBluetoothStateListener() {
        mVpoperateManager.registerBluetoothStateListener(mBluetoothStateListener);

    }

    private void getNotification() {
//        FirebaseDatabase.getInstance().getReference("FriendRequest")
//                .child(StaticVariables.user_id)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//                        if (dataSnapshot.exists()) {
//                            String bookingStatus = dataSnapshot.child("status").getValue().toString();
//                            if (bookingStatus.equals("Active")) {
//                                createNotification("You have a following request");
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
    }

    private void createNotificationChannel() {
//        CharSequence channelName = CHANNEL_ID;
//        String channelDesc = "channelDesc";
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
//            channel.setDescription(channelDesc);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            assert notificationManager != null;
//            NotificationChannel currChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
//            if (currChannel == null)
//                notificationManager.createNotificationChannel(channel);
//        }
    }

    public void createNotification(String message) {
        CHANNEL_ID = "Pulse";
        if (message != null) {
            createNotificationChannel();

            // Intent intent = new Intent(this, PulseNotications.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Pulse Mobile App")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(uri);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            int notificationId = (int) (System.currentTimeMillis() / 4);
            notificationManager.notify(notificationId, mBuilder.build());
        }
    }

    private final IABleConnectStatusListener mBleConnectStatusListener = new IABleConnectStatusListener() {

        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if (status == Constants.STATUS_CONNECTED) {
                Log.e(TAG, "STATUS_CONNECTED");
                // qr_scan.setVisibility(View.GONE);
            } else if (status == Constants.STATUS_DISCONNECTED) {
                Log.e(TAG, "STATUS_DISCONNECTED");
                //Toast.makeText(mContext,"Please check if your Smart Watch is on...",Toast.LENGTH_LONG).show();
                intent.putExtra("steps", "");
                intent.putExtra("distances", "");
                intent.putExtra("kcals", "");
                intent.putExtra("connect", "dis");
                sendBroadcast(intent);
//                noSoundNotification("Wearable Disonnected", "Please enable Bluetooth & Location services");
                /// circleImageView.setBorderColor(ResourcesCompat.getColor(mContext.getResources(), R.color.red, null));
                //qr_scan.setVisibility(View.VISIBLE);

            }
        }
    };

    /**
     * Monitor the callback status between Bluetooth and the device
     */
    private final IABluetoothStateListener mBluetoothStateListener = new IABluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            Log.e(TAG, "open=" + openOrClosed);
            if (openOrClosed) {
                initBLE();
                scanDevice();
            }
        }
    };

    private final ISportModelStateListener iSportModelStateListener = new ISportModelStateListener() {
        @Override
        public void onSportModelStateChange(SportModelStateData sportModelStateData) {
            Log.e("IDIGIT_21_07", String.valueOf(sportModelStateData.getSportModeType()));
        }
    };

    /**
     * scannedCallback
     */
    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
        }

        @Override
        public void onDeviceFounded(final SearchResult device) {
            if (deviceMac != null) {
                if (String.valueOf(device.getAddress()).contains(deviceMac)) {
                    intent.putExtra("steps", "");
                    intent.putExtra("distances", "");
                    intent.putExtra("kcals", "");
                    intent.putExtra("connect", "connecting");
                    sendBroadcast(intent);
//                    noSoundNotification("Wearable Reconnecting", "");
                    if (deviceMac.contains(":")) {
                        Log.e("IDIGIT_21_07_CONNECTION", deviceMac);
                        if (!mIsOadModel) {
                            connectDevice(deviceMac, device.getName());
                        }
                    } else {
                        Log.e("TrynosTira222", deviceMac);
                        mIsOadModel = false;
                        mVpoperateManager.disconnectWatch(DeviceConnect.this);
                    }
                }

            }
        }

        @Override
        public void onSearchStopped() {
            final SearchResult device;

            // refreshStop();
            Log.e(TAG, "onSearchStopped");
        }

        @Override
        public void onSearchCanceled() {
            // refreshStop();
            Log.e(TAG, "onSearchCanceled");
        }
    };

    private void connectDevice(final String mac, final String deviceName) {
        System.out.println("++++++++++++Connect Device");
        BluetoothManager btManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<BluetoothDevice> connectedDevices = btManager.getConnectedDevices(GATT);
        boolean isConnected = false;
        if (connectedDevices != null) {
            for (int j = 0; j < connectedDevices.size(); j++) {
                if (connectedDevices.get(j).getAddress().equalsIgnoreCase(mac)) {
                    isConnected = true;
                    break;
                }
            }
        }


        if (isConnected) {
            Log.e("IDIGIT_CONNECTED", "TRUE");
            afterconnection();
        } else {
            Log.e("IDIGIT_CONNECTED", "FALSE");
            WristbandManager.getInstance(this).registerCallback(new WristbandManagerCallback(){
                @Override
                public void onConnectionStateChange(boolean status) {
                    super.onConnectionStateChange(status);
                    if(status){

//                        Intent intent = new Intent();
//                        intent.putExtra("mac", mac);
//                        intent.putExtra("macaddress", mac);
//                        intent.putExtra("connect", "connected");
//                        sendBroadcast(intent);
                       // setResult(0x02, intent);
//                        syncData();

                    }else {
                        disConnect();
                    }
                }
                @Override
                public void onError(int error){
                    super.onError(error);
                }
            });
            WristbandManager.getInstance(this).connect(mac);
        }


    }

    private void disConnect() {
        WristbandManager.getInstance(this).close();
    }

    private void afterconnection() {
        Log.e(TAG, "After Connection");
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.beep);
        mediaPlayer.start();
        intent.putExtra("connect", "connected");
        sendBroadcast(intent);

//        noSoundNotification("Wearable Connected", "");
        try {

            int height = Integer.parseInt(sharedPreferences.getString("height", "0"));
            int weight = Integer.parseInt(sharedPreferences.getString("weight", "0"));
            int target = Integer.parseInt(sharedPreferences.getString("master_target", "10000"));
            int age = 28;
            String gender = sharedPreferences.getString("gender", "Male");
            ESex eSex = null;
            if (gender.equals("Male")) {
                eSex = ESex.MAN;
            } else {
                eSex = ESex.WOMEN;
            }
            PersonInfoData personInfoData = new PersonInfoData(eSex, height, weight, age, target);
            VPOperateManager.getMangerInstance(mContext).syncPersonInfo(DeviceConnect.this, new IPersonInfoDataListener() {
                @Override
                public void OnPersoninfoDataChange(EOprateStauts eOprateStauts) {

                }
            }, personInfoData);

        } catch (Exception e) {


        }


        IsWearableConnected = true;

    }


//    private void pointsmanipulation(int step, String distances, String kcals) {
//
//        int totalpoints = Integer.parseInt(db.getTotal(user_id));
//
//        if (totalpoints < com.algebratech.pulse_wellness.utils.Constants.maxMonPoints) {
//
//            //  if (step < com.algebratech.pulse_wellness.utils.Constants.maxDaySteps) {
//
//            double coin = StaticMethods.calPulseCoins(step);
//
//            String points = String.valueOf(coin);
//
//            Date date = Calendar.getInstance().getTime();
//            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
//            String today = df.format(date);
//            db.addORupdate(today, points, step, distances, kcals);
//        }
//
//        List<DailyReads> dailyReads = db.dailyReads();
//        Log.e("POINTSS",dailyReads.get(dailyReads.size() - 1).getPoints());
//        dailyReadSync(dailyReads.get(dailyReads.size() - 1).getPoints(), dailyReads.get(dailyReads.size() - 1).getSteps(), dailyReads.get(dailyReads.size() - 1).getKcals(), dailyReads.get(dailyReads.size() - 1).getDistance(), dailyReads.get(dailyReads.size() - 1).getDate());
//        /*
//         * Commented by - Deep Amin
//         * Date Time - 21-07-2022
//         **/
//
////        ArrayList<ActivityListModel> activityListModels = db.toSync();
//
//
////        for (int trt = 0; trt < activityListModels.size(); trt++) {
////
////            //Toast.makeText(mContext, "Points : "+activityListModels.get(trt).getActivity_type(), Toast.LENGTH_LONG).show();
////            try {
////                //activitiesSync(activityListModels.get(trt).getActivity_type(), activityListModels.get(trt).getDuration(), String.valueOf(activityListModels.get(trt).getSteps()), activityListModels.get(trt).getKcals(), activityListModels.get(trt).getDistance(), activityListModels.get(trt).getDate(), activityListModels.get(trt).getMap_path(), activityListModels.get(trt).getMap_image(), activityListModels.get(trt).getCamera_image(), activityListModels.get(trt).getAverage_pace());
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////
////
////        }
////
//
//    }
//
//    private void dailyReadSync(String points, String steps, String kcals, String distance, String date) {
//
//        JSONObject object = new JSONObject();
//        try {
//            //input your API parameters
//            object.put("user_id", user_id);
//            object.put("date", date);
//            object.put("distance", distance);
//            object.put("steps", steps);
//            object.put("kcal", kcals);
//            object.put("points", points);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.dailysync, object,
//                new Response.Listener<JSONObject>() {
//                    @RequiresApi(api = Build.VERSION_CODES.M)
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            if (response.getString("status").equals("true")) {
//                            } else {
//                                //Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.e("Error", "Error: " + error.toString());
//                try {
//                    if (error.getMessage().contains(Api.baseurl)) {
//                        // Toast.makeText(getApplicationContext(), "No internet connection available!!!.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        //  Toast.makeText(getApplicationContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (Exception e) {
//                    Log.e(com.algebratech.pulse_wellness.utils.Constants.TAG, e.getMessage());
//                }
//            }
//        }
//        );
//
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(jsonObjectRequest);
//
//
//    }


    @SuppressLint({"LongLogTag", "NewApi"})
    @Override
    public void onResponse(int code) {
        sharedPreferences = getSharedPreferences(com.algebratech.pulse_wellness.utils.Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        deviceMac = sharedPreferences.getString("macAddress", "0");

        Log.e("Trynos write cmd status: ", deviceMac + "__" + String.valueOf(code));

        if (deviceMac.equals("0") || deviceMac.isEmpty()) {

            Log.e("TrynosTira222", "Hapana Watch apa saka bho");
            mIsOadModel = false;
            mVpoperateManager.disconnectWatch(DeviceConnect.this);

        } else {
            switch (code) {
                case Constants.BLUETOOTH_DISABLED:
                    initBLE();
                    scanDevice();
//                    noSoundNotification("Enable Your Bluetooth & Location", "");
                    IsWearableConnected = false;
                    break;
                case Constants.BLE_NOT_SUPPORTED:
                    initBLE();
                    IsWearableConnected = false;
                    scanDevice();
                    break;
                case Constants.CODE_DISCONNECT:
                    initBLE();
                    scanDevice();
                    IsWearableConnected = false;
                    break;
                case Constants.SERVICE_UNREADY:
                    IsWearableConnected = false;
                    initBLE();
                    scanDevice();
                    break;
                case Constants.SEARCH_CANCEL:
                    initBLE();
                    scanDevice();
                    IsWearableConnected = false;
                    break;
                case Constants.STATUS_UNKNOWN:
                    initBLE();
                    scanDevice();
                    IsWearableConnected = false;
                    break;
            }
            if (String.valueOf(code).contains("-1")) {
                initBLE();
                scanDevice();
            } else if (String.valueOf(code).contains("-5")) {
                initBLE();
                scanDevice();
            }
        }


    }

    @Override
    public void onSportModelStateChange(SportModelStateData sportModelStateData) {
        Log.e("modeeeee", String.valueOf(sportModelStateData.getSportModeType()));
    }

    void checkForResumeActivity() {
        String activityStatus = sharedPreferences.getString("Activity Status", "");
        Log.e(activityStatus.toString(), activityStatus.toString());
        if (activityStatus == "Started") {
            Log.e("Started and dis", "Started and disconnected");
            String activityTime = sharedPreferences.getString("Activity Time", "");
            String activityName = sharedPreferences.getString("Activity Name", "");

            AlertDialog.Builder builder1 = new AlertDialog.Builder(DeviceConnect.this);
            builder1.setMessage("You started " + activityName + "Would you like to resume this activity?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alert11 = builder1.create();
            alert11.show();

        } else
            Log.e("Started and dis", activityStatus);

    }

    int getAge(String birthYear) {
        int age;
        int by = Integer.parseInt(StaticMethods.correctDate(birthYear, "yyyy/MM/dd", "yyyy"));
        int cy = Calendar.getInstance().get(Calendar.YEAR);
        if (by > cy) {
            return age = 0;
        }
        return age = cy - by;

    }
}