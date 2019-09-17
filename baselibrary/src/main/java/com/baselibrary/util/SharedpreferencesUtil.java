package com.baselibrary.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xuhao
 * on 2017/11/24.
 */

public class SharedpreferencesUtil {
    private static SharedpreferencesUtil defaultInstance;
    private static SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;

    public static SharedpreferencesUtil getInstance() {
        if (defaultInstance == null) {
            synchronized (SharedpreferencesUtil.class) {
                if (defaultInstance == null) {
                    defaultInstance = new SharedpreferencesUtil();
                }
            }
        }
        return defaultInstance;
    }

    @SuppressLint("CommitPrefEdits")
    public void initSharedUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public float getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void removeKey(String key) {
        editor.remove(key);
        editor.commit();
    }

}
