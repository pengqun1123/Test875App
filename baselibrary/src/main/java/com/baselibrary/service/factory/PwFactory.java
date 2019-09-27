package com.baselibrary.service.factory;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.callBack.PwCallBack;
import com.baselibrary.service.pwService.PwCreateService;
import com.baselibrary.service.routerTest.PwTestService;

/**
 * Created By pq
 * on 2019/9/14
 */
public class PwFactory {

    public static String getUserAddress(String userName) {
        PwTestService pwTestService = ARouter.getInstance().navigation(PwTestService.class);
        if (pwTestService != null) {
            return pwTestService.userInfo(userName);
        }
        return "";
    }

    public static void createPw(Activity activity, PwCallBack pwCallBack) {
        PwCreateService pwCreateService = ARouter.getInstance().navigation(PwCreateService.class);
        if (pwCreateService != null) {
            pwCreateService.insertPw(activity, pwCallBack);
        }
    }

}
