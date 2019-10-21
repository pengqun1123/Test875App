package com.testApp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.callBack.CardInfoListener;
import com.baselibrary.callBack.FingerVerifyResultListener;
import com.baselibrary.constant.AppConstant;
import com.baselibrary.pojo.IdCard;
import com.baselibrary.service.IdCardService;
import com.baselibrary.service.factory.FingerFactory;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.ToastUtils;
import com.baselibrary.util.VerifyResultUi;
import com.face.activity.V3FaceRecActivity;
import com.finger.callBack.FingerDevStatusCallBack;
import com.finger.fingerApi.FingerApi;
import com.finger.service.FingerServiceUtil;
import com.orhanobut.logger.Logger;
import com.testApp.R;
import com.testApp.callBack.PositionBtnClickListener;
import com.testApp.dialog.AskDialog;

@Route(path = ARouterConstant.MENU_ACTIVITY)
public class MenuActivity extends BaseActivity implements CardInfoListener, FingerDevStatusCallBack
        , FingerVerifyResultListener {

    private IdCardService idCardService;

    @Override
    protected Integer contentView() {
        return R.layout.activity_menu;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initView() {
        bindViewWithClick(R.id.manageMenu, true);
        bindViewWithClick(R.id.userCenter, true);
        bindViewWithClick(R.id.backBtn, true);
    }

    @Override
    protected void initData() {
        //接收指静脉设备的连接状态

        //  fingerVerifyResult();

    }



    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                if (SPUtil.getOpenFace()) {
                    //跳转人脸识别页面
                    SkipActivityUtil.skipActivity(MenuActivity.this, V3FaceRecActivity.class);
                } else {
                    SkipActivityUtil.skipActivity(MenuActivity.this, DefaultVerifyActivity.class);
                }
                finish();
                break;
            case R.id.manageMenu:
                AskDialog.verifyManagerPwd(this, new AskDialog.ManagerPwdVerifyCallBack() {
                    @Override
                    public void managerPwdVerifyCallBack(Boolean isVerify) {
                        if (isVerify) {
                            SkipActivityUtil.skipActivity(MenuActivity.this,
                                    ManagerActivity.class);
                            finish();
                        } else {
                            ToastUtils.showSquareImgToast(MenuActivity.this,
                                    getString(R.string.manager_pwd_verify_fail),
                                    ActivityCompat.getDrawable(MenuActivity.this,
                                            R.drawable.cry_icon));
                        }
                    }
                });
                break;
            case R.id.userCenter:
                AskDialog.showAskUserCenterDialog(this, new PositionBtnClickListener() {
                    @Override
                    public void positionClickListener(int flag) {
                        if (flag == AppConstant.FINGER_MODEL) {
                            BaseApplication.AP.play_inputDownGently();
                            FingerFactory.getInstance().reStartFingerVerify();
                            FingerApi.getInstance().receiveFingerDevConnectStatus(MenuActivity.this);
                        } else if (flag == AppConstant.FACE_MODEL) {
                            BaseApplication.AP.playFaceScreen();
                            ARouterUtil.navigation(ARouterConstant.FACE_VERIFY_ACTIVITY);
                            finish();
                        } else if (flag == AppConstant.ID_CARD_MODEL) {
                            BaseApplication.AP.play_rfid_card();
                            idCardService = ARouter.getInstance().navigation(IdCardService.class);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    idCardService.verify_IdCard(MenuActivity.this);
                                }
                            }).start();
                        } else if (flag == AppConstant.PW_MODEL) {
                            BaseApplication.AP.playInputPw();
                            AskDialog.VerifyUserPwd(MenuActivity.this, new AskDialog.UserPwdVerifyCallBack() {
                                @Override
                                public void userPwdVerifyCallBack(Long id) {
                                    if (id != null) {
                                        VerifyResultUi.showVerifySuccess(MenuActivity.this,
                                                getString(R.string.user_pwd_verify_success), false);
                                        Bundle bundle = new Bundle();
                                        bundle.putInt(AppConstant.VERIFY_RESULT_TYPE, 4);
                                        bundle.putLong(AppConstant.VERIFY_TYPE_ID, id);
                                        SkipActivityUtil.skipDataActivity(MenuActivity.this, UserCenterActivity.class, bundle);
                                        finish();
                                    } else {
                                        VerifyResultUi.showVerifyFail(MenuActivity.this,
                                                getString(R.string.user_pwd_verify_fail), false);
                                    }
                                }
                            });

                        }

                    }
                });
                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        FingerServiceUtil.getInstance().pauseFingerVerify();
        if (isStartService) {
            FingerServiceUtil.getInstance().unbindFingerService(this);
            isStartService = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* if (receiver!=null) {
            unregisterReceiver(receiver);
        }*/
        if (idCardService != null) {
            idCardService.destroyIdCard();
        }
    }

    @Override
    public void onGetCardInfo(IdCard idCard) {
        if (idCard==null) {
            VerifyResultUi.showVerifyFail(MenuActivity.this,getString(R.string.id_card_verify_fail),false);

        } else {
            VerifyResultUi.showVerifySuccess(MenuActivity.this,getString(R.string.id_card_verify_success),false);
            Bundle bundle=new Bundle();
            bundle.putInt(AppConstant.VERIFY_RESULT_TYPE, 3);
            bundle.putLong(AppConstant.VERIFY_TYPE_ID, idCard.getUId());
            SkipActivityUtil.skipDataActivity(MenuActivity.this, UserCenterActivity.class, bundle);
            finish();
        }
    }

    @Override
    public void onRegisterResult(boolean result, IdCard idCard) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    /**
     * 指静脉验证的结果
     */
    private void fingerVerifyResult() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int verifyType = intent.getIntExtra(AppConstant.VERIFY_RESULT_TYPE, 0);
            if (verifyType == AppConstant.FINGER_MODEL) {
                int fingerVerifyResult = intent.getIntExtra(AppConstant.FINGER_VERIFY_RESULT, 0);
                if (fingerVerifyResult == 1) {
                    ToastUtils.showSquareImgToast(MenuActivity.this
                            , "指静脉验证成功"
                            , null);
                    SkipActivityUtil.skipActivity(MenuActivity.this, UserCenterActivity.class);
                } else {
                    ToastUtils.showSquareImgToast(MenuActivity.this
                            , "指静脉验证失败"
                            , ActivityCompat.getDrawable(MenuActivity.this
                                    , com.face.R.drawable.cry_icon));
                }
            }
        }
    };

    private Boolean isStartService = false;

    /**
     * 获取设备的连接状态
     *
     * @param res 指静脉设备的连接结果
     * @param msg 连接状态的提示
     */
    @Override
    public void fingerDevStatus(int res, String msg) {
        if (res == 1 && !isStartService){
            Logger.d("测试  MenuActivity  启动FingerService ");
            FingerServiceUtil.getInstance().startFingerService(this,
                    isStart -> {
                        isStartService = isStart;
                        if (isStart){
                            Logger.d("测试  MenuActivity  调用FingerService 1：N验证");
                            FingerServiceUtil.getInstance().setFingerVerifyResult(this);
                        }
                    });
        }
    }

    /**
     * 接收指静脉的验证结果
     *
     * @param res          指静脉验证结果的状态码
     * @param msg          提示信息
     * @param score        验证分数
     * @param index        模板可更新的下标
     * @param fingerId     验证成功的指静脉ID
     * @param updateFinger 可更新的指静脉的数据
     */
    @Override
    public void fingerVerifyResult(int res, String msg, int score,
                                   int index, Long fingerId, byte[] updateFinger) {
        if (res == 1) {
            //指静脉验证成功
            VerifyResultUi.showVerifySuccess(this,
                    getString(R.string.verify_success), false);
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstant.VERIFY_RESULT_TYPE, 1);
            bundle.putLong(AppConstant.VERIFY_TYPE_ID, fingerId);
            SkipActivityUtil.skipDataActivity(this, UserCenterActivity.class, bundle);
            finish();
        }else {
            VerifyResultUi.showVerifySuccess(this,
                    getString(R.string.verify_fail), false);
        }
    }
}
