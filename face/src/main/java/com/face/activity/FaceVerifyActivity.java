package com.face.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.callBack.CardInfoListener;
import com.baselibrary.callBack.FingerDevStatusConnectListener;
import com.baselibrary.callBack.FingerVerifyResultListener;
import com.baselibrary.callBack.OnStartServiceListener;
import com.baselibrary.pojo.IdCard;
import com.baselibrary.service.IdCardService;
import com.baselibrary.service.factory.FingerFactory;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.ToastUtils;
import com.face.R;
import com.face.common.FaceConfig;
import com.face.service.FaceService;
import com.face.ui.FaceRecBoxView;
import com.face.ui.FaceRecView;
import com.orhanobut.logger.Logger;
import com.zqzn.android.face.camera.FaceCamera;
import com.zqzn.android.face.data.FaceData;
import com.zqzn.android.face.data.FaceDetectData;
import com.zqzn.android.face.model.FaceSearchLibrary;
import com.zqzn.android.face.processor.BaseFaceRecProcessor;
import com.zqzn.android.face.processor.FaceDetectProcessor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
@Route(path = ARouterConstant.FACE_VERIFY_ACTIVITY)
public class FaceVerifyActivity extends BaseActivity implements BaseFaceRecProcessor.FaceRecCallback {

    private static final String TAG = FaceVerifyActivity.class.getSimpleName();

    private FaceRecView visCameraView;
    private FaceRecBoxView faceRecBoxView;
    private FaceCamera nirCamera;

    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static final int ACTION_REQUEST_PERMISSIONS = 0x005;

    private AppCompatTextView tv_result;

    @Override
    protected Integer contentView() {
        return R.layout.face_verify_face_rec;
    }

    @Override
    protected void initView() {
        visCameraView = (FaceRecView) findViewById(R.id.camera_view);
        faceRecBoxView = (FaceRecBoxView) findViewById(R.id.camera_mask_view);
        faceRecBoxView.bringToFront();

        nirCamera = FaceConfig.getInstance().getNirCamera();
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initData() {
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        }else {
            FaceService.getInstance().initCamera(visCameraView,faceRecBoxView,nirCamera,this);
        }
    }

    @Override
    protected void onViewClick(View view) {
    }


    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                FaceService.getInstance().initCamera(visCameraView,faceRecBoxView,nirCamera,this);
            } else {
                Toast.makeText(this, "权限拒绝！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
        } finally {
            try {
                visCameraView.releaseCamera();
            } catch (IOException e) {
            }
            try {
                if (nirCamera != null) {
                    nirCamera.release();
                }
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void onFaceDetected(FaceDetectData faceDetectData) {
        Log.d("555","onFaceDetected"+faceDetectData);
        //人脸框绘制
        runOnUiThread(() -> faceRecBoxView.sendFaceData(faceDetectData));
    }

    @Override
    public void onLivenessDetected(FaceDetectData faceDetectData) {
             Log.d("555","活体检测");
    }

    @Override
    public void onFaceChanged(FaceDetectData faceDetectData, List<FaceData> addFaces, List<FaceData> lostFaces) {
        Log.d("555", "onFaceChanged:" + faceDetectData);
    }

    @Override
    public void onFaceRecCompleted(FaceDetectData faceDetectData, FaceData faceData, BaseFaceRecProcessor.FaceTrackData faceTrackData) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ").format(new Date());
        Log.d("888","onFaceRecCompleted");
        try  {
            //后面根据recId保存识别相关记录
            String recId = Long.toString(System.currentTimeMillis());
            String livenessScore = "不判断";
            livenessScore = Float.toString(faceTrackData.livenessScore);
            long t = System.currentTimeMillis() - faceTrackData.createTime;
            StringBuilder log = new StringBuilder(String.format("%s(rec_id=%s, 耗时=%d ms): ", time, recId, t));
            //判断搜索次数
            if (faceTrackData.searchTimes.get() > 0) {
                if (faceTrackData.livenessDetectTimes.get() > 0 && !faceTrackData.livenessPass) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ARouterUtil.navigation(ARouterConstant.MENU_ACTIVITY);
                            ToastUtils.showSquareImgToast(FaceVerifyActivity.this
                                    , getString(R.string.face_verify_fail)
                                    , ActivityCompat.getDrawable(FaceVerifyActivity.this
                                            , R.drawable.cry_icon));
                        }
                    });
                }else if (faceTrackData.searchPass) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showSquareImgToast(FaceVerifyActivity.this
                                    , getString(R.string.face_verify_success),null);
                            ARouterUtil.navigation(ARouterConstant.USER_CENTER_ACTIVITY);
                        }
                    });

                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ARouterUtil.navigation(ARouterConstant.MENU_ACTIVITY);
                            ToastUtils.showSquareImgToast(FaceVerifyActivity.this
                                    , getString(R.string.face_verify_fail)
                                    , ActivityCompat.getDrawable(FaceVerifyActivity.this
                                            , R.drawable.cry_icon));

                        }
                    });
                }
            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ARouterUtil.navigation(ARouterConstant.MENU_ACTIVITY);
                        ToastUtils.showSquareImgToast(FaceVerifyActivity.this
                                , getString(R.string.face_verify_fail)
                                , ActivityCompat.getDrawable(FaceVerifyActivity.this
                                        , R.drawable.cry_icon));

                    }
                });
            }
            finish();
            log.append(String.format(" {质量检测次数: %d, 活体检测次数: %d, 搜索次数: %d}", faceTrackData.qualityDetectTimes.get(),
                    faceTrackData.livenessDetectTimes.get(), faceTrackData.searchTimes.get()));
            log.append("\r\n");

        } catch (Throwable e) {
            Logger.e(TAG, "解析人脸识别结果失败", e);
        }
    }



}
