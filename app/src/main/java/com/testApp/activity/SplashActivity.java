package com.testApp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baselibrary.callBack.PermissionC;
import com.baselibrary.callBack.PermissionResultCallBack;
import com.baselibrary.util.PermissionUtils;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.SkipActivityUtil;
import com.finger.callBack.DevOpenResult;
import com.finger.callBack.DevStatusResult;
import com.finger.callBack.FvInitResult;
import com.finger.fingerApi.FingerApiUtil;
import com.orhanobut.logger.Logger;
import com.testApp.R;
import com.testApp.dialog.AskDialog;

import java.io.InputStream;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        askPermission();
        //初始化数据的准备--异步加载


    }

    private void askPermission() {
        PermissionUtils.instance().requestPermission(this,
                getString(R.string.permissions), PermissionC.WR_FILES_PERMISSION,
                (PermissionResultCallBack) () -> {
                    //同意权限
                    fingerInit();
                    try {
                        Thread.sleep(300);
                        ask();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
    }


    private void ask() {
        Boolean hasManagerPwd = SPUtil.getHasManagerPwd();
        if (!hasManagerPwd) {
            //询问设置添加管理员
            AskDialog.showManagerDialog(this, new AskDialog.PositiveCallBack() {
                @Override
                public void positiveCallBack() {
                    skipVerifyACtivity();
                    SplashActivity.this.finish();
                }
            });
        } else {
            skipVerifyACtivity();
        }
    }

    private void skipVerifyACtivity() {
        Boolean openFace = SPUtil.getOpenFace();
        if (openFace) {
            //跳转人脸识别页面

        } else {
            //跳转不带人脸识别的页面
            SkipActivityUtil.skipActivity(SplashActivity.this, DefaultVerifyActivity.class);
        }
    }

    //指静脉初始化
    private void fingerInit() {
        InputStream LicenseIs = getResources().openRawResource(R.raw.license);
        //初始化指静脉
        FingerApiUtil.getInstance().initFinger(this, LicenseIs,
                new FvInitResult() {
                    @Override
                    public void fvInitResult(String msg) {
                        Logger.d(msg);
                    }
                }
                , new DevOpenResult() {

                    @Override
                    public void devOpenResult(Integer res, String msg) {
                        Logger.d(msg);
                    }
                }
                , new DevStatusResult() {

                    @Override
                    public void devStatusResult(Integer res, String msg) {
                        Logger.d(msg);
                    }
                });
    }


}
