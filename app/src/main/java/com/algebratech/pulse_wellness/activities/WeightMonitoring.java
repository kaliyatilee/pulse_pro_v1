package com.algebratech.pulse_wellness.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.algebratech.pulse_wellness.Helper;
import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.StaticMethods;
import com.algebratech.pulse_wellness.db.DBHelper;
import com.algebratech.pulse_wellness.models.WeightMonitoringModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class WeightMonitoring extends AppCompatActivity {
    Button recordWeight, targetWeightWeight;
    Dialog dialog;
    TextView targetWeight,dateRecorded;
    private DBHelper db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_monitoring);
        dialog = new Dialog(WeightMonitoring.this);
        recordWeight = findViewById(R.id.recordWeight);
        targetWeightWeight = findViewById(R.id.targetWeightWeight);
        targetWeight = findViewById(R.id.targetWeight);
        targetWeight.setVisibility(View.GONE);
        targetWeightWeight.setVisibility(View.GONE);
        BarChart barChart = findViewById(R.id.barChart);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Jan", "Feb", "Mar"}));

        YAxis yAxis = barChart.getAxisLeft();

        yAxis.setDrawGridLines(true);
        yAxis.setAxisMinimum(30);
        barChart.getAxisRight().setEnabled(false);
        ArrayList<BarEntry> entries = new ArrayList<>();
        db = new DBHelper(getApplicationContext());
        ArrayList<WeightMonitoringModel> weightMonitoringModel = db.getWeightMonitoringRecord();
        for(int i = 0; i <= weightMonitoringModel.size() -1; i++){
            entries.add(new BarEntry(i,weightMonitoringModel.get(i).getWeight()));
        }

        WeightMonitoringModel weightMonitoringModel1 = db.getTargetWeight();
        try {
            targetWeight.setText(""+weightMonitoringModel1.getWeight());
        }
        catch (Exception e){

        }


        BarDataSet dataSet = new BarDataSet(entries, "Weight Distribution");
        BarData barData = new BarData(dataSet);
        //barData.setBarWidth(10);
        barChart.setData(barData);
        barChart.invalidate();

        targetWeightWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.number_picker_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);

                EditText etWeight = dialog.findViewById(R.id.etWeight);
                Button saveButton = dialog.findViewById(R.id.saveButton);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String wt = etWeight.getText().toString();
                        if (wt.isEmpty()){
                            etWeight.setError("Required");
                            return;
                        }
                        try {
                            db.setOrUpdateTargetWeight(StaticMethods.getCurrentTimeStamp(),Float.parseFloat(wt),Boolean.FALSE);
                        }
                        catch (Exception e){
                            Toast.makeText(WeightMonitoring.this, "Error Occuired", Toast.LENGTH_SHORT).show();

                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        recordWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.number_picker_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                EditText etWeight = dialog.findViewById(R.id.etWeight);
                Button saveButton = dialog.findViewById(R.id.saveButton);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String wt = etWeight.getText().toString();
                        if (wt.isEmpty()){
                            etWeight.setError("Required");
                            return;
                        }
                        try {
                            db.recordWeight(StaticMethods.getCurrentTimeStamp(),Float.parseFloat(wt),Boolean.FALSE);
                        }
                        catch (Exception e){
                            Toast.makeText(WeightMonitoring.this, "Error Occuired", Toast.LENGTH_SHORT).show();

                        }
                        dialog.dismiss();
                        Toast.makeText(WeightMonitoring.this, "Weight Recorded", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
            }

        });

    }
}