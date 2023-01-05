package com.algebratech.pulse_wellness.recievers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.WearableNotification;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.model.enums.ESocailMsg;
import com.veepoo.protocol.model.settings.ContentPhoneSetting;
import com.veepoo.protocol.model.settings.ContentSetting;

public class PhoneStateReceiver extends BroadcastReceiver implements IBleWriteResponse {
    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {


       VPOperateManager vpOperateManager = VPOperateManager.getMangerInstance(context);

        try {
            System.out.println("Receiver start");
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                //Toast.makeText(context,"Incoming Call State",Toast.LENGTH_SHORT).show();
                //Toast.makeText(context,"Ringing State Number is -"+incomingNumber,Toast.LENGTH_SHORT).show();
                Log.d(Constants.TAG+"Ringing Number is",incomingNumber);
                new WearableNotification().phone(incomingNumber);
                ContentSetting phone = new ContentPhoneSetting(ESocailMsg.PHONE, incomingNumber);
                vpOperateManager.sendSocialMsgContent(this,phone);


            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
                vpOperateManager.offhookOrIdlePhone(this);
                //Toast.makeText(context,"Call Received State",Toast.LENGTH_SHORT).show();
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
               // vpOperateManager.offhookOrIdlePhone(this);
                //Toast.makeText(context,"Call Idle State",Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onResponse(int i) {

    }


}
