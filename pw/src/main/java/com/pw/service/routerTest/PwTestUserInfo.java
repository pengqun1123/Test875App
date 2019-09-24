package com.pw.service.routerTest;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.service.routerTest.PwTestService;

/**
 * Created By pq
 * on 2019/9/12
 */
@Route(path = "/pw/testUserInfo")
public class PwTestUserInfo implements PwTestService {
    @Override
    public String userInfo(String userInfo) {
        //这里边调用的是PW组件中的方法
        PwUserInfo.getInstance().setUserInfo(userInfo);
        return PwUserInfo.getInstance().getUserInfo();
    }

    @Override
    public void init(Context context) {

    }
}
