package com.algebratech.pulse_wellness.services;


import static android.content.ContentValues.TAG;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.algebratech.pulse_wellness.R;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class CloudMessaging extends FirebaseMessagingService {



//    public CloudMessaging() {
//        FirebaseInstallations.getInstance().getId().addOnCompleteListener(
//                task -> {
//                    if (task.isSuccessful()) {
//                        String token = task.getResult();
//                        Log.i("token ---->>", token);
//
//                        // store the token in shared preferences
//                   //     PrefUtils.getInstance(getApplicationContext()).setValue(PrefKeys.FCM_TOKEN, token);
//
//                    }
//                }
//        );
//    }

    // either use below function to get the token or directly get from the shared preferences
//    public static String getToken(Context context) {
//        return PrefUtils.getInstance(context).getStringValue(PrefKeys.FCM_TOKEN, "");
//    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("FCM_CALLED_1111","not");

//        try {
//            // whatever you want
//            Log.d(TAG, "From: " + remoteMessage.getFrom());
//
//            // Check if message contains a data payload.
//            if (remoteMessage.getData().size() > 0) {
//                Log.e("TAG", "Message data payload: " + remoteMessage.getData());
//
//
//            }
//
//            // Check if message contains a notification payload.
//            if (remoteMessage.getNotification() != null) {
//                Log.e("TAG", "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            }
//        } catch (Exception e) {
//            // whatever you want
//            e.printStackTrace();
//        }
        Log.e("not","not");
        getFirebaseMessage(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    public void getFirebaseMessage(String title,String msg)
    {
        NotificationCompat.Builder  builder = new NotificationCompat.Builder(
                this,"myChannel"
        ).setSmallIcon(R.drawable.ic_add)
                .setContentTitle(title)
                .setContentTitle(msg)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(101,builder.build());
    }

    @Override
    public void onNewToken(@NotNull String token) {
        super.onNewToken(token);
    }

}