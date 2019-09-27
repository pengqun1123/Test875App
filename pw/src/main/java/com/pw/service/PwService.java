package com.pw.service;

import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.callBack.PwCallBack;
import com.baselibrary.service.pwService.PwCreateService;
import com.pw.pwApi.PwApi;

/**
 * Created By pq
 * on 2019/9/27
 */
@Route(path = "/pw/createPw")
public class PwService implements PwCreateService {
    @Override
    public void insertPw(Activity activity, PwCallBack pwCallBack) {
        PwApi.pwRegister(activity, pwCallBack);
    }

    @Override
    public void init(Context context) {

    }
}
