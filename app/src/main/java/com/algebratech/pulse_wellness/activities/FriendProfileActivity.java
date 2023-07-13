package com.algebratech.pulse_wellness.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.adapters.allActivityAdapter;
import com.algebratech.pulse_wellness.models.allActivityModel;
//this screen is not usefull we are not using it
public class FriendProfileActivity extends AppCompatActivity {
    private Toolbar toolbarPolicy;
    private RecyclerView recyclerView;
    private Button addFriend;
    String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        recyclerView = findViewById(R.id.recycle_activity_data);
        addFriend = findViewById(R.id.addFriend);
        setSupportActionBar(toolbarPolicy);
        setTitle("Friend Profile");
        id = getIntent().getStringExtra("id");
        Log.e("ID",id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        allActivityModel[] allActivityModels = new allActivityModel[]
                {
                        new allActivityModel("11-12 pm", "90", "3000", "55", "Running"),
                        new allActivityModel("1-2 pm", "20", "1000", "100", "Walking"),
                };

        RecyclerView recyclerView = findViewById(R.id.recycle_activity_data);

        allActivityAdapter adapter = new allActivityAdapter(allActivityModels);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Friend request sent", Toast.LENGTH_SHORT).show();
            }
        });

    }
}