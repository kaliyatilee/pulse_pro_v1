package com.algebratech.pulse_wellness.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.algebratech.pulse_wellness.R;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFeedDetailActivity extends AppCompatActivity {

    private TextView username;
    private CircleImageView profile;
    private ImageView image;
    private TextView post, date;


    private RelativeLayout mBottomSheetLayout;
    private BottomSheetBehavior sheetBehavior;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_news_feed_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        String usrname = getIntent().getStringExtra("username");
        String ppic = getIntent().getStringExtra("profile");
        String postdata = getIntent().getStringExtra("post");
        String type = getIntent().getStringExtra("type");
        String data = getIntent().getStringExtra("data");
        String headerdata = getIntent().getStringExtra("headline");
        String datee = getIntent().getStringExtra("date");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        Date d = null;
        try {
            d = sdf.parse(datee);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        username = findViewById(R.id.username);
        profile = findViewById(R.id.profilePic);
        image = findViewById(R.id.image);
        post = findViewById(R.id.post);
        date = findViewById(R.id.date);

        username.setText(usrname);
        Glide.with(getApplicationContext()).load(ppic).error(R.drawable.placeholder).into(profile);
        if (!data.isEmpty())
            Glide.with(getApplicationContext()).load(data).into(image);
        post.setText(postdata);
        date.setText((df.format("dd MMM yyyy h:mm a", d)));


        mBottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);


        mBottomSheetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
