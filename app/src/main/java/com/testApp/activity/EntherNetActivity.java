package com.testApp.activity;

import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baselibrary.base.BaseActivity;
import com.baselibrary.util.ToastUtils;
import com.testApp.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 以太网页面
 */
public class EntherNetActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.backBtn)
    AppCompatImageView backBtn;
    @BindView(R.id.devPatternValue)
    AppCompatTextView devPatternValue;
    @BindView(R.id.devPattern)
    RelativeLayout devPattern;
    @BindView(R.id.ipAddressValue)
    AppCompatEditText ipAddressValue;
    @BindView(R.id.maskValue)
    AppCompatEditText maskValue;
    @BindView(R.id.devModel)
    AppCompatTextView devModel;

    @Override
    protected Integer contentView() {
        return R.layout.activity_enther_net;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initView() {
        backBtn.setVisibility(View.VISIBLE);
        toolbarTitle.setText(getString(R.string.enther_net));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {

    }

    @OnClick({R.id.backBtn, R.id.devPattern})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.devPattern:
                ToastUtils.showShortToast(this, "设置以太网模式");
                break;
        }
    }
}
