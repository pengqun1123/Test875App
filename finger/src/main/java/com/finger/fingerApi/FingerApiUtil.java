package com.finger.fingerApi;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.util.ToastUtils;
import com.finger.callBack.AllFingerData;
import com.finger.callBack.DevOpenResult;
import com.finger.callBack.DevStatusResult;
import com.finger.callBack.FingerRegisterCallBack;
import com.finger.callBack.FingerVerifyResult;
import com.finger.callBack.FvInitResult;
import com.finger.service.FingerService;
import com.orhanobut.logger.Logger;
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.CallBack.RegisterCallBack;
import com.sd.tgfinger.pojos.Msg;
import com.sd.tgfinger.tgApi.Constant;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.InputStream;
import java.util.List;

/**
 * Created By pq
 * on 2019/10/9
 */
public class FingerApiUtil {

    private static class Holder {
        private static final FingerApiUtil INSTANCE = new FingerApiUtil();
    }

    public static FingerApiUtil getInstance() {
        return Holder.INSTANCE;
    }

    /***********初始化指静脉***********/
    //临时放到这里初始化指静脉
    public void initFinger(@NonNull Activity activity, InputStream inputStream,
                           FvInitResult fvInitResult) {
        FingerApi.fingerInit(activity, inputStream, new FvInitCallBack() {
            @Override
            public void fvInitResult(Msg msg) {
                fvInitResult.fvInitResult(msg.getResult(),msg.getTip());
//                Integer result = msg.getResult();
//                if (result == 1) {
//
//                    openFinger(activity, callBack, devStatusResult);
//                } else {
//                    fvInitResult.fvInitResult("指静脉初始化失败:" + msg.getTip());
//                    Logger.e("指静脉初始化失败:" + msg.getTip());
//                }
            }
        });
    }

    public void openFinger(@NonNull Activity activity, DevOpenResult callBack, DevStatusResult devStatusResult) {
        Boolean devOpenSatus = FingerApi.getDevOpenSatus();
        if (!devOpenSatus) {
            FingerApi.openDev(activity, Constant.TEMPL_MODEL_6, true, new DevOpenCallBack() {
                @Override
                public void devOpenResult(Msg msg) {
                    callBack.devOpenResult(msg.getResult(), msg.getTip());
                    Logger.d(msg.getTip());
                }
            }, new DevStatusCallBack() {
                @Override
                public void devStatus(Msg msg) {
                    devStatusResult.devStatusResult(msg.getResult(), msg.getTip());
                    Logger.d("设备的连接状态:" + msg.getTip());
                }
            });
        }
    }

    //指静脉的注册
    public void fingerRegister(@NonNull Activity activity, FingerRegisterCallBack callBack) {
        FingerApi.register(activity, null, 0, new RegisterCallBack() {
            @Override
            public void registerResult(Msg msg) {
                Integer result = msg.getResult();
                if (result == 8) {
                    byte[] fingerData = msg.getFingerData();
//                    Finger6 finger6 = new Finger6();
//                    finger6.setFinger6Feature(fingerData);
//                    insertOrReplaceFinger(finger6);
                    callBack.fingerRegisterCallBack(8, msg.getTip(), fingerData);
//                            verifyFinger(fingerData, 1);
                }
            }
        });
    }

    //验证指静脉
    public void verifyFinger(@NonNull Activity activity, byte[] fingerData, Integer fingerSize
            , FingerVerifyResult callBack) {
        FingerApi.verifyN(activity, fingerData, fingerSize, msg -> {
            Integer result = msg.getResult();
            if (result == 8) {
                callBack.fingerVerifyResult(8, msg.getTip()
                        , msg.getFingerData(), msg.getScore(), msg.getIndex());
                ToastUtils.showSingleToast(activity, "验证成功");
            } else {
                callBack.fingerVerifyResult(msg.getResult(), msg.getTip()
                        , null, msg.getScore(), -1);
                ToastUtils.showSingleToast(activity, "验证失败");
            }
        });
    }

    //获取已存在的所有的指静脉的数据
    public void getAllFingerData(AllFingerData callBack) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        QueryBuilder<Finger6> queryBuilder = dbUtil.getQueryBuilder(Finger6.class);
        dbUtil.setDbCallBack(new DbCallBack<Finger6>() {
            @Override
            public void onSuccess(Finger6 result) {

            }

            @Override
            public void onSuccess(List<Finger6> result) {
                Logger.d("线程:" + Thread.currentThread().getName());
                Logger.d("指静脉模板的数量:"+result.size());
                if (result != null && result.size() > 0) {
                    callBack.allFingerData(result);
                }
            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onNotification(boolean result) {

            }
        }).queryAsyncAll(Finger6.class, queryBuilder);
    }


}
