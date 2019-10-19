package com.testApp.activity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.constant.AppConstant;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.dao.db.UserDao;
import com.baselibrary.pojo.User;
import com.testApp.R;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

@Route(path = ARouterConstant.USER_CENTER_ACTIVITY)
public class UserCenterActivity extends BaseActivity {

    private AppCompatEditText nameEt;
    private AppCompatEditText ageEt;
    private AppCompatTextView sex;
    private AppCompatEditText staffNoEt;
    private AppCompatEditText phoneEt;
    private AppCompatEditText companyNameEt;
    private AppCompatEditText departmentEt;
    private AppCompatTextView fingerModel;
    private AppCompatTextView faceModel;
    private AppCompatTextView idCardModel;
    private AppCompatTextView pwModel;
    private AppCompatButton registerBtn;

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
        sex = bindViewWithClick(R.id.sex, true);
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

    }

    @Override
    protected void initData() {
        Bundle extras = getIntent().getExtras();
        int type = extras.getInt(AppConstant.VERIFY_RESULT_TYPE);
        long id = extras.getLong(AppConstant.VERIFY_TYPE_ID);
        Log.d("445",type+"");
        DBUtil dbUtil = BaseApplication.getDbUtil();
        dbUtil.setDbCallBack(new DbCallBack<User>() {
            @Override
            public void onSuccess(User result) {

            }

            @Override
            public void onSuccess(List<User> result) {
               if (result.size()>0){
                   User user = result.get(0);
                   nameEt.setText(user.getName());
                   ageEt.setText(user.getAge());
                   sex.setText(user.getSex());
                   staffNoEt.setText(user.getWorkNum());
                   phoneEt.setText(user.getPhone());
                   companyNameEt.setText(user.getOrganizName());
                   departmentEt.setText(user.getSection());
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
            WhereCondition whereCondition= UserDao.Properties.UId.eq(id);
            dbUtil.queryAsync(User.class,whereCondition);
        }else if (type==2){
            WhereCondition whereCondition= UserDao.Properties.FaceId.eq(id);
            dbUtil.queryAsync(User.class,whereCondition);
        }else if(type==3){
            id=2;
            WhereCondition whereCondition= UserDao.Properties.UId.eq(id);
            dbUtil.queryAsync(User.class,whereCondition);
        }else if (type==4){
            WhereCondition whereCondition= UserDao.Properties.PwId.eq(id);
            dbUtil.queryAsync(User.class,whereCondition);
        }
    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
               finish();
                break;
            case R.id.fingerModel:
                //指静脉注册

                break;
            case R.id.faceModel:
                //人脸注册

                break;
            case R.id.idCardModel:
                //身份证注册

                break;
            case R.id.pwModel:
                //密码模式注册

                break;
        }
    }


}
