package com.testApp.activity;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baselibrary.base.BaseActivity;
import com.testApp.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 无线网页面
 */
public class WLANActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.backBtn)
    AppCompatImageView backBtn;
    @BindView(R.id.openWLAN)
    RelativeLayout openWLAN;
    @BindView(R.id.wlanList)
    RecyclerView wlanList;

    @Override
    protected Integer contentView() {
        return R.layout.activity_wlan;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initView() {
        backBtn.setVisibility(View.VISIBLE);
        toolbarTitle.setText(getString(R.string.wlan));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {

    }

    @OnClick({R.id.backBtn, R.id.openWLAN})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.openWLAN:
                break;
        }
    }
}
