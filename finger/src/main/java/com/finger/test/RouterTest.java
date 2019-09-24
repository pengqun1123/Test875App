package com.finger.test;

import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.service.routerTest.PwFactory;

/**
 * Created By pq
 * on 2019/9/24
 * 路由组件间页面跳转，组件间通信
 * eg:
 */
public class RouterTest {

    /**
     * 组件间页面跳转(不带值跳转，无需在目标界面注册)
     */
    public static void routerSkipActNoParams(String path) {
        ARouter.getInstance().build(path).navigation();
    }

    /**
     * 组件间界面的跳转，带值需在目标界面进行路由注册
     */
    public static void routerSkipActParams(String path, Bundle bundle) {
        ARouter.getInstance().build(path)
                .with(bundle)
                .navigation();
    }

    /**
     * 组件间通信
     */
    public static String routerModuleCommuni(String userName) {
        //在finger组件中调用pw组件中的方法
        String userAddress = PwFactory.getUserAddress(userName);
        return userAddress;
    }

}
