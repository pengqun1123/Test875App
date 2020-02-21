package com.mywifi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.mywifi.MainActivity;
import com.mywifi.R;
import com.mywifi.util.WifiAdmin;
import com.mywifi.util.WifiApAdmin;

public class Main2Activity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private Button mBtn1, mBtn2;

    private WifiAdmin mWifiAdmin;

    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.wf_activity_main2);
        mBtn1 = findViewById(R.id.button1);
        mBtn2 = findViewById(R.id.button2);
        mBtn1.setText("点击连接Wifi");
        mBtn2.setText("点击创建Wifi热点");
        mBtn1.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mWifiAdmin = new WifiAdmin(mContext) {

                    @Override
                    public void myUnregisterReceiver(BroadcastReceiver receiver) {
                        Main2Activity.this.unregisterReceiver(receiver);
                    }

                    @Override
                    public Intent myRegisterReceiver(BroadcastReceiver receiver,
                                                     IntentFilter filter) {
                        Main2Activity.this.registerReceiver(receiver, filter);
                        return null;
                    }

                    @Override
                    public void onNotifyWifiConnected() {
                        Log.v(TAG, "have connected success!");
                        Log.v(TAG, "###############################");
                    }

                    @Override
                    public void onNotifyWifiConnectFailed() {
                        Log.v(TAG, "have connected failed!");
                        Log.v(TAG, "###############################");
                    }
                };
                mWifiAdmin.openWifi();
                mWifiAdmin.addNetwork(mWifiAdmin.createWifiInfo("SDKJ_4G",
                        "kjsdyscl604", WifiAdmin.TYPE_WPA));

            }
        });

        mBtn2.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                WifiApAdmin wifiAp = new WifiApAdmin(mContext);
                wifiAp.startWifiAp("\"HotSpot\"", "hhhhhh123");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(android.R.menu.wf_activity_main2, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("Rssi", "Registered");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("Rssi", "Unregistered");
    }

}
