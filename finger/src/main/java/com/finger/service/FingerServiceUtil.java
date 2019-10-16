package com.finger.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.baselibrary.callBack.FingerVerifyResultListener;
import com.baselibrary.callBack.OnStartServiceListener;
import com.baselibrary.constant.AppConstant;
import com.baselibrary.pojo.Finger6;
import com.finger.constant.FingerConstant;

import java.util.ArrayList;

/**
 * Created By pq
 * on 2019/10/9
 */
public class FingerServiceUtil {

    private static class Holder {
        private static final FingerServiceUtil INSTANCE = new FingerServiceUtil();
    }

    public static FingerServiceUtil getInstance() {
        return Holder.INSTANCE;
    }

    private Activity activity;
    private ArrayList<Finger6> finger6ArrayList;
    private OnStartServiceListener startServiceListener;
    private FingerVerifyResultListener fingerVerifyResultListener;

    public void setFingerData(ArrayList<Finger6> fingerList/*byte[] fingerData, int fingerDataSize*/) {
        this.finger6ArrayList = fingerList;
    }

    public void setFingerVerifyResult(FingerVerifyResultListener verifyResultListener) {
        this.fingerVerifyResultListener = verifyResultListener;
    }

    public void startFingerService(Activity activity, OnStartServiceListener startServiceListener) {
        if (activity != null) {
            this.activity = activity;
            this.startServiceListener = startServiceListener;
            Intent intent = new Intent();
            intent.setAction(FingerService.ACTION);
            intent.addCategory(FingerService.CATEGORY);
            PackageManager packageManager = activity.getPackageManager();
            ResolveInfo resolveInfo = packageManager.resolveService(intent, 0);
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo != null) {
                //获取service的包名
                String packageName = serviceInfo.packageName;
                //获取service的类名
                String name = serviceInfo.name;
                //构建一个ComponentName，将隐式intent变成一个显示intent，
                // 因为Android5.0后不允许隐式启动service
                ComponentName componentName = new ComponentName(packageName, name);
                intent.setComponent(componentName);
                activity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                startServiceListener.startServiceListener(true);
            } else {
                startServiceListener.startServiceListener(false);
            }
        } else {
            startServiceListener.startServiceListener(false);
        }
    }

    public void unbindDevService(Context context) {
        this.startServiceListener.startServiceListener(false);
        context.unbindService(serviceConnection);
    }

    private Messenger fingerServiceMessenger;

    public void pauseFingerVerify() {
        if (fingerServiceMessenger != null) {
            try {
                Message message = new Message();
                message.what = FingerConstant.PAUSE_VERIFY_CODE;
                fingerServiceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void reStartFingerVerify() {
        if (fingerServiceMessenger != null) {
            try {
                Message message = new Message();
                message.what = FingerConstant.RESTART_VERIFY_CODE;
                fingerServiceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void addFinger(byte[] newFinger) {
        if (fingerServiceMessenger != null) {
            try {
                Message message = new Message();
                Bundle bundle = new Bundle();
                message.what = FingerConstant.ADD_FINGER_CODE;
                bundle.putByteArray(AppConstant.ADD_FINGER, newFinger);
                message.setData(bundle);
                fingerServiceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteFinger(int position) {
        if (fingerServiceMessenger != null) {
            try {
                Message message = new Message();
                Bundle bundle = new Bundle();
                message.what = FingerConstant.DELETE_FINGER_CODE;
                bundle.putInt(AppConstant.DELETE_FINGER, position);
                message.setData(bundle);
                fingerServiceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                fingerServiceMessenger = new Messenger(iBinder);
                Message message = new Message();
                message.what = FingerConstant.SEND_CODE;
                message.obj = activity;
                message.replyTo = fingerUtilMessenger;
                Bundle bundle = new Bundle();
//                bundle.putByteArray(AppConstant.FINGER_DATA, fingerData);
//                bundle.putInt(AppConstant.FINGER_SIZE, fingerDataSize);
                bundle.putParcelableArrayList(AppConstant.FINGER_DATA_LIST, finger6ArrayList);
                message.setData(bundle);
                fingerServiceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            fingerServiceMessenger = null;
        }
    };

    @SuppressLint("HandlerLeak")
    private Messenger fingerUtilMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == FingerConstant.SEND_MSG_1) {
                if (fingerServiceMessenger != null) {
                    try {
                        Message message = new Message();
                        message.what = FingerConstant.SEND_MSG_2;
                        message.obj = fingerVerifyResultListener;
                        fingerServiceMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });


}
