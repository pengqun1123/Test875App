package com.testApp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.baselibrary.callBack.FingerDevStatusConnectListener;
import com.baselibrary.callBack.FingerVerifyResultListener;
import com.baselibrary.callBack.OnStartServiceListener;
import com.baselibrary.constant.AppConstant;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.pojo.IdCard;
import com.baselibrary.service.IdCardService;
import com.baselibrary.service.factory.FingerFactory;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.ToastUtils;
import com.baselibrary.util.VerifyResultUi;
import com.face.activity.V3FaceRecActivity;
import com.testApp.R;
import com.testApp.callBack.PositionBtnClickListener;
import com.testApp.dialog.AskDialog;

import java.util.function.LongFunction;

@Route(path = ARouterConstant.MENU_ACTIVITY)
public class MenuActivity extends BaseActivity implements CardInfoListener, FingerDevStatusConnectListener {

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
        fingerVerifyResult();
    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                if (SPUtil.getOpenFace()) {
                    //跳转人脸识别页面
                    SkipActivityUtil.skipActivity(MenuActivity.this, V3FaceRecActivity.class);
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
                            FingerFactory.getInstance().fingerDevConnectStatus(MenuActivity.this);
                        } else if (flag == AppConstant.FACE_MODEL) {
                            BaseApplication.AP.playFaceScreen();
                            ARouterUtil.navigation(ARouterConstant.FACE_VERIFY_ACTIVITY);
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
                                        ToastUtils.showSquareImgToast(MenuActivity.this
                                                , "密码验证成功"
                                                , null);
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("type", 4);
                                        bundle.putLong("id", id);
                                        SkipActivityUtil.skipDataActivity(MenuActivity.this, UserCenterActivity.class, bundle);
                                        finish();
                                    } else {
                                        ToastUtils.showSquareImgToast(MenuActivity.this
                                                , "密码验证失败"
                                                , ActivityCompat.getDrawable(MenuActivity.this
                                                        , com.face.R.drawable.cry_icon));
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
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (isStartService) {
            FingerFactory.getInstance().unbindDevService(this);
            isStartService = false;
        }
        if (idCardService != null) {
            idCardService.destroyIdCard();
        }
    }

    /**
     * 指静脉验证的结果
     */
    private void fingerVerifyResult() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.USER_MENU_RECEIVER);
        registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(AppConstant.USER_MENU_RECEIVER)) {
                int verifyType = intent.getIntExtra(AppConstant.VERIFY_RESULT_TYPE, 0);
                if (verifyType == AppConstant.FINGER_MODEL) {
                    int fingerVerifyResult = intent.getIntExtra(AppConstant.FINGER_VERIFY_RESULT, 0);
                    if (fingerVerifyResult == 1) {
                        VerifyResultUi.showVerifySuccess(MenuActivity.this
                                , getString(R.string.verify_success), false);
                        SkipActivityUtil.skipActivity(MenuActivity.this, UserCenterActivity.class);
                    } else {
                        VerifyResultUi.showVerifySuccess(MenuActivity.this
                                , getString(R.string.verify_fail), false);
                    }
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        FingerFactory.getInstance().reStartFingerVerify();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FingerFactory.getInstance().pauseFingerVerify();
    }

    @Override
    public void onGetCardInfo(IdCard idCard) {
        if (idCard == null) {
            ToastUtils.showSquareImgToast(MenuActivity.this
                    , "身份证验证失败"
                    , ActivityCompat.getDrawable(MenuActivity.this
                            , com.face.R.drawable.cry_icon));

        } else {
            ToastUtils.showSquareImgToast(this, "身份证验证成功", null);
            Bundle bundle = new Bundle();
            bundle.putInt("type", 3);
            bundle.putLong("id", idCard.getUId());
            SkipActivityUtil.skipActivity(MenuActivity.this, UserCenterActivity.class);
            finish();
        }
    }

    @Override
    public void onRegisterResult(boolean result, IdCard idCard) {

    }

    private Boolean isStartService = false;

    @Override
    public void fingerDevStatusConnect(int res, String msg) {
        if (res == 1 && !isStartService) {
            FingerFactory.getInstance().startFingerService(this, new FingerVerifyResultListener() {
                @Override
                public void fingerVerifyResult(int res, String msg, int score,
                                               int index, Long fingerId, byte[] updateFinger) {
                    if (res == 1) {
                        ToastUtils.showSquareImgToast(MenuActivity.this
                                , "指静脉验证成功"
                                , null);
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 1);
                        bundle.putLong("id", fingerId);
                        SkipActivityUtil.skipDataActivity(MenuActivity.this, UserCenterActivity.class, bundle);
                        finish();
                    } else {
                        ToastUtils.showSquareImgToast(MenuActivity.this
                                , "指静脉验证失败"
                                , ActivityCompat.getDrawable(MenuActivity.this
                                        , com.face.R.drawable.cry_icon));
                    }
                }
            }, new OnStartServiceListener() {
                @Override
                public void startServiceListener(Boolean isStart) {
                    isStartService = isStart;
                }
            });
        }
    }
}
