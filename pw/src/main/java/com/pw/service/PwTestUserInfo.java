package com.pw.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.ARouter.ARouterConstrant;
import com.baselibrary.service.pwService.PwTestService;

/**
 * Created By pq
 * on 2019/9/12
 */
@Route(path = "/pw/testUserInfo")
public class PwTestUserInfo implements PwTestService {
    @Override
    public String userInfo(String userInfo) {
        PwUserInfo.getInstance().setUserInfo(userInfo);
        return PwUserInfo.getInstance().getUserInfo();
    }

    @Override
    public void init(Context context) {

    }
}
