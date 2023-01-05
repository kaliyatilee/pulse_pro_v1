package com.algebratech.pulse_wellness.services;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Telephony;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.WearableNotification;
import com.veepoo.protocol.listener.base.IBleWriteResponse;

import java.io.ByteArrayOutputStream;


public class NotificationService extends NotificationListenerService implements IBleWriteResponse {

    Context context;
    SharedPreferences sharedPreferences;
    int whatsApp;
    int sms;
    int email;
    int facebook;
    int instagram;
    int twitter;
    int other;

    @Override

    public void onCreate() {
        Log.e("24082022_NOTIFICATION", "ON CREATE");
        super.onCreate();
        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        StatusBarNotification[] activeNotifs = getActiveNotifications();
        Log.e("24082022_NOTIFICATION", "NO NULL");
        if (activeNotifs != null) {
            Log.e("24082022_NOTIFICATION", "--" + activeNotifs.length);

            for (StatusBarNotification it : activeNotifs)
                onNotificationPosted(it);
        }

    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.e("24082022_NOTIFICATION", "onListenerConnected");
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.e("24082022_NOTIFICATION", "onListenerDisconnected");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        whatsApp = sharedPreferences.getInt(Constants.WHATSAPP, -1);
        sms = sharedPreferences.getInt(Constants.SMS, -1);
        email = sharedPreferences.getInt(Constants.EMAIL, -1);

        facebook = sharedPreferences.getInt(Constants.FACEBOOK, -1);
        instagram = sharedPreferences.getInt(Constants.INSTA, -1);
        twitter = sharedPreferences.getInt(Constants.TWITTER, -1);
        other = sharedPreferences.getInt(Constants.OTHER, -1);
        Log.e("24082022_NOTIFICATION", "___RECEIVED");
        try {
            String pack = sbn.getPackageName();
            String ticker = "";
            if (sbn.getNotification().tickerText != null) {
                ticker = sbn.getNotification().tickerText.toString();
            }
            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString("android.title");
            String text = null;
            try {
                text = extras.getCharSequence("android.text").toString();
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }

            int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);
            Bitmap id = sbn.getNotification().largeIcon;


            Log.e("Package", pack);
            Log.e("Ticker", ticker);
            Log.e("Title", title);
            Log.e("Text", text);

            Intent msgrcv = new Intent(Constants.NOTIFY);
            msgrcv.putExtra("package", pack);
            msgrcv.putExtra("ticker", ticker);
            msgrcv.putExtra("title", title);
            msgrcv.putExtra("text", text);
            if (id != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                id.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                msgrcv.putExtra("icon", byteArray);
            }

            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
            Log.d(com.algebratech.pulse_wellness.utils.Constants.TAG, title + "___" + text + "---" + pack + "#####" + defaultSmsPackageName);

            Log.d("MunasheNow : ", title + "___" + text + "---" + pack + "#####");


            if (title != null && text != null) {
                //whatsapp
                if (pack.contains("com.whatsapp") && whatsApp == 1) {
                    if (!title.equals("WhatsApp")) {

                        if (!title.equals("WhatsApp Web")) {
                            Log.e(com.algebratech.pulse_wellness.utils.Constants.TAG + "Whatsapp", title + "___" + text + "---" + pack);
                            //                    ContentSetting whatsapp = new ContentSmsSetting(ESocailMsg.WHATS, "0774166961", "Hello How are you");
                            //                    VPOperateManager.getMangerInstance(DeviceConnect.this).sendSocialMsgContent(DeviceConnect.this, whatsapp);

                            new WearableNotification().whatsapp(title, text);
                            //new WearableNotification().gmail(title,text);
                            //new WearableNotification().phone("0774166961");

                        }


                    }

                }
                //sms
                else if (pack.contains(defaultSmsPackageName) && sms == 1) {
                    new WearableNotification().sms(title, text);
                }
                //email
                else if (pack.contains("com.google.android.gm") && email == 1) {
                    new WearableNotification().gmail(title, text);
                }
                //facebook
                else if ((pack.contains("com.facebook.katana") || pack.contains("com.facebook.lite") || pack.contains("com.facebook.orca")) && facebook == 1) {
                    new WearableNotification().facebook(title, text);
                }
                //instagram
                else if (pack.contains("com.instagram.android") && instagram == 1) {
                    new WearableNotification().instagram(title, text);
                }
                //twitter
                else if (pack.contains("com.twitter.android") || pack.contains("com.twitter.android.lite") && twitter == 1) {
                    new WearableNotification().twitter(title, text);
                }
                //other
                else if (other == 1) {
                    new WearableNotification().other(title, text);
                }
            }


        } catch (Exception e) {
            Log.d(Constants.TAG + "NotiError :", e.getMessage());
        }


    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");

    }

    @Override
    public void onResponse(int i) {

    }


}
