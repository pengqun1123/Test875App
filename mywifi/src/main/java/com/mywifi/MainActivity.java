package com.mywifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.baselibrary.dialog.UIDialog;
import com.baselibrary.util.VerifyResultUi;
import com.mywifi.adapter.WifiAdapter;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.reflect.Method;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Wifi开发的博客
 * https://blog.csdn.net/zhangbijun1230/article/details/83006734
 */
public class MainActivity extends AppCompatActivity implements WifiAdapter.WifiItemClick {

    private WifiManager wifiManager;
    private LocalBroadcastManager localBroadcastManager;
    private WifiConfiguration wifiConfig;
    private WifiAdapter wifiAdapter;
    private Dialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wf_activity_main);

        RecyclerView wifiRv = findViewById(R.id.wifiRv);
        setRv(wifiRv);

        //注册接收Wifi的广播
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(br, intentFilter);
//        localBroadcastManager.registerReceiver(br, intentFilter);

        checkPermission();

        if (wifiManager == null)
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        localBroadcastManager.unregisterReceiver(br);
        unregisterReceiver(br);
    }

    private static String[] per = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @SuppressLint("CheckResult")
    private void checkPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(per)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (!permission.granted || !permission.name.equals(per[0])) {
                            return;
                        }
                    }
                });
    }

    private void setRv(RecyclerView rv) {
        LinearLayoutManager manager = new LinearLayoutManager(this,
                OrientationHelper.VERTICAL, false);
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rv.setLayoutManager(manager);
        wifiAdapter = new WifiAdapter();
        wifiAdapter.setClick(this);
        rv.setAdapter(wifiAdapter);
    }

    /**
     * 搜索Wifi列表
     *
     * @param view view
     */
    public void findWifiList(View view) {
        waitDialog = UIDialog.showWaitDialog(this, "搜索中...");
        searchWifi();
    }

    /**
     * 开启Wifi
     */
    public void openMyWifi(View view) {
        openWifi();
    }

    /**
     * 关闭Wifi
     */
    public void closeMyWifi(View view) {
        closeWifi();
    }

    //热点的名称
    private static final String WIFI_HOT_PORT_SSID = "MY_WIFI_HOT_PORT";
    //热点的密码
    private static final String WIFI_HOT_PWD = "12345678";

    /**
     * 创建Wifi热点
     */
    public void createWifiHotPort() {
        if (wifiManager.isWifiEnabled()) {
            //如果wifi处于打开状态，则先关闭wifi
            wifiManager.setWifiEnabled(false);
        }
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = WIFI_HOT_PORT_SSID;
        wifiConfiguration.preSharedKey = WIFI_HOT_PWD;
        wifiConfiguration.hiddenSSID = true;
        wifiConfiguration.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.OPEN);//开放系统认证
        wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        //WIFI的加密方式
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfiguration.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfiguration.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
        try {
            Method method = wifiManager.getClass().
                    getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            Boolean b = (Boolean) method.invoke(wifiManager, wifiConfiguration, true);
            if (b) {
                Logger.d("热点已开启 SSID:" + WIFI_HOT_PORT_SSID + "   密码:" + WIFI_HOT_PWD);
            } else {
                Logger.d("热点创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.d("热点创建失败:" + e.getMessage());
        }
    }

    /**
     * 关闭Wifi热点
     */
    public void closeWifiHotPort() {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration wifiConfiguration = (WifiConfiguration) method.invoke(wifiManager);
            Method method1 = wifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, Boolean.class);
            method1.invoke(wifiManager, wifiConfiguration, false);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.d("关闭WiFi热点失败");
        }
    }

    /**
     * 打开wifi
     */
    public void openWifi() {
        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
    }

    /**
     * 关闭Wifi
     */
    public void closeWifi() {
        if (wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
    }

    /**
     * 搜索Wifi
     */
    public void searchWifi() {
        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
        wifiManager.startScan();
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UIDialog.disMiss(waitDialog);
            String action = intent.getAction();
            if (action != null && action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                if (scanResults != null && scanResults.size() > 0) {
                    Logger.d("搜索到的Wifi结果:" + scanResults.size());
                    wifiAdapter.clearData();
                    wifiAdapter.addData(scanResults);
                }
            } else if (action != null && action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    VerifyResultUi.showTvToast(MainActivity.this, "连接已断开");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    VerifyResultUi.showTvToast(MainActivity.this, "已连接到网络:" + wifiInfo.getSSID());
                }
            } else {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    NetworkInfo.DetailedState state = info.getDetailedState();
                    if (state == NetworkInfo.DetailedState.CONNECTING) {
                        VerifyResultUi.showTvToast(MainActivity.this, "连接中...");
                    } else if (state == NetworkInfo.DetailedState.AUTHENTICATING) {
                        VerifyResultUi.showTvToast(MainActivity.this, "正在验证身份信息...");
                    } else if (state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                        VerifyResultUi.showTvToast(MainActivity.this, "正在获取IP地址...");
                    } else if (state == NetworkInfo.DetailedState.FAILED) {
                        VerifyResultUi.showTvToast(MainActivity.this, "连接失败");
                    }
                }
            }
        }
    };

    private WifiPassType type;

    /**
     * Wifi的连接
     */
    public void wifiConnect(ScanResult scanResult) {
        wifiManager.disconnect();
        String capabilities = scanResult.capabilities;
        if (!TextUtils.isEmpty(capabilities)) {
            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                type = WifiPassType.WIFI_CIPHER_WPA;
            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                type = WifiPassType.WIFI_CIPHER_WEP;
            } else {
                type = WifiPassType.WIFICIPHER_NOPASS;
            }
        }
        wifiConfig = isExsits(scanResult.SSID);
        if (wifiConfig == null) {
            if (type != WifiPassType.WIFICIPHER_NOPASS) {
                //需要密码
                final EditText editText = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("请输入Wifi密码").setIcon(
                        android.R.drawable.ic_dialog_info).setView(
                        editText).setPositiveButton("确定", (dialog, which) -> {
                    Logger.d("AAA", "editText.getText():" + editText.getText());
                    if (type == WifiPassType.WIFI_CIPHER_WEP) {
                        wifiConfig = createWifiInfo(scanResult.SSID,
                                editText.getText().toString(), WifiPassType.WIFI_CIPHER_WEP);
                    } else if (type == WifiPassType.WIFI_CIPHER_WPA) {
                        wifiConfig = createWifiInfo(scanResult.SSID,
                                editText.getText().toString(), WifiPassType.WIFI_CIPHER_WPA);
                    }
                    connect(wifiConfig);
                })
                        .setNegativeButton("取消", null).show();
            } else {
                //不需要密码
                wifiConfig = createWifiInfo(scanResult.SSID, "", type);
                connect(wifiConfig);
            }
        } else {
            connect(wifiConfig);
        }
    }

    private void connect(WifiConfiguration config) {
        waitDialog = UIDialog.showWaitDialog(MainActivity.this, "连接中...");
        int wcgID = wifiManager.addNetwork(config);
        wifiManager.enableNetwork(wcgID, true);
    }


    /**
     * 判断系统是否保存有当前WIFI的信息
     *
     * @param SSID Wifi的名称
     * @return WifiConfiguration
     */
    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    /**
     * 创建WifiConfiguration对象
     *
     * @param SSID     wifi名称
     * @param password WiFi密码
     * @param type     wifi加密类型
     * @return WifiConfiguration对象
     */
    public WifiConfiguration createWifiInfo(String SSID, String password, WifiPassType type) {
        Logger.d("SSID = " + SSID + "## Password = " + password + "## Type = " + type);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        WifiConfiguration tempConfig = this.isExsits(SSID);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }
        // 分为三种情况：1没有密码2用wep加密3用wpa加密
        if (type == WifiPassType.WIFICIPHER_NOPASS) {// WIFICIPHER_NOPASS
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WifiPassType.WIFI_CIPHER_WEP) {  //  WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WifiPassType.WIFI_CIPHER_WPA) {   // WIFICIPHER_WPA
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }


    @Override
    public void clickItem(ScanResult scanResult) {
        Logger.d("wifi  SSID：" + scanResult.SSID);

        wifiConnect(scanResult);
    }
}
