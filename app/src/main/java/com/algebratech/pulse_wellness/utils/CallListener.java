package com.algebratech.pulse_wellness.utils;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.model.enums.ESocailMsg;
import com.veepoo.protocol.model.settings.ContentPhoneSetting;
import com.veepoo.protocol.model.settings.ContentSetting;

public class CallListener extends PhoneStateListener {

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        if (TelephonyManager.CALL_STATE_RINGING == state) {
            // Incoming call handling
            Log.d("Incoming Call", " ring ring ring" + incomingNumber);


        }
        if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
            // Outgoing call handling and answer
            Log.d("Incoming Call", " ofhook" + incomingNumber);

        }
        if (TelephonyManager.CALL_STATE_IDLE == state) {
            // Device back to normal state (not in a call)
            Log.d("Incoming Idle", " idle" + incomingNumber);
    }
}
}
