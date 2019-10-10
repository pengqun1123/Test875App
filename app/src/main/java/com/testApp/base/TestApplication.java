package com.testApp.base;

import android.os.Environment;

import com.baselibrary.base.BaseApplication;
import com.face.common.FaceConfig;

import java.io.File;

/**
 * Created By pq
 * on 2019/9/9
 */
public class TestApplication extends BaseApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        //TGBApi.getTGAPI().startDevService(TestApplication.this);
     //   FaceConfig.getInstance().setAppRootDir(new File(Environment.getExternalStorageDirectory(), "zhiqu"));
//        FingerServiceUtil.getInstance().startFingerService(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //TGBApi.getTGAPI().unbindDevService(TestApplication.this);
    }
}

