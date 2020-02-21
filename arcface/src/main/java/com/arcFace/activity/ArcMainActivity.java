package com.arcFace.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;

import com.arcFace.R;
import com.arcFace.callBack.CheckOutCallBack;
import com.arcFace.common.Constants;
import com.arcFace.util.dbUtil.ArcFaceDb;
import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.RuntimeABI;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.pojo.ArcFace;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.VerifyResultUi;
import com.baselibrary.util.glidUtils.GlideUtil;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ArcMainActivity extends BaseActivity {

    /*采取在线激活*/
    private String[] per = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    boolean libraryExists = false;
    // Demo 所需的动态库文件
    private static final String[] LIBRARIES = new String[]{
            // 人脸相关
            "libarcsoft_face_engine.so",
            "libarcsoft_face.so",
            // 图像库相关
            "libarcsoft_image_util.so",
    };
    private AppCompatEditText searchCondition;
    private AppCompatImageView resultIv;

    @Override
    protected Integer contentView() {
        return R.layout.arc_activity_arc_main;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initView() {
        libraryExists = checkSoFile(LIBRARIES);
        ApplicationInfo applicationInfo = getApplicationInfo();
        Logger.d("onCreate: " + applicationInfo.nativeLibraryDir + "   libraryExists:" + libraryExists);
        activeFaceEngine();
        searchCondition = findViewById(R.id.searchCondition);
        resultIv = findViewById(R.id.resultIv);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {

    }

    private boolean isGranted = false;

    //权限申请
    @SuppressLint("CheckResult")
    private void checkPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(per)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            Logger.d("申请权限：" + permission.name);
                            isGranted = true;
                            activeEngine();
                        } else {
                            isGranted = false;
                            VerifyResultUi.showTvToast(ArcMainActivity.this
                                    , getString(R.string.arc_agree_per));
                        }
                    }
                });
    }

    //在线激活
    @SuppressLint("CheckResult")
    private void activeOnLine() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                RuntimeABI runtimeABI = FaceEngine.getRuntimeABI();
                Logger.d("平台架构：ABI：" + runtimeABI);
                int activeCode = FaceEngine.activeOnline(ArcMainActivity.this
                        , Constants.ACTIVE_KEY, Constants.APP_ID, Constants.SDK_KEY);
                emitter.onNext(activeCode);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            VerifyResultUi.showTvToast(ArcMainActivity.this,
                                    getString(R.string.arc_active_success));
                            SPUtil.putArcSoftSDK_Activated(true);
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            VerifyResultUi.showTvToast(ArcMainActivity.this,
                                    getString(R.string.arc_already_activated));
                            SPUtil.putArcSoftSDK_Activated(true);
                        } else {
                            VerifyResultUi.showTvToast(ArcMainActivity.this,
                                    getString(R.string.arc_active_failed));
                            SPUtil.putArcSoftSDK_Activated(false);
                        }
                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = FaceEngine.getActiveFileInfo(ArcMainActivity.this, activeFileInfo);
                        if (res == ErrorInfo.MOK) {
                            Logger.d("激活信息：" + activeFileInfo.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        VerifyResultUi.showTvToast(ArcMainActivity.this, e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 检查能否找到动态链接库，如果找不到，请修改工程配置
     *
     * @param libraries 需要的动态链接库
     * @return 动态库是否存在
     */
    private boolean checkSoFile(String[] libraries) {
        File dir = new File(getApplicationInfo().nativeLibraryDir);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return false;
        }
        List<String> libraryNameList = new ArrayList<>();
        for (File file : files) {
            libraryNameList.add(file.getName());
        }
        boolean exists = true;
        for (String library : libraries) {
            exists &= libraryNameList.contains(library);
        }
        return exists;
    }

    private void activeEngine() {
        if (libraryExists)
            activeOnLine();
        else
            VerifyResultUi.showTvToast(this,
                    getString(R.string.arc_library_not_found));
    }

    /**
     * 激活人脸引擎
     */
    public void activeFaceEngine() {
        if (!isGranted) {
            checkPermission();
        } else {
            if (!SPUtil.getArcSoftSDK_Activated())
                activeEngine();
            else
                VerifyResultUi.showTvToast(this, getString(R.string.arc_actived));
        }
    }

    /**
     * 搜索
     */
    public void checkOut(View view) {
        String condition = searchCondition.getText().toString().trim();
        if (TextUtils.isEmpty(condition)) {
            VerifyResultUi.showTvToast(this, "查询条件不能为空");
            return;
        }
        ArcFaceDb.checkFace(condition, new CheckOutCallBack() {
            @Override
            public void checkOutCallBack(ArcFace arcFace) {
                if (arcFace != null) {
                    byte[] headImg = arcFace.getHeadImg();
                    Glide.with(ArcMainActivity.this).load(headImg).into(resultIv);
                } else {
                    VerifyResultUi.showRegisterFail(ArcMainActivity.this,
                            "无该条件的查询结果", false);
                }
            }
        });
    }

    /**
     * 批量人脸注册
     *
     * @param view view
     */
    public void batchFaceRegister(View view) {
        checkLibraryAndJump(ArcBatchRegisterActivity.class);
    }

    /**
     * 跳转RGB验证
     *
     * @param view view
     */
    public void jumpToRGB(View view) {
        checkLibraryAndJump(ArcRGBDetachActivity.class);
    }

    /**
     * 跳转RGB+IR验证
     *
     * @param view view
     */
    public void jumpToIR_RGB(View view) {
        checkLibraryAndJump(ArcIR_RGB_DetachActivity.class);
    }

    void checkLibraryAndJump(Class activityClass) {
        if (!libraryExists) {
            VerifyResultUi.showTvToast(this, getString(R.string.arc_library_not_found));
            return;
        }
        if (!isGranted)
            VerifyResultUi.showTvToast(ArcMainActivity.this
                    , getString(R.string.arc_agree_per));
        else
            startActivity(new Intent(this, activityClass));
    }


}
