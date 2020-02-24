package com.baselibrary.base;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.BuildConfig;
import com.baselibrary.util.AppUtil;

/**
 * Created By pq
 * on 2019/9/11
 * App的相关配置
 * <p>
 * 枚举实现单例可以杜绝其他的新建单例对象的状况
 * https://blog.csdn.net/happy_horse/article/details/51164262
 */
public enum AppConfig {

    INSTANCE;

    public void initConfig(Application application) {
        AppUtil.init(application);
        //线程池的初始化

        //路由的初始化
        initARouter();

    }

    private void initARouter() {
        if (BuildConfig.IS_DEBUG) {
            //打印日志
            ARouter.openLog();
            //开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug();
        }
        //推荐在Application中初始化
        ARouter.init(AppUtil.getApp());
    }

}
