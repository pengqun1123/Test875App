package com.face.activity;

import android.view.View;

import com.baselibrary.base.BaseActivity;
import com.baselibrary.util.ToastUtils;
import com.face.R;

/**
 * Created by wangyu on 2019/10/15.
 */

public abstract   class FaceBaseActivity extends BaseActivity{

    @Override
    protected void initView() {
        bindViewWithClick(R.id.password_verify,true);
        bindViewWithClick(R.id.user_center,true);
        bindViewWithClick(R.id.manage_set_,true);
    }

    @Override
    protected void onViewClick(View view) {
        if (view.getId()==R.id.password_verify){
            ToastUtils.showSingleToast(this,"密码验证");
        }else if (view.getId()==R.id.user_center){
            ToastUtils.showSingleToast(this,"用户中心");
        }else if (view.getId()==R.id.manage_set_){
            ToastUtils.showSingleToast(this,"用户管理");
        }
    }
}
