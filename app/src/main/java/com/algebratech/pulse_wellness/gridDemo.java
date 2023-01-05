package com.algebratech.pulse_wellness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;

import com.algebratech.pulse_wellness.adapters.gridActivityAdapter;
import com.algebratech.pulse_wellness.models.gridActivityModel;

import java.util.ArrayList;

public class gridDemo extends AppCompatActivity {
    GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_demo);

        gridView = findViewById(R.id.gridView);

        ArrayList<gridActivityModel> courseModelArrayList = new ArrayList<gridActivityModel>();
        courseModelArrayList.add(new gridActivityModel("Outdoor Run", R.drawable.runningg));
        courseModelArrayList.add(new gridActivityModel("Outdoor Walk",  R.drawable.runningg));
        courseModelArrayList.add(new gridActivityModel("Indoor Run",  R.drawable.runningg));
        courseModelArrayList.add(new gridActivityModel("Indoor Walk",  R.drawable.runningg));
        courseModelArrayList.add(new gridActivityModel("Hiking", R.drawable.runningg));
        courseModelArrayList.add(new gridActivityModel("Stair Stepper", R.drawable.runningg));
        courseModelArrayList.add(new gridActivityModel("Outdoor Cycle", R.drawable.bicyclee));
        courseModelArrayList.add(new gridActivityModel("Stationary Bike",R.drawable.treadmill));
        courseModelArrayList.add(new gridActivityModel("Eliplical", R.drawable.treadmill));
        courseModelArrayList.add(new gridActivityModel("Rowing Machine",R.drawable.treadmill));

        gridActivityAdapter adapter = new gridActivityAdapter(this, courseModelArrayList);
        gridView.setAdapter(adapter);
    }
}