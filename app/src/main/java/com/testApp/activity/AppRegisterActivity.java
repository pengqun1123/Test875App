package com.testApp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baselibrary.base.BaseActivity;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.custom.CEditText;
import com.baselibrary.listener.OnceClickListener;
import com.baselibrary.pojo.Pw;
import com.baselibrary.util.EtClearUtil;
import com.baselibrary.util.SoftInputKeyboardUtils;
import com.baselibrary.util.ToastUtils;
import com.testApp.AppDialog;
import com.testApp.R;

/**
 * 该注册页面以公司考勤应用场景为例
 */
public class AppRegisterActivity extends BaseActivity {

    private AppCompatEditText nameEt;
    private AppCompatEditText ageEt;
    private AppCompatEditText phoneEt;
    private AppCompatEditText companyNameEt;
    private AppCompatEditText departmentEt;
    private AppCompatButton pwBtn;
    private AppCompatButton inputPwBtn;
    private View nameBottomLine;

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
        inputPwBtn = bindViewWithClick(R.id.inputPwBtn, true);
        pwBtn = bindViewWithClick(R.id.pwBtn, true);
        nameBottomLine = bindViewWithClick(R.id.nameBottomLine, true);
        SoftInputKeyboardUtils.hiddenKeyboard(nameEt);
        spinnerListener();

//        etChangeListener(nameEt);
//        etChangeListener(ageEt);
//        etChangeListener(phoneEt);
//        etChangeListener(companyNameEt);
//        etChangeListener(departmentEt);
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
            case R.id.inputPwBtn:
                pwRegister();
                break;
            case R.id.pwBtn:
                String userName = nameEt.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    nameBottomLine.setBackgroundColor(R.color.red);
                    ToastUtils.showSingleToast(this, getString(R.string.please_input_name));
                    return;
                }
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

                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //监听EdiText的变化
    private void etChangeListener(AppCompatEditText et) {
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
                    et.setVisibility(View.GONE);
                } else {
                    et.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void pwRegister() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.input_pw_view, null);
        CEditText inputPw = dialogView.findViewById(R.id.inputPw);
        AppCompatImageView dismissBtn = dialogView.findViewById(R.id.dismissBtn);
        LinearLayout btnParent = dialogView.findViewById(R.id.btnParent);
        AppCompatButton nextBtn = dialogView.findViewById(R.id.nextBtn);
        AppCompatButton cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        AppCompatButton positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        AppCompatTextView inputPwTitle = dialogView.findViewById(R.id.inputPwTitle);
        final String[] pw1 = new String[1];
        nextBtn.setOnClickListener(new OnceClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                String pw = inputPw.getText().toString().trim();
                if (pw.length() < 8) {
                    ToastUtils.showSquareTvToast(
                            AppRegisterActivity.this, getString(R.string.please_input_8_pw));
                    return;
                }
                pw1[0] = pw;
                inputPw.getText().clear();
                inputPwTitle.setText(getString(R.string.check_pw));
                nextBtn.setVisibility(View.GONE);
                btnParent.setVisibility(View.VISIBLE);
            }
        });
        Dialog dialog = AppDialog.gmDialog(this, dialogView, false);
        dismissBtn.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                pw1[0] = null;
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                pw1[0] = null;
                dialog.dismiss();
            }
        });
        positiveBtn.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String pw = inputPw.getText().toString().trim();
                if (pw.length() < 8) {
                    ToastUtils.showSquareTvToast(
                            AppRegisterActivity.this, getString(R.string.please_input_8_pw));
                    return;
                }
                if (pw.equals(pw1[0])) {
                    //存储密码，注册成功
                    long pwId = System.currentTimeMillis();
                    Pw pwData = new Pw();
                    pwData.setUId(pwId);
                    pwData.setPassword(pw);
                    BaseApplication.getDbUtil().getDaoSession().getPwDao().insertOrReplace(pwData);

                    ToastUtils.showSquareImgToast(AppRegisterActivity.this,
                            getString(R.string.pw_register_success),
                            ActivityCompat.getDrawable(AppRegisterActivity.this,
                                    R.drawable.ic_emoje));
                    pw1[0] = null;
                    inputPwBtn.setVisibility(View.GONE);
                    pwBtn.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                } else {
                    ToastUtils.showSquareImgToast(
                            AppRegisterActivity.this,
                            getString(R.string.pw_register_no_eq),
                            ActivityCompat.getDrawable(AppRegisterActivity.this,
                                    R.drawable.cry_icon)
                    );
                }
            }
        });
    }

}
