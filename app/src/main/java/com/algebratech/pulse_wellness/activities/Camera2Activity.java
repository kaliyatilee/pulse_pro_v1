package com.algebratech.pulse_wellness.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.IntentUtils;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;

import java.util.ArrayList;

import me.zhanghai.android.systemuihelper.SystemUiHelper;

public class Camera2Activity extends AppCompatActivity {

    String path;
    SystemUiHelper uiHelper;
    private static final int CAMERA_REQUEST = 9514;
    Options options;
    private ArrayList<String> returnValue = new  ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new SystemUiHelper(this, SystemUiHelper.LEVEL_IMMERSIVE, SystemUiHelper.FLAG_IMMERSIVE_STICKY);

        options = Options.init()
                .setRequestCode(CAMERA_REQUEST)
                .setCount(1)
                .setFrontfacing(true)
                .setPreSelectedUrls(returnValue)
                .setMode(Options.Mode.All)
                .setSpanCount(5)
                .setVideoDurationLimitinSeconds(30)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath(Constants.PREF_NAME+"/"+Constants.PREF_NAME+" Gallery");

        Pix.start(Camera2Activity.this, options);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Log.d(Constants.TAG+"Camera:",returnValue.toString());
            setResult(CAMERA_REQUEST, data);
            finish();
        }else {
            Log.d(Constants.TAG+"Camera:", "Zvaramba");
           finish();
        }

    }



    @Override
    protected void onStart() {
        super.onStart();

        //hiding system bars
        uiHelper.hide();
    }
}
