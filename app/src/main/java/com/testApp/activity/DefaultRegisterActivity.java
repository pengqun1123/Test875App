package com.testApp.activity;

import android.app.Dialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.baselibrary.base.BaseActivity;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.custom.CEditText;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.dao.db.PwDao;
import com.baselibrary.listener.OnceClickListener;
import com.baselibrary.pojo.Pw;
import com.baselibrary.util.ToastUtils;
import com.baselibrary.util.dialogUtil.AppDialog;
import com.testApp.R;

import org.greenrobot.greendao.DbUtils;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class DefaultRegisterActivity extends BaseActivity {

    @Override
    protected Integer contentView() {
        return R.layout.activity_default_register;
    }

    @Override
    protected void initView() {
        AppCompatTextView pwVerify = bindViewWithClick(R.id.pwVerify, true);
        AppCompatTextView fingerNVerify = bindViewWithClick(R.id.fingerNVerify, true);
        AppCompatTextView finger1Verify = bindViewWithClick(R.id.finger1Verify, true);
        AppCompatTextView cardVerify = bindViewWithClick(R.id.cardVerify, true);

    }

    @Override
    protected void initToolBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        Toolbar toolBar = bindViewWithClick(R.id.toolbar, false);
//        if (toolBar == null) {
//            return;
//        }
//        TextView toolbarTitle = bindViewWithClick(R.id.toolbar_title, false);
//        String title = getString(R.string.pw_register);
//        toolbarTitle.setText(title);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.pwVerify:
                pwInputVerify();
                break;
            case R.id.fingerNVerify:

                break;
            case R.id.finger1Verify:

                break;
            case R.id.cardVerify:

                break;
        }
    }

    private void pwInputVerify() {
        View dialogView = LayoutInflater.from(this).inflate(com.pw.R.layout.input_pw_view, null);
        CEditText inputPw = dialogView.findViewById(com.pw.R.id.inputPw);
        AppCompatImageView dismissBtn = dialogView.findViewById(com.pw.R.id.dismissBtn);
        LinearLayout btnParent = dialogView.findViewById(com.pw.R.id.btnParent);
        AppCompatButton nextBtn = dialogView.findViewById(com.pw.R.id.nextBtn);
        AppCompatButton cancelBtn = dialogView.findViewById(com.pw.R.id.cancelBtn);
        AppCompatButton positiveBtn = dialogView.findViewById(com.pw.R.id.positiveBtn);
        AppCompatTextView inputPwTitle = dialogView.findViewById(com.pw.R.id.inputPwTitle);
        inputPwTitle.setText(getString(R.string.please_input_pw));
        nextBtn.setText(getString(R.string.positive));
        nextBtn.setVisibility(View.VISIBLE);
        btnParent.setVisibility(View.GONE);
        Dialog dialog = AppDialog.gmDialog(this, dialogView, false);
        dismissBtn.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                dialog.dismiss();
            }
        });
        nextBtn.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String pw = inputPw.getText().toString().trim();
                if (pw.length() < 6) {
                    ToastUtils.showSquareTvToast(DefaultRegisterActivity.this
                            , getString(com.pw.R.string.please_input_6_pw));
                    return;
                }
                queryAllPw(pw);
                dialog.dismiss();
            }
        });
    }

    private void queryAllPw(String pwd) {
        //设置查询的dialog

        DBUtil dbUtil = BaseApplication.getDbUtil();
        PwDao pwDao = dbUtil.getDaoSession().getPwDao();
        QueryBuilder<Pw> pwQueryBuilder = pwDao.queryBuilder();
        dbUtil.setDbCallBack(new DbCallBack<Pw>() {
            @Override
            public void onSuccess(Pw result) {

            }

            @Override
            public void onSuccess(List<Pw> result) {
                for (Pw pw : result) {
                    String password = pw.getPassword();
                    if (pwd.equals(password)) {
                        ToastUtils.showSquareImgToast(DefaultRegisterActivity.this,
                                getString(R.string.pw_register_success),
                                ActivityCompat.getDrawable(DefaultRegisterActivity.this,
                                        R.drawable.ic_emoje));
                        BaseApplication.AP.play_verifySuccess();
                        break;
                    }
                }
            }

            @Override
            public void onFailed() {
                ToastUtils.showSquareImgToast(DefaultRegisterActivity.this,
                        getString(R.string.pw_register_success),
                        ActivityCompat.getDrawable(DefaultRegisterActivity.this,
                                R.drawable.cry_icon));
                BaseApplication.AP.play_verifyFail();
            }

            @Override
            public void onNotification(boolean result) {

            }
        }).queryAsyncAll(Pw.class, pwQueryBuilder);
    }




}
