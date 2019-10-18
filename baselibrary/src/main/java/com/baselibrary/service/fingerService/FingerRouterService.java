package com.baselibrary.service.fingerService;

import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.baselibrary.callBack.FingerDevCloseListener;
import com.baselibrary.callBack.FingerDevOpenListener;
import com.baselibrary.callBack.FingerDevStatusConnectListener;
import com.baselibrary.callBack.FingerVerifyResultListener;
import com.baselibrary.callBack.OnGetVerifyFingerImgListener;
import com.baselibrary.callBack.OnStartServiceListener;

/**
 * Created By pq
 * on 2019/10/14
 */
public interface FingerRouterService extends IProvider {

    void openFingerDev(Activity activity, Boolean isSound,
                       FingerDevOpenListener fingerDevOpenListener,
                       FingerDevStatusConnectListener listener);

    void closeFingerDev(Activity activity, FingerDevCloseListener fingerDevCloseListener);

    void fingerDevConnectStatus(FingerDevStatusConnectListener listener);

    void setFingerVerifyResultListener(FingerVerifyResultListener fingerVerifyResultListener);

    void startFingerService(Activity activity,
                            OnStartServiceListener startServiceListener);

    void unbindDevService(Context context);

    void verifyGetFingerImg(OnGetVerifyFingerImgListener listener);

    void pauseFingerVerify();

    void reStartFingerVerify();

    void addFinger(byte[] newFinger);

    void deleteFinger(int position);


}
