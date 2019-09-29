package com.face.activity;

import android.app.Application;
import android.os.Environment;

import com.baselibrary.base.BaseApplication;
import com.face.BuildConfig;
import com.tencent.bugly.crashreport.CrashReport;
import com.face.common.FaceConfig;

import java.io.File;


/**
 * demo应用
 */
public class DemoApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        FaceConfig.getInstance().setAppRootDir(new File(Environment.getExternalStorageDirectory(), "zhiqu"));
        //注册bugly，以捕获崩溃日志
        CrashReport.setAppVersion(getApplicationContext(), BuildConfig.VERSION_NAME);
        CrashReport.setAppChannel(getApplicationContext(), "demo");
        CrashReport.initCrashReport(getApplicationContext(), "3fd99dae55", true);
        //设置APP默认存储目录，所有数据都分类存在此目录下
        FaceConfig.getInstance().setAppRootDir(new File(Environment.getExternalStorageDirectory(), "zhiqu"));
        //demo模式下，打开SQLiteStudio连接功能，以方便查看数据库内容，在发布生产APP时不要使用。
       // SQLiteStudioService.instance().start(getApplicationContext());
    }

}
