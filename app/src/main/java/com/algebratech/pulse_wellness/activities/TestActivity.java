package com.algebratech.pulse_wellness.activities;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.io.File;
import net.sqlcipher.database.SQLiteDatabase;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.algebratech.pulse_wellness.R;

import com.algebratech.pulse_wellness.db.DBHelper;
import com.algebratech.pulse_wellness.utils.Constants;

public class TestActivity extends AppCompatActivity {

    private DBHelper db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentStatusAndNavigation();
        setContentView(R.layout.activity_create_profile2);
       // InitializeSQLCipher();
        SQLiteDatabase.loadLibs(TestActivity.this);
       // DBHelper.getInstance(TestActivity.this).insertActivities("Running","","",1,"","","1","","","",false);
        db = new DBHelper(TestActivity.this);
       // db.insertActivities("Running","","",1,"","","2020","","","",false);

        try {

            Cursor cursor = db.getActivityData("2020");
            cursor.moveToFirst();
            Log.d("Trynos=>Cursor : ", cursor.getString(0));
        }catch (Exception e){
            Log.d("Trynos=>Error : ", e.getMessage());
        }
        //db.getReadableDatabase(Constants.KEY);
        //db.getWritableDatabase(Constants.KEY);


    }

    private void transparentStatusAndNavigation() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void InitializeSQLCipher() {
        SQLiteDatabase.loadLibs(this);

        String databaseFile = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/" + Constants.PREF_NAME
                +"/" + ".Database2/"+ "testdemo.crypt12";

        String outputSource = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+ Constants.PREF_NAME+"/" + ".Database2/";
        Log.d(Constants.TAG+"Folder",outputSource);
        File dir = new File(outputSource);
        if (!dir.exists()){
            dir.mkdirs();
            Log.d(Constants.TAG+"@Database","Success");
            Log.d(Constants.TAG+"Folder","Ndagadzira");
        }else{
            Log.d(Constants.TAG+"Folder","Ndiripo");
        }

        //databaseFile.delete();
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, "test12333", null);
        database.execSQL("create table t1(a, b)");
        database.execSQL("insert into t1(a, b) values(?, ?)", new Object[]{"one for the money",
                "two for the show"});
        Cursor c = database.rawQuery("SELECT * FROM t1 ",null);
        if(c.moveToFirst()){
            Log.d("Trynos:DB",c.getString(0));
        }
    }
}
