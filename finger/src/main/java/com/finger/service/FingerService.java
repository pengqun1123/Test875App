package com.finger.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.pojo.Finger6;
import com.finger.callBack.FingerVerifyResult;
import com.finger.constant.FingerConstant;
import com.finger.fingerApi.FingerApi;
import com.finger.fingerApi.FingerApiUtil;
import com.orhanobut.logger.Logger;
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.api.TGAPI;
import com.sd.tgfinger.api.TGXG661API;
import com.sd.tgfinger.pojos.Msg;
import com.sd.tgfinger.service.DevService;
import com.sd.tgfinger.tgApi.Constant;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FingerService extends Service {

    public static final String ACTION = "com.finger.FingerService.action";
    public static final String CATEGORY = "android.intent.category.DEFAULT";

    private Timer timer;
    private MyTask myTask;

    public FingerService() {
    }

    private Messenger fingerUtilMessennger;
    private Activity activity;
    private Messenger messenger = new Messenger(new FingerServiceHandler());

    @SuppressLint("HandlerLeak")
    private class FingerServiceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == FingerConstant.SEND_CODE) {
                activity = (Activity) msg.obj;
                fingerUtilMessennger = msg.replyTo;
            }
//            if (msg.what == Constant.SEND_MESSAGE_CODE) {
//                tg661JMessennger = msg.replyTo;
//                tgxg661API = TGAPI.getTG661();
//                if (tgxg661API != null) {
//                    timer.schedule(myTask, 1000, 1000);
//                }
//            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.d("fingerService 的 onBind ");
        timer = new Timer();
        myTask = new MyTask();
        return messenger.getBinder();
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
        releaseTimerTask();
    }


    private void fingerVerifyN(@NonNull Activity activity, byte[] fingerData,
                               int fingerSize, FingerVerifyResult callBack) {
        FingerApiUtil.getInstance().verifyFinger(activity, fingerData, fingerSize, callBack);
    }

    private class MyTask extends TimerTask {

        @Override
        public void run() {

            //如果设备开启，获取设备当前的状态
//            int devStatus = tgxg661API.TGGetDevStatus();
//            Log.d("===TAG", "  DevService 获取设备状态  :" + devStatus);
//            Message devServiceMessage = new Message();
//            devServiceMessage.what = Constant.RECEIVE_MESSAGE_CODE;
//            Bundle bundle = new Bundle();
//            if (devStatus >= 0) {
//                //设备已经连接
//                bundle.putInt(Constant.STATUS, 0);
//            } else {
//                writeCMD();
//                //设备未连接
//                bundle.putInt(Constant.STATUS, -2);
//            }
//            if (tg661JMessennger != null) {
//                try {
//                    devServiceMessage.setData(bundle);
//                    tg661JMessennger.send(devServiceMessage);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                    Log.d("===TAG===", "  DevService 向客户端发送信息失败！ E:" + e.toString());
//                }
//            }
        }
    }

    private void releaseTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (myTask != null) {
            myTask.cancel();
            myTask = null;
        }
    }

    private List<Finger6> result;





}
