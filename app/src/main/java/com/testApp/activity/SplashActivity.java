package com.testApp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.callBack.FaceInitListener;
import com.baselibrary.callBack.PermissionC;
import com.baselibrary.callBack.PermissionResultCallBack;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.util.PermissionUtils;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.ToastUtils;
import com.face.callback.FaceListener;
import com.face.service.FaceService;
import com.baselibrary.util.ToastUtils;
import com.finger.activity.FingerActivity;
import com.finger.callBack.AllFingerData;
import com.finger.callBack.DevOpenResult;
import com.finger.callBack.DevStatusResult;
import com.finger.callBack.FvInitResult;
import com.finger.fingerApi.FingerApiUtil;
import com.orhanobut.logger.Logger;
import com.testApp.R;
import com.testApp.dialog.AskDialog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        askPermission();

        checkMyPermissions(PermissionC.WR_FILES_PERMISSION);
        //初始化数据的准备--异步加载


    }


    private void initFace() {
        AskDialog.showManagerDialog(this, new AskDialog.PositiveCallBack() {
            @Override
            public void positiveCallBack() {
                skipVerifyActivity();
                SplashActivity.this.finish();
            }
        });
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
                    (PermissionResultCallBack) () -> {
                        //同意权限
                        fingerInit();
    private void askPermission() {
        PermissionUtils.instance().requestPermission(this,
                getString(R.string.permissions), PermissionC.WR_FILES_PERMISSION,
                (PermissionResultCallBack) () -> {
                    //同意权限
                 //   fingerInit();

                    try {
                        Thread.sleep(300);
                        ask();
                    });
        } else {
            fingerInit();
            ask();
        }
    }


    private void ask() {
        Boolean hasManagerPwd = SPUtil.getHasManagerPwd();
        if (!hasManagerPwd) {
            //询问设置添加管理员
            AskDialog.showManagerDialog(this, () -> {
                //人脸注册激活
                skipVerifyActivity();
                SplashActivity.this.finish();
            AskDialog.showManagerDialog(this, new AskDialog.PositiveCallBack() {
                @Override
                public void positiveCallBack() {
                    //人脸注册激活
                    skipVerifyActivity();
                }

                @Override
                public void activationCodeCallBack(String code) {
                    initFace(code);
                }
            });
        } else {
            //人脸注册激活
            String faceActiveCode = SPUtil.getFaceActiveCode();
            if (faceActiveCode!=null){
                initFace(faceActiveCode);
            }else {
                skipVerifyActivity();
            }
        }
    }

    private void skipVerifyActivity() {
        Boolean openFace = SPUtil.getOpenFace();
        if (openFace) {
//            ARouterUtil.navigation(ARouterConstant.FINGER_ACTIVITY,ARouterConstant.GROUP_FINGER);
//            SkipActivityUtil.skipActivity(this, FingerActivity.class);
            //跳转人脸识别页面
            ARouterUtil.navigation(ARouterConstant.FACE_1_N_ACTIVITY);
        } else {
            //跳转不带人脸识别的页面
            SkipActivityUtil.skipActivity(SplashActivity.this, DefaultVerifyActivity.class);
        }
        SplashActivity.this.finish();
    }

    //指静脉初始化
    private void fingerInit() {
        new Thread(() -> {
            InputStream LicenseIs = getResources().openRawResource(R.raw.license);
            runOnUiThread(() -> initFinger(LicenseIs));
        }).start();
    }

    private void initFinger(InputStream is) {
        //初始化指静脉
        FingerApiUtil.getInstance().initFinger(this, is, new FvInitResult() {
            @Override
            public void fvInitResult(int res, String msg) {
                if (res == 1) {
                    FingerApiUtil.getInstance().openFinger(SplashActivity.this,
                            new DevOpenResult() {
                                @Override
                                public void devOpenResult(Integer res, String msg) {
                                    if (res == 1) {
                                        //指静脉打开成功,获取所有的指静脉数据
                                        getAllFingerData();
                                    }
                                    Logger.d(msg);
                                }
                            }, new DevStatusResult() {
                                @Override
                                public void devStatusResult(Integer res, String msg) {
                                    ToastUtils.showSingleToast(SplashActivity.this,
                                            "指静脉连接状态：" + msg);
                                    Logger.d(msg);
                                }
                            });
                }else {
                    ToastUtils.showSingleToast(SplashActivity.this,
                            "算法初始化状态：" + msg);
                    Logger.d(msg);
                }
            }
        });
    }

    private void getAllFingerData() {
        FingerApiUtil.getInstance().getAllFingerData(new AllFingerData() {
            @Override
            public void allFingerData(List<Finger6> fingerList) {

            }
        });
    }

    //初始化face
    private void initFace(String code) {
       FaceService.getInstance().initFace(code, this, new FaceInitListener() {
           @Override
           public void initFail(String error) {
               ToastUtils.showShortToast(SplashActivity.this,error);
           }

           @Override
           public void initSuccess() {
           FaceService.getInstance().loadUserToSearchLibrary(SplashActivity.this, new FaceListener() {
               @Override
               public void onLoadDataListener() {
                 //  ToastUtils.showShortToast(SplashActivity.this,"加载数据成功");
                   Log.d("uuu","ddd");
                   skipVerifyActivity();
                  //
               }
           });
           }
       });
    }




}
