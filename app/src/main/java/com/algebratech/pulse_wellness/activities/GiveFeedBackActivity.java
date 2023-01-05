package com.algebratech.pulse_wellness.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;

public class GiveFeedBackActivity extends AppCompatActivity {

    private Toolbar toolbarPolicy;
    private WebView privacy_policy;
    private TextView no_data;
    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        no_data = findViewById(R.id.no_data);
        setSupportActionBar(toolbarPolicy);
        handler = new Handler();
        setTitle("Give Us Feedback");
        CM.showProgressLoader(GiveFeedBackActivity.this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        privacy_policy = findViewById(R.id.privacy_policy);

        privacy_policy.getSettings().setJavaScriptEnabled(true);
        if(CM.isConnected(GiveFeedBackActivity.this))
        {
            no_data.setVisibility(View.GONE);
            privacy_policy.setVisibility(View.VISIBLE);
            privacy_policy.loadUrl(Constants.FEEDBACK_URL);
        }
        else
            Toast.makeText(GiveFeedBackActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                CM.HideProgressLoader();

            }
        }, 500);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
