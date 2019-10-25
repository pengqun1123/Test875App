package com.face.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.callBack.CardInfoListener;
import com.baselibrary.callBack.FingerDevCloseListener;
import com.baselibrary.callBack.FingerDevStatusConnectListener;
import com.baselibrary.callBack.FingerVerifyResultListener;
import com.baselibrary.callBack.OnGetVerifyFingerImgListener;
import com.baselibrary.callBack.OnStartServiceListener;
import com.baselibrary.constant.AppConstant;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.pojo.IdCard;
import com.baselibrary.service.IdCardService;
import com.baselibrary.service.factory.FingerFactory;
import com.baselibrary.util.FingerListManager;
import com.baselibrary.util.GetUIDimens;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.ToastUtils;
import com.baselibrary.util.VerifyResultUi;
import com.face.R;
import com.face.service.FaceService;
import com.face.ui.FaceRecBoxView;
import com.orhanobut.logger.Logger;
import com.zqzn.android.face.camera.FaceCamera;
import com.zqzn.android.face.data.FaceData;
import com.zqzn.android.face.data.FaceDetectData;
import com.zqzn.android.face.model.FaceSearchLibrary;
import com.zqzn.android.face.processor.BaseFaceRecProcessor;
import com.zqzn.android.face.processor.FaceDetectProcessor;
import com.face.common.FaceConfig;
import com.face.ui.FaceRecView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Route(path = ARouterConstant.FACE_1_N_ACTIVITY)
public class V3FaceRecActivity extends FaceBaseActivity implements BaseFaceRecProcessor.FaceRecCallback,
        FingerDevStatusConnectListener, CardInfoListener, FingerVerifyResultListener {

    private static final String TAG = V3FaceRecActivity.class.getSimpleName();

    private FaceRecView visCameraView;
    private FaceRecBoxView faceRecBoxView;
    private FaceCamera visCamera;
    private FaceCamera nirCamera;
    private BaseFaceRecProcessor faceRecProcessor;
    private BaseFaceRecProcessor.FaceRecConfig faceRecConfig;
    private File logFilePath;
    private SurfaceView nirPreview;
    private String serialNumber = "VG9D-QVA7-956E-RE97 ";

    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static final int ACTION_REQUEST_PERMISSIONS = 0x005;
    private FaceSearchLibrary faceSearchLibrary;
    private AppCompatImageView iv_in;
    private AppCompatImageView iv_sight;
    private ObjectAnimator sight_rotate;


    private ObjectAnimator in_rotate;
    private AppCompatTextView tv_result;
    private IdCardService idCardService;

    @Override
    protected Integer contentView() {
        return R.layout.face_activity_v3_face_rec;
    }

    @Override
    protected void initView() {
        super.initView();
        visCameraView = (FaceRecView) findViewById(R.id.camera_view);
        faceRecBoxView = (FaceRecBoxView) findViewById(R.id.camera_mask_view);
        faceRecBoxView.bringToFront();
        tv_result = bindViewWithClick(R.id.homeMenu, true);

        nirCamera = FaceConfig.getInstance().getNirCamera();
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initData() {

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            FaceService.getInstance().initCamera(visCameraView, faceRecBoxView, nirCamera, this);
        }
        tv_result.setTextColor(getResources().getColor(R.color.blue_10));
        tv_result.setText("菜单管理");

        if (SPUtil.getFingerVerifyFlag()) {
            FingerFactory.getInstance().fingerDevConnectStatus(this);
        }

        if (SPUtil.getCardVerifyFlag()) {
            idCardService = ARouter.getInstance().navigation(IdCardService.class);

            idCardService.verify_IdCard(V3FaceRecActivity.this);
        }

    }

    @Override
    protected void onViewClick(View view) {
        super.onViewClick(view);
    }

    private void startAnimator() {
//        if (sight_rotate.isStarted()){
//            if (sight_rotate.isPaused()){
//                tv_result.setText(getString(R.string.face_verifying));
//                tv_result.setTextColor(getResources().getColor(R.color.blue_10));
//                tv_result.setVisibility(View.VISIBLE);
//                sight_rotate.resume();
//                in_rotate.resume();
//            }
//        }else {
//            tv_result.setText(getString(R.string.face_verifying));
//            tv_result.setTextColor(getResources().getColor(R.color.blue_10));
//            tv_result.setVisibility(View.VISIBLE);
//            sight_rotate.start();
//            in_rotate.start();
//        }

    }


    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                // FaceConfig.getInstance().init(this, serialNumber.toUpperCase(), this);
                FaceService.getInstance().initCamera(visCameraView, faceRecBoxView, nirCamera, this);
            } else {
                Toast.makeText(this, "权限拒绝！", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        FingerFactory.getInstance().reStartFingerVerify();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();
        FingerFactory.getInstance().pauseFingerVerify();
        if (isStartService) {
            Logger.d("测试  V3FaceActivity  解绑FingerService ");
            FingerFactory.getInstance().unbindDevService(this);
            isStartService = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            if (idCardService != null) {
                idCardService.destroyIdCard();
            }
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tv_result.getText().toString().equals("管理")) {
                    tv_result.setText(getString(R.string.face_verifying));
                    tv_result.setTextColor(getResources().getColor(R.color.blue_10));
                }
            }
        });

        Log.d("555", "onFaceDetected" + faceDetectData);

        //人脸框绘制
        runOnUiThread(() -> faceRecBoxView.sendFaceData(faceDetectData));
    }

    @Override
    public void onLivenessDetected(FaceDetectData faceDetectData) {
        Log.d("555", "活体检测");
    }

    @Override
    public void onFaceChanged(FaceDetectData faceDetectData, List<FaceData> addFaces, List<FaceData> lostFaces) {
        Log.d("555", "onFaceChanged:" + faceDetectData);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                faceRecBoxView.sendFaceData(faceDetectData);
                if (faceDetectData == null) {
                    tv_result.setTextColor(getResources().getColor(R.color.blue_10));
                    tv_result.setText("管理");

                }
            }
        });
    }

    @Override
    public void onFaceRecCompleted(FaceDetectData faceDetectData, FaceData faceData,
                                   BaseFaceRecProcessor.FaceTrackData faceTrackData) {
        //faceRecConfig.livenessDetectMode = BaseFaceRecProcessor.LivenessDetectMode.NIR_LIVENESS;
        // Log.d("888","onFaceRecCompleted:"+faceTrackData.searchedPerson.getFaceId());
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ").format(new Date());
        Log.d("888", "onFaceRecCompleted");
        try {
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
                            tv_result.setText(getString(R.string.face_verify_fail));
                            tv_result.setTextColor(getResources().getColor(R.color.red_2));

                        }
                    });
                } else if (faceTrackData.searchPass) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_result.setText(getString(R.string.face_verify_success));
                            tv_result.setTextColor(getResources().getColor(R.color.green_8));
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_result.setText(getString(R.string.face_verify_fail));
                            tv_result.setTextColor(getResources().getColor(R.color.red_6));

                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_result.setText(getString(R.string.face_verify_fail));
                        tv_result.setTextColor(getResources().getColor(R.color.red_2));

                    }
                });
            }
     /*           //判断活体检测结果
                if (faceTrackData.livenessDetectTimes.get() > 0 && !faceTrackData.livenessPass) {
                    // 攻击未过，记录攻击日志
                    log.append(String.format("攻击，疑似人员: %s，相似度: %f, %s活体分数：%s", faceTrackData.searchedPerson.getPerson(),
                            faceTrackData.searchedPerson.getSimilarity(), BaseFaceRecProcessor.LivenessDetectMode.NIR_LIVENESS, livenessScore));
               //     subDir = "attack";
                } else if (faceTrackData.searchPass) {
                    //识别成功
                    log.append(String.format("识别成功，识别人员: %s，相似度: %f, %s活体分数: %s", faceTrackData.searchedPerson.getPerson(),
                            faceTrackData.searchedPerson.getSimilarity(), BaseFaceRecProcessor.LivenessDetectMode.NIR_LIVENESS, faceTrackData.livenessScore));
                } else {
                    //搜索不通过，记录陌生人或无权限
               //     subDir = "none";
                    SearchedPerson p = faceTrackData.searchedPerson;
                    log.append(String.format("陌生人，最相似人员: %s，相似度: %f", p == null ? "无" : p.getPerson(),
                            p == null ? 0 : p.getSimilarity()));
                }
            } else if (faceTrackData.livenessDetectTimes.get() > 0 && !faceTrackData.livenessPass) {
                log.append(String.format("攻击, 活体分数：%s", livenessScore));
            //    subDir = "attack";
            } else {
            //    subDir = "none";
                log.append(String.format("活体，活体分数: %s ", livenessScore));
            }*/
            log.append(String.format(" {质量检测次数: %d, 活体检测次数: %d, 搜索次数: %d}", faceTrackData.qualityDetectTimes.get(),
                    faceTrackData.livenessDetectTimes.get(), faceTrackData.searchTimes.get()));
            log.append("\r\n");
            //  fileOutputStream.write(log.toString().getBytes());
            // File path = new File(String.format("%s/images/%s", logFilePath.getAbsolutePath(), subDir));
            // path.mkdirs();
            //保存可见光图片
            //    Tool.save(faceDetectData.getImage().toBitmap(), new File(String.format("%s/%s_vis.jpg", path.getAbsolutePath(), recId)));
            //     Tool.save(faceDetectData.getImage().drawFacesToBitmap(faceData), new File(String.format("%s/%s_vis_track.jpg", path.getAbsolutePath(), recId)));
            FaceDetectProcessor.FaceDetectProcessorMonitorData monitorData = (FaceDetectProcessor.FaceDetectProcessorMonitorData) faceDetectData.getMonitorData();
            //if (faceRecConfig.livenessDetectMode == BaseFaceRecProcessor.LivenessDetectMode.NIR_LIVENESS) {
            //如果是红外活体识别模式，保存红外图片
            //    Tool.save(monitorData.getNirImage().toBitmap(), new File(String.format("%s/%s_nir.jpg", path.getAbsolutePath(), recId)));
            //  }
            log.append(String.format(", track: %s, conv: %s, nir_liv: %s",
                    String.valueOf(monitorData.getDetectTime()),
                    String.valueOf(monitorData.getImageConvertTime()),
                    String.valueOf(monitorData.getNirLivenessTime())));
            Logger.d("onFaceRecCompleted: " + log.toString());
        } catch (Throwable e) {
            Logger.e(TAG, "解析人脸识别结果失败", e);
        }
    }

    private Boolean isStartService = false;

    @Override
    public void fingerDevStatusConnect(int res, String msg) {
        if (res == 1 && !isStartService) {
            Logger.d("测试 V3FaceActivity 执行启动FingerService  ");
            FingerFactory.getInstance().startFingerService(this,
                    this, new OnStartServiceListener() {
                        @Override
                        public void startServiceListener(Boolean isStart) {
                            isStartService = isStart;
//                            if (isStart) {
                            Logger.d("测试  V3FaceActivity  调用FingerService 1：N验证");
//                                FingerFactory.getInstance().setFingerVerifyResult(this);
//                            }
                        }
                    });
        }
//        if (res == 1 && !isStartService) {
//            FingerFactory.getInstance().startFingerService(this, new FingerVerifyResultListener() {
//                @Override
//                public void fingerVerifyResult(int res, String msg, int score,
//                                               int index, Long fingerId, byte[] updateFinger) {
//                    if (res == 1) {
//
//                    } else {
//
//                    }
//                }
//            }, new OnStartServiceListener() {
//                @Override
//                public void startServiceListener(Boolean isStart) {
//                    isStartService = isStart;
//                }
//            });
//        }
    }


    @Override
    public void onGetCardInfo(IdCard idCard) {
        if (idCard == null) {
            VerifyResultUi.showVerifyFail(this, getString(R.string.face_verify_fail), false);
        } else {
            Log.d("999", idCard.getName());
            VerifyResultUi.showVerifySuccess(this, getString(R.string.face_verify_success), false);
        }
    }

    @Override
    public void onRegisterResult(boolean result, IdCard idCard) {

    }

    @Override
    public void fingerVerifyResult(int res, String msg, int score, int index, Long fingerId, byte[] updateFinger) {
        if (res == 1) {
            VerifyResultUi.showVerifySuccess(this, getString(R.string.face_verify_success), true);
        } else {
            Logger.d(" V3  指静脉验证结果：" + res);
            if (res == -1 || res == -2)
                VerifyResultUi.showVerifyFail(this, getString(R.string.face_verify_fail), true);
        }
    }
}
