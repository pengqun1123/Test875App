package com.finger.base;

import com.baselibrary.base.BaseApplication;
import com.sd.tgfinger.CallBack.OnStartDevStatusServiceListener;
import com.sd.tgfinger.tgApi.TGBApi;

/**
 * Created By pq
 * on 2019/9/12
 */
public class FingerApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        TGBApi.getTGAPI().startDevService(this, new OnStartDevStatusServiceListener() {
            @Override
            public void startDevServiceStatus(Boolean isStart) {

            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        TGBApi.getTGAPI().unbindDevService(this);
    }
}
