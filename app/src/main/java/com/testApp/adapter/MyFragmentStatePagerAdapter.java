package com.testApp.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Create By Administrator
 * on 2019/7/1
 */
public class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;
    private List<String> titles;

    public MyFragmentStatePagerAdapter(FragmentManager fm, List<Fragment> fragments,
                                       List<String> titles) {
        super(fm);
        this.fragmentList = fragments;
        this.titles = titles;

    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList == null ? null : fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles == null ? "" : titles.get(position);
    }
}
