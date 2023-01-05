package com.algebratech.pulse_wellness.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.interfaces.OnRecycleViewClickCallback;
import com.bumptech.glide.Glide;
import com.inuker.bluetooth.library.search.SearchResult;
import com.algebratech.pulse_wellness.R;

import java.util.List;


/**
 * Created by timaimee on 2016/7/25.
 */
public class BleScanViewAdapter extends RecyclerView.Adapter<BleScanViewAdapter.NormalTextViewHolder> {
    private final LayoutInflater mLayoutInflater;
    List<SearchResult> itemData;
    OnRecycleViewClickCallback mBleCallback;
    Context mContext;

    public BleScanViewAdapter(Context context, List<SearchResult> data) {
        this.mContext = context;
        this.itemData = data;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_main, parent, false));
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, int position) {
        if (itemData.get(position).getName().equals("V19")){
            holder.mBleRssi.setText("Pulse Spirit");
            holder.device.setText("( "+ itemData.get(position).getAddress()+" )");
            Glide.with(mContext).load(R.drawable.watchimage).error(R.drawable.watchimage).into(holder.deviceImg);
        }
        if (itemData.get(position).getName().equals("GT2")){
            holder.mBleRssi.setText("Pulse Force");
            holder.device.setText("( "+ itemData.get(position).getAddress()+" )");
            Glide.with(mContext).load(R.drawable.pulse_force).error(R.drawable.pulse_force).into(holder.deviceImg);
        }
        if (itemData.get(position).getName().equals("Pulse F")){
            holder.mBleRssi.setText("Pulse Force");
            holder.device.setText("( "+ itemData.get(position).getAddress()+" )");
            Glide.with(mContext).load(R.drawable.pulse_force).error(R.drawable.pulse_force).into(holder.deviceImg);
        }
        if (itemData.get(position).getName().equals("Pulse S")){
            holder.mBleRssi.setText("Pulse Spirit");
            holder.device.setText("( "+ itemData.get(position).getAddress()+" )");
            Glide.with(mContext).load(R.drawable.watchimage).error(R.drawable.watchimage).into(holder.deviceImg);
        }


    }


    @Override
    public int getItemCount() {
        return itemData == null ? 0 : itemData.size();
    }


    public void setBleItemOnclick(OnRecycleViewClickCallback bleCallback) {
        this.mBleCallback = bleCallback;
    }


    public class NormalTextViewHolder extends RecyclerView.ViewHolder {

        TextView mBleRssi,device;
        ImageView deviceImg;


        NormalTextViewHolder(View view) {
            super(view);
            mBleRssi = (TextView) view.findViewById(R.id.tv);
            device = (TextView) view.findViewById(R.id.device);
            deviceImg = (ImageView) view.findViewById(R.id.watch);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBleCallback.OnRecycleViewClick(getPosition());
                    Log.d("NormalTextViewHolder", "onClick--> position = " + getPosition());
                }
            });
        }
    }
}