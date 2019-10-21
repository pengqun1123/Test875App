package com.testApp.activity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.callBack.CardInfoListener;
import com.baselibrary.constant.AppConstant;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.dao.db.PwDao;
import com.baselibrary.dao.db.UserDao;
import com.baselibrary.pojo.Face;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.pojo.IdCard;
import com.baselibrary.pojo.Manager;
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
import com.sun.jna.platform.win32.Netapi32Util;
import com.testApp.R;
import com.testApp.callBack.CancelBtnClickListener;
import com.testApp.callBack.PositionBtnClickListener;
import com.testApp.callBack.SaveUserInfo;
import com.testApp.dialog.AskDialog;
import com.testApp.fragment.UserRegisterFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Route(path = ARouterConstant.USER_CENTER_ACTIVITY)
public class UserCenterActivity extends BaseActivity {

    private AppCompatEditText nameEt;
    private AppCompatEditText ageEt;
    private String sex;
    private AppCompatEditText staffNoEt;
    private AppCompatEditText phoneEt;
    private AppCompatEditText companyNameEt;
    private AppCompatEditText departmentEt;
    private AppCompatTextView fingerModel;
    private AppCompatTextView faceModel;
    private AppCompatTextView idCardModel;
    private AppCompatTextView pwModel;
    private AppCompatButton registerBtn;
    private AppCompatTextView tv_sex;

    @Override
    protected Integer contentView() {
        return R.layout.activity_user_center;
    }

    @Override
    protected void initToolBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Toolbar toolBar = bindViewWithClick(R.id.toolbar, false);
        if (toolBar == null) {
            return;
        }

        TextView toolbarTitle = bindViewWithClick(R.id.toolbar_title, false);
        String title = getString(R.string.user_center);
        toolbarTitle.setText(title);
    }

    @Override
    protected void initView() {
        AppCompatImageView backBtn = bindViewWithClick(R.id.backBtn, true);
        nameEt = bindViewWithClick(R.id.nameEt, true);
        ageEt = bindViewWithClick(R.id.ageEt, true);
        tv_sex = bindViewWithClick(R.id.sex, true);
        staffNoEt = bindViewWithClick(R.id.staffNoEt, true);
        phoneEt = bindViewWithClick(R.id.phoneEt, true);
        companyNameEt = bindViewWithClick(R.id.companyNameEt, true);
        departmentEt = bindViewWithClick(R.id.departmentEt, true);
        fingerModel = bindViewWithClick(R.id.fingerModel, true);
        faceModel = bindViewWithClick(R.id.faceModel, true);
        idCardModel = bindViewWithClick(R.id.idCardModel, true);
        pwModel = bindViewWithClick(R.id.pwModel, true);
        registerBtn = bindViewWithClick(R.id.registerBtn, true);
        backBtn.setVisibility(View.VISIBLE);
        staffNoEt.setEnabled(false);
        staffNoEt.setClickable(false);
        staffNoEt.setFocusable(false);
        SoftInputKeyboardUtils.hiddenKeyboard(nameEt);
        spinnerListener();
    }

    private void spinnerListener() {
        AppCompatSpinner spinner = bindViewWithClick(R.id.spinner, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view;
                if (textView!=null) {
                    String selectItem = textView.getText().toString();
                    if (selectItem.equals(getString(R.string.sex))) {
                        sex = "";
                    } else {
                        sex = selectItem;
                    }
                    tv_sex.setText(sex);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private Long pwdId;
    private Long idCardId;
    private Long faceId;
    private Long fg6Id;

    private Pw pwd;
    private IdCard idCard;
    private Face face;
    private Finger6 fg6;

    private User user;
    private byte[] allFingerData;
    private int allFingerSize;
    @Override
    protected void initData() {
        Bundle extras = getIntent().getExtras();
        int type = extras.getInt(AppConstant.VERIFY_RESULT_TYPE);
        long id = extras.getLong(AppConstant.VERIFY_TYPE_ID);
        Log.d("445",id+"");
        DBUtil dbUtil = BaseApplication.getDbUtil();
        dbUtil.setDbCallBack(new DbCallBack<User>() {
            @Override
            public void onSuccess(User result) {

            }

            @Override
            public void onSuccess(List<User> result) {
               if (result.size()>0){
                   user = result.get(0);
                   nameEt.setText(user.getName());
                   ageEt.setText(user.getAge());
                   tv_sex.setText(user.getSex());
                   staffNoEt.setText(user.getWorkNum());
                   phoneEt.setText(user.getPhone());
                   companyNameEt.setText(user.getOrganizName());
                   departmentEt.setText(user.getSection());
                   faceId=user.getFaceId();
                   fg6Id=user.getFinger6Id();
                   idCardId=user.getCardId();
                   pwdId=user.getPwId();
               }
            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onNotification(boolean result) {

            }
        });

       if (type==1){
            WhereCondition whereCondition= UserDao.Properties.Finger6Id.eq(id);
            dbUtil.queryAsync(User.class,whereCondition);
        }else if (type==2){
            WhereCondition whereCondition= UserDao.Properties.FaceId.eq(id);
            dbUtil.queryAsync(User.class,whereCondition);
        }else if(type==3){
            WhereCondition whereCondition= UserDao.Properties.CardId.eq(id);
            dbUtil.queryAsync(User.class,whereCondition);
        }else if (type==4){
            WhereCondition whereCondition= UserDao.Properties.PwId.eq(id);
            dbUtil.queryAsync(User.class,whereCondition);
        }
        EventBus.getDefault().register(this);
        fingerListToFingerByte();
        registerBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                boolean changer = isChanger();
                Log.d("666",changer+"");
                if (changer){
                    requestIsSave();
                }else {
                    SkipActivityUtil.skipActivity(UserCenterActivity.this,MenuActivity.class);

                }
                break;
            case R.id.fingerModel:
                if (fg6Id!=null) {
                    AskDialog.showAskUpdateDialog(this, getString(R.string.ask_user_register_msg_update), new PositionBtnClickListener() {
                        @Override
                        public void positionClickListener(int flag) {
                            fingerRegister();
                        }
                    }, new CancelBtnClickListener() {
                        @Override
                        public void cancelClickListener() {

                        }
                    });
                }else {
                    fingerRegister();
                }

                break;
            case R.id.faceModel:
                //人脸注册
                if (faceId!=null) {
                    AskDialog.showAskUpdateDialog(this, getString(R.string.ask_user_register_msg_update), new PositionBtnClickListener() {
                        @Override
                        public void positionClickListener(int flag) {
                            faceRegister();
                        }
                    }, new CancelBtnClickListener() {
                        @Override
                        public void cancelClickListener() {

                        }
                    });
                }else {
                    faceRegister();
                }
                break;
            case R.id.idCardModel:
                //身份证注册
                if (idCardId!=null) {
                    AskDialog.showAskUpdateDialog(this, getString(R.string.ask_user_register_msg_update), new PositionBtnClickListener() {
                        @Override
                        public void positionClickListener(int flag) {
                          idCardRegister();
                        }
                    }, new CancelBtnClickListener() {
                        @Override
                        public void cancelClickListener() {

                        }
                    });
                }else {
                    idCardRegister();
                }
                break;
            case R.id.pwModel:
                //密码模式注册
                if (pwdId!=null) {
                    AskDialog.showAskUpdateDialog(this, getString(R.string.ask_user_register_msg_update), new PositionBtnClickListener() {
                        @Override
                        public void positionClickListener(int flag) {
                            pwRegister();
                        }
                    }, new CancelBtnClickListener() {
                        @Override
                        public void cancelClickListener() {

                        }
                    });
                }else {
                    pwRegister();
                }
                break;
            case R.id.registerBtn:
                if (isChanger()) {
                    registerUser();
                }else {
                    SkipActivityUtil.skipActivity(UserCenterActivity.this,MenuActivity.class);
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void requestIsSave() {
        AskDialog.showAskSaveDialog(Objects.requireNonNull(this),
                getString(R.string.ask_user_update_msg_save), new PositionBtnClickListener() {
                    @Override
                    public void positionClickListener(int flag) {
                       registerUser();
                    }
                }, new CancelBtnClickListener() {
                    @Override
                    public void cancelClickListener() {
                        //删除已经出入数据库的数据
                        DBUtil dbUtil = BaseApplication.getDbUtil();
                        if (pwd != null) {

                            dbUtil.insertOrReplace(user.getPw());
                            pwd = null;
                        }
                        if (fg6 != null) {
                            dbUtil.insertOrReplace(user.getFinger6());
                            fg6 = null;
                        }
                        if (idCard != null) {
                            dbUtil.insertOrReplace(user.getIdCard());
                            idCard = null;
                        }
                        if (face != null) {
                            dbUtil.insertOrReplace(user.getFace());
                            face = null;
                        }
                        SkipActivityUtil.skipActivity(UserCenterActivity.this, MenuActivity.class);
                        finish();
                    }
                });
    }

    private boolean isChanger() {
        boolean isChanger=false;
        String userName = nameEt.getText().toString().trim();

        String userAge = ageEt.getText().toString().trim();

        String userPhone = phoneEt.getText().toString().trim();

        String companyName = companyNameEt.getText().toString().trim();

        String department = departmentEt.getText().toString().trim();

        String sex= tv_sex.getText().toString().trim();

        if (!user.getName().equals(userName)) {
            isChanger = true;
            return isChanger;
        }
        if (!user.getAge().equals(userAge)) {
            isChanger = true;
            return isChanger;
        }
        if (!user.getPhone().equals(userPhone)) {
            isChanger = true;
            return isChanger;
        }
        if (!user.getOrganizName().equals(companyName)) {
            isChanger = true;
            return isChanger;
        }
        if (!user.getSection().equals(department)) {
            isChanger = true;
            return isChanger;
        }
        if (!user.getSex().equals(sex)){
            return  true;
        }
        if (face!=null){
            return true;
        }

        if (pwd!=null){
            return true;
        }

        if (fg6!=null){
            return true;
        }

        if (idCard!=null){
            return true;
        }

        return isChanger;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void registerUser() {
        @SuppressLint("ResourceAsColor")
            String userName = nameEt.getText().toString().trim();

            String userAge = ageEt.getText().toString().trim();

            String userPhone = phoneEt.getText().toString().trim();

            String companyName = companyNameEt.getText().toString().trim();

            String department = departmentEt.getText().toString().trim();


        //插入user数据
            user.setName(userName);
            user.setAge(userAge);
            user.setSex(sex);
            user.setPhone(userPhone);
            user.setOrganizName(companyName);
            user.setSection(department);

                        if (pwd != null) {
                            user.setPwId(pwd.getUId());
                            user.setPw(pwd);
                        }

                        if (fg6 != null) {
                            user.setFinger6Id(fg6.getUId());
                            user.setFinger6(fg6);
                            FingerListManager.getInstance().addFingerData(fg6);
                            FingerServiceUtil.getInstance().updateFingerData();
                        }
                        if (idCard != null) {
                            user.setCardId(idCard.getUId());
                            user.setIdCard(idCard);
                        }
                        if (face != null) {
                            user.setFaceId(face.getUId());
                            user.setFace(face);
                        }
                        try {
                            DBUtil dbUtil = BaseApplication.getDbUtil();
                            dbUtil.insertOrReplace(user);

                            ToastUtils.showSquareImgToast(this, getString(R.string.register_success)
                                    , ActivityCompat.getDrawable(Objects.requireNonNull(this),
                                            R.drawable.ic_emoje));
                            SkipActivityUtil.skipActivity(this, MenuActivity.class);
                            finish();
                        }catch (Exception e){

                        }
      }

    /**
     * 跳转验证页面
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void skipVerify() {
        //跳转人脸识别的界面(只要开启了人脸)
        if (SPUtil.getOpenFace()) {
            SkipActivityUtil.skipActivity(this,V3FaceRecActivity.class);
        } else {
            //跳转默认的识别页面(没有开启人脸)
            SkipActivityUtil.skipActivity(this, DefaultVerifyActivity.class);
        }
        Objects.requireNonNull(this).finish();
    }



    /**
     * 人脸注册
     */
    private void faceRegister() {
        Boolean openFace = SPUtil.getOpenFace();
        if (openFace) {
            String userName = nameEt.getText().toString().trim();
            if (TextUtils.isEmpty(userName)) {
                ToastUtils.showSingleToast(this, getResources().getString(R.string.please_input_name));
            } else {
                Bundle bundle = new Bundle();
                if (faceId !=null) {
                    bundle.putLong(AppConstant.FACE_ID, faceId);
                    if (face!=null) {
                        File file = new File(face.getImagePath());
                        file.delete();
                    }
                }
                bundle.putString("name", userName);
                ARouterUtil.navigation(ARouterConstant.FACE_RIGSTER_ACTIVITY, bundle);
            }
        } else {
            ToastUtils.showSingleToast(this, getString(R.string.please_select_open_face));
        }
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
          VerifyResultUi.showRegisterSuccess(this, getString(com.face.R.string.face_register_success),
                    false);
            UserCenterActivity.this.face = face;
            UserCenterActivity.this.faceId=face.getUId();
        } else {
            VerifyResultUi.showRegisterSuccess(this, getString(com.face.R.string.face_register_fail),
                    false);
        }
    }

    /**
     * 密码注册
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void pwRegister() {
        PwFactory.createPw(this, pw -> {
            DBUtil dbUtil = BaseApplication.getDbUtil();
            PwDao pwDao = dbUtil.getDaoSession().getPwDao();
            QueryBuilder<Pw> pwQueryBuilder = pwDao.queryBuilder();
            List<Pw> list = pwQueryBuilder.where(PwDao.Properties.Password.eq(pw.getPassword())).list();
            if (list != null && list.size() > 0) {
                VerifyResultUi.showRegisterFail(Objects.requireNonNull(this),
                        getString(R.string.pw_register_repeat), false);
            } else {
                if (pwdId!=null) {
                    pw.setUId(pwdId);
                }
                dbUtil.insertOrReplace(pw);
                //可添加到User表的pwd
                UserCenterActivity.this.pwdId=pw.getUId();
                UserCenterActivity.this.pwd = pw;
                VerifyResultUi.showRegisterSuccess(Objects.requireNonNull(this),
                        getString(R.string.pw_register_success), false);
            }
        });
    }

    /**
     * 身份证注册
     */
    private void idCardRegister() {
        Long idCardId = -1L;
        if (this.idCardId!=null) {
            idCardId = this.idCardId;
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
                    UserCenterActivity.this.idCard = idCard;
                    UserCenterActivity.this.idCardId=idCard.getUId();
                    VerifyResultUi.showRegisterSuccess(UserCenterActivity.this, getString(com.id_card.R.string.id_card_register_success),false);

                } else {
                    Logger.d("身份证注册失败");
                    VerifyResultUi.showRegisterSuccess(UserCenterActivity.this, getString(com.id_card.R.string.id_card_register_fail),false);
                }
            }
        }, finalIdCardId)).start();
    }


    /**
     * 指静脉注册
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void fingerRegister() {
        FingerApi.getInstance().cancelFingerImg(Objects.requireNonNull(this)
                , res -> {
                    if (res == 1) {
                        FingerApi.getInstance().register(Objects.requireNonNull(this),
                                allFingerData, allFingerSize, msg -> {
                                    if (msg.getResult() == 8) {
                                        //可插入finger表的指静脉模板
                                        insertOrReplaceFinger(msg.getFingerData());
                                    } else {
                                        ToastUtils.showSquareImgToast(this
                                                , msg.getTip()
                                                , ActivityCompat.getDrawable(this, R.drawable.cry_icon));
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
        if (fg6Id!=null) {
            finger6.setUId(fg6Id);
        }
        finger6.setFinger6Feature(fingerData);
        if (fg6Id!=null){
            FingerListManager.getInstance().coverFinger(finger6);
        }
        DBUtil dbUtil = BaseApplication.getDbUtil();
        dbUtil.setDbCallBack(new DbCallBack<Finger6>() {
            @Override
            public void onSuccess(Finger6 result) {
                Logger.d("Fg6成功插入：" + result);
                //可插入User表的数据
                UserCenterActivity.this.fg6 = result;
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
}
