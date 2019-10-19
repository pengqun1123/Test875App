package com.finger.fingerApi;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.callBack.OnGetVerifyFingerImgListener;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.pojo.Finger6;
import com.finger.R;
import com.finger.callBack.AllFingerData;
import com.finger.callBack.FingerDevStatusCallBack;
import com.finger.callBack.FingerRegisterCallBack;
import com.finger.callBack.OnCancelFingerImg;
import com.orhanobut.logger.Logger;
import com.sd.tgfinger.CallBack.CancelImgCallBack;
import com.sd.tgfinger.CallBack.DevCloseCallBack;
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.CallBack.OnStartDevStatusServiceListener;
import com.sd.tgfinger.CallBack.RegisterCallBack;
import com.sd.tgfinger.CallBack.Verify1_1CallBack;
import com.sd.tgfinger.CallBack.Verify1_NCallBack;
import com.sd.tgfinger.CallBack.VerifyGetFingerImgListener;
import com.sd.tgfinger.pojos.Msg;
import com.sd.tgfinger.tgApi.Constant;
import com.sd.tgfinger.tgApi.TGBApi;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.InputStream;
import java.util.List;

/**
 * Created By pq
 * on 2019/9/29
 */
public class FingerApi {

    private static FingerApi fingerApi;

    public static synchronized FingerApi getInstance() {
        if (fingerApi == null) {
            fingerApi = new FingerApi();
        }
        return fingerApi;
    }

    /**
     * 指静脉初始化
     *
     * @param activity
     * @param inputStream
     * @param callBack
     */
    public void fingerInit(@NonNull Activity activity,
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
     * @param sound             是否播放声音
     * @param devOpenCallBack   设备是否打开的回调
     * @param devStatusCallBack 设备连接状态的回调
     */
    public void openDev(@NonNull Activity activity, Boolean sound,
                        DevOpenCallBack devOpenCallBack, DevStatusCallBack devStatusCallBack) {
        if (!TGBApi.getTGAPI().isDevOpen()) {
            //初始化准备数据
            TGBApi.getTGAPI().openDev(activity, Constant.WORK_BEHIND, Constant.TEMPL_MODEL_6, sound
                    , devOpenCallBack, devStatusCallBack);
        } else {
            Logger.d(activity.getString(R.string.dev_open));
        }
    }

    /**
     * 指静脉注册
     *
     * @param activity   activity
     * @param fingerData 指静脉数据
     * @param fingerSize 指静脉数量
     * @param callBack   监听回调
     */
    public void register(@NonNull Activity activity, byte[] fingerData,
                         Integer fingerSize,
                         RegisterCallBack callBack) {
        TGBApi.getTGAPI().extractFeatureRegister(activity, fingerData, fingerSize, callBack);
    }

    /**
     * 指静脉设备取消抓图
     *
     * @param activity activity
     */
    public void cancelFingerImg(@NonNull Activity activity, OnCancelFingerImg cancelFingerImg) {
        TGBApi.getTGAPI().cancelRegisterGetImg(activity, new CancelImgCallBack() {
            @Override
            public void cancelImgCallBack(Msg msg) {
                cancelFingerImg.cancelFingerImg(msg.getResult());
            }
        });
    }

    /**
     * 获取1:N验证时抓图成功的状态
     *
     * @param listener 监听回调
     */
    public void verifyGetFingerImg(OnGetVerifyFingerImgListener listener) {
        TGBApi.getTGAPI().setVerifyGetImgListener(new VerifyGetFingerImgListener() {
            @Override
            public void verifyGetFingerImgSuccess() {
                listener.verifyFingerImgListener();
            }
        });
    }

    /**
     * 1:1验证
     *
     * @param activity   activity
     * @param fingerData 所有的指静脉数据
     * @param callBack   验证回调
     */
    public void verify1(@NonNull Activity activity, byte[] fingerData, Verify1_1CallBack callBack) {
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

    /**
     * 1:N验证
     *
     * @param activity   activity
     * @param fingerData 所有的指静脉数据
     * @param fingerSize 所有的指静脉数据数量
     * @param isSound    是否发出声音
     * @param callBack   监听回调
     */
    public void verifyN(@NonNull Activity activity, byte[] fingerData, Integer fingerSize, Boolean isSound,
                        Verify1_NCallBack callBack) {
        TGBApi.getTGAPI().featureCompare1_N(activity, fingerData, fingerSize, isSound, new Verify1_NCallBack() {
            @Override
            public void verify1_NCallBack(Msg msg) {
                Integer result = msg.getResult();
                Logger.d("验证的结果:" + result);
                callBack.verify1_NCallBack(msg);
//                if (result == 1) {
//                    //比对的分数
//                    Integer score = msg.getScore();
//                    //比对模板对应的位置
//                    Integer index = msg.getIndex();
//                    //可更新的模板数据
//                    byte[] updateFingerData = msg.getFingerData();
//                    //当比对分数大于某个设定的值，可更新指静脉模板
//                    //eg：
//                    if (score >= 80) {
//                        //更新指静脉模板数据
////                        updateFingerData(index, updateFingerData);
//
//                    }
//                } else {
//
//                }
            }
        });
    }

    /**
     * 获取设备的状态
     *
     * @param activity activity
     * @param callBack 监听回调
     */
    public static void devStatus(@NonNull Activity activity, DevStatusCallBack callBack) {
        TGBApi.getTGAPI().getDevStatus(activity, callBack);
    }

    /**
     * 设备关闭
     *
     * @param activity activity
     * @param callBack 监听回调
     */
    public void closeDev(@NonNull Activity activity, DevCloseCallBack callBack) {
        TGBApi.getTGAPI().closeDev(activity, callBack);
    }

    /**
     * 接收指静脉的连接状态
     *
     * @param callBack 监听回调
     */
    public void receiveFingerDevConnectStatus(FingerDevStatusCallBack callBack) {
        TGBApi.getTGAPI().setDevStatusCallBack(new DevStatusCallBack() {
            @Override
            public void devStatus(Msg msg) {
                callBack.fingerDevStatus(msg.getResult(), msg.getTip());
            }
        });
    }

    /**
     * 获取设备打开的状态
     *
     * @return 返回结果
     */
    public static Boolean getDevOpenSatus() {
        return TGBApi.getTGAPI().isDevOpen();
    }

    /**
     * 获取所有的指静脉数据
     *
     * @param callBack 监听回调
     */
    public void getAllFingerData(AllFingerData callBack) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        QueryBuilder<Finger6> queryBuilder = dbUtil.getQueryBuilder(Finger6.class);
        dbUtil.setDbCallBack(new DbCallBack<Finger6>() {
            @Override
            public void onSuccess(Finger6 result) {

            }

            @Override
            public void onSuccess(List<Finger6> result) {
                if (result != null) {
                    Logger.d("指静脉模板的数量:" + result.size());
                    callBack.allFingerData(result);
                }
            }

            @Override
            public void onFailed() {
                callBack.allFingerFail();
            }

            @Override
            public void onNotification(boolean result) {

            }
        }).queryAsyncAll(Finger6.class, queryBuilder);
    }

    private Boolean isStart = false;

    /**
     * 开启指静脉验证Service的监听
     *
     * @param context context
     */
    public void startReStartFinger(Context context) {
        if (!isStart) {
            TGBApi.getTGAPI().startDevService(context, new OnStartDevStatusServiceListener() {
                @Override
                public void startDevServiceStatus(Boolean aBoolean) {
                    FingerApi.this.isStart = aBoolean;
                }
            });
        }
    }

    public void unReStartFinger(Context context) {
        TGBApi.getTGAPI().unbindDevService(context);
        FingerApi.this.isStart = false;
    }

}
