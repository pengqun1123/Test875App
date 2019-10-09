package com.testApp.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.base.BaseFragment;
import com.baselibrary.callBack.PwCallBack;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.pojo.Finger3;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.pojo.Manager;
import com.baselibrary.pojo.Pw;
import com.baselibrary.pojo.User;
import com.baselibrary.service.factory.PwFactory;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.SoftInputKeyboardUtils;
import com.baselibrary.util.ToastUtils;
import com.finger.fingerApi.FingerApi;
import com.orhanobut.logger.Logger;
import com.testApp.R;
import com.testApp.activity.DefaultVerifyActivity;
import com.testApp.activity.SearchActivity;
import com.testApp.adapter.ManagerAdapter;
import com.testApp.adapter.UserManageAdapter;
import com.testApp.dialog.AskDialog;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import static com.testApp.dialog.AskDialog.reviseMaxManagerNum;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserManageFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private int type;
    private LinearLayoutManager mLayoutManager;
    private int lastVisibleItemPosition;

    public static UserManageFragment instance(int type) {
        UserManageFragment userManageFragment = new UserManageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        userManageFragment.setArguments(bundle);
        return userManageFragment;
    }

    public UserManageFragment() {
        // Required empty public constructor
    }

    @Override
    protected Integer contentView() {
        type = getArguments().getInt("type");
        if (type == 1) {
            return R.layout.fragment_user_manage;
        } else if (type == 2) {
            return R.layout.fragment_user_register;
        } else if (type == 3) {
            return R.layout.fragment_manager_manage;
        } else {
            return R.layout.fragment_user_register;
        }
    }

    @Override
    protected void initView() {
        //设置内容
        if (type == 1) {
            setUserManageView();
        } else if (type == 2) {
            userRegister();
        } else if (type == 3) {
            managerLayout();
        }

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.searchUserBtn:
                SkipActivityUtil.skipActivity(getActivity(), SearchActivity.class);
                break;
            case R.id.pwBtn:
                String pwBtnText = pwBtn.getText().toString().trim();
                if (pwBtnText.equals(getString(R.string.register))) {
                    registerUser();
                } else if (pwBtnText.equals(getString(R.string.complete))) {
                    //跳转人脸识别的界面(只要开启了人脸)
                    if (SPUtil.getOpenFace()) {

                    } else {
                        //跳转默认的识别页面(没有开启人脸)
                        SkipActivityUtil.skipActivity(getActivity(), DefaultVerifyActivity.class);
                    }
                    Objects.requireNonNull(getActivity()).finish();
                }
                break;
            case R.id.fingerModel:
                pwBtn.setText(getString(R.string.register));

                break;
            case R.id.faceModel:
                pwBtn.setText(getString(R.string.register));

                break;
            case R.id.idCardModel:
                pwBtn.setText(getString(R.string.register));

                break;
            case R.id.pwModel:
                pwBtn.setText(getString(R.string.register));
                pwRegister();
                break;
            case R.id.registerManagerMaxNum:
                //修改可注册的管理员的最大数量
                reviseMaxManagerNum(Objects.requireNonNull(getActivity()));
                break;
            case R.id.addManager:
                //新增管理员
                AskDialog.showManagerDialog(Objects.requireNonNull(getActivity()),
                        new AskDialog.PositiveCallBack() {
                            @Override
                            public void positiveCallBack() {

                            }
                        });
                break;

        }
    }

    /*************** 用户管理--Start **************/

    @SuppressLint("ResourceAsColor")
    private void setUserManageView() {
        bindViewWithClick(R.id.searchUserBtn, true);
        SwipeRefreshLayout userRefresh = bindViewWithClick(R.id.userRefresh, false);
        RecyclerView userRv = bindViewWithClick(R.id.userRv, false);
        AppCompatTextView noData = bindViewWithClick(R.id.noData, false);
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
        UserManageAdapter userManageAdapter = new UserManageAdapter();
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
    public void onRefresh() {

    }

    private void getUserData(UserManageAdapter userManageAdapter, AppCompatTextView noData) {
        List<User> users = getUsers(pageSize);
        if (users.size() > 0) {
            noData.setVisibility(View.GONE);
            userManageAdapter.addData(users);
        }
    }

    private int pageSize = 0;

    private List<User> getUsers(int pageSize) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        List<User> users = dbUtil.queryAll(User.class);
        List<User> users1 = dbUtil.queryAll(User.class);
//        QueryBuilder<User> queryBuilder = dbUtil.getQueryBuilder(User.class);
//        queryBuilder.offset(pageSize * 20).limit(20);
//        dbUtil.setDbCallBack(new DbCallBack<User>() {
//            @Override
//            public void onSuccess(User result) {
//
//            }
//
//            @Override
//            public void onSuccess(List<User> result) {
//
//            }
//
//            @Override
//            public void onFailed() {
//
//            }
//
//            @Override
//            public void onNotification(boolean result) {
//
//            }
//        }).queryAsync();
        return dbUtil.getQueryBuilder(User.class).offset(pageSize * 20).limit(20).list();
    }

    /*************** 用户注册--Start **************/
    private AppCompatEditText nameEt, ageEt, phoneEt, companyNameEt, departmentEt, staffNoEt;
    private AppCompatTextView fingerModel, faceModel, idCardModel, pwModel;
    private AppCompatButton pwBtn;
    private View nameBottomLine, ageBottomLine, phoneEtLine, companyNameLine, departmentLine, staffNoLine;
    //    private long pwId = 0L;
    private String sex;

    private void userRegister() {
        nameEt = bindViewWithClick(R.id.nameEt, false);
        ageEt = bindViewWithClick(R.id.ageEt, false);
        phoneEt = bindViewWithClick(R.id.phoneEt, false);
        companyNameEt = bindViewWithClick(R.id.companyNameEt, false);
        departmentEt = bindViewWithClick(R.id.departmentEt, false);
        staffNoEt = bindViewWithClick(R.id.staffNoEt, false);
        pwBtn = bindViewWithClick(R.id.pwBtn, true);
        fingerModel = bindViewWithClick(R.id.fingerModel, true);
        faceModel = bindViewWithClick(R.id.faceModel, true);
        idCardModel = bindViewWithClick(R.id.idCardModel, true);
        pwModel = bindViewWithClick(R.id.pwModel, true);
        nameBottomLine = bindViewWithClick(R.id.nameBottomLine, false);
        ageBottomLine = bindViewWithClick(R.id.ageBottomLine, false);
        phoneEtLine = bindViewWithClick(R.id.ageBottomLine, false);
        companyNameLine = bindViewWithClick(R.id.companyNameLine, false);
        departmentLine = bindViewWithClick(R.id.departmentLine, false);
        staffNoLine = bindViewWithClick(R.id.staffNoLine, false);
        SoftInputKeyboardUtils.hiddenKeyboard(nameEt);
        spinnerListener();


//        etChangeListener(nameEt, nameBottomLine);
//        etChangeListener(ageEt, ageBottomLine);
//        etChangeListener(phoneEt, phoneEtLine);
//        etChangeListener(companyNameEt, companyNameLine);
//        etChangeListener(departmentEt, departmentLine);
//        etChangeListener(staffNoEt, staffNoLine);

        //设置音量
        float streamVolumeMax = BaseApplication.AP.getStreamVolumeMax();
        BaseApplication.AP.setVolume((int) streamVolumeMax);
    }

    private void spinnerListener() {
        AppCompatSpinner spinner = bindViewWithClick(R.id.spinner, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view;
                String selectItem = textView.getText().toString();
                if (selectItem.equals(getString(R.string.sex))) {
                    sex = "";
                } else {
                    sex = selectItem;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void registerUser() {
        String userName = nameEt.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            nameBottomLine.setBackgroundColor(R.color.red);
            ToastUtils.showSingleToast(getActivity(), getString(R.string.please_input_name));
            return;
        }
        String userAge = ageEt.getText().toString().trim();
        if (TextUtils.isEmpty(userAge)) {
            ageBottomLine.setBackgroundColor(R.color.red);
            ToastUtils.showSingleToast(getActivity(), getString(R.string.please_input_age));
            return;
        }
        String userPhone = phoneEt.getText().toString().trim();
        if (TextUtils.isEmpty(userPhone)) {
            phoneEtLine.setBackgroundColor(R.color.red);
            ToastUtils.showSingleToast(getActivity(), getString(R.string.please_input_phone));
            return;
        }
        String companyName = companyNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(companyName)) {
            companyNameLine.setBackgroundColor(R.color.red);
            ToastUtils.showSingleToast(getActivity(), getString(R.string.please_input_company));
            return;
        }
        String department = departmentEt.getText().toString().trim();
        if (TextUtils.isEmpty(department)) {
            departmentLine.setBackgroundColor(R.color.red);
            ToastUtils.showSingleToast(getActivity(), getString(R.string.please_input_department));
            return;
        }
        String staffNo = staffNoEt.getText().toString().trim();
        if (TextUtils.isEmpty(staffNo)) {
            staffNoLine.setBackgroundColor(R.color.red);
            ToastUtils.showSingleToast(getActivity(), getString(R.string.please_input_staff_no));
            return;
        }
        if (sex.equals("")) {
            ToastUtils.showSingleToast(getActivity(), getString(R.string.please_select_sex));
            return;
        }
        User newUser = getNewUser(userName, userAge, userPhone, companyName, department, staffNo);
//        insertUser(newUser, );
    }

    private User getNewUser(String userName, String userAge, String userPhone,
                            String companyName, String department, String staffNo) {
        User user = new User();
        user.setName(userName);
        user.setAge(userAge);
        user.setSex(sex);
        user.setPhone(userPhone);
        user.setOrganizName(companyName);
        user.setSection(department);
        user.setWorkNum(staffNo);
        return user;
    }

    private Pw pwd;
    private Finger6 fg6;
    private Finger3 fg3;

    private void pwRegister() {
        PwFactory.createPw(getActivity(), new PwCallBack() {
            @Override
            public void pwCallBack(Pw pw) {
                insertOrReplacePw(pw);
            }
        });
    }

    private void insertOrReplacePw(Pw pw) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        dbUtil.setDbCallBack(new DbCallBack<Pw>() {
            @Override
            public void onSuccess(List<Pw> result) {

            }

            @Override
            public void onSuccess(Pw result) {
                Logger.d("Pw成功插入：" + result);
                pwd = result;
            }

            @Override
            public void onFailed() {
                Logger.d("Pw插入失败：");
            }

            @Override
            public void onNotification(boolean result) {
                Logger.d("插入Pw成功:" + result);

            }
        }).insertAsyncSingle(pw);
    }

    private void insertOrReplaceFinger(Finger6 fg) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        dbUtil.setDbCallBack(new DbCallBack<Finger6>() {
            @Override
            public void onSuccess(Finger6 result) {
                Logger.d("Fg6成功插入：" + result);
                fg6 = result;
            }

            @Override
            public void onSuccess(List<Finger6> result) {

            }

            @Override
            public void onFailed() {
                Logger.d("Fg6插入失败：");
            }

            @Override
            public void onNotification(boolean result) {

            }
        }).insertAsyncSingle(fg);
    }

    private void insertOrReplaceFinger(Finger3 fg) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        dbUtil.setDbCallBack(new DbCallBack<Finger3>() {
            @Override
            public void onSuccess(Finger3 result) {
                Logger.d("Fg3成功插入：" + result);
                fg3 = result;
            }

            @Override
            public void onSuccess(List<Finger3> result) {

            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onNotification(boolean result) {

            }
        }).insertAsyncSingle(fg);
    }

    /*****************管理员列表*****************/

    private void managerLayout() {
        AppCompatTextView registerManagerMaxNum = bindViewWithClick(R.id.registerManagerMaxNum, true);
        bindViewWithClick(R.id.addManager, true);
        // SwipeRefreshLayout managerRefresh = bindViewWithClick(R.id.managerRefresh, false);
        RecyclerView managerRv = bindViewWithClick(R.id.managerRv, false);
        AppCompatTextView noData = bindViewWithClick(R.id.noData, false);
        noData.setVisibility(View.VISIBLE);
        //managerRefresh.setRefreshing(false);
        registerManagerMaxNum.setText(MessageFormat.format("{0}{1}",
                getString(R.string.current_register_max_manager_mun), SPUtil.getMacManagerNum()));
        managerRv.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(),
                OrientationHelper.VERTICAL, false);
        managerRv.setLayoutManager(mLayoutManager);
        managerRv.setItemAnimator(new DefaultItemAnimator());
        ManagerAdapter managerAdapter = new ManagerAdapter(callBack);
        managerRv.setAdapter(managerAdapter);
        queryAllManagerData(managerAdapter, noData);

    }

    //查询manager的数据
    private void queryAllManagerData(ManagerAdapter managerAdapter, AppCompatTextView noData) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        QueryBuilder<Manager> queryBuilder = dbUtil.getQueryBuilder(Manager.class);
        dbUtil.setDbCallBack(new DbCallBack<Manager>() {
            @Override
            public void onSuccess(Manager result) {

            }

            @Override
            public void onSuccess(List<Manager> result) {
                Logger.d("管理员数据查询成功");
                if (result.size() > 0) {
                    managerAdapter.addData(result);
                    noData.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailed() {
                Logger.d("管理员数据查询失败");
            }

            @Override
            public void onNotification(boolean result) {

            }
        }).queryAsyncAll(Manager.class, queryBuilder);
    }

    private ManagerAdapter.ManagerItemCallBack callBack = new ManagerAdapter.ManagerItemCallBack() {
        @Override
        public void managerItemCallBack(int position) {
            ToastUtils.showSingleToast(getActivity(), "点击了managerItem");
        }
    };

}
