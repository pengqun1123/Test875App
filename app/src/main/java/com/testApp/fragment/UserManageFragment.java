package com.testApp.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.base.BaseFragment;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.pojo.User;
import com.baselibrary.util.SkipActivityUtil;
import com.testApp.R;
import com.testApp.activity.SearchActivity;
import com.testApp.adapter.UserManageAdapter;
import java.util.List;
import java.util.Objects;

import static com.testApp.dialog.AskDialog.reviseMaxManagerNum;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserManageFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private LinearLayoutManager mLayoutManager;
    private int lastVisibleItemPosition;
    private UserManageAdapter userManageAdapter;
    private Long userCount;
    private AppCompatTextView showAllData;


    public static UserManageFragment instance() {
        UserManageFragment userManageFragment = new UserManageFragment();
        Bundle bundle = new Bundle();
        userManageFragment.setArguments(bundle);
        return userManageFragment;
    }

    public UserManageFragment() {
    }

    @Override
    protected Integer contentView() {
        return R.layout.fragment_user_manage;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void initView() {
        bindViewWithClick(R.id.searchUserBtn, true);
        SwipeRefreshLayout userRefresh = bindViewWithClick(R.id.userRefresh, false);
        RecyclerView userRv = bindViewWithClick(R.id.userRv, false);
        AppCompatTextView noData = bindViewWithClick(R.id.noData, false);
        showAllData = bindViewWithClick(R.id.showAllData, false);
        noData.setVisibility(View.VISIBLE);

        //设置加载时候的颜色,最多设置4中颜色
        userRefresh.setColorSchemeColors(R.color.blue_5, R.color.blue_7,
                R.color.green_20, R.color.pinkColor);
        userRefresh.setOnRefreshListener(this);
        //设置第一次进入页面时显示加载进度条
        userRefresh.setProgressViewOffset(true, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        userRv.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(),
                OrientationHelper.VERTICAL, false);
        userRv.setLayoutManager(mLayoutManager);
        userRv.setItemAnimator(new DefaultItemAnimator());
        userManageAdapter = new UserManageAdapter();
        userRv.setAdapter(userManageAdapter);

        userRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItemPosition + 1)
                        == userManageAdapter.getItemCount()) {
                    //加载更多
                    pageSize++;
                    getUserData(userManageAdapter, noData);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
            }
        });

        //获取用户的数据
        getUserData(userManageAdapter, noData);
    }

    @Override
    protected void initData() {
        userCount = BaseApplication.getDbUtil().count(User.class);
    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.searchUserBtn:
                SkipActivityUtil.skipActivity(getActivity(), SearchActivity.class);
                break;
            case R.id.registerManagerMaxNum:
                //修改可注册的管理员的最大数量
                reviseMaxManagerNum(Objects.requireNonNull(getActivity()));
                break;
        }
    }

    @Override
    public void onRefresh() {

    }

    private void getUserData(UserManageAdapter userManageAdapter, AppCompatTextView noData) {
        List<User> users = getUsers(pageSize);
        if (users != null && users.size() > 0 && userManageAdapter != null) {
            noData.setVisibility(View.GONE);
            userManageAdapter.addData(users);
            if (userManageAdapter.getItemCount() == userCount) {
                showAllData.setVisibility(View.VISIBLE);
            }
        }
    }

    private int pageSize = 0;

    private List<User> getUsers(int pageSize) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        return dbUtil.getQueryBuilder(User.class).offset(pageSize * 20).limit(20).list();
    }

    public void addNewUser(User user) {
        if (userManageAdapter != null) {
            userManageAdapter.addData(user);
            if (userManageAdapter.getItemCount() == userCount) {
                showAllData.setVisibility(View.VISIBLE);
            }
        }
    }


}
