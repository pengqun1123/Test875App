package com.finger.fingerApi;

import android.app.Activity;
import android.content.Context;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;

import com.finger.R;
import com.orhanobut.logger.Logger;
import com.sd.tgfinger.CallBack.DevCloseCallBack;
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.CallBack.RegisterCallBack;
import com.sd.tgfinger.CallBack.Verify1_1CallBack;
import com.sd.tgfinger.CallBack.Verify1_NCallBack;
import com.sd.tgfinger.api.TGAPI;
import com.sd.tgfinger.pojos.Msg;
import com.sd.tgfinger.tgApi.Constant;
import com.sd.tgfinger.tgApi.TGBApi;
import com.sd.tgfinger.utils.LogUtils;

import java.io.InputStream;

/**
 * Created By pq
 * on 2019/9/29
 */
public class FingerApi {

    public static void fingerInit(@NonNull Activity activity,
                                  InputStream inputStream,
                                  FvInitCallBack callBack) {
        if (inputStream == null) {
            TGBApi.getTGAPI().init(activity, null, callBack);
        } else {
            TGBApi.getTGAPI().init(activity, inputStream, callBack);
        }
    }

    /**
     * 打开设备
     *
     * @param activity          activity
     * @param fingerModelType   3/6特征模式
     * @param sound             是否播放声音
     * @param devOpenCallBack   设备是否打开的回调
     * @param devStatusCallBack 设备连接状态的回调
     */
    public static void openDev(@NonNull Activity activity, Integer fingerModelType, Boolean sound,
                               DevOpenCallBack devOpenCallBack, DevStatusCallBack devStatusCallBack) {
        if (!TGBApi.getTGAPI().isDevOpen()) {
            //初始化准备数据
            TGBApi.getTGAPI().openDev(activity, Constant.WORK_BEHIND, fingerModelType, sound
                    ,devOpenCallBack,devStatusCallBack);
        } else {
            Logger.d(activity.getString(R.string.dev_open));
        }
    }

    //注册
    public static void register(@NonNull Activity activity, byte[] fingerData,
                                Integer fingerSize,
                                RegisterCallBack callBack) {
        TGBApi.getTGAPI().extractFeatureRegister(activity, fingerData, fingerSize,callBack);
    }

    //1:1验证
    public static void verify1(@NonNull Activity activity, byte[] fingerData,Verify1_1CallBack callBack) {
        TGBApi.getTGAPI().featureCompare1_1(activity, fingerData, new Verify1_1CallBack() {
            @Override
            public void verify1CallBack(Msg msg) {
                Integer result = msg.getResult();
                Integer score = 0;
                if (result == 1) {
                    //验证分数
                    score = msg.getScore();
                }
            }
        });
    }

    //1:N验证
    public static void verifyN(@NonNull Activity activity, byte[] fingerData, Integer fingerSize,
                               Verify1_NCallBack callBack) {
        TGBApi.getTGAPI().featureCompare1_N(activity, fingerData, fingerSize, new Verify1_NCallBack() {
            @Override
            public void verify1_NCallBack(Msg msg) {
                Integer result = msg.getResult();
                Logger.d("验证的结果:" + result);
                if (result == 1) {
                    //比对的分数
                    Integer score = msg.getScore();
                    //比对模板对应的位置
                    Integer index = msg.getIndex();
                    //可更新的模板数据
                    byte[] updateFingerData = msg.getFingerData();
                    //当比对分数大于某个设定的值，可更新指静脉模板
                    //eg：
                    if (score >= 80) {
                        //更新指静脉模板数据
//                        updateFingerData(index, updateFingerData);
                    }
                } else {

                }
            }
        });
    }

    //获取设备的状态
    public static void devStatus(@NonNull Activity activity, DevStatusCallBack callBack) {
        TGBApi.getTGAPI().getDevStatus(activity, callBack);
    }

    //设备关闭
    public static void closeDev(@NonNull Activity activity, DevCloseCallBack callBack) {
        TGBApi.getTGAPI().closeDev(activity, callBack);
    }

}
