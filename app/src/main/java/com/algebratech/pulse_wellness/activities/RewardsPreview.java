package com.algebratech.pulse_wellness.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.algebratech.pulse_wellness.R;
import com.bumptech.glide.Glide;

public class RewardsPreview extends AppCompatActivity {

    private Button btnRedeem;
    private TextView tvHeadline,tvDesc,tvPrice;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_rewards_preview);

        tvHeadline = findViewById(R.id.tvHeadline);
        tvDesc = findViewById(R.id.tvDesc);
        tvPrice = findViewById(R.id.tvPrice);
        btnRedeem = findViewById(R.id.btnRedeem);

        tvHeadline.setText(getIntent().getStringExtra("title"));
        tvDesc.setText(getIntent().getStringExtra("description"));
        tvPrice.setText(getIntent().getStringExtra("points"));
        Glide.with(getApplicationContext()).load(getIntent().getStringExtra("image")).into((ImageView) findViewById(R.id.imageView));
        btnRedeem.setText("Redeem Point ($"+getIntent().getStringExtra("points")+")");



    }
}
