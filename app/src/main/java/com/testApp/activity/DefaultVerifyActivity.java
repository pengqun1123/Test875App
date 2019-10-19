package com.testApp.activity;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.callBack.CardInfoListener;
import com.baselibrary.callBack.FingerVerifyResultListener;
import com.baselibrary.constant.AppConstant;
import com.baselibrary.pojo.IdCard;
import com.baselibrary.service.IdCardService;
import com.baselibrary.util.AnimatorUtils;
import com.baselibrary.util.CalendarUtil;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.ToastUtils;

import com.baselibrary.util.VerifyResultUi;
import com.finger.callBack.FingerDevStatusCallBack;
import com.finger.fingerApi.FingerApi;
import com.finger.service.FingerServiceUtil;
import com.orhanobut.logger.Logger;

import com.testApp.R;

import java.text.MessageFormat;


public class DefaultVerifyActivity extends BaseActivity implements FingerDevStatusCallBack,
        CardInfoListener, FingerVerifyResultListener {

    private IdCardService idCardService;
    private AppCompatImageView gear1, gear2, gear3, gear4;
    private ObjectAnimator gear1Anim, gear2Anim, gear3Anim, gear4Anim;
    private AppCompatTextView currentTime, currentDate;

    @Override
    protected Integer contentView() {
        return R.layout.activity_default_register;
    }

    @Override
    protected void initView() {
        bindViewWithClick(R.id.homeMenu, true);
        currentTime = bindViewWithClick(R.id.currentTime, true);
        currentDate = bindViewWithClick(R.id.currentDate, true);
        gear1 = bindViewWithClick(R.id.gear1, true);
        gear2 = bindViewWithClick(R.id.gear2, true);
        gear3 = bindViewWithClick(R.id.gear3, true);
        gear4 = bindViewWithClick(R.id.gear4, true);

        gear1Anim = AnimatorUtils.rotateAnim(gear1, 3900L, 359F);
        gear2Anim = AnimatorUtils.rotateAnim(gear2, 3100L, 359F);
        gear3Anim = AnimatorUtils.rotateAnim(gear3, 3400L, -359F);
        gear4Anim = AnimatorUtils.rotateAnim(gear4, 2800L, -359F);

        setCurrentTime();
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
        //接收指静脉设备的连接状态
        FingerApi.getInstance().receiveFingerDevConnectStatus(this);
        idCardService = ARouter.getInstance().navigation(IdCardService.class);
        if (SPUtil.getCardVerifyFlag()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    idCardService.verify_IdCard(DefaultVerifyActivity.this);
                }
            }).start();
        }
        getSystemTime();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        if (gear1Anim != null) {
            AnimatorUtils.resumeAnim(gear1Anim);
        }
        if (gear2Anim != null) {
            AnimatorUtils.resumeAnim(gear2Anim);
        }
        if (gear3Anim != null) {
            AnimatorUtils.resumeAnim(gear3Anim);
        }
        if (gear4Anim != null) {
            AnimatorUtils.resumeAnim(gear4Anim);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FingerServiceUtil.getInstance().reStartFingerVerify();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();
        if (gear1Anim != null) {
            AnimatorUtils.pauseAnim(gear1Anim);
        }
        if (gear2Anim != null) {
            AnimatorUtils.pauseAnim(gear2Anim);
        }
        if (gear3Anim != null) {
            AnimatorUtils.pauseAnim(gear3Anim);
        }
        if (gear4Anim != null) {
            AnimatorUtils.pauseAnim(gear4Anim);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FingerServiceUtil.getInstance().pauseFingerVerify();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FingerServiceUtil.getInstance().unbindFingerService(this);
        isStartService = false;
        if (idCardService != null)
            idCardService.destroyIdCard();
        if (gear1Anim != null) {
            AnimatorUtils.cancelAnim(gear1Anim);
        }
        if (gear2Anim != null) {
            AnimatorUtils.cancelAnim(gear2Anim);
        }
        if (gear3Anim != null) {
            AnimatorUtils.cancelAnim(gear3Anim);
        }
        if (gear4Anim != null) {
            AnimatorUtils.cancelAnim(gear4Anim);
        }
        unregisterReceiver(systemTimeReceiver);
    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.homeMenu:
                SkipActivityUtil.skipActivity(this, MenuActivity.class);
                break;

        }
    }

    private static Boolean isStartService = false;

    @Override
    public void fingerDevStatus(int res, String msg) {
        if (res == 1)
            FingerServiceUtil.getInstance().startFingerService(this,
                    isStart -> {
                        isStartService = isStart;
                        FingerServiceUtil.getInstance().setFingerVerifyResult(this);
                    });
    }

    @Override
    public void onGetCardInfo(IdCard idCard) {
        if (idCard == null) {
            Logger.d("身份证验证失败");
            VerifyResultUi.showVerifyFail(this,
                    getString(R.string.verify_fail), true);
        } else {
            Logger.d("身份证验证成功");
            VerifyResultUi.showVerifySuccess(this,
                    getString(R.string.verify_success), true);
        }
    }

    @Override
    public void onRegisterResult(boolean result, IdCard idCard) {
        if (result)
            Logger.d("身份证注册的结果：" + idCard.toString());
    }

    @Override
    public void fingerVerifyResult(int res, String msg, int score,
                                   int index, Long fingerId, byte[] updateFinger) {
        if (res == 1) {
            VerifyResultUi.showVerifySuccess(this, getString(R.string.verify_success), true);
            Intent intent = new Intent();
            intent.setAction(AppConstant.USER_MENU_RECEIVER);
            intent.putExtra(AppConstant.VERIFY_RESULT_TYPE, AppConstant.FINGER_MODEL);
            intent.putExtra(AppConstant.FINGER_VERIFY_RESULT, res);
            sendBroadcast(intent);
        } else {
            VerifyResultUi.showVerifySuccess(this, getString(R.string.verify_fail), true);
        }
    }

    /**
     * 注册实时获取系统时间的广播
     */
    private void getSystemTime() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(systemTimeReceiver, filter);
    }

    private BroadcastReceiver systemTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取系统时间
            String action = intent.getAction();
            if (action != null && action.equals(Intent.ACTION_TIME_TICK)) {
                setCurrentTime();
            }
        }
    };

    private void setCurrentTime() {
        String hour = String.valueOf(CalendarUtil.getHour());
        String minute = String.valueOf(CalendarUtil.getMinute());
        String year = String.valueOf(CalendarUtil.getYear());
        String month = String.valueOf(CalendarUtil.getMonth());
        String day = String.valueOf(CalendarUtil.getDay());
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        if (year.length() == 1) {
            year = "0" + year;
        }
        if (month.length() == 1) {
            month = "0" + month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }
        currentTime.setText(MessageFormat.format("{0}:{1}", hour, minute));
        currentDate.setText(MessageFormat.format("{0}-{1}-{2}", year, month, day));
    }
}
