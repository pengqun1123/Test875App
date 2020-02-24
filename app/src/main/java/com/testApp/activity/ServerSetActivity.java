package com.testApp.activity;

import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.TextView;

import com.baselibrary.base.BaseActivity;
import com.testApp.R;

import butterknife.BindView;
import butterknife.OnClick;

public class ServerSetActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.backBtn)
    AppCompatImageView backBtn;
    @BindView(R.id.ipAddressServerValue)
    AppCompatEditText ipAddressServerValue;
    @BindView(R.id.ipAddressValue)
    AppCompatEditText ipAddressValue;

    @Override
    protected Integer contentView() {
        return R.layout.activity_server_set;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initView() {
        backBtn.setVisibility(View.VISIBLE);
        toolbarTitle.setText(getString(R.string.server));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {

    }

    @OnClick(R.id.backBtn)
    public void onViewClicked(View view) {
        if (view.getId() == R.id.backBtn) {
            finish();
        }
    }
}
