package com.baselibrary.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.baselibrary.R;
import com.baselibrary.base.BaseApplication;

import java.util.Objects;

/**
 * Created By pq
 * on 2019/10/19
 * 验证的结果展示
 */
public class VerifyResultUi {

    public static void showVerifyFail(@NonNull Activity activity, String tip, boolean isSound) {
        ToastUtils.showSquareImgToast(activity,
                tip, ActivityCompat.getDrawable(activity, R.drawable.ic_error));
        if (isSound)
            BaseApplication.AP.play_verifyFail();
    }

    public static void showVerifySuccess(@NonNull Activity activity, String tip, boolean isSound) {
        ToastUtils.showSquareImgToast(activity, tip,
                ActivityCompat.getDrawable(activity, R.drawable.ic_tick));
        if (isSound)
            BaseApplication.AP.play_verifySuccess();
    }

    public static void showRegisterFail(@NonNull Activity activity, String tip, boolean isSound) {
        if (isSound)
            BaseApplication.AP.play_checkInFail();
        ToastUtils.showSquareImgToast(activity, tip,
                ActivityCompat.getDrawable(activity, R.drawable.ic_error));
    }

    public static void showRegisterSuccess(@NonNull Activity activity, String tip, boolean isSound) {
        if (isSound)
            BaseApplication.AP.play_checkInSuccess();
        ToastUtils.showSquareImgToast(activity, tip,
                ActivityCompat.getDrawable(activity, R.drawable.ic_tick));
    }

    public static void showTvToast(@NonNull Activity activity,String tip){
        ToastUtils.showSingleToast(activity,tip);
    }
    public static void showTvLongToast(@NonNull Activity activity,String tip){
        ToastUtils.showSingleToast(activity,tip);
    }
}
