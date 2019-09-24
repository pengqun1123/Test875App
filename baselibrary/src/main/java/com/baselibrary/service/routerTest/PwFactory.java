package com.baselibrary.service.routerTest;

import com.alibaba.android.arouter.launcher.ARouter;
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

}
