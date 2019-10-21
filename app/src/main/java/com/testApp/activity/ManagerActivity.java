package com.testApp.activity;

import android.annotation.SuppressLint;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.base.BaseActivity;

import com.baselibrary.pojo.User;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.SkipActivityUtil;
import com.face.activity.V3FaceRecActivity;
import com.testApp.R;
import com.testApp.adapter.MyFragmentStatePagerAdapter;
import com.testApp.fragment.ManagerFragment;
import com.testApp.fragment.UserManageFragment;
import com.testApp.fragment.UserRegisterFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint("ParcelCreator")
public class ManagerActivity extends BaseActivity {

    private UserRegisterFragment userRegisterFragment;
    private UserManageFragment manageFragment;

    @Override
    protected Integer contentView() {
        return R.layout.activity_manager;
    }

    @Override
    protected void initView() {
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
            //跳转人脸识别页面
            SkipActivityUtil.skipActivity(ManagerActivity.this, V3FaceRecActivity.class);
            finish();
        } else {
            SkipActivityUtil.skipActivity(ManagerActivity.this, DefaultVerifyActivity.class);
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

        manageFragment = UserManageFragment.instance();
        userRegisterFragment = UserRegisterFragment.instance();
        fragments.add(manageFragment);
        fragments.add(userRegisterFragment);
        fragments.add(ManagerFragment.instance());
        return fragments;
    }

    public void skipFaceActivity() {
        //跳转人脸识别页面
        SkipActivityUtil.skipActivity(this,V3FaceRecActivity.class);
    }

    public void addNewUser(User user) {
        if (manageFragment != null && user != null) {
            manageFragment.addNewUser(user);
        }
    }
}
