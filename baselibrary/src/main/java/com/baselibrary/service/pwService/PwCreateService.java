package com.baselibrary.service.pwService;

import android.app.Activity;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.baselibrary.callBack.PwCallBack;

/**
 * Created By pq
 * on 2019/9/27
 */
public interface PwCreateService extends IProvider {

    void insertPw(Activity activity, PwCallBack pwCallBack);
}
