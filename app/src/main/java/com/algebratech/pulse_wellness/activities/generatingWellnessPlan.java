package com.algebratech.pulse_wellness.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.algebratech.pulse_wellness.R;

public class generatingWellnessPlan extends AppCompatActivity {
    private ProgressBar progressBar;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating_wellness_plan);

        progressBar = findViewById(R.id.progress_bar);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (i <= 100) {
                    progressBar.setProgress(i);
                    i++;
                    handler.postDelayed(this, 70);
                } else {
                    handler.removeCallbacks(this);
                    startActivity(new Intent(getApplicationContext(), ProfileCreatedView.class));
                    finish();
                }
            }
        }, 200);
    }
}
