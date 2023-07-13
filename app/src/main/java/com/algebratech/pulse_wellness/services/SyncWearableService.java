package com.algebratech.pulse_wellness.services;

import static com.algebratech.pulse_wellness.utils.Constants.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.MainActivity;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.db.DBHelper;
import com.algebratech.pulse_wellness.models.DailyReads;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.SharedPreferenceUtil;
import com.algebratech.pulse_wellness.utils.StaticMethods;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wosmart.ukprotocollibary.WristbandManager;
import com.wosmart.ukprotocollibary.WristbandManagerCallback;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerBeginPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerBp3ContinuousPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerBpPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerDeviceInfoPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerECGPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerEcgToDevicePacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerFunctionPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerHrpItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerHrpPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerRateItemPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerRateListPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerReadHealthPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSleepPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSpo2Packet;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerSportPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerStepPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerTemperatureControlPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerTodaySumSportPacket;
import com.wosmart.ukprotocollibary.applicationlayer.ApplicationLayerUserPacket;
import com.wosmart.ukprotocollibary.model.db.GlobalGreenDAO;
import com.wosmart.ukprotocollibary.model.hrp.HrpData;
import com.wosmart.ukprotocollibary.model.sleep.SleepData;
import com.wosmart.ukprotocollibary.model.sleep.SleepSubData;
import com.wosmart.ukprotocollibary.model.sport.SportData;
import com.wosmart.ukprotocollibary.model.sport.SportSubData;
import com.wosmart.ukprotocollibary.util.WristbandCalculator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SyncWearableService  extends Service {
    public static boolean IsRunning = false;
    private static final String TAG = "TrySoftServiceLog";
    private Timer syncDataTimer;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private boolean DATA_SYNC = false;
    public static final String BROADCAST_ACTION = "com.algebratech.pulse_wellness.DeviceConnectData";
    Intent intent;
    private String user_id;
    SharedPreferences sharedPreferences;
    private DBHelper db;
    Boolean isWebSyncing = false;
    @Nullable
    @Override
    public void onCreate() {
        super.onCreate();
        IsRunning = true;
        intent = new Intent(BROADCAST_ACTION);
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        user_id = sharedPreferences.getString("userID", "");
        intent.putExtra("sync_status","connecting Wearable");
        sendBroadcast(intent);
        noSoundNotification("Connecting Wearable","Connecting");
        db = new DBHelper(getApplicationContext());
        WristbandManager.getInstance(this).registerCallback(wristbandManagerCallback);
        syncDataTimer = new Timer();

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras().getBoolean(Constants.START_SERVICE)) {
            WristbandManager.getInstance(this).connect(sharedPreferenceUtil.getStringPreference(Constants.DEVICE_MAC, null));
            //noSoundNotification("");
        }

        return START_NOT_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void noSoundNotification(String wearable_connected, String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                1442, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name) + " Service")
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(null)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    WristbandManagerCallback wristbandManagerCallback = new WristbandManagerCallback() {

        @Override
        public void onDeviceInfo(ApplicationLayerDeviceInfoPacket packet) {
            super.onDeviceInfo(packet);
            Log.d(TAG, "onDeviceInfo: "+packet.toString());
        }

        @Override
        public void onDeviceFunction(ApplicationLayerFunctionPacket packet) {
            super.onDeviceFunction(packet);
            Log.d(TAG, "onDeviceFunction: "+packet.toString());
        }

        @Override
        public void onSyncDataBegin(ApplicationLayerBeginPacket packet) {
            super.onSyncDataBegin(packet);
          //  Toast.makeText(SyncWearableService.this, "Wearable Sync Data Started", Toast.LENGTH_SHORT).show();
            intent.putExtra("isSyncing","yes");
            sendBroadcast(intent);
            noSoundNotification("Wearable Connected", "Wearable Sync Data Started");
            Log.d(TAG, "onSyncDataBegin: "+packet.toString());
        }

        @Override
        public void onSyncDataEnd(ApplicationLayerTodaySumSportPacket packet) {
            super.onSyncDataEnd(packet);
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            List<SleepData> sleepData = GlobalGreenDAO.getInstance().loadSleepDataByDate(year,month,day);
            SleepSubData sleepSubData = WristbandCalculator.sumOfSleepDataByDate(year,month,day,sleepData);

            Log.d(Constants.TAG,"Sleep data"+ sleepData);

            try {
                intent.putExtra("deepSleep",""+sleepSubData.getDeepSleepTime());
            }catch (Exception e){

            }

            List<SportData> steps = GlobalGreenDAO.getInstance().loadSportDataByDate(year, month, day);
            SportSubData subData = WristbandCalculator.sumOfSportDataByDate(year, month, day, steps);
            float distance = StaticMethods.loadCalculate(subData.getDistance());
            float calorie = StaticMethods.loadCalculate(subData.getCalory());

            String kcals = String.valueOf(calorie);
            String mSteps = String.valueOf(subData.getStepCount());

            intent.putExtra("kcals", String.valueOf(calorie));
            intent.putExtra("distance",String.valueOf(distance));
            intent.putExtra("steps", String.valueOf(subData.getStepCount()));
            intent.putExtra("connect", "connected");
            intent.putExtra("sync_status", "Syncing ..");
            intent.putExtra("isSyncing","no");
            sendBroadcast(intent);
            noSoundNotification("Wearable Connected", "Steps : " + mSteps + " \n Distance : " + distance + " \n Kcals : " + kcals);

            if (isWebSyncing == false){
                pointsmanipulation(subData.getStepCount(), String.valueOf(distance), kcals);
            }

            WristbandManager.getInstance(getApplicationContext()).sendDataRequest();
        }

        @Override
        public void onTemperatureData(ApplicationLayerHrpPacket packet) {
            super.onTemperatureData(packet);
            for (ApplicationLayerHrpItemPacket item : packet.getHrpItems()) {
                intent.putExtra("temperature",String.valueOf(item.getTemperature()));
                sendBroadcast(intent);
                Log.d(TAG, "temp origin value :" + item.getTempOriginValue() + " temperature adjust value : " + item.getTemperature() + " is wear :" + item.isWearStatus() + " is adjust : " + item.isAdjustStatus() + "is animation :" + item.isAnimationStatus());
            }
        }

        @Override
        public void onTemperatureMeasureSetting(ApplicationLayerTemperatureControlPacket packet) {
            super.onTemperatureMeasureSetting(packet);
            Log.d(TAG, "temp setting : show = " + packet.isShow() + " adjust = " + packet.isAdjust() + " celsius unit = " + packet.isCelsiusUnit());
        }

        @Override
        public void onTemperatureMeasureStatus(int status) {
            super.onTemperatureMeasureStatus(status);
            Log.d(TAG, "temp status :" + status);
        }


        @Override
        public void onSportDataReceiveIndication(ApplicationLayerSportPacket packet) {
            super.onSportDataReceiveIndication(packet);
            //sharedPreferenceUtil.setSportData(packet.getSportItems());
            Log.d(TAG, "onSportDataReceiveIndication: "+packet.toString());
        }

        @Override
        public void onHrpDataReceiveIndication(ApplicationLayerHrpPacket packet) {
            super.onHrpDataReceiveIndication(packet);

        }

        @Override
        public void onBpDataReceiveIndication(ApplicationLayerBpPacket packet) {
            super.onBpDataReceiveIndication(packet);
            Log.d(TAG, "onBpDataReceiveIndication: "+packet.toString());
        }

        @Override
        public void onEcgTestDataPacket(ApplicationLayerECGPacket ecgPacket) {
            super.onEcgTestDataPacket(ecgPacket);
            Log.d(TAG, "onEcgTestDataPacket: "+ecgPacket.toString());
        }

        @Override
        public void onEcgTestData(byte[] data) {
            super.onEcgTestData(data);
            Log.d(TAG, "onEcgTestData: "+data.toString());
        }

        //ECG DATA
        @Override
        public void onReadHealthDataPacket(ApplicationLayerReadHealthPacket readHealthPacket) {
            super.onReadHealthDataPacket(readHealthPacket);
            Log.d(TAG, "onReadHealthDataPacket: "+readHealthPacket.toString());
        }

        @Override
        public void onEcgToDeviceRsp(ApplicationLayerEcgToDevicePacket packet) {
            super.onEcgToDeviceRsp(packet);
            Log.d(TAG, "onEcgToDeviceRsp: "+packet.toString());
        }

        @Override
        public void onEcgTestStatus(int status) {
            super.onEcgTestStatus(status);
            Log.d(TAG, "onEcgTestStatus: "+status);
        }

        @Override
        public void onSpo2DataReceive(ApplicationLayerSpo2Packet packet) {
            super.onSpo2DataReceive(packet);
            Log.d(TAG, "onSpo2DataReceive: "+packet.toString());
        }


        @Override
        public void onSleepDataReceiveIndication(ApplicationLayerSleepPacket packet) {
            super.onSleepDataReceiveIndication(packet);
            sharedPreferenceUtil.setSleepData(packet.getSleepItems());
            Log.d(TAG, "onSleepDataReceiveIndication: "+ packet);
        }

        @Override
        public void onStepDataReceiveIndication(ApplicationLayerStepPacket packet) {
            super.onStepDataReceiveIndication(packet);
            //sharedPreferenceUtil.setStepData(packet);
            Log.d(TAG, "onStepDataReceiveIndication: "+packet.toString());
        }

        @Override
        public void onBp3Continuous(ApplicationLayerBp3ContinuousPacket bp3ContinuousPacket) {
            super.onBp3Continuous(bp3ContinuousPacket);
        }

        @Override
        public void onLoginStateChange(int state) {
            super.onLoginStateChange(state);
            if (state == WristbandManager.STATE_WRIST_LOGIN) {
                boolean deviceInfo = WristbandManager.getInstance(getApplicationContext()).requestDeviceInfo();
                boolean deviceFunction = WristbandManager.getInstance(getApplicationContext()).sendFunctionReq();
                if (deviceInfo && deviceFunction) {
                    if (WristbandManager.getInstance(getApplicationContext()).setTimeSync()) {
                        noSoundNotification("Wearable Connected", "Wearable Logged In");
                        ApplicationLayerUserPacket userInfo = new ApplicationLayerUserPacket(true, 33, 169, 69);
                        if (WristbandManager.getInstance(getApplicationContext()).setUserProfile(userInfo)) {

                            intent.putExtra("connect", "connected");
                            intent.putExtra("sync_status", "Updating User Info");
                            sendBroadcast(intent);
                            noSoundNotification("Wearable Connected", "Wearable Update User Info");
                            WristbandManager.getInstance(getApplicationContext()).setCustomExerciseLack();
                            boolean steps = WristbandManager.getInstance(getApplicationContext()).setTargetStep(10 * 1000); //10000 steps
                            boolean kcals = WristbandManager.getInstance(getApplicationContext()).setTargetCalc(700); //700kcal
                            boolean sleep = WristbandManager.getInstance(getApplicationContext()).setTargetSleep(60 * 8); //8 hours
                            //Start Data Sync intervals
                            if (steps && kcals && sleep) {
                                WristbandManager.getInstance(getApplicationContext()).sendDataRequest();
//                               syncDataTimer.scheduleAtFixedRate(new TimerTask() {
//                                   @Override
//                                   public void run() {
//                                       //Data Sync
//                                       if (!DATA_SYNC)
//
//                                       DATA_SYNC = true;
//                                   }
//                               }, 0, 60 * 1000); //1 minute
                            }
                        }

                    } else {
                        WristbandManager.getInstance(getApplicationContext()).startLoginProcess("1234567890");
                    }
                }
            }
        }

        @Override
        public void onConnectionStateChange(boolean status) {
            super.onConnectionStateChange(status);
            if (status) {
                intent.putExtra("connect", "connected");
                intent.putExtra("sync_status","");
                sendBroadcast(intent);
                noSoundNotification("Wearable Connected", "Wearable Paired");
                WristbandManager.getInstance(getApplicationContext()).startLoginProcess("1234567890");

            } else {
                intent.putExtra("connect", "dis");
                intent.putExtra("sync_status","Attempting to connect");
                sendBroadcast(intent);
                noSoundNotification("Wearable Connected", "Error occurred while pairing wearable");
            }
        }

        @Override
        public void onError(int error) {
            super.onError(error);
            DATA_SYNC = false;
        }
    };

    private void pointsmanipulation(int step,String distances, String kcals) {
        isWebSyncing = true;
        int totalpoints = Integer.parseInt(db.getTotal(user_id));
        if (totalpoints < com.algebratech.pulse_wellness.utils.Constants.maxMonPoints) {
            double coin = StaticMethods.calPulseCoins(step);
            String points = String.valueOf(coin);
            Date date = java.util.Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String today = df.format(date);
            db.addORupdate(today, points, step, distances, kcals);
        }

        List<DailyReads> dailyReads = db.dailyReads();
        Log.e("POINTSS",dailyReads.get(dailyReads.size() - 1).getPoints());
        dailyReadSync(dailyReads.get(dailyReads.size() - 1).getPoints(), dailyReads.get(dailyReads.size() - 1).getSteps(), dailyReads.get(dailyReads.size() - 1).getKcals(), dailyReads.get(dailyReads.size() - 1).getDistance(), dailyReads.get(dailyReads.size() - 1).getDate());

    }

    private void dailyReadSync(String points, String steps, String kcals, String distance, String date) {
            JSONObject object = new JSONObject();
            try {
                //input your API parameters
                object.put("user_id", user_id);
                object.put("date", date);
                object.put("distance", distance);
                object.put("steps", steps);
                object.put("kcal", kcals);
                object.put("points", points);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.dailysync, object,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onResponse(JSONObject response) {
                            isWebSyncing = false;
                            try {
                                if (response.getString("status").equals("true")) {
                                } else {
                                    //Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    isWebSyncing = false;
                    VolleyLog.e("Error", "Error: " + error.toString());
                    try {
                        if (error.getMessage().contains(Api.baseurl)) {
                            pointsmanipulation(Integer.parseInt(steps), String.valueOf(distance), String.valueOf(kcals));
                            // Toast.makeText(getApplicationContext(), "No internet connection available!!!.", Toast.LENGTH_SHORT).show();
                        } else {
                            pointsmanipulation(Integer.parseInt(steps), String.valueOf(distance), String.valueOf(kcals));
                            //  Toast.makeText(getApplicationContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Log.e(com.algebratech.pulse_wellness.utils.Constants.TAG, e.getMessage());
                    }
                }
            }
            );

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);


        }



    @Override
    public void onDestroy() {
        super.onDestroy();
        syncDataTimer = null;
        wristbandManagerCallback = null;
        IsRunning = false;
    }

}
