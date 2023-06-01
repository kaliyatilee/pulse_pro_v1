package com.algebratech.pulse_wellness.services;

import static com.wosmart.ukprotocollibary.db.dao.SportInfoDao.Properties.steps;

import android.app.Notification;
import android.app.PendingIntent;
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
import com.algebratech.pulse_wellness.utils.StaticMethods;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
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

public class DeviceSyncService extends Service {
    public static boolean IsRunning = false;
    public static final String BROADCAST_ACTION = "com.algebratech.pulse_wellness.DeviceConnectData";
    Context context = DeviceSyncService.this;
    private final String tag = "SyncDataActivity";
    String deviceMac;
    SharedPreferences.Editor myEdit;
    SharedPreferences sharedPreferences;
    private BluetoothManager mBManager;
    private BluetoothAdapter mBAdapter;
    private BluetoothLeScanner mBScanner;
    private String user_id;
    public Handler handler = null;
    public static Runnable runnable = null;
    boolean isSyncRunning = false;
    Intent intent;
    private DBHelper db;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        deviceMac = sharedPreferences.getString("macAddress", null);
        user_id = sharedPreferences.getString("userID", "");

        intent = new Intent(BROADCAST_ACTION);
        db = new DBHelper(getApplicationContext());


        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                boolean isConnected = WristbandManager.getInstance(context).isConnect();
//                if(isConnected == true) {
//                    Calendar calendar = Calendar.getInstance();
//                    int year = calendar.get(Calendar.YEAR);
//                    int month = calendar.get(Calendar.MONTH) + 1;
//                    int day = calendar.get(Calendar.DAY_OF_MONTH);
//                    List<SportData> steps = GlobalGreenDAO.getInstance().loadSportDataByDate(year, month, day);
//                    SportSubData subData = WristbandCalculator.sumOfSportDataByDate(year, month, day, steps);
//                    Toast.makeText(context, subData.toString(), Toast.LENGTH_SHORT).show();

                    int stepcount = 0;
                    int calories = 0;
                    int distance = 0;
//                    //List<SportData> steps = GlobalGreenDAO.getInstance().loadSportDataByDate(year,month,day);
//                     List<SportData> steps = GlobalGreenDAO.getInstance().loadAllSportData();
//                    System.out.println("+++++++++++++Steps "+steps );
//                    if (null != steps) {
//                        for (SportData item : steps) {
//                            if (item.getMonth() == month && item.getYear() == year && item.getDay() == day) {
//                                distance = distance + item.getDistance();
//                                calories = calories + item.getCalory();
//                                stepcount = stepcount + item.getStepCount();
//                            }
//                        }
//                    }
//                    float final_distance = StaticMethods.loadCalculate(distance);
                  // float final_calorie = StaticMethods.loadCalculate(calories);
//
//                    intent.putExtra("kcals", String.valueOf(final_calorie));
//                    intent.putExtra("distance",String.valueOf(final_distance));
//                    intent.putExtra("steps", String.valueOf(stepcount));
//                    intent.putExtra("connect", "connected");
//                    sendBroadcast(intent);
//
               //   noSoundNotification("Wearable Connected", "Steps : " + stepcount + " \n Distance : " + String.valueOf(final_distance) + " \n Kcals : " + String.valueOf(final_calorie));
//                    pointsmanipulation(stepcount, String.valueOf(final_distance), String.valueOf(final_calorie));







            }
        }, 0, 60000);
    }

    private void stopMeasure() {
//        CM.HideProgressLoader();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (WristbandManager.getInstance(context).setTemperatureStatus(false)) {
                    WristbandManager.getInstance(context).readBpValue();
                } else {
                    System.out.println("+++++++++++++++++++Stop Measure failed");
                }
            }
        });
        thread.start();
    }



    private void noSoundNotification(String contentTitle, String contentText) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent,
                    0 | PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);
        }

        Notification notification = new NotificationCompat.Builder(this, com.algebratech.pulse_wellness.utils.Constants.CHANNEL_ID_NO_SOUND)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(null)
                .setSilent(true)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    private void pointsmanipulation(int step, String distances, String kcals) {

        int totalpoints = Integer.parseInt(db.getTotal(user_id));

        if (totalpoints < com.algebratech.pulse_wellness.utils.Constants.maxMonPoints) {

            //  if (step < com.algebratech.pulse_wellness.utils.Constants.maxDaySteps) {

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
                VolleyLog.e("Error", "Error: " + error.toString());
                try {
                    if (error.getMessage().contains(Api.baseurl)) {
                        // Toast.makeText(getApplicationContext(), "No internet connection available!!!.", Toast.LENGTH_SHORT).show();
                    } else {
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