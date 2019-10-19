package com.testApp.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.callBack.FaceInitListener;
import com.baselibrary.callBack.PermissionResultCallBack;
import com.baselibrary.constant.AppConstant;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.pojo.Face;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.pojo.Manager;
import com.baselibrary.pojo.Pw;
import com.baselibrary.pojo.User;
import com.baselibrary.util.AnimatorUtils;
import com.baselibrary.util.FingerListManager;
import com.baselibrary.util.PermissionUtils;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.ToastUtils;
import com.face.callback.FaceListener;
import com.face.service.FaceService;
import com.finger.callBack.AllFingerData;
import com.finger.fingerApi.FingerApi;
import com.orhanobut.logger.Logger;
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.pojos.Msg;
import com.testApp.R;
import com.testApp.dialog.AskDialog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends BaseActivity {

    private AppCompatTextView loadTv;
    private LinearLayout loadingParent;

    private Boolean fingerLoadOver = false;
    private Boolean faceLoadOver = false;
    private AppCompatImageView loadingView;
    private ObjectAnimator objectAnimator;

    String[] per = {Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.ACCESS_NETWORK_STATE};

    @Override
    protected Integer contentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        loadingParent = bindViewWithClick(R.id.loadingParent, false);
        loadingView = bindViewWithClick(R.id.loadingView, false);
        loadTv = bindViewWithClick(R.id.loadingTv, false);
    }

    @Override
    protected void initToolBar() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initData() {
        SPUtil.putCardVerifyFlag(true);
        SPUtil.putFingerVerifyFlag(true);
        SPUtil.putPwVerifyFlag(true);

        //清除数据库缓存
//        DBUtil dbUtil = BaseApplication.getDbUtil();
//        dbUtil.getDaoSession().clear();
      //  clearData();

        setDevMaxVoice();

        checkMyPermissions(per);

    }

    @Override
    protected void onViewClick(View view) {

    }

    private void setDevMaxVoice() {
        //设置音量
        float streamVolumeMax = BaseApplication.AP.getStreamVolumeMax();
        Logger.d("设备的最大音量:" + streamVolumeMax);
        BaseApplication.AP.setVolume((int) streamVolumeMax);
    }

    private void clearData() {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        dbUtil.deleteAll(User.class);
        dbUtil.deleteAll(Manager.class);
        dbUtil.deleteAll(Pw.class);
        dbUtil.deleteAll(Finger6.class);
        dbUtil.deleteAll(Face.class);
        SPUtil.putHasManagerPwd(false);
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
                            ask();
                        }
                    });
        } else {
            ask();
        }
    }

    private void loadingData() {
        loadingParent.setVisibility(View.VISIBLE);
        objectAnimator = AnimatorUtils.objRotateAnim(loadingView);
    }

    private void cancelLoad() {
        objectAnimator.cancel();
        loadingParent.setVisibility(View.GONE);
    }

    private void ask() {
        Boolean hasManagerPwd = SPUtil.getHasManagerPwd();
        Logger.d("是否已经添加管理员：" + hasManagerPwd);
        if (!hasManagerPwd) {
            //询问设置添加管理员
            AskDialog.showManagerDialog(this, AppConstant.INIT_ADD_MANAGER,
                    new AskDialog.PositiveCallBack() {

                        @Override
                        public void positiveCallBack(int flag, Manager manager) {
                            loadData(null);
                        }

                        @Override
                        public void activationCodeCallBack(String code) {
                            SPUtil.putFaceVerifyFlag(true);
                            loadData(code);
                        }
                    });
        } else {
            loadingData();
            Boolean devOpenStatus = FingerApi.getDevOpenSatus();
            if (!devOpenStatus) {
                //开启指静脉
                initFingerFv();
            }
            boolean openFace = SPUtil.getOpenFace();
            if (!openFace) {
                faceLoadOver = true;
                return;
            }
            String faceActiveCode = SPUtil.getFaceActiveCode();
            if (!TextUtils.isEmpty(faceActiveCode)) {
                Logger.d("激活码1：" + faceActiveCode);
                initFace(faceActiveCode);
            }
        }
    }

    private void loadData(String code) {
        loadingData();
        if (!TextUtils.isEmpty(code)) {
            //开启指静脉
            SPUtil.putFaceActiveCode(code);
            initFingerFv();
            initFace(code);
        } else {
            //开启指静脉
            initFingerFv();
        }
    }

    private void initFingerFv() {
        InputStream LicenseIs = getResources().openRawResource(R.raw.license);
        FingerApi.getInstance().fingerInit(this, LicenseIs, new FvInitCallBack() {
            @Override
            public void fvInitResult(Msg msg) {
                if (msg.getResult() == 1) {
                    Logger.d("===初始化指静脉:" + msg.getTip());
                    //获取指静脉模板数据
                    getAllFingerData();
                    //开启指静脉
                    openFingerDev();
                } else {
                    Logger.d("===初始化指静脉:" + msg.getTip());
                }
            }
        });
    }

    private void openFingerDev() {
        FingerApi.getInstance().openDev(SplashActivity.this, true,
                new DevOpenCallBack() {
                    @Override
                    public void devOpenResult(Msg msg) {
                        if (msg.getResult() == 1) {
                            Logger.d("=== 打开指静脉：" + msg.getTip());
                        } else {
                            Logger.d("===指静脉打开失败：" + msg.getTip());
                        }
                    }
                }, new DevStatusCallBack() {
                    @Override
                    public void devStatus(Msg msg) {
                        Logger.d("===指静脉设备的连接状态:" + msg.getTip());
                    }
                });
    }

    private void getAllFingerData() {
        FingerApi.getInstance().getAllFingerData(new AllFingerData() {
            @Override
            public void allFingerData(List<Finger6> fingerList) {
                FingerListManager.getInstance().addFingerDataList((ArrayList<Finger6>)
                        fingerList);
                SplashActivity.this.fingerLoadOver = true;
                Logger.d("===初始化    指静脉初始化成功:fingerLoadOver ：" + fingerLoadOver);
                if (SPUtil.getOpenFace()) {
                    if (SplashActivity.this.faceLoadOver) {
                        skipVerifyActivity();
                    }
                } else {
                    skipVerifyActivity();
                }
            }

            @Override
            public void allFingerFail() {
                ToastUtils.showSquareImgToast(SplashActivity.this
                        , getString(R.string.init_finger_data_fail)
                        , ActivityCompat.getDrawable(SplashActivity.this
                                , R.drawable.cry_icon));
            }
        });
    }

    //初始化face
    private void initFace(String code) {
        Logger.d("激活码2：" + code);
        FaceService.getInstance().initFace(code, this, new FaceInitListener() {
            @Override
            public void initFail(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Logger.d("===初始化    人脸初始化失败:");
                        ToastUtils.showSquareImgToast(SplashActivity.this, error,
                                ActivityCompat.getDrawable(SplashActivity.this, R.drawable.cry_icon));
                    }
                });
            }

            @Override
            public void initSuccess() {
                FaceService.getInstance().loadUserToSearchLibrary(SplashActivity.this, new FaceListener() {
                    @Override
                    public void onLoadDataListener() {
                        Logger.d("===初始化    人脸初始化成功:");
                        SplashActivity.this.faceLoadOver = true;
                        Logger.d("  指静脉数据加载完成：fingerLoadOver：" + fingerLoadOver);
                        if (SplashActivity.this.fingerLoadOver) {
                            skipVerifyActivity();
                        }
                    }
                });
            }
        });
    }

    private void skipVerifyActivity() {
        runOnUiThread(() -> cancelLoad());
        Boolean openFace = SPUtil.getOpenFace();
        if (openFace) {
            //跳转人脸识别页面
            ARouterUtil.navigation(ARouterConstant.FACE_1_N_ACTIVITY);
        } else {
            //跳转不带人脸识别的页面
            SkipActivityUtil.skipActivity(SplashActivity.this,
                    DefaultVerifyActivity.class);
        }
        SplashActivity.this.finish();
    }

}
