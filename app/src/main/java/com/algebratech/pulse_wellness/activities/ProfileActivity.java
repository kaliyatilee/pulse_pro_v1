package com.algebratech.pulse_wellness.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.StaticMethods;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView name,points,sub_type;
    private CircleImageView circleImageView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor myEdit;
    String user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile2);

        sharedPreferences  = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        user_id = sharedPreferences.getString("userID",null);

        initToolbar();
        init();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_back);
    }

    private void init(){

        name = findViewById(R.id.name);
        points = findViewById(R.id.points);
        sub_type = findViewById(R.id.sub_type);
        circleImageView = findViewById(R.id.profilePic);
        setData();
    }

    private void setData() {

        name.setText(sharedPreferences.getString("fullname",null));
        Glide.with(getApplicationContext()).load(sharedPreferences.getString("profileURL",null)).error(R.drawable.placeholder).into(circleImageView);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
