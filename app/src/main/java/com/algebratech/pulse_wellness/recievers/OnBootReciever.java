package com.algebratech.pulse_wellness.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.algebratech.pulse_wellness.services.DeviceConnect;

public class OnBootReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

            if (!DeviceConnect.IsRunning) {
                Intent intentService = new Intent(context, DeviceConnect.class);
                //context.startService(intentService);
                ContextCompat.startForegroundService(context, intentService);
            }

    }
}
