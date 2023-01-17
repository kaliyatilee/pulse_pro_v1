package com.algebratech.pulse_wellness.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.DailyWalkReport;
import com.algebratech.pulse_wellness.activities.ShowRecordsActivity;

public class ShowDataFragment extends AppCompatActivity {

    private ScrollView layout;
    private CardView outdoorRunning, outdoorwalk, indoorRun, indoorWalk, Hiking, stairStepper, outdoorCycle, stationaryBike, treadmill, rowingMachine,dailyWalk;
    private Toolbar toolbarPolicy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_show_data);

        dailyWalk = findViewById(R.id.dailyWalk);
        outdoorRunning = findViewById(R.id.outdoorRunning);
        outdoorwalk = findViewById(R.id.outdoorwalk);
        indoorRun = findViewById(R.id.indoorRun);
        indoorWalk = findViewById(R.id.indoorWalk);
        Hiking = findViewById(R.id.Hiking);
        stairStepper = findViewById(R.id.stairStepper);
        outdoorCycle = findViewById(R.id.outdoorCycle);
        stationaryBike = findViewById(R.id.stationaryBike);
        treadmill = findViewById(R.id.treadmill);
        rowingMachine = findViewById(R.id.rowingMachine);
        toolbarPolicy = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarPolicy);
        setTitle("Activities");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intent = new Intent(ShowDataFragment.this, ShowRecordsActivity.class);

        dailyWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowDataFragment.this, DailyWalkReport.class);
                intent.putExtra("title", "Daily Walking");
                intent.putExtra("type", "walking");
                startActivity(intent);
            }
        });

        outdoorRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent.putExtra("title", "Outdoor Running");
                intent.putExtra("type", "Outdoor Run");
                startActivity(intent);

            }
        });

        outdoorwalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent.putExtra("title", "Outdoor Walking");
                intent.putExtra("type", "Outdoor Walk");
                startActivity(intent);

            }
        });

        indoorRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent.putExtra("title", "Indoor Running");
                intent.putExtra("type", "Indoor Run");
                startActivity(intent);

            }
        });

        indoorWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent.putExtra("title", "Indoor Walking");
                intent.putExtra("type", "Indoor Walking");
                startActivity(intent);

            }
        });

        Hiking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent.putExtra("title", "Hiking");
                intent.putExtra("type", "Hiking");
                startActivity(intent);

            }
        });

        outdoorCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent.putExtra("title", "Outdoor Cycling");
                intent.putExtra("type", "Outdoor Cycling");
                startActivity(intent);

            }
        });

        stationaryBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent.putExtra("title", "Stationary Bike");
                intent.putExtra("type", "Stationary Bike");
                startActivity(intent);

            }
        });

        treadmill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent.putExtra("title", "Treadmill");
                intent.putExtra("type", "Treadmill");
                startActivity(intent);

            }
        });

        rowingMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent.putExtra("title", "Rowing Machine");
                intent.putExtra("type", "Rowing Machine");
                startActivity(intent);

            }
        });

        stairStepper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("title", "Stair Stepper");
                intent.putExtra("type", "Stair Stepper");
                startActivity(intent);

            }
        });


    }
}