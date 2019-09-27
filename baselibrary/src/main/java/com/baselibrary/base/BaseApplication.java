package com.baselibrary.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.util.AudioProvider;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

/**
 * Created By pq
 * on 2019/9/11
 */
public class BaseApplication extends Application {

    private static DBUtil dbUtil;
    public static BaseApplication INSTANCE;
    public static AudioProvider AP;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        Logger.init("UE875==>: ");
        //初始化数据库
        if (dbUtil == null)
            dbUtil = DBUtil.getInstance(this);
        if (AP == null)
            AP = getAP(this);
//        DBUtil.instance().initDB(this);
        //APP的相关配置初始化
        AppConfig.INSTANCE.initConfig(this);
        //
    }

    public static DBUtil getDbUtil() {
        return dbUtil;
    }

    private AudioProvider getAP(Context context) {
        return AudioProvider.getInstance(context);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 程序终止时执行
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        ARouterUtil.destroyARouter();
        DBUtil.getInstance(this).closeData();
    }

    /**
     * 低内存时执行
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    /**
     * 按HOME键，程序在内存清理时执行
     *
     * @param level
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }


}
