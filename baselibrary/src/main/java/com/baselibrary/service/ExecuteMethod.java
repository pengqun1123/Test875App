package com.baselibrary.service;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.service.pwService.ExecuteMethodImpl;

/**
 * Created By pq
 * on 2019/9/14
 * 利用路由调用
 */
public class ExecuteMethod {

    public static String executeAimMethod() {
        ExecuteMethodImpl method = ARouter.getInstance().navigation(ExecuteMethodImpl.class);
        if (method != null) {
            String s = method.executeMethod();
            return s;
        } else {
            return "";
        }
    }
}
