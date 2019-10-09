package com.finger.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.sd.tgfinger.tgApi.Constant;

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

    public void startFingerService(Context context) {
        if (context != null) {
            Intent intent = new Intent();
            intent.setAction(FingerService.ACTION);
            intent.addCategory(FingerService.CATEGORY);
            PackageManager packageManager = context.getPackageManager();
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
                context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        }
    }

    public void unbindDevService(Context context) {
        context.unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            devServiceMessenger = new Messenger(iBinder);
//            //如果设备开启
//            Message tg661JMessage = new Message();
//            tg661JMessage.what = Constant.SEND_MESSAGE_CODE;
//            tg661JMessage.replyTo = tg661JMessenger;
//            try {
//                devServiceMessenger.send(tg661JMessage);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
//            devServiceMessenger = null;
        }
    };

}
