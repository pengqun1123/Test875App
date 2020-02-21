package com.finger.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.baselibrary.base.BaseActivity;
import com.baselibrary.callBack.PermissionResultCallBack;
import com.baselibrary.util.PermissionUtils;
import com.finger.R;
import com.orhanobut.logger.Logger;
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.pojos.Msg;
import com.sd.tgfinger.tgApi.Constant;
import com.sd.tgfinger.tgApi.TGBApi;
import com.sd.tgfinger.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class FingerActivity extends BaseActivity implements DevStatusCallBack {

    private TGBApi tgapi;
    String[] per = {Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.ACCESS_NETWORK_STATE};

    @Override
    protected Integer contentView() {
        return R.layout.finger_activity_finger;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initView() {
        bindViewWithClick(R.id.openFingerDev, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initData() {
        tgapi = TGBApi.getTGAPI();
        initFV();
//        checkMyPermissions(per);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkMyPermissions(String[] permissions) {
        List<String> pers = new ArrayList<>();
        //当Android版本大于等于M时候
        for (String permission : permissions) {
            int checkSelfPermission = checkSelfPermission(permission);
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                pers.add(permission);
            }
        }
        if (pers.size() > 0) {
            String[] strings = pers.toArray(new String[]{});
            PermissionUtils.instance().requestPermission(this,
                    getString(R.string.permissions), strings,
                    new PermissionResultCallBack() {
                        @Override
                        public void permissionCallBack() {
                            initFV();
                        }
                    });
        } else {
            initFV();
        }
    }

    private void initFV() {
        //初始化指静脉
        tgapi.init(this, null, new FvInitCallBack() {
            @Override
            public void fvInitResult(Msg msg) {
                Integer result = msg.getResult();
                if (result >= 0) {
                    ToastUtil.toast(FingerActivity.this, "指静脉算法初始化成功");
                    openDev();
                }
            }
        });
    }

    private void openDev() {
        tgapi.openDev(this,Constant.WORK_BEHIND, Constant.TEMPL_MODEL_6, true,
                false,new DevOpenCallBack() {
                    @Override
                    public void devOpenResult(Msg msg) {
                        Integer result = msg.getResult();
                        if (result >= 0) {
                            ToastUtil.toast(FingerActivity.this, "指静脉打开成功");
                        }
                    }
                }, this);
    }

    @Override
    protected void onViewClick(View view) {
        if (view.getId() == R.id.openFingerDev) {

        }
    }

    @Override
    public void devStatus(Msg msg) {
        Logger.d("  msg:" + msg.getResult() + "  :  " + msg.getTip());
    }
}
