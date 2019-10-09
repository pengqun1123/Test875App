package com.testApp.base;

import com.baselibrary.base.BaseApplication;

/**
 * Created By pq
 * on 2019/9/9
 */
public class TestApplication extends BaseApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        //TGBApi.getTGAPI().startDevService(TestApplication.this);

//        FingerServiceUtil.getInstance().startFingerService(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //TGBApi.getTGAPI().unbindDevService(TestApplication.this);
    }
}

