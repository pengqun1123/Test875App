package com.mywifi.util;

import android.net.wifi.ScanResult;

public class WifiUtil {

    /**
     * Wifi信号的强度
     *
     * @param scanResult scanResult
     * @return
     */
    public static String getWifiLever(ScanResult scanResult) {
        int level = scanResult.level;
        if (level <= 0 && level >= -50) {
            return "信号很好";
        } else if (level < -50 && level >= -70) {
            return "信号较好";
        } else if (level < -70 && level >= -80) {
            return "信号一般";
        } else if (level < -80 && level >= -100) {
            return "信号较差";
        } else {
            return "信号很差";
        }
    }

    /**
     * wifi是否加密
     */
    public static boolean getWifiCapability(ScanResult scanResult) {
        String capabilities = scanResult.capabilities;
        if (capabilities.contains("WPA") || capabilities.contains("wpa")
                || capabilities.contains("wep") || capabilities.contains("WEP")) {
            return true;
        } else {
            return false;
        }
    }

}
