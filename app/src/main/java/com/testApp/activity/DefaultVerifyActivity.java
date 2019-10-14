package com.testApp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.WindowManager;

import com.baselibrary.base.BaseActivity;
import com.baselibrary.callBack.FingerVerifyResultListener;
import com.baselibrary.callBack.OnStartServiceListener;
import com.baselibrary.constant.AppConstant;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.ToastUtils;

import com.finger.callBack.FingerDevStatusCallBack;
import com.finger.fingerApi.FingerApi;
import com.finger.service.FingerServiceUtil;
import com.orhanobut.logger.Logger;
import com.pw.pwApi.PwApi;

import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.testApp.R;
import com.testApp.dialog.AskDialog;

import java.util.ArrayList;

public class DefaultVerifyActivity extends BaseActivity implements FingerDevStatusCallBack,
        FingerVerifyResultListener {

    private ArrayList<Finger6> fingerList;

//    private byte[] allFingerData;
//    private int allFingerSize;

    @Override
    protected Integer contentView() {
        return R.layout.activity_default_register;
    }

    @Override
    protected void initView() {
        AppCompatTextView pwVerify = bindViewWithClick(R.id.pwVerify, true);
        AppCompatTextView manage_set = bindViewWithClick(R.id.manage_set, true);
        AppCompatTextView userCenter = bindViewWithClick(R.id.userCenter, true);
    }

    @Override
    protected void initToolBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        Toolbar toolBar = bindViewWithClick(R.id.toolbar, false);
//        if (toolBar == null) {
//            return;
//        }
//        TextView toolbarTitle = bindViewWithClick(R.id.toolbar_title, false);
//        String title = getString(R.string.pw_register);
//        toolbarTitle.setText(title);
    }

    @Override
    protected void initData() {
        //接收指静脉设备的连接状态
        FingerApi.getInstance().receiveFingerDevConnectStatus(this);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
//                allFingerData = bundle.getByteArray(AppConstant.FINGER_DATA);
//                allFingerSize = bundle.getInt(AppConstant.FINGER_SIZE);
                fingerList = bundle.getParcelableArrayList(AppConstant.FINGER_DATA_LIST);
                Logger.d("DefaultActivity 1 指静脉模板数量：" + fingerList.size());
                FingerServiceUtil.getInstance().setFingerData(fingerList);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("   验证页面  onResume");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.d("   验证页面  onNewIntent");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.d("    验证页面  onReStart");
        FingerServiceUtil.getInstance().reStartFingerVerify();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d("   验证页面 onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d("   验证页面 onStop");
        FingerServiceUtil.getInstance().pauseFingerVerify();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FingerServiceUtil.getInstance().unbindDevService(this);
    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.pwVerify:
                PwApi.pwInputVerify(this);
                break;
            case R.id.manage_set:
                AskDialog.verifyManagerPwd(this, new AskDialog.ManagerPwdVerifyCallBack() {
                    @Override
                    public void managerPwdVerifyCallBack(Boolean isVerify) {
                        if (isVerify) {
                            if (fingerList != null) {
                                Bundle bundle = new Bundle();
                                Logger.d("指静脉数据的数量：" + fingerList.size());
//                            bundle.putByteArray(AppConstant.FINGER_DATA, allFingerData);
//                            bundle.putInt(AppConstant.FINGER_SIZE, allFingerSize);
                                bundle.putParcelableArrayList(AppConstant.FINGER_DATA_LIST, fingerList);
                                SkipActivityUtil.skipDataActivity(DefaultVerifyActivity.this,
                                        ManagerActivity.class, bundle);
                            } else {
                                SkipActivityUtil.skipActivity(DefaultVerifyActivity.this,
                                        ManagerActivity.class);
                            }
                        } else {
                            ToastUtils.showSquareImgToast(DefaultVerifyActivity.this,
                                    getString(R.string.manager_pwd_verify_fail),
                                    ActivityCompat.getDrawable(DefaultVerifyActivity.this,
                                            R.drawable.cry_icon));
                        }
                    }
                });
//                finish();
                break;
            case R.id.userCenter:
                SkipActivityUtil.skipActivity(this, UserCenterActivity.class);
//                finish();
                break;

        }
    }

    private static Boolean isStartService = false;

    @Override
    public void fingerDevStatus(int res, String msg) {
        if (res == 1 && !isStartService) {
            FingerServiceUtil.getInstance().startFingerService(this,
                    new OnStartServiceListener() {
                        @Override
                        public void startServiceListener(Boolean isStart) {
                            isStartService = isStart;
                        }
                    });
        }
    }

    @Override
    public void fingerfVerifyResult(int res, String msg) {

    }
}
