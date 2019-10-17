package com.face.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by wangyu on 2019/10/15.
 */

public class CameraSerrvice extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public boolean onUnbind(Intent intent){
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
