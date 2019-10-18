package com.testApp.activity;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.base.BaseActivity;
import com.testApp.R;
@Route(path = ARouterConstant.USER_CENTER_ACTIVITY)
public class UserCenterActivity extends BaseActivity {

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
        AppCompatEditText nameEt = bindViewWithClick(R.id.nameEt, true);
        AppCompatEditText ageEt = bindViewWithClick(R.id.ageEt, true);
        AppCompatTextView sex = bindViewWithClick(R.id.sex, true);
        AppCompatEditText staffNoEt = bindViewWithClick(R.id.staffNoEt, true);
        AppCompatEditText phoneEt = bindViewWithClick(R.id.phoneEt, true);
        AppCompatEditText companyNameEt = bindViewWithClick(R.id.companyNameEt, true);
        AppCompatEditText departmentEt = bindViewWithClick(R.id.departmentEt, true);
        AppCompatTextView fingerModel = bindViewWithClick(R.id.fingerModel, true);
        AppCompatTextView faceModel = bindViewWithClick(R.id.faceModel, true);
        AppCompatTextView idCardModel = bindViewWithClick(R.id.idCardModel, true);
        AppCompatTextView pwModel = bindViewWithClick(R.id.pwModel, true);
        AppCompatButton registerBtn = bindViewWithClick(R.id.registerBtn, true);

        backBtn.setVisibility(View.VISIBLE);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {

    }


}
