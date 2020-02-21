package com.arcFace.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.arcFace.R;
import com.arcFace.adapter.FaceSearchResultAdapter;
import com.arcFace.callBack.FaceHeadCallBack;
import com.arcFace.common.Constants;
import com.arcFace.dialog.ArcDialog;
import com.arcFace.faceServer.CompareResult;
import com.arcFace.faceServer.FaceServer;
import com.arcFace.model.DrawInfo;
import com.arcFace.model.FacePreviewInfo;
import com.arcFace.util.ConfigUtil;
import com.arcFace.util.DrawHelper;
import com.arcFace.util.camera.CameraListener;
import com.arcFace.util.camera.DualCameraHelper;
import com.arcFace.util.dbUtil.ArcFaceDb;
import com.arcFace.util.face.FaceHelper;
import com.arcFace.util.face.FaceListener;
import com.arcFace.util.face.LivenessType;
import com.arcFace.util.face.RecognizeColor;
import com.arcFace.util.face.RequestFeatureStatus;
import com.arcFace.util.face.RequestLivenessStatus;
import com.arcFace.widget.FaceRectView;
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.VersionInfo;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.pojo.ArcFace;
import com.baselibrary.util.FileUtil;
import com.baselibrary.util.VerifyResultUi;
import com.baselibrary.util.dialogUtil.EtCallBack;
import com.orhanobut.logger.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.arcsoft.face.enums.DetectFaceOrientPriority.ASF_OP_0_ONLY;

/**
 * RGB+IR双重活体验证
 * <p>
 * /**
 * * 1.活体检测使用IR摄像头数据，其他都使用RGB摄像头数据
 * * <p>
 * * 2.本界面仅实现IR数据和RGB数据预览大小相同且画面十分接近的情况（RGB数据和IR数据无旋转、镜像的关系）的活体检测，
 * * <p>
 * * 3.若IR数据和RGB数据预览大小不同或两者成像的人脸位置差别很大，需要自己实现人脸框的调整方案。
 * * <p>
 * * 4.由于不同的厂商对IR Camera和RGB Camera的CameraId设置可能会有所不同，开发者可能需要根据实际情况修改
 * * {@link ArcIR_RGB_DetachActivity#cameraRgbId}和
 * * {@link ArcIR_RGB_DetachActivity#cameraIrId}的值
 * * <p>
 * * 5.由于一般情况下android设备的前置摄像头，即cameraId为{@link Camera.CameraInfo#CAMERA_FACING_FRONT}的摄像头在打开后会自动被镜像预览。
 * * 为了便于开发者们更直观地了解两个摄像头成像的关系，实现人脸框的调整方案，本demo对cameraId为{@link Camera.CameraInfo#CAMERA_FACING_FRONT}
 * * 的预览画面做了再次镜像的处理，也就是恢复为原画面。
 */
public class ArcIR_RGB_DetachActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "ArcIR_RGB_DetachActivity";
    private static final int MAX_DETECT_NUM = 10;
    /**
     * 当FR成功，活体未成功时，FR等待活体的时间
     */
    private static final int WAIT_LIVENESS_INTERVAL = 50;
    /**
     * 失败重试间隔时间（ms）
     */
    private static final long FAIL_RETRY_INTERVAL = 1000;
    /**
     * 出错重试最大次数
     */
    private static final int MAX_RETRY_TIME = 3;

    private DualCameraHelper cameraHelper;
    private DualCameraHelper cameraHelperIr;
    private DrawHelper drawHelperRgb;
    private DrawHelper drawHelperIr;
    private Camera.Size previewSize;
    private Camera.Size previewSizeIr;

    /**
     * RGB摄像头和IR摄像头的ID，若和实际不符，需要修改以下两个值。
     * 同时，可能需要修改默认的VIDEO模式人脸检测角度
     */
    private Integer cameraRgbId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private Integer cameraIrId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private FaceEngine ftEngine;
    private FaceEngine frEngine;
    private FaceEngine flEngine;

    private int ftInitCode = -1;
    private int frInitCode = -1;
    private int flInitCode = -1;

    private FaceHelper faceHelperIr;
    private List<CompareResult> compareResultList;
    private FaceSearchResultAdapter adapter;
    /**
     * 活体检测的开关
     */
    private boolean livenessDetect = true;

    /**
     * 注册人脸状态码，准备注册
     */
    private static final int REGISTER_STATUS_READY = 0;
    /**
     * 注册人脸状态码，注册中
     */
    private static final int REGISTER_STATUS_PROCESSING = 1;
    /**
     * 注册人脸状态码，注册结束（无论成功失败）
     */
    private static final int REGISTER_STATUS_DONE = 2;

    private int registerStatus = REGISTER_STATUS_DONE;
    private String registerName;

    /**
     * 用于记录人脸识别相关状态
     */
    private ConcurrentHashMap<Integer, Integer> requestFeatureStatusMap = new ConcurrentHashMap<>();
    /**
     * 用于记录人脸特征提取出错重试次数
     */
    private ConcurrentHashMap<Integer, Integer> extractErrorRetryMap = new ConcurrentHashMap<>();
    /**
     * 用于存储活体值
     */
    private ConcurrentHashMap<Integer, Integer> livenessMap = new ConcurrentHashMap<>();
    /**
     * 用于存储活体检测出错重试次数
     */
    private ConcurrentHashMap<Integer, Integer> livenessErrorRetryMap = new ConcurrentHashMap<>();

    private CompositeDisposable getFeatureDelayedDisposables = new CompositeDisposable();
    private CompositeDisposable delayFaceTaskCompositeDisposable = new CompositeDisposable();
    /**
     * 相机预览显示的控件，可为SurfaceView或TextureView
     */
    private View previewViewRgb, previewViewIr;
    /**
     * 绘制人脸框的控件
     */
    private FaceRectView faceRectView;
    private FaceRectView faceRectViewIr;

    private Switch switchLivenessDetect;

    /**
     * 识别阈值
     */
    private static final float SIMILAR_THRESHOLD = 0.8F;

    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private volatile byte[] rgbData;
    private volatile byte[] irData;
    private AppCompatTextView statusTip;
    private long liveNessTime;

    @Override
    protected Integer contentView() {
        return R.layout.arc_activity_arc_ir__rgb__detach;
    }

    @Override
    protected void initToolBar() {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void initView() {
        initConfig();
        previewViewRgb = bindViewWithClick(R.id.dual_camera_texture_preview_rgb, false);
        //在布局结束后才做初始化操作
        previewViewRgb.getViewTreeObserver().addOnGlobalLayoutListener(this);
        previewViewIr = bindViewWithClick(R.id.dual_camera_texture_previewIr, false);
        faceRectView = bindViewWithClick(R.id.dual_camera_face_rect_view, false);
        faceRectViewIr = bindViewWithClick(R.id.dual_camera_face_rect_viewIr, false);
        switchLivenessDetect = bindViewWithClick(R.id.dual_camera_switch_liveness_detect, false);
        /**
         * 检测提示
         */
        statusTip = bindViewWithClick(R.id.statusTip, false);

        switchLivenessDetect.setChecked(livenessDetect);
        switchLivenessDetect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                livenessDetect = isChecked;
            }
        });
        RecyclerView recyclerShowFaceInfo = bindViewWithClick(R.id.dual_camera_recycler_view_person, false);
        compareResultList = new ArrayList<>();
        adapter = new FaceSearchResultAdapter(compareResultList, this);
        recyclerShowFaceInfo.setAdapter(adapter);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int spanCount = (int) (dm.widthPixels / (getResources().getDisplayMetrics().density * 100 + 0.5f));
        recyclerShowFaceInfo.setLayoutManager(new GridLayoutManager(this, spanCount));
        recyclerShowFaceInfo.setItemAnimator(new DefaultItemAnimator());

        //设置检测人脸的角度
        ConfigUtil.setFtOrient(ArcIR_RGB_DetachActivity.this, ASF_OP_0_ONLY);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initConfig() {
        //保持亮屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            WindowManager.LayoutParams attributes = getWindow().getAttributes();
//            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//            getWindow().setAttributes(attributes);
//        }
        // Activity启动后就锁定为启动时的方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        //本地人脸库初始化
        FaceServer.getInstance().init(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {

    }

    /**
     * 初始化引擎
     */
    private void initEngine() {
        ftEngine = new FaceEngine();
        ftInitCode = ftEngine.init(this, DetectMode.ASF_DETECT_MODE_VIDEO,
                ConfigUtil.getFtOrient(this),
                ConfigUtil.DETACT_FACE_SCALE_VAL, MAX_DETECT_NUM, FaceEngine.ASF_FACE_DETECT);

        frEngine = new FaceEngine();
        frInitCode = frEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE,
                DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                ConfigUtil.DETACT_FACE_SCALE_VAL, MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION /*| FaceEngine.ASF_GENDER*/);

        flEngine = new FaceEngine();
        flInitCode = flEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE,
                DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                ConfigUtil.DETACT_FACE_SCALE_VAL, MAX_DETECT_NUM,
                FaceEngine.ASF_IR_LIVENESS | FaceEngine.ASF_LIVENESS);

        VersionInfo versionInfo = new VersionInfo();
        ftEngine.getVersion(versionInfo);
        Logger.i(TAG, "initEngine:  init: " + ftInitCode + "  version:" + versionInfo);
        if (ftInitCode != ErrorInfo.MOK) {
            String error = getString(R.string.arc_specific_engine_init_failed, "ftEngine", ftInitCode);
            Logger.e(TAG, "initEngine: " + error);
            VerifyResultUi.showTvToast(this, error);
        }
        if (frInitCode != ErrorInfo.MOK) {
            String error = getString(R.string.arc_specific_engine_init_failed, "frEngine", ftInitCode);
            Logger.e(TAG, "initEngine: " + error);
            VerifyResultUi.showTvToast(this, error);
        }
        if (flInitCode != ErrorInfo.MOK) {
            String error = getString(R.string.arc_specific_engine_init_failed, "flEngine", ftInitCode);
            Logger.e(TAG, "initEngine: " + error);
            VerifyResultUi.showTvToast(this, error);
        }
    }

    /**
     * 销毁引擎，faceHelperIr中可能会有特征提取耗时操作仍在执行，加锁防止crash
     */
    private void unInitEngine() {
        if (ftInitCode == ErrorInfo.MOK && ftEngine != null) {
            synchronized (ftEngine) {
                int ftUnInitCode = ftEngine.unInit();
                Logger.i(TAG, "unInitEngine: " + ftUnInitCode);
            }
        }
        if (frInitCode == ErrorInfo.MOK && frEngine != null) {
            synchronized (frEngine) {
                int frUnInitCode = frEngine.unInit();
                Logger.i(TAG, "unInitEngine: " + frUnInitCode);
            }
        }
        if (flInitCode == ErrorInfo.MOK && flEngine != null) {
            synchronized (flEngine) {
                int flUnInitCode = flEngine.unInit();
                Logger.i(TAG, "unInitEngine: " + flUnInitCode);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (cameraHelper != null) {
                cameraHelper.start();
            }
            if (cameraHelperIr != null) {
                cameraHelperIr.start();
            }
        } catch (RuntimeException e) {
            VerifyResultUi.showTvToast(this, e.getMessage()
                    + getString(R.string.arc_camera_error_notice));
        }
    }

    @Override
    protected void onPause() {
        if (cameraHelper != null) {
            cameraHelper.stop();
        }
        if (cameraHelperIr != null) {
            cameraHelperIr.stop();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
        if (cameraHelperIr != null) {
            cameraHelperIr.release();
            cameraHelperIr = null;
        }

        unInitEngine();

        if (faceHelperIr != null) {
            ConfigUtil.setTrackedFaceCount(this, 0/*faceHelperIr.getTrackedFaceCount()*/);
            faceHelperIr.release();
            faceHelperIr = null;
        }

        if (getFeatureDelayedDisposables != null) {
            getFeatureDelayedDisposables.clear();
        }
        if (delayFaceTaskCompositeDisposable != null) {
            delayFaceTaskCompositeDisposable.clear();
        }

        FaceServer.getInstance().unInit();
        super.onDestroy();
    }

    private void initRgbCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final FaceListener faceListener = new FaceListener() {
            @Override
            public void onFail(Exception e) {
                Logger.e(TAG, "onFail: " + e.getMessage());
            }

            //请求FR的回调
            @Override
            public void onFaceFeatureInfoGet(@Nullable final FaceFeature faceFeature,
                                             final Integer requestId, final Integer errorCode,
                                             int gander) {
                //FR成功
                if (faceFeature != null) {
//                    Log.i(TAG, "onPreview: fr end = " + System.currentTimeMillis() + " trackId = " + requestId);
                    Integer liveness = livenessMap.get(requestId);
                    //不做活体检测的情况，直接搜索
                    Log.i("活体检测：", "  11   结果：" + liveness);
                    if (!livenessDetect) {
                        searchFace(System.currentTimeMillis(), faceFeature, requestId, gander);
                    }
                    //活体检测通过，搜索特征
                    else if (liveness != null && liveness == LivenessInfo.ALIVE) {
                        searchFace(System.currentTimeMillis(), faceFeature, requestId, gander);
                    }
                    //活体检测未出结果，或者非活体，延迟执行该函数
                    else {
                        if (requestFeatureStatusMap.containsKey(requestId)) {
                            Observable.timer(WAIT_LIVENESS_INTERVAL, TimeUnit.MILLISECONDS)
                                    .subscribe(new Observer<Long>() {
                                        Disposable disposable;

                                        @Override
                                        public void onSubscribe(Disposable d) {
                                            disposable = d;
                                            getFeatureDelayedDisposables.add(disposable);
                                        }

                                        @Override
                                        public void onNext(Long aLong) {
                                            onFaceFeatureInfoGet(faceFeature, requestId, errorCode, gander);
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {
                                            getFeatureDelayedDisposables.remove(disposable);
                                        }
                                    });
                        }
                    }
                }
                //特征提取失败
                else {
                    if (increaseAndGetValue(extractErrorRetryMap, requestId) > MAX_RETRY_TIME) {
                        extractErrorRetryMap.put(requestId, 0);
                        String msg;
                        // 传入的FaceInfo在指定的图像上无法解析人脸，此处使用的是RGB人脸数据，一般是人脸模糊
                        if (errorCode != null && errorCode == ErrorInfo.MERR_FSDK_FACEFEATURE_LOW_CONFIDENCE_LEVEL) {
                            msg = getString(R.string.arc_low_confidence_level);
                        } else {
                            msg = "ExtractCode:" + errorCode;
                        }
                        faceHelperIr.setName(requestId,
                                getString(R.string.arc_recognize_failed_notice, msg));
                        // 在尝试最大次数后，特征提取仍然失败，则认为识别未通过
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                        retryRecognizeDelayed(requestId);
                    } else {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.TO_RETRY);
                    }
                }
            }

            @Override
            public void onFaceLivenessInfoGet(@Nullable LivenessInfo livenessRGBInfo,
                                              final Integer requestId, Integer errorCode,
                                              LivenessType livenessType, long time) {

            }

            @Override
            public void onFaceDoubleLivenessInfo(Boolean isAlive,
                                                 @Nullable LivenessInfo livenessRGBInfo,
                                                 @NonNull LivenessInfo livenessIRInfo,
                                                 Integer requestId, Integer errorCode,
                                                 LivenessType livenessType, long time) {
                liveNessTime = time;
                if (livenessRGBInfo != null) {
                    int liveness = livenessRGBInfo.getLiveness();
                    livenessMap.put(requestId, liveness);
                    // 非活体，重试
                    Logger.d("预览    RGB_IRActivity   活体检测：" + liveness);
                    if (liveness == LivenessInfo.NOT_ALIVE) {
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void run() {
                                String s = MessageFormat.format("活体IR_RGB 未通过 非活体 false:{0}", time) + "\n";
                                statusTip.setText(s);
                                FileUtil.writeStr(FaceHelper.LOG_TXT, s);
                            }
                        });
                        faceHelperIr.setName(requestId,
                                getString(R.string.arc_recognize_failed_notice,
                                        getString(R.string.arc_not_alive)));
                        // 延迟 FAIL_RETRY_INTERVAL 后，将该人脸状态置为UNKNOWN，帧回调处理时会重新进行活体检测
                        retryLivenessDetectDelayed(requestId);
                    } else if (liveness == LivenessInfo.ALIVE) {
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void run() {
                                String s = MessageFormat.format("活体IR_RGB 通过 活体 true:{0}", time) + "\n";
                                statusTip.setText(s);
                                FileUtil.writeStr(FaceHelper.LOG_TXT, s);
                            }
                        });
                    }
                } else {
                    if (increaseAndGetValue(livenessErrorRetryMap, requestId) > MAX_RETRY_TIME) {
                        livenessErrorRetryMap.put(requestId, 0);
                        String msg;
                        // 传入的FaceInfo在指定的图像上无法解析人脸，此处使用RGB人脸框 + IR数据，一般是人脸模糊或画面中无人脸
                        if (errorCode != null && errorCode == ErrorInfo.MERR_FSDK_FACEFEATURE_LOW_CONFIDENCE_LEVEL) {
                            msg = getString(R.string.arc_low_confidence_level);
                        } else {
                            msg = "ProcessCode:" + errorCode;
                        }
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void run() {
                                String s = MessageFormat.format("验证IR_RGB 未通过: 图像质量差，无法解析有效数据 {0}", time) + "\n";
                                statusTip.setText(s);
                                FileUtil.writeStr(FaceHelper.LOG_TXT, s);
                            }
                        });
                        faceHelperIr.setName(requestId, getString(R.string.arc_recognize_failed_notice, msg));
                        // 在尝试最大次数后，活体检测仍然失败，则认定为非活体
                        livenessMap.put(requestId, LivenessInfo.NOT_ALIVE);
                        retryLivenessDetectDelayed(requestId);
                    } else {
                        livenessMap.put(requestId, LivenessInfo.UNKNOWN);
                    }
                }
            }
        };
        CameraListener rgbCameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                previewSize = camera.getParameters().getPreviewSize();
                Log.d("预览", "   RGB  w:" + previewSize.width + "   h:" + previewSize.height);
//                ViewGroup.LayoutParams layoutParams = adjustPreviewViewSize(previewViewRgb,
//                        faceRectView, previewSize, displayOrientation);
                drawHelperRgb = new DrawHelper(previewSize.width, previewSize.height,
                        previewViewRgb.getWidth()/*layoutParams.width*/, previewViewRgb.getHeight()
                        /*layoutParams.height*/, displayOrientation,
                        cameraId, isMirror, false, false);
                if (faceHelperIr == null) {
                    faceHelperIr = new FaceHelper.Builder()
                            .ftEngine(ftEngine)
                            .frEngine(frEngine)
                            .flEngine(flEngine)
                            .frQueueSize(MAX_DETECT_NUM)
                            .flQueueSize(MAX_DETECT_NUM)
                            .previewSize(previewSize)
                            .faceListener(faceListener)
                            .trackedFaceCount(ConfigUtil.getTrackedFaceCount(ArcIR_RGB_DetachActivity.this.getApplicationContext()))
                            .build();
                }
                TextView textViewRgb = new TextView(ArcIR_RGB_DetachActivity.this, null);
                textViewRgb.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                        , ViewGroup.LayoutParams.WRAP_CONTENT));
                textViewRgb.setText(MessageFormat.format("{0}\n{1}x{2}",
                        getString(R.string.arc_camera_rgb), previewSize.width, previewSize.height));
                textViewRgb.setTextColor(Color.WHITE);
                textViewRgb.setBackgroundColor(getResources().getColor(R.color.arc_color_bg_notification));
                ((FrameLayout) previewViewRgb.getParent()).addView(textViewRgb);
            }

            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                rgbData = nv21;
                processPreviewData();
            }

            @Override
            public void onCameraClosed() {
                Logger.i(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Logger.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelperRgb != null) {
                    drawHelperRgb.setCameraDisplayOrientation(displayOrientation);
                }
                Logger.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };
        int rgbPreViewW = previewViewRgb.getMeasuredWidth();
        int rgbPreViewH = previewViewRgb.getMeasuredHeight();
        Logger.d("预览   rgb   w:" + rgbPreViewW + "   h:" + rgbPreViewH);
        cameraHelper = new DualCameraHelper.Builder()
                .previewViewSize(new Point(rgbPreViewW, rgbPreViewH))
//                .previewViewSize(new Point(960, 1280))
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(cameraRgbId != null ? cameraRgbId : Camera.CameraInfo.CAMERA_FACING_BACK)
                .previewOn(previewViewRgb)
                .cameraListener(rgbCameraListener)
                .isMirror(cameraRgbId != null && Camera.CameraInfo.CAMERA_FACING_FRONT == cameraRgbId)
//                .previewSize(new Point(1280, 960)) //相机预览大小设置，RGB与IR需使用相同大小
                .build();
        try {
            cameraHelper.init();
            cameraHelper.start();
        } catch (RuntimeException e) {
            VerifyResultUi.showTvToast(this,
                    e.getMessage() + getString(R.string.arc_camera_error_notice));
        }
    }

    private void initIrCamera() {
        CameraListener irCameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                previewSizeIr = camera.getParameters().getPreviewSize();
//                ViewGroup.LayoutParams layoutParams = adjustPreviewViewSize(previewViewIr,
//                        faceRectViewIr, previewSizeIr, displayOrientation);
                drawHelperIr = new DrawHelper(previewSizeIr.width, previewSizeIr.height,
                        previewViewIr.getWidth()/*layoutParams.width*/, previewViewIr.getHeight()
                        /*layoutParams.height*/, displayOrientation,
                        cameraId, isMirror, false, false);
                TextView textViewIr = new TextView(ArcIR_RGB_DetachActivity.this, null);
                textViewIr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                        , ViewGroup.LayoutParams.WRAP_CONTENT));
                textViewIr.setText(MessageFormat.format("{0}\n{1}x{2}",
                        getString(R.string.arc_camera_rgb), previewSizeIr.width, previewSizeIr.height));
                textViewIr.setTextColor(Color.WHITE);
                textViewIr.setBackgroundColor(getResources().getColor(R.color.arc_color_bg_notification));
                ((FrameLayout) previewViewIr.getParent()).addView(textViewIr);
            }

            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                irData = nv21;
                processPreviewData();
            }

            @Override
            public void onCameraClosed() {
                Logger.i(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Logger.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelperIr != null) {
                    drawHelperIr.setCameraDisplayOrientation(displayOrientation);
                }
                Logger.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };

        int irPreViewW = previewViewIr.getMeasuredWidth();
        int irPreViewH = previewViewIr.getMeasuredHeight();
        Logger.d("预览   ir   w:" + irPreViewW + "   h:" + irPreViewH);
        cameraHelperIr = new DualCameraHelper.Builder()
                .previewViewSize(new Point(irPreViewW, irPreViewH))
//                .previewViewSize(new Point(960, 1280))
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(cameraIrId != null ? cameraIrId : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .previewOn(previewViewIr)
                .cameraListener(irCameraListener)
                .isMirror(cameraIrId != null && Camera.CameraInfo.CAMERA_FACING_FRONT == cameraIrId)
//                .previewSize(new Point(1280, 960)) //相机预览大小设置，RGB与IR需使用相同大小
//                .additionalRotation(270) //额外旋转角度
                .build();
        try {
            cameraHelperIr.init();
            cameraHelperIr.start();
        } catch (RuntimeException e) {
            VerifyResultUi.showTvToast(this,
                    e.getMessage() + getString(R.string.arc_camera_error_notice));
        }
    }

    /**
     * 调整View的宽高，使2个预览同时显示
     *
     * @param previewView        显示预览数据的view
     * @param faceRectView       画框的view
     * @param previewSize        预览大小
     * @param displayOrientation 相机旋转角度
     * @return 调整后的LayoutParams
     */
    private ViewGroup.LayoutParams adjustPreviewViewSize(View previewView, FaceRectView faceRectView
            , Camera.Size previewSize, int displayOrientation) {
        ViewGroup.LayoutParams layoutParams = previewView.getLayoutParams();
        int measuredWidth = previewView.getMeasuredWidth();
        int measuredHeight = previewView.getMeasuredHeight();
        float ratio = ((float) previewSize.height) / (float) previewSize.width;
        if (ratio > 1) {
            ratio = 1 / ratio;
        }
        if (displayOrientation % 180 == 0) {
            layoutParams.width = measuredWidth;
            layoutParams.height = (int) (measuredWidth * ratio);
        } else {
            layoutParams.height = measuredHeight;
            layoutParams.width = (int) (measuredHeight * ratio);
        }
        Logger.i(TAG, "adjustPreviewViewSize: " + layoutParams.width
                + "x" + layoutParams.height + "  旋转角度：" + displayOrientation);
        previewView.setLayoutParams(layoutParams);
        faceRectView.setLayoutParams(layoutParams);
        return layoutParams;
    }

    /**
     * 处理预览数据
     */
    private synchronized void processPreviewData() {
        if (rgbData != null && irData != null) {
            final byte[] cloneNv21Rgb = rgbData.clone();
            if (faceRectView != null) {
                faceRectView.clearFaceInfo();
            }
            if (faceRectViewIr != null) {
                faceRectViewIr.clearFaceInfo();
            }
            List<FacePreviewInfo> facePreviewInfoList = faceHelperIr.onPreviewFrame(cloneNv21Rgb);
            if (facePreviewInfoList != null && faceRectView != null && drawHelperRgb != null
                    && faceRectViewIr != null && drawHelperIr != null) {
                drawPreviewInfo(facePreviewInfoList);
            }
            registerFace(registerName, cloneNv21Rgb, facePreviewInfoList);
            clearLeftFace(facePreviewInfoList);

            if (facePreviewInfoList != null && facePreviewInfoList.size() > 0 && previewSize != null) {
                for (int i = 0; i < facePreviewInfoList.size(); i++) {
                    // 注意：这里虽然使用的是IR画面活体检测，RGB画面特征提取，但是考虑到成像接近，所以只用了RGB画面的图像质量检测
                    Integer status = requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId());
                    /**
                     * 在活体检测开启，在人脸活体状态不为处理中（ANALYZING）且不为处理完成（ALIVE、NOT_ALIVE）时重新进行活体检测
                     */
                    if (livenessDetect && (status == null || status != RequestFeatureStatus.SUCCEED)) {
                        Integer liveness = livenessMap.get(facePreviewInfoList.get(i).getTrackId());
                        if (liveness == null
                                || (liveness != LivenessInfo.ALIVE && liveness != LivenessInfo.NOT_ALIVE
                                && liveness != RequestLivenessStatus.ANALYZING)) {
                            livenessMap.put(facePreviewInfoList.get(i).getTrackId(), RequestLivenessStatus.ANALYZING);
                            // IR数据偏移
                            FaceInfo faceInfo = facePreviewInfoList.get(i).getFaceInfo().clone();
                            faceInfo.getRect().offset(Constants.HORIZONTAL_OFFSET, Constants.VERTICAL_OFFSET);
//                            if (livenessType == LivenessType.IR) {
//                                faceHelperIr.requestFaceLiveness(cloneNv21Rgb, faceInfo,
//                                        previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21,
//                                        facePreviewInfoList.get(i).getTrackId(), livenessType);
//                            } else if (livenessType == LivenessType.RGB) {
//                                faceHelperIr.requestFaceLiveness(irData.clone(), faceInfo,
//                                        previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21,
//                                        facePreviewInfoList.get(i).getTrackId(), livenessType);
//                            }
                            faceHelperIr.requestFaceLiveness(cloneNv21Rgb, facePreviewInfoList.get(i).getFaceInfo(),
                                    previewSize.width, previewSize.height, irData.clone(), faceInfo,
                                    previewSizeIr.width, previewSizeIr.height, FaceEngine.CP_PAF_NV21,
                                    facePreviewInfoList.get(i).getTrackId(), LivenessType.RGB_IR);
                        }
                    }
                    /**
                     * 对于每个人脸，若状态为空或者为失败，则请求特征提取（可根据需要添加其他判断以限制特征提取次数），
                     * 特征提取回传的人脸特征结果在{@link FaceListener#onFaceFeatureInfoGet(FaceFeature, Integer, Integer)}中回传
                     */
                    if (status == null || status == RequestFeatureStatus.TO_RETRY) {
                        requestFeatureStatusMap.put(facePreviewInfoList.get(i).getTrackId(), RequestFeatureStatus.SEARCHING);
                        faceHelperIr.requestFaceFeature(cloneNv21Rgb, facePreviewInfoList.get(i).getFaceInfo(),
                                previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21,
                                facePreviewInfoList.get(i).getTrackId());
                    }
                }
            }
            rgbData = null;
            irData = null;
        }

    }

    /**
     * 绘制预览相关数据
     *
     * @param facePreviewInfoList {@link FaceHelper#onPreviewFrame(byte[])}回传的处理结果
     */
    private void drawPreviewInfo(List<FacePreviewInfo> facePreviewInfoList) {
        List<DrawInfo> drawInfoList = new ArrayList<>();
        List<DrawInfo> drawInfoListIr = new ArrayList<>();
        for (int i = 0; i < facePreviewInfoList.size(); i++) {
            int trackId = facePreviewInfoList.get(i).getTrackId();
            String name = faceHelperIr.getName(trackId);
            Integer liveness = livenessMap.get(trackId);
            Rect ftRect = facePreviewInfoList.get(i).getFaceInfo().getRect();


            Integer recognizeStatus = requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId());

            // 根据识别结果和活体结果设置颜色
            int color = RecognizeColor.COLOR_UNKNOWN;
            if (recognizeStatus != null) {
                if (recognizeStatus == RequestFeatureStatus.FAILED) {
                    color = RecognizeColor.COLOR_FAILED;
                }
                if (recognizeStatus == RequestFeatureStatus.SUCCEED) {
                    color = RecognizeColor.COLOR_SUCCESS;
                }
            }
            if (liveness != null && liveness == LivenessInfo.NOT_ALIVE) {
                color = RecognizeColor.COLOR_FAILED;
            }
            drawInfoList.add(new DrawInfo(drawHelperRgb.adjustRect(ftRect),
                    GenderInfo.UNKNOWN, AgeInfo.UNKNOWN_AGE,
                    liveness != null ? liveness : LivenessInfo.UNKNOWN, color,
                    name == null ? String.valueOf(trackId) : name));

            Rect offsetFtRect = new Rect(ftRect);
            offsetFtRect.offset(Constants.HORIZONTAL_OFFSET, Constants.VERTICAL_OFFSET);
            drawInfoListIr.add(new DrawInfo(drawHelperIr.adjustRect(offsetFtRect),
                    GenderInfo.UNKNOWN, AgeInfo.UNKNOWN_AGE,
                    liveness != null ? liveness : LivenessInfo.UNKNOWN, color,
                    name == null ? String.valueOf(trackId) : name));
        }
        drawHelperRgb.draw(faceRectView, drawInfoList);
        drawHelperIr.draw(faceRectViewIr, drawInfoListIr);
    }

    /**
     * 注册人脸
     *
     * @param name                注册的名称
     * @param nv21Rgb             RGB摄像头的帧数据
     * @param facePreviewInfoList {@link FaceHelper#onPreviewFrame(byte[])}回传的处理结果
     *                            /data/user/0/com.arcFace/files/register/imgs/卡路里.jpg
     */
    private void registerFace(final String name, final byte[] nv21Rgb, final List<FacePreviewInfo> facePreviewInfoList) {
        if (registerStatus == REGISTER_STATUS_READY && facePreviewInfoList != null && facePreviewInfoList.size() > 0) {
            registerStatus = REGISTER_STATUS_PROCESSING;
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) {
                    boolean success = FaceServer.getInstance().registerNv21(
                            ArcIR_RGB_DetachActivity.this, nv21Rgb,
                            previewSize.width, previewSize.height,
                            facePreviewInfoList.get(0).getFaceInfo(),
                            name == null ? "registered " + faceHelperIr.getTrackedFaceCount() : name
                            , new FaceHeadCallBack() {
                                @Override
                                public void faceHeadCallBack(byte[] faceHead, byte[] faceFeature,
                                                             String name) {
                                    //数据库存储注册的人脸头像
                                    ArcFace arcFace = new ArcFace();
                                    arcFace.setFaceFeature(faceFeature);
                                    arcFace.setName(name);
                                    arcFace.setHeadImg(faceHead);
                                    ArcFaceDb.updateFaceUser(arcFace);
                                }
                            });
                    emitter.onNext(success);
                }
            })
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Boolean success) {
                            String result = success ? getString(R.string.arc_register_success)
                                    : getString(R.string.arc_register_fail);
                            VerifyResultUi.showRegisterSuccess(ArcIR_RGB_DetachActivity.this,
                                    result, true);
                            registerStatus = REGISTER_STATUS_DONE;
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            VerifyResultUi.showRegisterFail(ArcIR_RGB_DetachActivity.this,
                                    getString(R.string.arc_register_fail), true);
                            registerStatus = REGISTER_STATUS_DONE;
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * 删除已经离开的人脸
     *
     * @param facePreviewInfoList 人脸和trackId列表
     */
    private void clearLeftFace(List<FacePreviewInfo> facePreviewInfoList) {
        if (compareResultList != null) {
            for (int i = compareResultList.size() - 1; i >= 0; i--) {
                if (!requestFeatureStatusMap.containsKey(compareResultList.get(i).getTrackId())) {
                    compareResultList.remove(i);
                    adapter.notifyItemRemoved(i);
                }
            }
        }
        if (facePreviewInfoList == null || facePreviewInfoList.size() == 0) {
            requestFeatureStatusMap.clear();
            livenessMap.clear();
            livenessErrorRetryMap.clear();
            extractErrorRetryMap.clear();
            if (getFeatureDelayedDisposables != null) {
                getFeatureDelayedDisposables.clear();
            }
            return;
        }
        Enumeration<Integer> keys = requestFeatureStatusMap.keys();
        while (keys.hasMoreElements()) {
            int key = keys.nextElement();
            boolean contained = false;
            for (FacePreviewInfo facePreviewInfo : facePreviewInfoList) {
                if (facePreviewInfo.getTrackId() == key) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                requestFeatureStatusMap.remove(key);
                livenessMap.remove(key);
                livenessErrorRetryMap.remove(key);
                extractErrorRetryMap.remove(key);
            }
        }


    }

    private void searchFace(final long startTime, final FaceFeature frFace, final Integer requestId, int gander) {
        Observable
                .create(new ObservableOnSubscribe<CompareResult>() {
                    @Override
                    public void subscribe(ObservableEmitter<CompareResult> emitter) {
//                        Log.i(TAG, "subscribe: fr search start = " + System.currentTimeMillis() + " trackId = " + requestId);
                        CompareResult compareResult = FaceServer.getInstance().getTopOfFaceLib(frFace, gander);
//                        Log.i(TAG, "subscribe: fr search end = " + System.currentTimeMillis() + " trackId = " + requestId);
                        emitter.onNext(compareResult);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CompareResult>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                        delayFaceTaskCompositeDisposable.add(disposable);
                    }

                    @Override
                    public void onNext(CompareResult compareResult) {
                        if (compareResult == null || compareResult.getUserName() == null) {
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            faceHelperIr.setName(requestId, "VISITOR " + requestId);
                            return;
                        }
//                        Log.i(TAG, "onNext: fr search get result  = " + System.currentTimeMillis() +
//                        " trackId = " + requestId + "  similar = " + compareResult.getSimilar());
                        if (compareResult.getSimilar() > SIMILAR_THRESHOLD) {
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void run() {
                                    String s = MessageFormat.format("验证IR_RGB 通过:{0} UserName:{1} 相似度:{2}",
                                            (System.currentTimeMillis() - startTime + liveNessTime),
                                            compareResult.getUserName(), compareResult.getSimilar()) + "\n";
                                    statusTip.setText(s);
                                    FileUtil.writeStr(FaceHelper.LOG_TXT, s);
                                }
                            });
                            boolean isAdded = false;
                            if (compareResultList == null) {
                                requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                                faceHelperIr.setName(requestId, "VISITOR " + requestId);
                                return;
                            }
                            for (CompareResult compareResult1 : compareResultList) {
                                if (compareResult1.getTrackId() == requestId) {
                                    isAdded = true;
                                    break;
                                }
                            }
                            if (!isAdded) {
                                //对于多人脸搜索，假如最大显示数量为 MAX_DETECT_NUM 且有新的人脸进入，则以队列的形式移除
                                if (compareResultList.size() >= MAX_DETECT_NUM) {
                                    compareResultList.remove(0);
                                    adapter.notifyItemRemoved(0);
                                }
                                //添加显示人员时，保存其trackId
                                compareResult.setTrackId(requestId);
                                compareResultList.add(compareResult);
                                adapter.notifyItemInserted(compareResultList.size() - 1);
                            }
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
                            faceHelperIr.setName(requestId,
                                    getString(R.string.arc_recognize_success_notice,
                                            compareResult.getUserName()));
                            BaseApplication.AP.play_verifySuccess();
                        } else {
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void run() {
                                    String s = MessageFormat.format("验证IR_RGB 未通过  未注册:{0}",
                                            (System.currentTimeMillis() - startTime + liveNessTime)) + "\n";
                                    statusTip.setText(s);
                                    FileUtil.writeStr(FaceHelper.LOG_TXT, s);
                                }
                            });
                            faceHelperIr.setName(requestId,
                                    getString(R.string.arc_recognize_failed_notice,
                                            getString(R.string.arc_no_register)));
                            retryRecognizeDelayed(requestId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void run() {
                                String s = MessageFormat.format("验证IR_RGB 未通过  未注册 error:{0}{1}\n",
                                        e.getMessage(), System.currentTimeMillis() - startTime + liveNessTime);
                                statusTip.setText(s);
                                FileUtil.writeStr(FaceHelper.LOG_TXT, s);
                            }
                        });
//                        Logger.d("验证  未通过  end：  trackId = " + requestId + "  time:"
//                                + (System.currentTimeMillis() - startTime));
                        if (faceHelperIr != null) {
                            faceHelperIr.setName(requestId,
                                    getString(R.string.arc_recognize_failed_notice,
                                            getString(R.string.arc_no_register)));
                        }
                        retryRecognizeDelayed(requestId);
                    }

                    @Override
                    public void onComplete() {
                        delayFaceTaskCompositeDisposable.remove(disposable);
                    }
                });
    }


    /**
     * 将准备注册的状态置为{@link #REGISTER_STATUS_READY}
     *
     * @param view 注册按钮
     */
    public void register(View view) {
        ArcDialog.showEtDialog(this, new EtCallBack() {
            @Override
            public void etContent(String content, Dialog dialog) {
                if (registerStatus == REGISTER_STATUS_DONE) {
                    registerStatus = REGISTER_STATUS_READY;
                    registerName = content;
                }
            }
        });
    }

    /**
     * 在{@link #previewViewRgb}第一次布局完成后，去除该监听，并且进行引擎和相机的初始化
     */
    @SuppressLint("CheckResult")
    @Override
    public void onGlobalLayout() {
        previewViewRgb.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//        RxPermissions rxPermissions = new RxPermissions(this);
//        rxPermissions.requestEach(NEEDED_PERMISSIONS)
//                .subscribe(permission -> {
//                    if (permission.granted) {
//                        Logger.d("申请权限：" + permission.name + Thread.currentThread().getName());
//                        initEngine();
//                        initRgbCamera();
//                        initIrCamera();
//                    } else {
//                        VerifyResultUi.showTvToast(ArcIR_RGB_DetachActivity.this
//                                , getString(R.string.arc_agree_per));
//                    }
//                });
        initEngine();
        initRgbCamera();
        initIrCamera();
    }

    /**
     * @param view
     */
    public void drawIrRectVerticalMirror(View view) {
        if (drawHelperIr != null) {
            drawHelperIr.setMirrorVertical(!drawHelperIr.isMirrorVertical());
        }
    }

    public void drawIrRectHorizontalMirror(View view) {
        if (drawHelperIr != null) {
            drawHelperIr.setMirrorHorizontal(!drawHelperIr.isMirrorHorizontal());
        }
    }


    /**
     * 将map中key对应的value增1回传
     *
     * @param countMap map
     * @param key      key
     * @return 增1后的value
     */
    public int increaseAndGetValue(Map<Integer, Integer> countMap, int key) {
        if (countMap == null) {
            return 0;
        }
        Integer value = countMap.get(key);
        if (value == null) {
            value = 0;
        }
        countMap.put(key, ++value);
        return value;
    }

    /**
     * 延迟 FAIL_RETRY_INTERVAL 重新进行活体检测
     *
     * @param requestId 人脸ID
     */
    private void retryLivenessDetectDelayed(final Integer requestId) {
        Observable.timer(FAIL_RETRY_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Long>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                        delayFaceTaskCompositeDisposable.add(disposable);
                    }

                    @Override
                    public void onNext(Long aLong) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        // 将该人脸状态置为UNKNOWN，帧回调处理时会重新进行活体检测
                        if (livenessDetect) {
                            faceHelperIr.setName(requestId, Integer.toString(requestId));
                        }
                        livenessMap.put(requestId, LivenessInfo.UNKNOWN);
                        delayFaceTaskCompositeDisposable.remove(disposable);
                    }
                });
    }

    /**
     * 延迟 FAIL_RETRY_INTERVAL 重新进行人脸识别
     *
     * @param requestId 人脸ID
     */
    private void retryRecognizeDelayed(final Integer requestId) {
        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
        Observable.timer(FAIL_RETRY_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Long>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                        delayFaceTaskCompositeDisposable.add(disposable);
                    }

                    @Override
                    public void onNext(Long aLong) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        // 将该人脸特征提取状态置为FAILED，帧回调处理时会重新进行活体检测
                        faceHelperIr.setName(requestId, Integer.toString(requestId));
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.TO_RETRY);
                        delayFaceTaskCompositeDisposable.remove(disposable);
                    }
                });
    }

}
