package com.mywifi.adapter;

import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baselibrary.util.glidUtils.GlideUtil;
import com.mywifi.R;
import com.mywifi.util.WifiUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 展示Wifi列表的Adapter
 */
public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.Holder> {
    private List<ScanResult> data;

    public WifiAdapter() {
        data = new ArrayList<>();
    }

    private WifiItemClick click;

    public void setClick(WifiItemClick wifiItemClick) {
        this.click = wifiItemClick;
    }

    public void addData(List<ScanResult> newData) {
        if (newData != null && newData.size() > 0) {
            data.addAll(newData);
            notifyDataSetChanged();
        }
    }

    public void clearData() {
        if (data.size() > 0) {
            data.clear();
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.wf_wifi_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        ScanResult scanResult = data.get(i);
        holder.wifiSSID.setText(scanResult.SSID);
        holder.wifiMgmt.setText(WifiUtil.getWifiCapability(scanResult) ? "加密" : "未加密");
        GlideUtil.loadImage(holder.itemView.getContext(), R.drawable.icon_wifi_white_gray,
                R.drawable.icon_wifi_light_gray, holder.wifiLever);
        holder.itemView.setOnClickListener(v -> {
            if (click != null)
                click.clickItem(scanResult);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private final AppCompatTextView wifiSSID;
        private final AppCompatTextView wifiMgmt;
        private final AppCompatImageView wifiLever;

        Holder(@NonNull View itemView) {
            super(itemView);
            wifiSSID = itemView.findViewById(R.id.wifiSSID);
            wifiMgmt = itemView.findViewById(R.id.wifiMgmt);
            wifiLever = itemView.findViewById(R.id.wifiLever);
        }
    }

    public interface WifiItemClick {
        void clickItem(ScanResult scanResult);
    }
}
