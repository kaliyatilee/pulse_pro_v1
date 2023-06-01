package com.algebratech.pulse_wellness.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.algebratech.pulse_wellness.Comment_activity;
import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.NotificationActivity;
import com.algebratech.pulse_wellness.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private String mTitle, mNotificationMessage;

    private boolean mIsEvent = true;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("FCM_JSON_OBJECT", s);
        getSharedPreferences("fcm_token", MODE_PRIVATE).edit().putString("fcm_token", s).apply();
        Log.e("JSON_OBJECT", s);
    }


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("FCM_CALLED_NOTIFICATION", "From: ");
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e("NOTIFICATION", "From: " + remoteMessage.getFrom());

        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        Log.e("JSON_OBJECT", object.toString());
        //sendNotification("TYPChennai", "You had a new Events/Announcement");

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            //Toast.makeText(NIBSApplication.getThis(), "Push Message = " + remoteMessage.getData(), Toast.LENGTH_SHORT).show();

            Map<String, String> jsonData = new HashMap<String, String>();

            jsonData = remoteMessage.getData();

            Iterator it = jsonData.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println(TAG + " " + pair.getKey() + " = " + pair.getValue());

                if (pair.getKey().equals("title")) {
                    mTitle = pair.getValue().toString();
                }

                if (pair.getKey().equals("body")) {
                    mNotificationMessage = pair.getValue().toString();

                    mIsEvent = mNotificationMessage.toUpperCase().contains("EVENT:");
                }
            }

            sendNotification(mTitle, mNotificationMessage, mIsEvent);

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            //Toast.makeText(TypApplication.getThis(), "Push Message Body = " + remoteMessage.getNotification().getBody(), Toast.LENGTH_SHORT).show();

            Map<String, String> jsonData = new HashMap<String, String>();

            jsonData = remoteMessage.getData();

            Iterator it = jsonData.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println(TAG + " " + pair.getKey() + " = " + pair.getValue());

                if (pair.getKey().equals("title")) {
                    mTitle = pair.getValue().toString();
                }

                if (pair.getKey().equals("body")) {
                    mNotificationMessage = pair.getValue().toString();

                    mIsEvent = mNotificationMessage.toUpperCase().contains("EVENT:");
                }
            }

            sendNotification(mTitle, remoteMessage.getNotification().getBody(), mIsEvent);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */


    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    @SuppressLint("WrongConstant")
    private void sendNotification(String title, String messageBody, boolean isEvent) {
        Intent intent = new Intent(this, NotificationActivity.class);
        //intent.putExtra(IntentConstant.IS_EVENT, isEvent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("fcm","fcm");
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }
        //String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "1")
                        .setSmallIcon(R.drawable.logo_new)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("1",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}