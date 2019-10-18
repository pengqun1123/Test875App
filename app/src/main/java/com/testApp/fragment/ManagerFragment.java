package com.testApp.fragment;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.base.BaseFragment;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.pojo.Manager;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.ToastUtils;
import com.testApp.R;
import com.testApp.adapter.ManagerAdapter;
import com.testApp.dialog.AskDialog;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

/**
 * Created By pq
 * on 2019/10/18
 */
public class ManagerFragment extends BaseFragment {

    public static ManagerFragment instance() {
        ManagerFragment managerFragment = new ManagerFragment();
        Bundle bundle = new Bundle();
        managerFragment.setArguments(bundle);
        return managerFragment;
    }

    @Override
    protected Integer contentView() {
        return R.layout.fragment_manager_manage;
    }

    @Override
    protected void initView() {
        AppCompatTextView registerManagerMaxNum = bindViewWithClick(R.id.registerManagerMaxNum, true);
        bindViewWithClick(R.id.addNewManager, true);
        RecyclerView managerRv = bindViewWithClick(R.id.managerRv, false);
        AppCompatTextView showAllData = bindViewWithClick(R.id.showAllData, false);
        AppCompatTextView noData = bindViewWithClick(R.id.noData, false);
        bindViewWithClick(R.id.verifySet, true);
        bindViewWithClick(R.id.addNewManager, true);

        noData.setVisibility(View.VISIBLE);
        registerManagerMaxNum.setText(MessageFormat.format("{0}{1}",
                getString(R.string.current_register_max_manager_mun), SPUtil.getMacManagerNum()));
        managerRv.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                OrientationHelper.VERTICAL, false);
        managerRv.setLayoutManager(mLayoutManager);
        managerRv.setItemAnimator(new DefaultItemAnimator());
        ManagerAdapter managerAdapter = new ManagerAdapter(callBack);
        managerRv.setAdapter(managerAdapter);
        queryAllManagerData(managerAdapter, noData, showAllData);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.addNewManager:
                //新增管理员
                AskDialog.showManagerDialog(Objects.requireNonNull(getActivity()),
                        new AskDialog.PositiveCallBack() {
                            @Override
                            public void positiveCallBack() {

                            }

                            @Override
                            public void activationCodeCallBack(String code) {

                            }
                        });
                break;
            case R.id.verifySet:
                AskDialog.showVerifyMethodSetDialog(Objects.requireNonNull(getActivity()));
                break;
        }
    }

    //查询manager的数据
    private void queryAllManagerData(ManagerAdapter managerAdapter, AppCompatTextView noData
            , AppCompatTextView showAllData) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        List<Manager> managers = dbUtil.queryAll(Manager.class);
        Long count = dbUtil.count(Manager.class);
        if (managers.size() > 0) {
            managerAdapter.addData(managers);
            noData.setVisibility(View.GONE);
        }
        if (managerAdapter.getItemCount() == count) {
            showAllData.setVisibility(View.VISIBLE);
        }
    }

    private ManagerAdapter.ManagerItemCallBack callBack = position ->
            ToastUtils.showSingleToast(getActivity(), "点击了managerItem");


}
