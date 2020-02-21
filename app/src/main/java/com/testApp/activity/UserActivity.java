package com.testApp.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baselibrary.base.BaseActivity;
import com.baselibrary.util.SkipActivityUtil;
import com.testApp.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户页面
 */
public class UserActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.backBtn)
    AppCompatImageView backBtn;
    @BindView(R.id.searchUserBtn)
    AppCompatTextView searchUserBtn;
    @BindView(R.id.userList)
    RecyclerView userList;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected Integer contentView() {
        return R.layout.activity_user;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initView() {

        if (backBtn != null) {
            backBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {

    }

    @OnClick({R.id.backBtn, R.id.searchUserBtn, R.id.goToAllDepartment, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.searchUserBtn:

                break;
            case R.id.goToAllDepartment:
                SkipActivityUtil.skipActivity(this, DepartmentActivity.class);
                break;
            case R.id.fab:
                SkipActivityUtil.skipActivity(this, AddDepartmentUserActivity.class);
                break;
        }
    }
}
