package com.testApp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.baselibrary.base.BaseActivity;

import com.baselibrary.constant.AppConstant;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.SkipActivityUtil;
import com.face.activity.V3FaceRecActivity;
import com.orhanobut.logger.Logger;
import com.testApp.R;
import com.testApp.adapter.MyFragmentStatePagerAdapter;
import com.testApp.callBack.SaveUserInfo;
import com.testApp.fragment.ManagerFragment;
import com.testApp.fragment.UserManageFragment;
import com.testApp.fragment.UserRegisterFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManagerActivity extends BaseActivity {

    private UserRegisterFragment userRegisterFragment;
    private ArrayList<Finger6> fingerList;

    @Override
    protected Integer contentView() {
        return R.layout.activity_manager;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
//            allFingerData = bundle.getByteArray(AppConstant.FINGER_DATA);
//            allFingerSize = bundle.getInt(AppConstant.FINGER_SIZE);
            fingerList = bundle.getParcelableArrayList(AppConstant.FINGER_DATA_LIST);
            Logger.d("ManagerActivity 1 指静脉模板数量：" + fingerList.size());
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

    //跳转验证页面
    private void skipVerify() {
        //跳转人脸识别的界面(只要开启了人脸)
        if (SPUtil.getOpenFace()) {
            //回到人脸识别页面
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(AppConstant.FINGER_DATA_LIST, fingerList);
            Logger.d("SplashActivity 2 指静脉模板数量：" + fingerList.size());
            //跳转人脸识别页面
            SkipActivityUtil.skipDataActivity(ManagerActivity.this, V3FaceRecActivity.class, bundle);
            finish();
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(AppConstant.FINGER_DATA_LIST, fingerList);
            SkipActivityUtil.skipDataActivity(ManagerActivity.this, DefaultVerifyActivity.class, bundle);
        }
    }


    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backPreviousBtn:
                if (userRegisterFragment != null) {
                    userRegisterFragment.checkRegisterContent(isSave -> skipVerify());
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
        Logger.d("managerFragment 2 指静脉模板数量：" + fingerList.size());
        fragments.add(UserManageFragment.instance());
        userRegisterFragment = UserRegisterFragment.instance(fingerList);
        fragments.add(userRegisterFragment);
        fragments.add(ManagerFragment.instance());
        return fragments;
    }

}
