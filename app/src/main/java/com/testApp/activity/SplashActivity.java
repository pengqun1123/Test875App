package com.testApp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baselibrary.callBack.PermissionC;
import com.baselibrary.callBack.PermissionResultCallBack;
import com.baselibrary.util.PermissionUtils;
import com.baselibrary.util.SkipActivityUtil;
import com.testApp.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        PermissionUtils.instance().requestPermission(this,
                getString(R.string.permissions), PermissionC.WR_FILES_PERMISSION,
                (PermissionResultCallBack) () -> {
                    SkipActivityUtil.skipActivity(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.finish();
                });
    }
}
