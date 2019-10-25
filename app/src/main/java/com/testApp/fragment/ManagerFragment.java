package com.testApp.fragment;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.base.BaseFragment;
import com.baselibrary.constant.AppConstant;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.pojo.Manager;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.ToastUtils;
import com.orhanobut.logger.Logger;
import com.testApp.R;
import com.testApp.adapter.ManagerAdapter;
import com.testApp.adapter.UserManageAdapter;
import com.testApp.callBack.PositionBtnClickListener;
import com.testApp.dialog.AskDialog;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

/**
 * Created By pq
 * on 2019/10/18
 */
public class ManagerFragment extends BaseFragment {

    private ManagerAdapter managerAdapter;

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
        AppCompatTextView noData = bindViewWithClick(R.id.noData, false);
        bindViewWithClick(R.id.verifySet, true);
        bindViewWithClick(R.id.addNewManager, true);
        RadioGroup faceOpenRg = bindViewWithClick(R.id.faceOpenRg, false);
        AppCompatRadioButton openFace = bindViewWithClick(R.id.openFace, false);
        AppCompatRadioButton noOpenFace = bindViewWithClick(R.id.noOpenFace, false);

        noData.setVisibility(View.VISIBLE);
        registerManagerMaxNum.setText(MessageFormat.format("{0}{1}",
                getString(R.string.current_register_max_manager_mun), SPUtil.getMacManagerNum()));
        managerRv.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                OrientationHelper.VERTICAL, false);
        managerRv.setLayoutManager(mLayoutManager);
        managerRv.setItemAnimator(new DefaultItemAnimator());
        managerAdapter = new ManagerAdapter(callBack);
        managerRv.setAdapter(managerAdapter);
        queryAllManagerData(managerAdapter, noData);

        setRgCheckStatus(openFace, noOpenFace);
        setRgCheckListener(faceOpenRg, openFace, noOpenFace);
        //为RecyclerView设置FooterView
        setFooterView(managerRv, managerAdapter);


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
                        AppConstant.ADD_NEW_MANAGER, new AskDialog.PositiveCallBack() {

                            @Override
                            public void positiveCallBack(int flag, Manager manager) {
                                if (flag == 1 && manager != null) {
                                    managerAdapter.addData(manager);
                                }
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
    private void queryAllManagerData(ManagerAdapter managerAdapter, AppCompatTextView noData) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        List<Manager> managers = dbUtil.queryAll(Manager.class);
        if (managers.size() > 0) {
            managerAdapter.addData(managers);
            noData.setVisibility(View.GONE);
        }
    }

    private ManagerAdapter.ManagerItemCallBack callBack = new ManagerAdapter.ManagerItemCallBack() {
        @Override
        public void managerItemCallBack(int position) {

        }

        @Override
        public void itemLongClickListener(Manager manager, String managerName, int position) {
            AskDialog.deleteItemDataDialog(Objects.requireNonNull(getActivity()),
                    null, manager, managerName, flag -> {
                        if (flag == 1) {
                            if (managerAdapter != null) {
                                managerAdapter.removeData(manager);
                                DBUtil dbUtil = BaseApplication.getDbUtil();
                                dbUtil.delete(manager);
                                ToastUtils.showSquareImgToast(getActivity(),
                                        getString(R.string.delete_success),
                                        ActivityCompat.getDrawable(getActivity(), R.drawable.ic_tick));
                            }
                        }
                    });
        }
    };

    /**
     * 给RecyclerView设置FooterView
     * 博文链接：
     * https://www.jianshu.com/p/9333e20456e2
     */
    private void setFooterView(RecyclerView rv, ManagerAdapter managerAdapter) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.manager_item_footer, rv, false);
        managerAdapter.setFooterView(view);
    }

    /**
     * 设置RadioGroup的初始化选项
     *
     * @param openFace   开启人脸识别
     * @param noOpenFace 不开启人脸识别
     */
    private void setRgCheckStatus(AppCompatRadioButton openFace, AppCompatRadioButton noOpenFace) {
        Boolean spOpenFace = SPUtil.getOpenFace();
        if (spOpenFace) {
            openFace.setChecked(true);
            noOpenFace.setChecked(false);
        } else {
            openFace.setChecked(false);
            noOpenFace.setChecked(true);
        }
    }

    private void setRgCheckListener(RadioGroup faceOpenRg, AppCompatRadioButton openFace
            , AppCompatRadioButton noOpenFace) {
        faceOpenRg.setOnCheckedChangeListener((radioGroup, i) -> {
            if (openFace.getId() == i) {
                SPUtil.putOpenFace(true);
            } else if (noOpenFace.getId() == i) {
                SPUtil.putOpenFace(false);
            }
        });
    }

}
