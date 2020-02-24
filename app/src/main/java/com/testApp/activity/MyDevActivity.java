package com.testApp.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baselibrary.base.BaseActivity;
import com.baselibrary.util.ToastUtils;
import com.testApp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 跳转到我的设备页面
 */
public class MyDevActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.backBtn)
    AppCompatImageView backBtn;
    @BindView(R.id.devNameValue)
    AppCompatTextView devNameValue;
    @BindView(R.id.devName)
    RelativeLayout devName;
    @BindView(R.id.devSnValue)
    AppCompatTextView devSnValue;
    @BindView(R.id.devSN)
    RelativeLayout devSN;
    @BindView(R.id.devFwValue)
    AppCompatTextView devFwValue;
    @BindView(R.id.android_version)
    AppCompatTextView androidVersion;
    @BindView(R.id.fingerSnValue)
    AppCompatTextView fingerSnValue;
    @BindView(R.id.fingerFwValue)
    AppCompatTextView fingerFwValue;
    @BindView(R.id.resetDev)
    RelativeLayout resetDev;
    @BindView(R.id.systemmCheck)
    RelativeLayout systemmCheck;

    @Override
    protected Integer contentView() {
        return R.layout.activity_my_dev;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initView() {
          backBtn.setVisibility(View.VISIBLE);
          toolbarTitle.setText(getString(R.string.my_dev));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {

    }

    @OnClick({R.id.backBtn, R.id.resetDev, R.id.systemmCheck})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.resetDev:
                ToastUtils.showShortToast(this,"设备重置");
                break;
            case R.id.systemmCheck:
                ToastUtils.showShortToast(this,"跳转系统自检");
                break;
        }
    }
}
