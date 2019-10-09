package com.testApp.activity;

import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.WindowManager;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.ToastUtils;

import com.pw.pwApi.PwApi;

import com.testApp.R;
import com.testApp.dialog.AskDialog;

public class DefaultVerifyActivity extends BaseActivity {

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
                            SkipActivityUtil.skipActivity(DefaultVerifyActivity.this,
                                    ManagerActivity.class);
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


}
