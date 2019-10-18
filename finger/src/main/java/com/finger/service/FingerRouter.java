package com.finger.service;

import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.callBack.FingerDevCloseListener;
import com.baselibrary.callBack.FingerDevOpenListener;
import com.baselibrary.callBack.FingerDevStatusConnectListener;
import com.baselibrary.callBack.FingerVerifyResultListener;
import com.baselibrary.callBack.OnGetVerifyFingerImgListener;
import com.baselibrary.callBack.OnStartServiceListener;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.service.fingerService.FingerRouterService;
import com.finger.callBack.FingerDevStatusCallBack;
import com.finger.fingerApi.FingerApi;
import com.orhanobut.logger.Logger;
import com.sd.tgfinger.CallBack.DevCloseCallBack;
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.sd.tgfinger.pojos.Msg;

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
    public void openFingerDev(Activity activity, Boolean isSound,
                              FingerDevOpenListener fingerDevOpenListener,
                              FingerDevStatusConnectListener listener) {
        FingerApi.getInstance().openDev(activity, isSound, new DevOpenCallBack() {
            @Override
            public void devOpenResult(Msg msg) {
                fingerDevOpenListener.fingerDevOpenListener(msg.getResult(), msg.getTip());
            }
        }, new DevStatusCallBack() {
            @Override
            public void devStatus(Msg msg) {
                listener.fingerDevStatusConnect(msg.getResult(), msg.getTip());
            }
        });
    }

    @Override
    public void closeFingerDev(Activity activity, FingerDevCloseListener fingerDevCloseListener) {
        FingerApi.getInstance().closeDev(activity, new DevCloseCallBack() {
            @Override
            public void devCloseResult(Msg msg) {

            }
        });
    }

    @Override
    public void fingerDevConnectStatus(FingerDevStatusConnectListener listener) {
        FingerApi.getInstance().receiveFingerDevConnectStatus(new FingerDevStatusCallBack() {
            @Override
            public void fingerDevStatus(int res, String msg) {
                listener.fingerDevStatusConnect(res, msg);
            }
        });
    }

    @Override
    public void setFingerVerifyResultListener(FingerVerifyResultListener fingerVerifyResultListener) {
        FingerServiceUtil.getInstance().setFingerVerifyResult(fingerVerifyResultListener);
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
