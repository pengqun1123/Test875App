package com.testApp.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
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
import com.baselibrary.pojo.Finger6;
import com.baselibrary.util.AnimatorUtils;
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
import com.finger.callBack.FingerDataInitCallBack;
import com.testApp.component.DeleteAllData;
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

//        clearData();

        checkMyPermissions(per);

    }

    @Override
    protected void onViewClick(View view) {

    }

    private void clearData() {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        DeleteAllData.getInstance(this).deleteAllUser(dbUtil);
        DeleteAllData.getInstance(this).deleteAllFinger(dbUtil);
        DeleteAllData.getInstance(this).deleteAllPw(dbUtil);
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
        if (!hasManagerPwd) {
            //询问设置添加管理员
            AskDialog.showManagerDialog(this, new AskDialog.PositiveCallBack() {
                @Override
                public void positiveCallBack() {
                    loadData(null);
                }

                @Override
                public void activationCodeCallBack(String code) {
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
          // openFace=false;
            if (!openFace){
                faceLoadOver=true;
                return;
            }
            String faceActiveCode = SPUtil.getFaceActiveCode();
            if (!TextUtils.isEmpty(faceActiveCode)) {
                initFace(faceActiveCode);
            }
        }
    }

    private void loadData(String code) {
        loadingData();
        if (!TextUtils.isEmpty(code)) {
            //开启指静脉
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

    private ArrayList<Finger6> allFingerDataList;

    private void getAllFingerData() {
        FingerApi.getInstance().getAllFingerData(new AllFingerData() {
            @Override
            public void allFingerData(List<Finger6> fingerList) {
                SplashActivity.this.allFingerDataList = (ArrayList<Finger6>) fingerList;
                SplashActivity.this.fingerLoadOver = true;
                Logger.d("===初始化    指静脉初始化成功:");
                if (SPUtil.getOpenFace()) {
                    if (SplashActivity.this.faceLoadOver) {
                        skipVerifyActivity((ArrayList<Finger6>) fingerList);
                    }
                } else {
                    skipVerifyActivity((ArrayList<Finger6>) fingerList);
                }
                //fingerData(fingerList);
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

    private void cellDataSpace(List<Finger6> fingerList, FingerDataInitCallBack callBack) {
        this.allFinerDataSize = fingerList.size();
        byte[] FingerData = new byte[fingerList.size() * AppConstant.FINGER6_DATA_SIZE];
        for (int i = 0; i < fingerList.size(); i++) {
            byte[] finger6Feature = fingerList.get(i).getFinger6Feature();
            System.arraycopy(finger6Feature, 0, FingerData,
                    AppConstant.FINGER6_DATA_SIZE * i, AppConstant.FINGER6_DATA_SIZE);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callBack.fingerDataInitCallBack(FingerData);
            }
        });
    }

    private byte[] allFinerData;
    private int allFinerDataSize;

    private void fingerData(List<Finger6> fingerList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cellDataSpace(fingerList, new FingerDataInitCallBack() {
                    @Override
                    public void fingerDataInitCallBack(byte[] finerData) {
                        SplashActivity.this.allFinerData = finerData;
                        SplashActivity.this.fingerLoadOver = true;
                        Logger.d("===初始化    指静脉初始化成功:");
//                        if (SPUtil.getOpenFace()) {
//                            if (SplashActivity.this.faceLoadOver) {
//                                skipVerifyActivity(finerData, fingerList.size());
//                            }
//                        } else {
//                            skipVerifyActivity(finerData, allFinerDataSize);
//                        }
                    }
                });
            }
        }).start();
    }

    //初始化face
    private void initFace(String code) {
        FaceService.getInstance().initFace(code, this, new FaceInitListener() {
            @Override
            public void initFail(String error) {
                ToastUtils.showSingleToast(SplashActivity.this, error);
            }

            @Override
            public void initSuccess() {
                FaceService.getInstance().loadUserToSearchLibrary(SplashActivity.this, new FaceListener() {
                    @Override
                    public void onLoadDataListener() {
                        Logger.d("===初始化    人脸初始化成功:");
                        SplashActivity.this.faceLoadOver = true;
                        if (SplashActivity.this.fingerLoadOver) {
                            skipVerifyActivity(SplashActivity.this.allFingerDataList);
                        }
                    }
                });
            }
        });
    }

    private void skipVerifyActivity(ArrayList<Finger6> finger6List/*byte[] finerData, int fingerSize*/) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cancelLoad();
            }
        });

        Boolean openFace = SPUtil.getOpenFace();
      //  openFace=false;
        if (openFace) {
            // TODO: 2019/10/11 请携带指静脉数据到人脸识别页面
            //跳转不带人脸识别的页面
            Bundle bundle = new Bundle();
//            bundle.putByteArray(AppConstant.FINGER_DATA, finerData);
//            bundle.putInt(AppConstant.FINGER_SIZE, fingerSize);
            bundle.putParcelableArrayList(AppConstant.FINGER_DATA_LIST,finger6List);
            Logger.d("SplashActivity 2 指静脉模板数量：" + finger6List.size());
            //跳转人脸识别页面
            ARouterUtil.navigation(ARouterConstant.FACE_1_N_ACTIVITY,bundle);
        } else {
            //跳转不带人脸识别的页面
            Bundle bundle = new Bundle();
//            bundle.putByteArray(AppConstant.FINGER_DATA, finerData);
//            bundle.putInt(AppConstant.FINGER_SIZE, fingerSize);
            bundle.putParcelableArrayList(AppConstant.FINGER_DATA_LIST,finger6List);
            Logger.d("SplashActivity 2 指静脉模板数量：" + finger6List.size());
            SkipActivityUtil.skipDataActivity(SplashActivity.this,
                    DefaultVerifyActivity.class, bundle);
        }
        SplashActivity.this.finish();
    }

}
