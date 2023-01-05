package com.algebratech.pulse_wellness.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.core.app.NotificationCompat;



public class NotifyListener extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // TODO Auto-generated method stub.
//Code when the event is caught
        Log.d(Constants.TAG, String.valueOf(event.describeContents()));
    }
    @Override
    public void onInterrupt() {
        // TODO Auto-generated method stub.

    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.feedbackType = 1;
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }
}







//
//        extends NotificationListenerService {
//
//    private static final String TAG = "NotificationListener";
//    private static final String WA_PACKAGE = "com.whatsapp";
//
//    @Override
//    public void onListenerConnected() {
//        Log.i(TAG, "Notification Listener connected");
//    }
//
//    @Override
//    public void onNotificationPosted(StatusBarNotification sbn) {
//        if (!sbn.getPackageName().equals(WA_PACKAGE)) return;
//
//        Notification notification = sbn.getNotification();
//        Bundle bundle = notification.extras;
//
//        String from = bundle.getString(NotificationCompat.EXTRA_TITLE);
//        String message = bundle.getString(NotificationCompat.EXTRA_TEXT);
//
//        Log.d(TAG, "From: " + from);
//        Log.d(TAG, "Message: " + message);
//    }
//}