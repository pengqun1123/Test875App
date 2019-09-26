package com.testApp.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.model.TestBean;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.ToastUtils;
import com.baselibrary.util.dialogUtil.DialogCallBack;
import com.testApp.AppDialog;
import com.testApp.R;
import com.testApp.constant.AppConstant;


/**
 * Created By pq
 * on 2019/9/9
 */
public class MainActivity extends BaseActivity {

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void initToolBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Toolbar toolBar = bindViewWithClick(R.id.toolbar, false);
        if (toolBar == null) {
            return;
        }
        TextView toolbarTitle = bindViewWithClick(R.id.toolbar_title, false);
        String title = getString(R.string.select_verify_model);
        toolbarTitle.setText(title);

    }

    @Override
    protected Integer contentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        bindViewWithClick(R.id.fingerModel, true);
        bindViewWithClick(R.id.faceModel, true);
        bindViewWithClick(R.id.idCardModel, true);
        bindViewWithClick(R.id.pwModel, true);
        bindViewWithClick(R.id.fingerIdCardModel, true);
        bindViewWithClick(R.id.faceIdCardModel, true);
        bindViewWithClick(R.id.fingerFaceModel, true);
        bindViewWithClick(R.id.fingerPwModel, true);
        bindViewWithClick(R.id.facePwModel, true);


    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.fingerModel:
                ToastUtils.showSquareTvToast(this, "指静脉验证");
                break;
            case R.id.faceModel:
                ToastUtils.showSquareTvToast(this, "人脸验证");

                break;
            case R.id.idCardModel:
                ToastUtils.showSquareTvToast(this, "身份证验证");
                break;
            case R.id.pwModel:
                showAskDialog(R.string.pw_verify_model_tip, AppConstant.PW_MODEL);
                break;
            case R.id.fingerIdCardModel:
                ToastUtils.showSquareTvToast(this, "指静脉+身份证验证");

                break;
            case R.id.faceIdCardModel:
                ToastUtils.showSquareTvToast(this, "人脸+身份证验证");

                break;
            case R.id.fingerFaceModel:
                ToastUtils.showSquareTvToast(this, "指静脉+人脸验证");

                break;
            case R.id.fingerPwModel:
                ToastUtils.showSquareTvToast(this, "指静脉+密码验证");

                break;
            case R.id.facePwModel:
                ToastUtils.showSquareTvToast(this, "人脸+密码验证");

                break;

        }
    }

    private void showAskDialog(Integer msgRes, Integer flag) {
        AppDialog.showAskDialog(this, msgRes, new DialogCallBack() {
            @Override
            public void positiveClick(DialogInterface dialog) {
                routerSkip(flag);
            }

            @Override
            public void negativeClick(DialogInterface dialog) {

            }
        });
    }

    private void routerSkip(Integer flag) {
        if (flag.equals(AppConstant.PW_MODEL)) {
            SkipActivityUtil.skipActivity(this, AppRegisterActivity.class);
        }
    }

    private void skipTest() {

        TestBean testBean = new TestBean("小刚", 24);
        ARouter.getInstance().build(ARouterConstant.FINGER_ACTIVITY
                , ARouterConstant.GROUP_FINGER)
                .withString("Address", "杭州市西湖区昌接任大姐")
                .withParcelable("testBean", testBean)
                .navigation();

    }
}
