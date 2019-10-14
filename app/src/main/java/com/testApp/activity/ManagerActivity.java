package com.testApp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.baselibrary.base.BaseActivity;

import com.baselibrary.constant.AppConstant;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.ToastUtils;
import com.orhanobut.logger.Logger;
import com.testApp.R;
import com.testApp.adapter.MyFragmentStatePagerAdapter;
import com.testApp.dialog.AskDialog;
import com.testApp.fragment.UserManageFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManagerActivity extends BaseActivity {

    private byte[] allFingerData;
    private int allFingerSize;
    private UserManageFragment userRegisterFragment;

    @Override
    protected Integer contentView() {
        return R.layout.activity_manager;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            allFingerData = bundle.getByteArray(AppConstant.FINGER_DATA);
            allFingerSize = bundle.getInt(AppConstant.FINGER_SIZE);
            Logger.d("ManagerActivity 1 指静脉模板数量：" + allFingerSize);
        }
        TabLayout manageTab = bindViewWithClick(R.id.manageTab, false);
        ViewPager managePg = bindViewWithClick(R.id.managePg, false);
        bindViewWithClick(R.id.backPreviousBtn, true);

        setTabContent(manageTab, managePg);
    }

    @Override
    protected void initToolBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Toolbar toolBar = bindViewWithClick(R.id.toolbar, false);
        if (toolBar == null) {
            return;
        }
        TextView toolbarTitle = bindViewWithClick(R.id.toolbar_title, false);
        toolbarTitle.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backPreviousBtn:
                if (SPUtil.getOpenFace()) {
                    //回到人脸识别页面

                } else {
                    if (userRegisterFragment != null) {
                        Boolean save = userRegisterFragment.checkRegisterContent();
                        if (save) {
                            userRegisterFragment.registerUser();
                        } else {
                            finish();
                        }
                    }else {
                        finish();
                    }
                }
                break;
        }
    }

    private void setTabContent(TabLayout mainTab, ViewPager viewPager) {
        //准备fragment集合list的数据
        List<Fragment> fragmentList = initFragments();
        String[] titles = new String[]{getString(R.string.user_manage),
                getString(R.string.user_register),
                getString(R.string.manager)};
        MyFragmentStatePagerAdapter mainFragmentAdapter =
                new MyFragmentStatePagerAdapter(getSupportFragmentManager(),
                        fragmentList, Arrays.asList(titles));
        viewPager.setAdapter(mainFragmentAdapter);
        mainTab.setupWithViewPager(viewPager, true);
        viewPager.setCurrentItem(1);
    }

    private List<Fragment> fragments;

    //准备fragment的数据
    private List<Fragment> initFragments() {
        if (fragments == null) {
            fragments = new ArrayList<>();
        }
        if (fragments.size() > 0) {
            fragments.clear();
        }
        Logger.d("managerFragment 2 指静脉模板数量：" + allFingerSize);
        fragments.add(UserManageFragment.instance(0, null, 0));//用户管理
        userRegisterFragment = UserManageFragment.instance(1, allFingerData, allFingerSize);
        fragments.add(userRegisterFragment);//用户注册
        fragments.add(UserManageFragment.instance(2, null, 0));//管理员列表
        return fragments;
    }

    //添加管理员
    private void addManager() {

    }

}
