package com.baselibrary.service.factory;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.callBack.FingerDevCloseListener;
import com.baselibrary.callBack.FingerDevOpenListener;
import com.baselibrary.callBack.FingerDevStatusConnectListener;
import com.baselibrary.callBack.FingerVerifyResultListener;
import com.baselibrary.callBack.OnGetVerifyFingerImgListener;
import com.baselibrary.callBack.OnStartServiceListener;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.service.fingerService.FingerRouterService;

import java.util.ArrayList;

/**
 * Created By pq
 * on 2019/10/14
 */
public class FingerFactory {

    private static FingerFactory fingerFactory = null;
    private final FingerRouterService fingerRouterService;

    private FingerFactory() {
        fingerRouterService = ARouter.getInstance().navigation(FingerRouterService.class);
    }

    public static synchronized FingerFactory getInstance() {
        if (fingerFactory == null)
            fingerFactory = new FingerFactory();
        return fingerFactory;
    }

    public void openFingerDev(@NonNull Activity activity, Boolean isSound,
                              FingerDevOpenListener fingerDevOpenListener,
                              FingerDevStatusConnectListener listener) {
        if (fingerRouterService != null) {
            fingerRouterService.openFingerDev(activity, isSound, fingerDevOpenListener, listener);
        }
    }

    public void closeFingerDev(@NonNull Activity activity, FingerDevCloseListener fingerDevCloseListener) {
        if (fingerRouterService != null) {
            fingerRouterService.closeFingerDev(activity, fingerDevCloseListener);
        }
    }

    public void fingerDevConnectStatus(FingerDevStatusConnectListener listener) {
        if (fingerRouterService != null) {
            fingerRouterService.fingerDevConnectStatus(listener);
        }
    }

    public void startFingerService(@NonNull Activity activity, FingerVerifyResultListener fingerVerifyResultListener,
                                   OnStartServiceListener startServiceListener) {
        if (fingerRouterService != null) {
            fingerRouterService.startFingerService(activity, startServiceListener);
        }
    }

    public void unbindDevService(@NonNull Context context) {
        if (fingerRouterService != null) {
            fingerRouterService.unbindDevService(context);
        }
    }

    public void setFingerData(@NonNull ArrayList<Finger6> fingerList) {
        if (fingerRouterService != null && fingerList.size() > 0) {
            fingerRouterService.setFingerData(fingerList);
        }
    }

    public void verifyGetFingerImg(OnGetVerifyFingerImgListener listener) {
        if (fingerRouterService != null) {
            fingerRouterService.verifyGetFingerImg(listener);
        }
    }

    public void pauseFingerVerify() {
        if (fingerRouterService != null) {
            fingerRouterService.pauseFingerVerify();
        }
    }

    public void reStartFingerVerify() {
        if (fingerRouterService != null) {
            fingerRouterService.reStartFingerVerify();
        }
    }

    public void addFinger(@NonNull byte[] newFinger) {
        if (fingerRouterService != null) {
            fingerRouterService.addFinger(newFinger);
        }
    }

    public void deleteFinger(int position) {
        if (fingerRouterService != null) {
            fingerRouterService.deleteFinger(position);
        }
    }

}
