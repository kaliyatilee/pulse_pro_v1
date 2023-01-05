package com.algebratech.pulse_wellness.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.db.DBHelper;
import com.algebratech.pulse_wellness.models.OutdoorResultModel;
import com.algebratech.pulse_wellness.utils.BitmapUtils;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.DirManager;
import com.algebratech.pulse_wellness.utils.ImageUtil;
import com.algebratech.pulse_wellness.utils.MessageType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import github.nisrulz.screenshott.ScreenShott;


public class RunResultsActivity extends AppCompatActivity {

    String userId;
    String proccessId;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;
    double longi, lati;
    TextView distance;
    TextView tvTime;
    String timeTaken;
    TextView tvsteps;
    TextView kmCovered;
    TextView calories;

    ImageView mapimg;
    FloatingActionButton btnShare, back;
    String mDistance;
    String mKcals;
    String mSteps;
    TextView heatRate, avpace, activityName;
    String avarageHeartRate;
    String avaragePace;
    String act_id;
    String activityNameString;

    int miSteps;
    double miCalories;
    double miDistance;
    LocationManager locationManager;
    String avgPace, camera_file, map_file;
    String imagePath;
    private DBHelper db;
    private RelativeLayout socialview, showImg, showMap, resultView;
    private TextView activity_type;
    Bitmap bitmap;
    Bitmap rotatedBitmap = null;
    String base64;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_results2);
        Log.e("PULSE_Result", "Activity Called");
        SQLiteDatabase.loadLibs(getApplicationContext());
        db = new DBHelper(getApplicationContext());

        Intent activityIntent = getIntent();
        timeTaken = activityIntent.getStringExtra("timeTaken");
        mDistance = activityIntent.getStringExtra("distance");
        mKcals = activityIntent.getStringExtra("kcals");
        mSteps = activityIntent.getStringExtra("steps");
        base64 = activityIntent.getStringExtra("base64");
        imagePath = activityIntent.getStringExtra("imagePath");
        mapimg = findViewById(R.id.mapimg);

        BitmapFactory.Options options = new BitmapFactory.Options();
        final Bitmap b = BitmapFactory.decodeFile(imagePath, options);
        mapimg.setImageBitmap(b);

        avarageHeartRate = activityIntent.getStringExtra("avarageHeartRate");
        if (Objects.equals(avarageHeartRate, "null")) {
            avarageHeartRate = "0";
        }
        avgPace = activityIntent.getStringExtra("avgPace");
        act_id = activityIntent.getStringExtra("act_id");
        activityNameString = activityIntent.getStringExtra("activity");
        camera_file = activityIntent.getStringExtra("camera_file");
        Log.e("Trynos=>ActId", act_id + " : " + camera_file);

        avaragePace = avgPace;

        distance = findViewById(R.id.distance);
        avpace = findViewById(R.id.avpace);
        activityName = findViewById(R.id.activityName);
        btnShare = findViewById(R.id.btnShare);
        back = findViewById(R.id.back);
        tvTime = findViewById(R.id.tvTime);
        tvsteps = findViewById(R.id.steps);
        calories = findViewById(R.id.calories);
        heatRate = findViewById(R.id.heatRate);

        socialview = findViewById(R.id.socialview);
        resultView = findViewById(R.id.resultView);
        showMap = findViewById(R.id.showMap);
        showImg = findViewById(R.id.showImg);
        avpace.setText("" + avaragePace);
        //String activityNameStringNew = activityNameString.replaceAll("\\s", "\n");
        activityName.setText(activityNameString);

        try {
            Cursor cursor = db.getActivityData(act_id);

            Log.e("map_file", db.getActivityData(act_id).getString(0));
            cursor.moveToFirst();
            map_file = cursor.getString(0);
//            mapimg.setImageBitmap(ImageUtil.convert(base64));
            Log.e("base64RESULE", base64);
//            mapimg.setImageBitmap(ImageUtil.convert(map_file));
            activity_type.setText("Test");

        } catch (Exception e) {
            Log.d("Trynos=>Error : ", e.getMessage());
        }


        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareSocial();
                //captionDialog();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mapimg.setImageBitmap(ImageUtil.convert(map_file));

                } catch (Exception e) {

                }

            }
        });


        showImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

//                    Picasso.with(RunResultsActivity.this).load("file:" + camera_file).into(mapimg);

                } catch (Exception e) {

                }


            }
        });

        Double mDistanceDob = Double.parseDouble(mDistance);
        Double kcalsDob = Double.parseDouble(mKcals);

        String tempkcal = String.format("%.2f", kcalsDob);
        String tempdis = String.format("%.2f", mDistanceDob);

        heatRate.setText(avarageHeartRate);
        calories.setText(tempkcal);
        distance.setText(tempdis);
        tvsteps.setText(mSteps);
        tvTime.setText(timeTaken);

        try {

            miSteps = Integer.parseInt(mSteps);
            miCalories = Double.parseDouble(mKcals);
            miDistance = Double.parseDouble(mDistance);
            // OutdoorResultModel outdoorResult = new OutdoorResultModel(userId, Constants.timeStamp, miSteps, miCalories, miDistance, avarageHeartRate, proccessId, timeTaken, avaragePace);

        } catch (Exception e) {
            e.printStackTrace();
        }


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    private void shareSocial() {


        String sharetext =
                "Pulse Helath"
                        + "\n \n Join Pulse Health for your wellness."
                        + "\n Download now Pulse Health : https://play.google.com/store/apps/details?id=com.algebratech.pulse_wellness ";

        back.setVisibility(View.GONE);
        Bitmap bitmap = ScreenShott.getInstance().takeScreenShotOfView(resultView);
        back.setVisibility(View.VISIBLE);
        File outputFile = DirManager.generateFile(MessageType.SHARE_SOCIAL);

        BitmapUtils.convertBitmapToJpeg(bitmap, outputFile);

        String path = outputFile.getPath();
        Log.d(Constants.TAG + "ScreenShot:", path);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TITLE, "Pulse Health App");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Join Pulse Community");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
        shareIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
        shareIntent.setType("*/*");
        startActivity(Intent.createChooser(shareIntent, "Share Pulse Activity"));

    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

}
