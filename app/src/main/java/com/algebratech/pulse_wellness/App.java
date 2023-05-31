package com.algebratech.pulse_wellness;

import static com.algebratech.pulse_wellness.utils.Constants.CHANNEL_ID;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;

import com.algebratech.pulse_wellness.activities.MainActivity;
//import com.algebratech.pulse_wellness.services.DeviceConnect;
import com.algebratech.pulse_wellness.activities.ScanActivity;
import com.algebratech.pulse_wellness.services.DeviceConnect;
import com.algebratech.pulse_wellness.services.SyncWearableService;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.SharedPreferenceUtil;
import com.realsil.sdk.core.RtkConfigure;
import com.realsil.sdk.core.RtkCore;
import com.realsil.sdk.dfu.RtkDfu;

import org.json.JSONObject;

public class App extends Application  {

   // private static String one_sign_id = "6765391d-f6bf-4ac3-8385-f831fe5b8fba";
    private static App mApp = null;
    private static String currentChatId = "";
    private static boolean chatActivityVisible;
    private static boolean phoneCallActivityVisible;
    private static boolean baseActivityVisible;
    private static boolean isCallActive = false;
    private boolean hasMovedToForeground = false;
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;
    public int fnoti = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        //add support for vector drawables on older APIs
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        myEdit.putInt("friendreq_not",1);
        myEdit.apply();


        RtkConfigure configure = new RtkConfigure.Builder()
                .debugEnabled(true)
                .printLog(true)
                .logTag("OTA")
                .globalLogLevel(1)
                .build();
        RtkCore.initialize(this, configure);
        RtkDfu.initialize(this, true);

        SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(this);
        if (sharedPreferenceUtil.getStringPreference(Constants.DEVICE_MAC,null) != null) {
            Intent serviceIntent = new Intent(getApplicationContext(), SyncWearableService.class);
            serviceIntent.putExtra(Constants.START_SERVICE, true);

            if(!SyncWearableService.IsRunning) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(this, serviceIntent);
                } else {
                    getApplicationContext().startService(serviceIntent);
                }

            }


        }
        createNotificationChannel();

        mApp = this;

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.app_name)+" Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setSound(null, null);
            serviceChannel.setShowBadge(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            NotificationChannel serviceChannelNoSound = new NotificationChannel(
//                    Constants.CHANNEL_ID_NO_SOUND,
//                    "Pulse No Sound",
//                    NotificationManager.IMPORTANCE_NONE
//            );
//            serviceChannelNoSound.setSound(null, null);
//            serviceChannelNoSound.setShowBadge(false);
//
//            NotificationChannel serviceChannelSound = new NotificationChannel(
//                    Constants.CHANNEL_ID_SOUND,
//                    "Pulse Sound",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            serviceChannelSound.setShowBadge(false);
//
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannelNoSound);
//            NotificationManager managerSound = getSystemService(NotificationManager.class);
//            managerSound.createNotificationChannel(serviceChannelSound);
       }



//    public class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler
//    {
//        @Override
//        public void notificationOpened(OSNotificationOpenResult result)
//        {
//            OSNotificationAction.ActionType actionType = result.action.type;
//            JSONObject data = new JSONObject();
//            data =  result.notification.payload.additionalData;
//            String activityToBeOpened;
//            String activity;
//
//            Log.d("OneSignalNoti=","onesignal open:");
//            if (data != null)
//            {
//                Log.d("OneSignalNoti=",data.toString());
//                activityToBeOpened = data.optString("intent", null);
//
//                if (activityToBeOpened != null && activityToBeOpened.equals("friendrequest"))
//                {
//                    Log.d("OneSignal", "customkey set with value: " + activityToBeOpened);
//                    Intent intent = new Intent(App.this, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("frgToLoad","friendrequest");
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    startActivity(intent);
//                } else if (activityToBeOpened != null && activityToBeOpened.equals("DEF"))
//                {
//                    Log.d("OneSignal", "customkey set with value: " + activityToBeOpened);
////                    Intent intent = new Intent(ApplicationClass.this, DEFActivity.class);
////                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
////                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
////                    startActivity(intent);
//                }
//            }
//        }
//    }
//
//    private class NotificationReceivedHandler implements OneSignal.NotificationReceivedHandler
//    {
//        @Override
//        public void notificationReceived(OSNotification notification) {
//            JSONObject data = notification.payload.additionalData;
//            String customKey,friendreqData;
//            int friendreq_not ;
//
//            Log.d("OneSignalNoti","onesignal receiver:"+data.length());
//            if (data != null) {
//                friendreqData = data.optString("intent", null);
//                if(friendreqData!= null && friendreqData.equals("friendrequest")){
//                    try{
//
//                        friendreq_not = sharedPreferences.getInt("friendreq_not",1);
//                        new MainActivity().showBadge(getApplicationContext(),R.id.notifications,String.valueOf(friendreq_not));
//                        friendreq_not++;
//                        myEdit.putInt("friendreq_not",friendreq_not);
//                        myEdit.apply();
//
//
//                    }
//                    catch (Exception e){
//
//                    }
//                }
//
//            }
//        }
//    }

    public static Context context() {

        return mApp.getApplicationContext();
    }

    //to run multi dex
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }





}
