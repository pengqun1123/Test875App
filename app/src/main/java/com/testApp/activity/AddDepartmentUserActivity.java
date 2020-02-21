package com.testApp.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.TextView;

import com.baselibrary.base.BaseActivity;
import com.testApp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加新用户到部门页面
 */
public class AddDepartmentUserActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.backBtn)
    AppCompatImageView backBtn;
    @BindView(R.id.rightIcon)
    AppCompatImageView rightIcon;
    @BindView(R.id.userName)
    AppCompatEditText userName;
    @BindView(R.id.userNo)
    AppCompatEditText userNo;
    @BindView(R.id.departmentValue)
    AppCompatTextView departmentValue;
    @BindView(R.id.jurisdictionValue)
    AppCompatTextView jurisdictionValue;

    @Override
    protected Integer contentView() {
        return R.layout.activity_add_department_user;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initView() {

        if (backBtn != null) {
            backBtn.setVisibility(View.VISIBLE);
        }
        if (rightIcon!=null){
            rightIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {

    }

    @OnClick({R.id.backBtn, R.id.rightIcon, R.id.departmentArrow, R.id.jurisdictionArrow,
            R.id.faceValue, R.id.faceArrow, R.id.face, R.id.finger1, R.id.finger1Arrow,
            R.id.finger2, R.id.finger2Arrow, R.id.finger3, R.id.finger3Arrow, R.id.finger4,
            R.id.finger4Arrow, R.id.finger5, R.id.finger5Arrow, R.id.finger6, R.id.finger6Arrow, R.id.moreSelect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.rightIcon:
                break;
            case R.id.departmentArrow:
                break;
            case R.id.jurisdictionArrow:
                break;
            case R.id.faceValue:
                break;
            case R.id.faceArrow:
                break;
            case R.id.face:
                break;
            case R.id.finger1:
                break;
            case R.id.finger1Arrow:
                break;
            case R.id.finger2:
                break;
            case R.id.finger2Arrow:
                break;
            case R.id.finger3:
                break;
            case R.id.finger3Arrow:
                break;
            case R.id.finger4:
                break;
            case R.id.finger4Arrow:
                break;
            case R.id.finger5:
                break;
            case R.id.finger5Arrow:
                break;
            case R.id.finger6:
                break;
            case R.id.finger6Arrow:
                break;
            case R.id.moreSelect:
                break;
        }
    }
}
