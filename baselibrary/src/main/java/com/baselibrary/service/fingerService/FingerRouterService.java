package com.baselibrary.service.fingerService;

import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.baselibrary.callBack.OnGetVerifyFingerImgListener;
import com.baselibrary.callBack.OnStartServiceListener;
import com.baselibrary.pojo.Finger6;

import java.util.ArrayList;

/**
 * Created By pq
 * on 2019/10/14
 */
public interface FingerRouterService extends IProvider {

    void startFingerService(Activity activity, OnStartServiceListener startServiceListener);

    void unbindDevService(Context context);

    void verifyGetFingerImg(OnGetVerifyFingerImgListener listener);

    void setFingerData(ArrayList<Finger6> fingerList);

    void pauseFingerVerify();

    void reStartFingerVerify();

    void addFinger(byte[] newFinger);

    void deleteFinger(int position);


}
