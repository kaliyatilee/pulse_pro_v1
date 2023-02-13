package com.algebratech.pulse_wellness.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.algebratech.pulse_wellness.Helper;
import com.algebratech.pulse_wellness.R;

public class WeightMonitoring extends AppCompatActivity {
    Button recordWeight;
    Dialog dialog;
    TextView weight,dateRecorded;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_monitoring);
        dialog = new Dialog(WeightMonitoring.this);
        weight = findViewById(R.id.weight);
        dateRecorded = findViewById(R.id.dateRecorded);
        recordWeight = findViewById(R.id.recordWeight);
        recordWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.number_picker_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                EditText etWeight = (EditText) dialog.findViewById(R.id.etWeight);
                Button saveButton = dialog.findViewById(R.id.saveButton);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String wt = etWeight.getText().toString();
                        if (wt.isEmpty()){
                            etWeight.setError("Required");
                            return;
                        }
                        weight.setText(wt);
                        dateRecorded.setText(Helper.ConvertTimestamp(Helper.timestamp));
                    }
                });

                dialog.show();
            }

        });

    }
}