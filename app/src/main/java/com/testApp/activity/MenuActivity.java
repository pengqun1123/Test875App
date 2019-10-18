package com.testApp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.constant.AppConstant;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.ToastUtils;
import com.face.activity.V3FaceRecActivity;
import com.testApp.R;
import com.testApp.callBack.PositionBtnClickListener;
import com.testApp.dialog.AskDialog;

@Route(path = ARouterConstant.MENU_ACTIVITY)
public class MenuActivity extends BaseActivity {

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
                        } else if (flag == AppConstant.FACE_MODEL) {
                            BaseApplication.AP.playFaceScreen();
                            ARouterUtil.navigation(ARouterConstant.FACE_VERIFY_ACTIVITY);
                            finish();
                        } else if (flag == AppConstant.IDCARD_MODEL) {
                            BaseApplication.AP.play_rfid_card();
                        } else if (flag == AppConstant.PW_MODEL) {
                            BaseApplication.AP.playInputPw();
                        }
                        finish();
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
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

                }
            }
        }
    };


}
