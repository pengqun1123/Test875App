package com.finger.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.finger.fingerApi.FingerApi;
import com.orhanobut.logger.Logger;
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.pojos.Msg;
import com.sd.tgfinger.tgApi.Constant;

public class FingerService extends Service {

    public static final String ACTION = "com.finger.FingerService.action";
    public static final String CATEGORY = "android.intent.category.DEFAULT";


    public FingerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.d("fingerService 的 onBind ");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("fingerService 的 onCreate ");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d("fingerService 的 onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("fingerService 的 onDestroy");
    }



}
