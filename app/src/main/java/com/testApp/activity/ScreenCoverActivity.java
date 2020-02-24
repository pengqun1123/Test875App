package com.testApp.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.TextView;

import com.baselibrary.base.BaseActivity;
import com.testApp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 屏保页面
 */
public class ScreenCoverActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.backBtn)
    AppCompatImageView backBtn;

    @Override
    protected Integer contentView() {
        return R.layout.activity_screen_cover;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initView() {
        backBtn.setVisibility(View.VISIBLE);
        toolbarTitle.setText(getString(R.string.screen_cover));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.backBtn)
    public void onViewClicked() {
    }
}
