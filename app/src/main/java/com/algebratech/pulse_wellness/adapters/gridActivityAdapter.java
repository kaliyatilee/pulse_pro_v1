package com.algebratech.pulse_wellness.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.models.gridActivityModel;

import java.util.ArrayList;

public class gridActivityAdapter extends ArrayAdapter<gridActivityModel> {
    public gridActivityAdapter(@NonNull Context context, ArrayList<gridActivityModel> gridActivityModel) {
        super(context, 0, gridActivityModel);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.raw_activity_grid, parent, false);
        }
        gridActivityModel gridActivityModel = getItem(position);
        TextView courseTV = listitemView.findViewById(R.id.idTVCourse);
        ImageView courseIV = listitemView.findViewById(R.id.idIVcourse);
        courseTV.setText(gridActivityModel.getActivity_name());
        courseIV.setImageResource(gridActivityModel.getActivity_image());
        return listitemView;
    }

}
