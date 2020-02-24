package com.testApp.base;

import com.face.activity.DemoApplication;
import com.finger.fingerApi.FingerApi;

/**
 * Created By pq
 * on 2019/9/9
 */
public class TestApplication extends DemoApplication {


    @Override
    public void onCreate() {
        super.onCreate();
//        FingerApi.getInstance().startReStartFinger(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
//        FingerApi.getInstance().unReStartFinger(this);
    }
}

