package com.testApp.activity;

import android.annotation.SuppressLint;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.baselibrary.base.BaseActivity;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.callBack.PwCallBack;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.dao.db.UserDao;
import com.baselibrary.pojo.Finger3;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.pojo.Pw;
import com.baselibrary.pojo.User;
import com.baselibrary.service.factory.PwFactory;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.SoftInputKeyboardUtils;
import com.baselibrary.util.ToastUtils;
import com.finger.fingerApi.FingerApi;
import com.orhanobut.logger.Logger;
import com.sd.tgfinger.CallBack.RegisterCallBack;
import com.sd.tgfinger.CallBack.Verify1_NCallBack;
import com.sd.tgfinger.pojos.Msg;
import com.testApp.R;
import com.baselibrary.constant.AppConstant;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;


/**
 * 该注册页面以公司考勤应用场景为例
 */
public class AppRegisterActivity extends BaseActivity {

    private AppCompatEditText nameEt, ageEt, phoneEt, companyNameEt, departmentEt, staffNoEt;
    private AppCompatTextView fingerModel, faceModel, idCardModel, pwModel, rightTv;
    private AppCompatButton pwBtn;
    private View nameBottomLine, ageBottomLine, phoneEtLine, companyNameLine, departmentLine, staffNoLine;
    //    private long pwId = 0L;
    private String sex;

    @Override
    protected Integer contentView() {
        return R.layout.activity_app_register;
    }

    @Override
    protected void initView() {
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
        AppCompatImageView backBtn = bindViewWithClick(R.id.backBtn, true);
        rightTv = bindViewWithClick(R.id.rightTv, true);
        nameBottomLine = bindViewWithClick(R.id.nameBottomLine, false);
        ageBottomLine = bindViewWithClick(R.id.ageBottomLine, false);
        phoneEtLine = bindViewWithClick(R.id.ageBottomLine, false);
        companyNameLine = bindViewWithClick(R.id.companyNameLine, false);
        departmentLine = bindViewWithClick(R.id.departmentLine, false);
        staffNoLine = bindViewWithClick(R.id.staffNoLine, false);
        SoftInputKeyboardUtils.hiddenKeyboard(nameEt);
        spinnerListener();

        backBtn.setVisibility(View.VISIBLE);
        rightTv.setVisibility(View.VISIBLE);

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

    @Override
    protected void initToolBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Toolbar toolBar = bindViewWithClick(R.id.toolbar, false);
        if (toolBar == null) {
            return;
        }
        TextView toolbarTitle = bindViewWithClick(R.id.toolbar_title, false);
        String title = getString(R.string.pw_register);
        toolbarTitle.setText(title);
    }

    @Override
    protected void initData() {

    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.pwBtn:
                String pwBtnText = pwBtn.getText().toString().trim();
                if (pwBtnText.equals(getString(R.string.register))) {
                    registerUser();
                } else if (pwBtnText.equals(getString(R.string.complete))) {
                    //跳转人脸识别的界面(只要开启了人脸)
//                    if (AppConstant.OPEN_FACE) {
//
//                    } else {
//                        //跳转默认的识别页面(没有开启人脸)
//                        SkipActivityUtil.skipActivity(this, DefaultVerifyActivity.class);
//                    }
                }
                break;
            case R.id.fingerModel:
                pwBtn.setText(getString(R.string.register));
                FingerApi.register(this, null, 0, new RegisterCallBack() {
                    @Override
                    public void registerResult(Msg msg) {
                        Integer result = msg.getResult();
                        if (result == 8) {
                            byte[] fingerData = msg.getFingerData();
                            Finger6 finger6 = new Finger6();
                            finger6.setFinger6Feature(fingerData);
                            insertOrReplaceFinger(finger6);
//                            verifyFinger(fingerData, 1);
                        }
                    }
                });
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
            case R.id.backBtn:
                AppRegisterActivity.this.finish();
                break;
            case R.id.rightTv:
                //跳转下一页
                //跳转人脸识别的界面(只要开启了人脸)
//                if (AppConstant.OPEN_FACE) {
//
//                } else {
//                    //跳转默认的识别页面(没有开启人脸)
//                    SkipActivityUtil.skipActivity(this, DefaultVerifyActivity.class);
//                }
                break;
        }
    }


    private void queryUser(DBUtil dbUtil, String params) {
        WhereCondition whereCondition = UserDao.Properties.Name.eq(params);
        dbUtil.setDbCallBack(new DbCallBack<User>() {
            @Override
            public void onSuccess(User result) {
                //返回的结果
                Logger.d("注册的结果：" + result);
            }

            @Override
            public void onSuccess(List<User> result) {

            }

            @Override
            public void onFailed() {
                //查询失败
            }

            @Override
            public void onNotification(boolean result) {
                //true 查询成功的通知  false  查询失败的通知

            }
        }).queryAsync(User.class, whereCondition);
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

    //监听EdiText的变化
    private void etChangeListener(AppCompatEditText et, View line) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean b = editable.length() > 0;
                if (b) {
                    line.setBackgroundColor(R.color.blue_5);
                }
            }
        });
    }

    private void registerUser() {
        String userName = nameEt.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            nameBottomLine.setBackgroundColor(R.color.red);
            ToastUtils.showSingleToast(this, getString(R.string.please_input_name));
            return;
        }
        String userAge = ageEt.getText().toString().trim();
        if (TextUtils.isEmpty(userAge)) {
            ageBottomLine.setBackgroundColor(R.color.red);
            ToastUtils.showSingleToast(this, getString(R.string.please_input_age));
            return;
        }
        String userPhone = phoneEt.getText().toString().trim();
        if (TextUtils.isEmpty(userPhone)) {
            phoneEtLine.setBackgroundColor(R.color.red);
            ToastUtils.showSingleToast(this, getString(R.string.please_input_phone));
            return;
        }
        String companyName = companyNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(companyName)) {
            companyNameLine.setBackgroundColor(R.color.red);
            ToastUtils.showSingleToast(this, getString(R.string.please_input_company));
            return;
        }
        String department = departmentEt.getText().toString().trim();
        if (TextUtils.isEmpty(department)) {
            departmentLine.setBackgroundColor(R.color.red);
            ToastUtils.showSingleToast(this, getString(R.string.please_input_department));
            return;
        }
        String staffNo = staffNoEt.getText().toString().trim();
        if (TextUtils.isEmpty(staffNo)) {
            staffNoLine.setBackgroundColor(R.color.red);
            ToastUtils.showSingleToast(this, getString(R.string.please_input_staff_no));
            return;
        }
        if (sex.equals("")) {
            ToastUtils.showSingleToast(this, getString(R.string.please_select_sex));
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

    /**
     * 存储用户的注册信息
     *
     * @param user 用户
     * @param type 注册的类型:
     */
    private void insertUser(User user, Integer type) {
        if (AppRegisterActivity.this.pw != null) {
            Long pwId = AppRegisterActivity.this.pw.getUId();
            user.setPwId(pwId);
            user.setPw(AppRegisterActivity.this.pw);
            DBUtil dbUtil = BaseApplication.getDbUtil();
            dbUtil.setDbCallBack(new DbCallBack<User>() {
                @Override
                public void onSuccess(User result) {
                    QueryBuilder<User> userQueryBuilder = dbUtil.getDaoSession().getUserDao().queryBuilder();
                    List<User> list = userQueryBuilder.list();
                    for (User user1 : list) {
                        Logger.d(" 查询user：" + user1);
                    }
                }

                @Override
                public void onSuccess(List<User> result) {

                }

                @Override
                public void onFailed() {

                }

                @Override
                public void onNotification(boolean result) {
                    if (result) {
                        ToastUtils.showSquareImgToast(AppRegisterActivity.this,
                                getString(R.string.pw_register_success),
                                ActivityCompat.getDrawable(AppRegisterActivity.this,
                                        R.drawable.ic_emoje));
                        BaseApplication.AP.play_checkInSuccess();
                        pwModel.setVisibility(View.GONE);
                        pwBtn.setText(getString(R.string.complete));
                    } else {
                        ToastUtils.showSquareImgToast(AppRegisterActivity.this,
                                getString(R.string.pw_register_fail),
                                ActivityCompat.getDrawable(AppRegisterActivity.this,
                                        R.drawable.cry_icon));
                        BaseApplication.AP.play_checkInFail();
                    }
                }
            }).insertAsyncSingle(user);

        } else if (AppRegisterActivity.this.fg6 != null || AppRegisterActivity.this.fg3 != null) {
            Long fg6Id = AppRegisterActivity.this.fg6.getUId();
            user.setFinger6Id(fg6Id);
            user.setFinger6(AppRegisterActivity.this.fg6);
            DBUtil dbUtil = BaseApplication.getDbUtil();
            dbUtil.setDbCallBack(new DbCallBack<User>() {
                @Override
                public void onSuccess(User result) {
                    QueryBuilder<User> userQueryBuilder = dbUtil.getDaoSession().getUserDao().queryBuilder();
                    List<User> list = userQueryBuilder.list();
                    for (User user1 : list) {
                        Logger.d(" 查询user：" + user1);
                    }

                }

                @Override
                public void onSuccess(List<User> result) {

                }

                @Override
                public void onFailed() {

                }

                @Override
                public void onNotification(boolean result) {
                    if (result) {
                        ToastUtils.showSquareImgToast(AppRegisterActivity.this,
                                getString(R.string.pw_register_success),
                                ActivityCompat.getDrawable(AppRegisterActivity.this,
                                        R.drawable.ic_emoje));
                        BaseApplication.AP.play_checkInSuccess();
                        pwModel.setVisibility(View.GONE);
                        pwBtn.setText(getString(R.string.complete));
                    } else {
                        ToastUtils.showSquareImgToast(AppRegisterActivity.this,
                                getString(R.string.pw_register_fail),
                                ActivityCompat.getDrawable(AppRegisterActivity.this,
                                        R.drawable.cry_icon));
                        BaseApplication.AP.play_checkInFail();
                    }
                }
            }).insertAsyncSingle(user);
        } else {
            ToastUtils.showSingleToast(this, getString(R.string.please_select_register_model));
        }
    }

    private Pw pw;
    private Finger6 fg6;
    private Finger3 fg3;

    private void pwRegister() {
        PwFactory.createPw(this, new PwCallBack() {
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
                AppRegisterActivity.this.pw = result;
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
                AppRegisterActivity.this.fg6 = result;
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
                Logger.d("Fg6成功插入：" + result);
                AppRegisterActivity.this.fg3 = result;
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


    //验证指静脉
    private void verifyFinger(byte[] fingerData, Integer fingerSize) {
        FingerApi.verifyN(this, fingerData, fingerSize, new Verify1_NCallBack() {
            @Override
            public void verify1_NCallBack(Msg msg) {
                Integer result = msg.getResult();
                if (result == 8) {
                    ToastUtils.showSingleToast(AppRegisterActivity.this, "验证成功");
                } else {
                    ToastUtils.showSingleToast(AppRegisterActivity.this, "验证失败");
                }
            }
        });
    }
}
