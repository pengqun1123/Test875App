package com.finger.fingerApi;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.baselibrary.util.ToastUtils;
import com.finger.callBack.DevOpenResult;
import com.finger.callBack.DevStatusResult;
import com.finger.callBack.FingerRegisterCallBack;
import com.finger.callBack.FvInitResult;
import com.orhanobut.logger.Logger;
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.CallBack.RegisterCallBack;
import com.sd.tgfinger.CallBack.Verify1_NCallBack;
import com.sd.tgfinger.pojos.Msg;
import com.sd.tgfinger.tgApi.Constant;

import java.io.InputStream;

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
                           FvInitResult fvInitResult, DevOpenResult callBack,
                           DevStatusResult devStatusResult) {
        FingerApi.fingerInit(activity, inputStream, new FvInitCallBack() {
            @Override
            public void fvInitResult(Msg msg) {
                Integer result = msg.getResult();
                if (result == 1) {
                    openFinger(activity, callBack, devStatusResult);
                } else {
                    fvInitResult.fvInitResult("指静脉初始化失败:" + msg.getTip());
                    Logger.e("指静脉初始化失败:" + msg.getTip());
                }
            }
        });
    }

    public void openFinger(@NonNull Activity activity, DevOpenResult callBack, DevStatusResult devStatusResult) {
        Boolean devOpenSatus = FingerApi.getDevOpenSatus();
        if (!devOpenSatus) {
            FingerApi.openDev(activity, Constant.TEMPL_MODEL_6, true, new DevOpenCallBack() {
                @Override
                public void devOpenResult(Msg msg) {
                    callBack.devOpenResult(msg.getResult(),msg.getTip());
                    Logger.d(msg.getTip());
                }
            }, new DevStatusCallBack() {
                @Override
                public void devStatus(Msg msg) {
                    devStatusResult.devStatusResult(msg.getResult(),msg.getTip());
                    Logger.d("设备的连接状态:" + msg.getTip());
                }
            });
        }
    }

    //指静脉的注册
    public void fingerRegister(@NonNull Activity activity, FingerRegisterCallBack callBack){
        FingerApi.register(activity, null, 0, new RegisterCallBack() {
            @Override
            public void registerResult(Msg msg) {
                Integer result = msg.getResult();
                // TODO: 2019/10/8 指静脉是3特征还是6特征模式
                if (result == 8) {
                    byte[] fingerData = msg.getFingerData();
//                    Finger6 finger6 = new Finger6();
//                    finger6.setFinger6Feature(fingerData);
//                    insertOrReplaceFinger(finger6);
                    callBack.fingerRegisterCallBack(8,msg.getTip(),fingerData);
//                            verifyFinger(fingerData, 1);
                }
            }
        });
    }

    //验证指静脉
    private void verifyFinger(@NonNull Activity activity,byte[] fingerData, Integer fingerSize) {
        FingerApi.verifyN(activity, fingerData, fingerSize, new Verify1_NCallBack() {
            @Override
            public void verify1_NCallBack(Msg msg) {
                Integer result = msg.getResult();
                if (result == 8) {
                    ToastUtils.showSingleToast(activity, "验证成功");
                } else {
                    ToastUtils.showSingleToast(activity, "验证失败");
                }
            }
        });
    }

}
