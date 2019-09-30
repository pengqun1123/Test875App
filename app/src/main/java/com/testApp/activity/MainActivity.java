package com.testApp.activity;

import android.app.Dialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.listener.OnceClickListener;
import com.baselibrary.model.TestBean;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.ToastUtils;
import com.baselibrary.util.dialogUtil.AppDialog;
import com.finger.fingerApi.FingerApi;
import com.orhanobut.logger.Logger;
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.pojos.Msg;
import com.sd.tgfinger.tgApi.Constant;
import com.testApp.R;
import com.baselibrary.constant.AppConstant;


/**
 * Created By pq
 * on 2019/9/9
 */
public class MainActivity extends BaseActivity {

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
        //监测设备的连接状态
//        TGBApi.getTGAPI().startDevService(this);
        bindViewWithClick(R.id.fingerModel, true);
        bindViewWithClick(R.id.faceModel, true);
        bindViewWithClick(R.id.idCardModel, true);
        bindViewWithClick(R.id.pwModel, true);
        bindViewWithClick(R.id.fingerIdCardModel, true);
        bindViewWithClick(R.id.faceIdCardModel, true);
        bindViewWithClick(R.id.fingerFaceModel, true);
        bindViewWithClick(R.id.fingerPwModel, true);
        bindViewWithClick(R.id.facePwModel, true);
        bindViewWithClick(R.id.add, true);
        bindViewWithClick(R.id.lose, true);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        TGBApi.getTGAPI().unbindDevService(this);
    }

    @Override
    protected void initData() {
        //初始化指静脉
        initFinger();

    }

    //临时放到这里初始化指静脉
    private void initFinger() {
        FingerApi.fingerInit(this, null, new FvInitCallBack() {
            @Override
            public void fvInitResult(Msg msg) {
                Integer result = msg.getResult();
                if (result == 1) {
                    openFinger();
                } else {
                    Logger.e("指静脉初始化失败:" + msg.getTip());
                }
            }
        });
    }

    private void openFinger() {
        FingerApi.openDev(this, Constant.TEMPL_MODEL_6, true, new DevOpenCallBack() {
            @Override
            public void devOpenResult(Msg msg) {
                Logger.d(msg.getTip());
            }
        }, new DevStatusCallBack() {
            @Override
            public void devStatus(Msg msg) {
                Logger.d("设备的连接状态:" + msg.getTip());
            }
        });
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
            case R.id.add:
                //声音加
                BaseApplication.AP.increaceVolume();
                BaseApplication.AP.play_di();
                break;
            case R.id.lose:
                //声音减
                BaseApplication.AP.decreaseVolume();
                BaseApplication.AP.play_di();
                break;
        }
    }

    private void showAskDialog(Integer msgRes, Integer flag) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.tip_dialog_view, null);
        AppCompatTextView inputPwTitle = dialogView.findViewById(R.id.inputPwTitle);
        AppCompatTextView tip = dialogView.findViewById(R.id.tip);
        LinearLayout btnParent = dialogView.findViewById(R.id.btnParent);
        AppCompatButton cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        AppCompatButton positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        inputPwTitle.setText(getString(R.string.verify_model));
        tip.setText(msgRes);
        btnParent.setVisibility(View.VISIBLE);
        Dialog dialog = AppDialog.gmDialog(this, dialogView, true);
        cancelBtn.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                dialog.dismiss();
            }
        });
        positiveBtn.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                routerSkip(flag);
                dialog.dismiss();
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
