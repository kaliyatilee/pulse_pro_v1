package com.algebratech.pulse_wellness.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.models.SearchResult;

import java.util.List;

public class ScanWearableAdapter extends RecyclerView.Adapter {

    private final Context context;
    private final List<SearchResult> devices;
    private onItemClickListener listener;

    public ScanWearableAdapter(Context context, List<SearchResult> devices) {
        this.context = context;
        this.devices = devices;
    }

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DeviceHolder(LayoutInflater.from(context).inflate(R.layout.scan_wearable_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        SearchResult device = devices.get(i);
        ((DeviceHolder) viewHolder).tv_name.setText(device.getName());
        ((DeviceHolder) viewHolder).tv_mac.setText(device.getAddress());
        ((DeviceHolder) viewHolder).tv_rrsi.setText(device.rssi + "");
        ((DeviceHolder) viewHolder).rl_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onItemClick(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == devices ? 0 : devices.size();
    }

    private class DeviceHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout rl_device;
        private final TextView tv_name;
        private final TextView tv_mac;
        private final TextView tv_rrsi;

        public DeviceHolder(@NonNull View itemView) {
            super(itemView);
            rl_device = itemView.findViewById(R.id.rl_device);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_mac = itemView.findViewById(R.id.tv_mac);
            tv_rrsi = itemView.findViewById(R.id.tv_rrsi);
        }
    }

    public interface onItemClickListener {
        void onItemClick(int pos);
    }
}
