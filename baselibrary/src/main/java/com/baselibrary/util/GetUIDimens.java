package com.baselibrary.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * Created by **
 * on 2018/9/6.
 * <p>
 * 测量View的大小的所有方法都放在这个类里边以及pa sp dp转换
 */

public class GetUIDimens {

    //全屏并且隐藏状态栏
    public static void hideStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }

    //全屏并且状态栏透明显示
    public static void showStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }

    //获取手机状态栏高度
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    private int sMeasuredHeight;

    //获取状态栏的高度
    public static int getStatusH(Context context) {
        int result = 0;
        //根据resource的Id 获取
        int resultId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resultId > 0) {
            result = context.getResources().getDimensionPixelSize(resultId);
        }
        Log.d("==TAG", "   方法二获取的状态栏的高度：h " + result);
        return result;
    }

    public static void fitsSystemWindows(boolean isTranslucentStatus, View view) {
        if (isTranslucentStatus) {
            view.getLayoutParams().height = calcStatusBarHeight(view.getContext());
        }
    }

    //获取状态栏的高度
    public static int calcStatusBarHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    //获取屏幕的尺寸
    public static int getWindowH(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int heightPixels = metrics.heightPixels;
        return heightPixels;
    }

    //获取屏幕的尺寸

    public static int getWindowW(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels;
        return widthPixels;
    }


    public static int dpToPx(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    public static int spToPx(Context context, float sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scaledDensity + 0.5);
    }

    public static int pxToDp(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5);
    }

    public static int pxToSp(Context context, int px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / scaledDensity + 0.5);
    }


}
