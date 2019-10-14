package com.finger.service;

import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.callBack.OnGetVerifyFingerImgListener;
import com.baselibrary.callBack.OnStartServiceListener;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.service.fingerService.FingerRouterService;
import com.finger.fingerApi.FingerApi;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * Created By pq
 * on 2019/10/14
 */
@Route(path = "/finger/fingerRouter")
public class FingerRouter implements FingerRouterService {

    @Override
    public void init(Context context) {
        Logger.d(" FingerRouter的init方法 ");
    }

    @Override
    public void startFingerService(Activity activity, OnStartServiceListener startServiceListener) {
        FingerServiceUtil.getInstance().startFingerService(activity, startServiceListener);
    }

    @Override
    public void unbindDevService(Context context) {
        FingerServiceUtil.getInstance().unbindDevService(context);
    }

    @Override
    public void verifyGetFingerImg(OnGetVerifyFingerImgListener listener) {
        FingerApi.getInstance().verifyGetFingerImg(listener);
    }

    @Override
    public void setFingerData(ArrayList<Finger6> fingerList) {
        FingerServiceUtil.getInstance().setFingerData(fingerList);
    }

    @Override
    public void pauseFingerVerify() {
        FingerServiceUtil.getInstance().pauseFingerVerify();
    }

    @Override
    public void reStartFingerVerify() {
        FingerServiceUtil.getInstance().reStartFingerVerify();
    }

    @Override
    public void addFinger(byte[] newFinger) {
        FingerServiceUtil.getInstance().addFinger(newFinger);
    }

    @Override
    public void deleteFinger(int position) {
        FingerServiceUtil.getInstance().deleteFinger(position);
    }


}
