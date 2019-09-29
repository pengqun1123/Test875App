package com.testApp.base;

import com.baselibrary.base.BaseApplication;
import com.sd.tgfinger.tgApi.TGBApi;

/**
 * Created By pq
 * on 2019/9/9
 */
public class TestApplication extends BaseApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        //TGBApi.getTGAPI().startDevService(TestApplication.this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //TGBApi.getTGAPI().unbindDevService(TestApplication.this);
    }
}

