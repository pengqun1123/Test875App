package com.testApp.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.base.BaseFragment;
import com.baselibrary.callBack.CardInfoListener;
import com.baselibrary.constant.AppConstant;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.dao.db.PwDao;
import com.baselibrary.dao.db.UserDao;
import com.baselibrary.pojo.Face;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.pojo.IdCard;
import com.baselibrary.pojo.Pw;
import com.baselibrary.pojo.User;
import com.baselibrary.service.IdCardService;
import com.baselibrary.service.factory.PwFactory;
import com.baselibrary.util.FingerListManager;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.SoftInputKeyboardUtils;
import com.baselibrary.util.ToastUtils;
import com.baselibrary.util.VerifyResultUi;
import com.face.activity.V3FaceRecActivity;
import com.finger.fingerApi.FingerApi;
import com.finger.service.FingerServiceUtil;
import com.orhanobut.logger.Logger;
import com.sd.tgfinger.CallBack.RegisterCallBack;
import com.sd.tgfinger.pojos.Msg;
import com.sd.tgfinger.pojos.VerifyResult;
import com.testApp.R;
import com.testApp.activity.DefaultVerifyActivity;
import com.testApp.activity.ManagerActivity;
import com.testApp.callBack.CancelBtnClickListener;
import com.testApp.callBack.PositionBtnClickListener;
import com.testApp.callBack.QueryUserNo;
import com.testApp.callBack.RegisterUserCallBack;
import com.testApp.callBack.SaveUserInfo;
import com.testApp.dialog.AskDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserRegisterFragment extends BaseFragment {

    private List<String> workNoList;//用户工号List
    private AppCompatEditText nameEt, ageEt, phoneEt, companyNameEt, departmentEt, staffNoEt;
    private AppCompatTextView fingerModel, faceModel, idCardModel, pwModel;
    private AppCompatButton registerBtn;
    private String sex;
    private byte[] allFingerData;
    private int allFingerSize;

    private ManagerActivity manageActivity;

    public UserRegisterFragment() {
        // Required empty public constructor
    }

    public static UserRegisterFragment instance() {
        UserRegisterFragment userRegisterFragment = new UserRegisterFragment();
        Bundle bundle = new Bundle();
        userRegisterFragment.setArguments(bundle);
        return userRegisterFragment;
    }

    @Override
    protected Integer contentView() {
        return R.layout.fragment_user_register;
    }

    @Override
    protected void initView() {
        nameEt = bindViewWithClick(R.id.nameEt, false);
        ageEt = bindViewWithClick(R.id.ageEt, false);
        phoneEt = bindViewWithClick(R.id.phoneEt, false);
        companyNameEt = bindViewWithClick(R.id.companyNameEt, false);
        departmentEt = bindViewWithClick(R.id.departmentEt, false);
        staffNoEt = bindViewWithClick(R.id.staffNoEt, false);
        registerBtn = bindViewWithClick(R.id.registerBtn, true);
        fingerModel = bindViewWithClick(R.id.fingerModel, true);
        faceModel = bindViewWithClick(R.id.faceModel, true);
        idCardModel = bindViewWithClick(R.id.idCardModel, true);
        pwModel = bindViewWithClick(R.id.pwModel, true);
        SoftInputKeyboardUtils.hiddenKeyboard(nameEt);
        spinnerListener();
        //隐藏注册的按钮，当用户至少选择了一种验证模式注册完成后才显示注册按钮
        registerBtn.setVisibility(View.GONE);


    }

    @Override
    protected void initData() {
        manageActivity = (ManagerActivity) getActivity();
        EventBus.getDefault().register(this);
        fingerListToFingerByte();
        //查询所有用户的工号
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.registerBtn:
                registerUser(null);
                break;
            case R.id.fingerModel:
                //指静脉注册
                fingerRegister();
                break;
            case R.id.faceModel:
                //人脸注册
                faceRegister();
                break;
            case R.id.idCardModel:
                //身份证注册
                idCardRegister();
                break;
            case R.id.pwModel:
                //密码模式注册
                pwRegister();
                break;
        }
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("ResourceAsColor")
    public void registerUser(SaveUserInfo saveUserInfo) {
        String userName = nameEt.getText().toString().trim();
//        if (TextUtils.isEmpty(userName)) {
//            nameBottomLine.setBackgroundColor(R.color.red);
//            ToastUtils.showSingleToast(getActivity(), getString(R.string.please_input_name));
//            return;
//        }
        String userAge = ageEt.getText().toString().trim();
//        if (TextUtils.isEmpty(userAge)) {
//            ageBottomLine.setBackgroundColor(R.color.red);
//            ToastUtils.showSingleToast(getActivity(), getString(R.string.please_input_age));
//            return;
//        }
        String userPhone = phoneEt.getText().toString().trim();
//        if (TextUtils.isEmpty(userPhone)) {
//            phoneEtLine.setBackgroundColor(R.color.red);
//            ToastUtils.showSingleToast(getActivity(), getString(R.string.please_input_phone));
//            return;
//        }
        String companyName = companyNameEt.getText().toString().trim();
//        if (TextUtils.isEmpty(companyName)) {
//            companyNameLine.setBackgroundColor(R.color.red);
//            ToastUtils.showSingleToast(getActivity(), getString(R.string.please_input_company));
//            return;
//        }
        String department = departmentEt.getText().toString().trim();
//        if (TextUtils.isEmpty(department)) {
//            departmentLine.setBackgroundColor(R.color.red);
//            ToastUtils.showSingleToast(getActivity(), getString(R.string.please_input_department));
//            return;
//        }
        String staffNo = staffNoEt.getText().toString().trim();
        if (TextUtils.isEmpty(staffNo)) {
            ToastUtils.showSingleToast(getActivity(), getString(R.string.please_input_staff_no));
        } else {
            //查询是否有相同的工号
            queryAllUserNo(staffNo, eq -> {
                if (eq) {
                    ToastUtils.showSquareImgToast(getActivity(),
                            getString(R.string.dont_equls_user_no),
                            ActivityCompat.getDrawable(Objects.requireNonNull(getActivity()),
                                    R.drawable.cry_icon));
                    staffNoEt.getText().clear();
                } else {
                    if (sex.equals("")) {
                        ToastUtils.showSingleToast(getActivity(), getString(R.string.please_select_sex));
                        return;
                    }
                    //先插入各验证模式的数据
                    User newUser = getNewUser(userName, userAge, userPhone, companyName, department, staffNo);
                    if (pwd == null && fg6 == null && idCard == null && face == null) {
                        ToastUtils.showSquareImgToast(getActivity(), getString(R.string.lest_select_one_verify),
                                ActivityCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.cry_icon));
                        return;
                    }
                    if (pwd != null) {
                        newUser.setPwId(pwd.getUId());
                        newUser.setPw(pwd);
                    }
                    if (fg6 != null) {
                        newUser.setFinger6Id(fg6.getUId());
                        newUser.setFinger6(fg6);
                    }
                    if (idCard != null) {
                        newUser.setCardId(idCard.getUId());
                    }
                    if (face != null) {
                        newUser.setFaceId(face.getUId());
                    }
                    DBUtil dbUtil = BaseApplication.getDbUtil();
                    dbUtil.insertOrReplace(newUser);
                    manageActivity.addNewUser(newUser);
                    ToastUtils.showSquareImgToast(getActivity(), getString(R.string.register_success)
                            , ActivityCompat.getDrawable(Objects.requireNonNull(getActivity()),
                                    R.drawable.ic_emoje));
                    if (saveUserInfo != null) {
                        User user = dbUtil.queryById(User.class, newUser.getUId());
                        Logger.d(" 存储完成的用户对象：" + user.toString());
                        saveUserInfo.saveUserInfo(true);
                    } else {
                        skipVerify();
                    }
                }
            });
        }
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
     * 注意:
     * 所有注册数据的插入都在用户点击了注册按钮后才进行；插入顺序依次是先插入各验证模式的数据，再user
     */
    private Pw pwd;
    private IdCard idCard;
    private Face face;
    private Finger6 fg6;

    /**
     * 密码注册
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void pwRegister() {
        PwFactory.createPw(getActivity(), pw -> {
            DBUtil dbUtil = BaseApplication.getDbUtil();
            PwDao pwDao = dbUtil.getDaoSession().getPwDao();
            QueryBuilder<Pw> pwQueryBuilder = pwDao.queryBuilder();
            List<Pw> list = pwQueryBuilder.where(PwDao.Properties.Password.eq(pw.getPassword())).list();
            if (list != null && list.size() > 0) {
                VerifyResultUi.showRegisterFail(Objects.requireNonNull(getActivity()),
                        getString(R.string.pw_register_repeat), false);
            } else {
                dbUtil.insertOrReplace(pw);
                //可添加到User表的pwd
                UserRegisterFragment.this.pwd = pw;
                registerBtn.setVisibility(View.VISIBLE);
                VerifyResultUi.showRegisterSuccess(Objects.requireNonNull(getActivity()),
                        getString(R.string.pw_register_success), false);
            }
        });
    }

    /**
     * 身份证注册
     */
    private void idCardRegister() {
        Long idCardId = -1L;
        if (this.idCard != null) {
            idCardId = idCard.getUId();
        }
        IdCardService idCardService = ARouter.getInstance().navigation(IdCardService.class);
        Long finalIdCardId = idCardId;
        new Thread(() -> idCardService.register_IdCard(new CardInfoListener() {
            @Override
            public void onGetCardInfo(IdCard idCard) {

            }

            @Override
            public void onRegisterResult(boolean result, IdCard idCard) {
                idCardService.destroyIdCard();
                if (result) {
                    Logger.d("身份证注册成功");
                    //可插入User表的数据
                    UserRegisterFragment.this.idCard = idCard;
                    registerBtn.setVisibility(View.VISIBLE);

                } else {
                    Logger.d("身份证注册失败");
                }
            }
        }, finalIdCardId)).start();
    }

    /**
     * 人脸注册
     */
    private void faceRegister() {
        Boolean openFace = SPUtil.getOpenFace();
        if (openFace) {
            String userName = nameEt.getText().toString().trim();
            if (TextUtils.isEmpty(userName)) {
                ToastUtils.showSingleToast(getContext(), getResources().getString(R.string.please_input_name));
            } else {
                Bundle bundle = new Bundle();
                if (face != null) {
                    bundle.putLong(AppConstant.FACE_ID, face.getUId());
                    File file = new File(face.getImagePath());
                    file.delete();
                }
                bundle.putString("name", userName);
                ARouterUtil.navigation(ARouterConstant.FACE_RIGSTER_ACTIVITY, bundle);
            }
        } else {
            ToastUtils.showSingleToast(getContext(), getString(R.string.please_select_open_face));
        }
    }

    /**
     * 指静脉注册
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void fingerRegister() {
        FingerApi.getInstance().cancelFingerImg(Objects.requireNonNull(getActivity())
                , res -> {
                    if (res == 1) {
                        Logger.d("UserManagerFragment 2 指静脉模板数量：" + allFingerSize);
                        FingerApi.getInstance().register(Objects.requireNonNull(getActivity()),
                                allFingerData, allFingerSize, msg -> {
                                    if (msg.getResult() == 8) {
                                        //可插入finger表的指静脉模板
                                        insertOrReplaceFinger(msg.getFingerData());
                                    } else {
                                        ToastUtils.showSquareImgToast(getActivity()
                                                , msg.getTip()
                                                , ActivityCompat.getDrawable(getActivity(), R.drawable.cry_icon));
                                    }
                                });
                    }
                });
    }

    /**
     * 指静脉list转成byte[]
     */
    private void fingerListToFingerByte() {
        ArrayList<Finger6> finger6ArrayList = FingerListManager.getInstance().getFingerData();
        if (finger6ArrayList != null && finger6ArrayList.size() > 0) {
            int fingerSize = finger6ArrayList.size();
            byte[] fingerData = new byte[AppConstant.FINGER6_DATA_SIZE * fingerSize];
            for (int i = 0; i < fingerSize; i++) {
                byte[] finger6Feature = finger6ArrayList.get(i).getFinger6Feature();
                System.arraycopy(finger6Feature, 0, fingerData,
                        AppConstant.FINGER6_DATA_SIZE * i, AppConstant.FINGER6_DATA_SIZE);
            }
            this.allFingerData = fingerData;
            this.allFingerSize = fingerSize;
        }
    }

    /**
     * 目前只考虑注册6特征模式
     *
     * @param fingerData 指静脉数据
     */
    private void insertOrReplaceFinger(byte[] fingerData) {
        Finger6 finger6 = new Finger6();
        finger6.setFinger6Feature(fingerData);
        DBUtil dbUtil = BaseApplication.getDbUtil();
        dbUtil.setDbCallBack(new DbCallBack<Finger6>() {
            @Override
            public void onSuccess(Finger6 result) {
                Logger.d("Fg6成功插入：" + result);
                //可插入User表的数据
                UserRegisterFragment.this.fg6 = result;
                registerBtn.setVisibility(View.VISIBLE);
                FingerServiceUtil.getInstance().addFinger(result.getFinger6Feature());
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
        }).insertAsyncSingle(finger6);
    }

    /**
     * 查询所有用户的工号
     *
     * @param staffNo     目标工号
     * @param queryUserNo 用户号的查询条件
     */
    private void queryAllUserNo(String staffNo, QueryUserNo queryUserNo) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        UserDao userDao = dbUtil.getDaoSession().getUserDao();
        QueryBuilder<User> userQueryBuilder = userDao.queryBuilder();
        List<User> list = userQueryBuilder.where(UserDao.Properties.WorkNum.eq(staffNo)).list();
        if (list.size() > 0) {
            if (queryUserNo != null)
                queryUserNo.queryUserNo(true);
        } else {
            if (queryUserNo != null)
                queryUserNo.queryUserNo(false);
        }
    }

    /**
     * 插入密码数据到数据库
     *
     * @param pw 密码
     */
    private void insertOrReplacePw(Pw pw) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        dbUtil.setDbCallBack(new DbCallBack<Pw>() {
            @Override
            public void onSuccess(List<Pw> result) {

            }

            @Override
            public void onSuccess(Pw result) {
                Logger.d("Pw成功插入：" + result);
                //可添加到User表的pwd
                UserRegisterFragment.this.pwd = result;
                registerBtn.setVisibility(View.VISIBLE);
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

    /**
     * 查询所有用户的号
     */
    private void queryAllUserNo() {
        //查出所有用户的工号
        DBUtil dbUtil = BaseApplication.getDbUtil();
        UserDao userDao = dbUtil.getDaoSession().getUserDao();
        QueryBuilder<User> userQueryBuilder = userDao.queryBuilder();
        dbUtil.setDbCallBack(new DbCallBack<User>() {
            @Override
            public void onSuccess(User result) {

            }

            @Override
            public void onSuccess(List<User> result) {
                if (result.size() > 0) {
                    workNoList = new ArrayList<>();
                    for (int i = 0; i < result.size(); i++) {
                        String workNum = result.get(i).getWorkNum();
                        workNoList.add(workNum);
                    }
                }
            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onNotification(boolean result) {

            }
        }).queryAsyncAll(User.class, userQueryBuilder);
    }

    /**
     * 接收人脸注册的结果
     *
     * @param face 人脸的数据
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(Face face) {
        if (face != null) {
            ToastUtils.showSquareImgToast(getActivity(), getString(com.face.R.string.face_register_success),
                    null);
            UserRegisterFragment.this.face = face;
            Log.d("777", face.getImagePath());
            registerBtn.setVisibility(View.VISIBLE);
        } else {
            ToastUtils.showSquareImgToast(getActivity(), getString(com.face.R.string.face_register_fail),
                    ActivityCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.cry_icon));
        }
    }

    /**
     * 跳转验证页面
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void skipVerify() {
        //跳转人脸识别的界面(只要开启了人脸)
        if (SPUtil.getOpenFace()) {
            if (manageActivity != null)
                manageActivity.skipFaceActivity();
        } else {
            //跳转默认的识别页面(没有开启人脸)
            SkipActivityUtil.skipActivity(getActivity(), DefaultVerifyActivity.class);
        }
        Objects.requireNonNull(getActivity()).finish();
    }

    /**
     * 检查用户信息是否已经保存
     *
     * @param saveUserInfo 要保存的用户信息
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void checkRegisterContent(SaveUserInfo saveUserInfo) {
        if (pwd == null && fg6 == null && idCard == null && face == null) {
            saveUserInfo.saveUserInfo(false);
        } else {
            AskDialog.showAskSaveDialog(Objects.requireNonNull(getActivity()),
                    null, new PositionBtnClickListener() {
                        @Override
                        public void positionClickListener(int flag) {
                            if (flag == 1) {
                                registerUser(saveUserInfo);
                            }
                        }
                    }, new CancelBtnClickListener() {
                        @Override
                        public void cancelClickListener() {
                            //删除已经出入数据库的数据
                            DBUtil dbUtil = BaseApplication.getDbUtil();
                            if (pwd != null) {
                                dbUtil.delete(pwd);
                                pwd = null;
                            }
                            if (fg6 != null) {
                                dbUtil.delete(fg6);
                                fg6 = null;
                            }
                            if (idCard != null) {
                                dbUtil.delete(idCard);
                                idCard = null;
                            }
                            if (face != null) {
                                dbUtil.delete(face);
                                face = null;
                            }
                            if (saveUserInfo != null)
                                saveUserInfo.saveUserInfo(false);
                        }
                    });
        }
    }


}
