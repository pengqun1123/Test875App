package com.face.activity;

import android.animation.ObjectAnimator;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.baselibrary.base.BaseActivity;
import com.face.R;
import com.face.common.TestFaceDetectProcessor;
import com.orhanobut.logger.Logger;
import com.zqzn.android.face.camera.CameraParams;
import com.zqzn.android.face.camera.FaceCamera;
import com.zqzn.android.face.data.FaceData;
import com.zqzn.android.face.data.FaceDetectData;
import com.zqzn.android.face.data.FaceQuality;
import com.zqzn.android.face.exceptions.FaceException;
import com.zqzn.android.face.exceptions.FaceQualityException;
import com.zqzn.android.face.model.FaceQualityDetector;
import com.zqzn.android.face.processor.FaceDetectProcessor;
import com.face.common.FaceConfig;
import com.face.ui.FaceBoxView;
import com.face.ui.FaceDataExtraInfo;

import java.io.IOException;
import java.util.List;

/**
 * 人脸检测Activity基类
 * <p>
 * 任何需要人脸检测功能的Activity可以继承此类，并做相应的检测后处理实现。
 * <p>
 * 此类实现的 FaceDetectProcessor.FaceDetectCallback是在FaceDetectProcessor进行人脸识别成功后接收回调信息时使用的接口
 */
public abstract class AbstractFaceDetectActivity extends FaceBaseActivity implements FaceDetectProcessor.FaceDetectCallback {

    private static final String TAG = AbstractFaceDetectActivity.class.getSimpleName();

    /**
     * 视频预览TextureView
     */
    protected TextureView svPreview;
    /**
     * 人脸框绘制View
     */
    protected FaceBoxView fbvFaceRect;
    /**
     * 可以直接使用的人脸检测处理器
     */
    protected FaceDetectProcessor faceDetectProcessor;
    /**
     * 人脸质量检测器
     */
    protected FaceQualityDetector faceQualityDetector;
    /**
     * 可见光摄像头
     */
    protected FaceCamera visCamera;
    /**
     * 近红外摄像头
     */
    private FaceCamera nirCamera;
    /**
     * 预览的SurfaceTexture
     */
    private SurfaceTexture surfaceTexture;
    protected int mScreenHeight = 0;
    protected int mScreenWidth = 0;
    private float coordinateScaleFactor = -1;
   // protected TextView tv_direct;

    protected AppCompatImageView iv_in;
    protected AppCompatImageView iv_sight;
    protected ObjectAnimator sight_rotate;


    protected ObjectAnimator  in_rotate;
    protected TextView tv_result;
    protected TextView tv_type;
    /**
     * 红外视频预览
     */

    @Override
    protected void initView() {
        super.initView();
        svPreview = (TextureView) findViewById(R.id.sv_preview);
        fbvFaceRect = (FaceBoxView) findViewById(R.id.fbv_face_rect);
        //tv_direct = (TextView) findViewById(R.id.tv_direct);
        //将人脸框绘制自定义View放置到最上层，以保持人脸检测框能正常显示
        fbvFaceRect.bringToFront();
        iv_in = ((AppCompatImageView) findViewById(R.id.iv_in));
        iv_sight = ((AppCompatImageView) findViewById(R.id.iv_sight));
        tv_result = ((TextView) findViewById(R.id.tv_result));
        tv_type = ((TextView) findViewById(R.id.tv_type));
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initData() {
        //初始化摄像头预览配置
        initPreview();
        //初始化人脸检测处理器
        initFaceDetectorProcessor();

        tv_type.setText(getString(R.string.face_verify_idCard));

        sight_rotate = ObjectAnimator.ofFloat(iv_sight, "rotation", 0.0f, 360.0f);
        sight_rotate.setDuration(4000);
        sight_rotate.setInterpolator(new LinearInterpolator());
        sight_rotate.setRepeatMode(ObjectAnimator.RESTART);
        sight_rotate.setRepeatCount(Animation.INFINITE);

        in_rotate = ObjectAnimator.ofFloat(iv_in, "rotation", 0.0f, -360.0f);
        //in_rotate = new RotateAnimation(0, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        in_rotate.setDuration(4000);
        in_rotate.setInterpolator(new LinearInterpolator());
        in_rotate.setRepeatMode(ObjectAnimator.RESTART);
        in_rotate.setRepeatCount(Animation.INFINITE);
    }

    protected void startAnimator(){
        if (sight_rotate.isStarted()){
            if (sight_rotate.isPaused()){
                tv_result.setText(getString(R.string.face_verifying));
                tv_result.setTextColor(getResources().getColor(R.color.white));
                tv_result.setVisibility(View.VISIBLE);
                sight_rotate.resume();
                in_rotate.resume();
            }
        }else {
            tv_result.setText(getString(R.string.face_verifying));
            tv_result.setTextColor(getResources().getColor(R.color.white));
            tv_result.setVisibility(View.VISIBLE);
            sight_rotate.start();
            in_rotate.start();
        }

    }

    @Override
    protected Integer contentView() {
        return R.layout.face_activity_face_detect;
    }

    /**
     * 初始化人脸检测处理器
     */
    protected void initFaceDetectorProcessor() {
        //初始化FaceDetectorProcessor
        try {
            faceDetectProcessor = new FaceDetectProcessor(visCamera, nirCamera);
            //设置人脸检测器
            faceDetectProcessor.setFaceDetector(FaceConfig.getInstance().getFaceSDK().getVisFaceDetector());
            //注册人脸检测回调接口
            faceDetectProcessor.setFaceDetectCallback(this);
            //获取人脸质量检测器
            faceQualityDetector = FaceConfig.getInstance().getFaceSDK().getFaceQualityDetector();
        } catch (FaceException e) {
            Logger.e(TAG, "onCreate: 初始化SDK失败", e);
            Toast.makeText(AbstractFaceDetectActivity.this, "初始化SDK失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 初始化预览配置
     */
    protected void initPreview() {
        //获取可见光摄像头
        visCamera = getVisCamera();
        if (visCamera == null) {
            throw new IllegalArgumentException("可见光摄像头不存在");
        }
        //设置异步处理模式
        visCamera.setAsync(true);
        //获取近红外摄像头
        nirCamera = getNirCamera();
        if (nirCamera != null) {
            nirCamera.setAsync(true);
        }

//        initNirPreview();

        //此demo中根据摄像头参数设置UI上预览窗体大小，实际使用时，可以根据需求自定义
        ViewGroup.LayoutParams svPreviewLayoutParams = svPreview.getLayoutParams();
        //这边根据屏幕的宽度设置图像的高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;
        CameraParams cameraParams = visCamera.getCameraParams();
        int previewWidth = cameraParams.getPreviewSize().getWidth();
        int previewHeight = cameraParams.getPreviewSize().getHeight();
        //如果配置为宽高互换，那么预览高宽参数互换
        if (cameraParams.isPreviewWHChange()) {
            previewWidth = cameraParams.getPreviewSize().getHeight();
            previewHeight = cameraParams.getPreviewSize().getWidth();
        }
        //判断此时是横屏还是竖直屏幕
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            svPreviewLayoutParams.width = mScreenWidth;
            coordinateScaleFactor = ((float) mScreenWidth / (float) previewWidth);
            svPreviewLayoutParams.height = (int) (coordinateScaleFactor * previewHeight);
            fbvFaceRect.setCoordinateScaleFactor(coordinateScaleFactor);
        } else {
            coordinateScaleFactor = ((float) mScreenHeight / (float) previewHeight);
            svPreviewLayoutParams.width = (int) ((coordinateScaleFactor) * previewWidth);
            svPreviewLayoutParams.height = mScreenHeight;
            fbvFaceRect.setCoordinateScaleFactor(coordinateScaleFactor);
        }

        svPreview.setLayoutParams(svPreviewLayoutParams);
        svPreview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                surfaceTexture = surface;
                if (visCamera == null) {
                    return;
                }
                try {
                    openCamera();
                    startPreview();
                } catch (Exception e) {
                    Logger.e(TAG, "打开摄像头失败", e);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                stopPreview();
                releaseCamera();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
    }

    /*
    private void initNirPreview() {
        if (nirPreview != null) {
            nirPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (nirCamera != null) {
                            nirCamera.setPreviewDisplay(holder);
                            nirCamera.open();
                        }
                    } catch (IOException e) {
                        logger.e(TAG, "红外摄像头开启失败");
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    if (nirCamera != null) {
                        try {
                            nirCamera.setPreviewDisplay(null);
                        } catch (Throwable ignore) {
                        }
                        try {
                            nirCamera.release();
                        } catch (Throwable e) {
                            logger.i(TAG, "红外摄像头关闭失败");
                        }
                    }

                }
            });
        }
    }
     */

    @Override
    protected void onResume() {
        super.onResume();
        openCamera();
        startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPreview();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPreview();
        releaseCamera();
    }

    private void stopPreview() {
        try {
            visCamera.stopPreview();
            if (nirCamera != null) {
                nirCamera.stopPreview();
            }
        } catch (IOException e) {
            Logger.e(TAG, "摄像头关闭预览失败", e);
            Toast.makeText(AbstractFaceDetectActivity.this, "摄像头关闭预览失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startPreview() {
        try {
            visCamera.setPreviewTexture(surfaceTexture);
            visCamera.startPreview();
            Logger.i(TAG, "可见光摄像头开启预览成功: " + visCamera);
            if (nirCamera != null) {
                nirCamera.startPreview();
                Logger.i(TAG, "近红外摄像头开启预览成功: " + nirCamera);
            }
        } catch (IOException e) {
            Logger.e(TAG, "摄像头预览打开失败", e);
            Toast.makeText(AbstractFaceDetectActivity.this, "摄像头预览失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openCamera() {
        //open camera
        try {
            visCamera.open();
            Logger.i(TAG, "可见光摄像头打开成功: " + visCamera);
            if (nirCamera != null) {
                nirCamera.open();
                Logger.i(TAG, "近红外摄像头打开成功: " + nirCamera);
            }
        } catch (IOException e) {
            Logger.e(TAG, "摄像头开启失败", e);
            Toast.makeText(AbstractFaceDetectActivity.this, "摄像头开启失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void releaseCamera() {
        try {
            visCamera.release();
        } catch (IOException e) {
            Logger.e(TAG, "释放可见光摄像头异常", e);
        }
        try {
            if (nirCamera != null) {
                nirCamera.release();
            }
        } catch (Exception e) {
            Logger.e(TAG, "释放红外摄像头异常", e);
        }
    }

    protected abstract FaceCamera getNirCamera();

    protected abstract FaceCamera getVisCamera();

    /**
     * 检测到人脸时的回调，FaceDetectProcessor中检测到人脸后，会通过此接口回调通知
     *
     * @param faceDetectData
     */
    @Override
    public void onFaceDetected(FaceDetectData faceDetectData) {
        FaceData[] faceList = faceDetectData.getFaceList();
        fbvFaceRect.drawFaceBox(faceList);
    }

    /**
     * 人脸检测发生变更时的回调，FaceDetectProcessor检测到视频流中人脸追踪发生变化时回调。
     * 视频流中检测到新的人脸或者之前的人脸有丢失，则会通过此接口回调
     *
     * @param faceDetectData
     * @param addList
     * @param lostList
     */
    @Override
    public void onFaceChanged(FaceDetectData faceDetectData, List<FaceData> addList, List<FaceData> lostList) {
        if (faceDetectData == null) {
            //faceDetectData为空时，表示没有人脸，需要清除界面上的人脸框
            fbvFaceRect.drawFaceBox(null);
        }

    }

    @Override
    public void onLivenessDetected(FaceDetectData faceDetectData) {
        //do nothing
    }

    /**
     * 人脸质量检测
     *
     * @param faceDetectData
     * @param faceData
     * @return
     */
    protected boolean checkFaceQuality(FaceDetectData faceDetectData, FaceData faceData) {
        FaceDataExtraInfo info = (FaceDataExtraInfo) faceData.getTag();
        if (info == null) {
            info = new FaceDataExtraInfo();
            faceData.setTag(info);
        }
        try {
            //调用人脸检测质量接口
            long start = System.currentTimeMillis();
            FaceQuality faceQuality = faceQualityDetector.qualityDetect(faceDetectData.getImage(), faceData);
            long time = System.currentTimeMillis() - start;
            Logger.i(TAG, "checkFaceQuality[" + time + "ms]: 人脸质量: " + faceQualityToString(faceQuality));
//            //判断遮挡
//            if (faceQuality.getMouthOcclusion() <= Constants.Threshold.OCCLUSION) {
              // logger.d(TAG, "嘴巴遮挡. track_id: " + faceData.getTrackId());
                setOcclusionMessage(faceData);
//                return false;
//            }
//            if (faceQuality.getNoseOcclusion() <= Constants.Threshold.OCCLUSION) {
//                logger.d(TAG, "鼻子遮挡. track_id: " + faceData.getTrackId());
//                setOcclusionMessage(faceData);
//                return false;
//            }
//            if (faceQuality.getLeftEyeOcclusion() <= Constants.Threshold.OCCLUSION) {
//                logger.d(TAG, "左眼遮挡. track_id: " + faceData.getTrackId());
//                setOcclusionMessage(faceData);
//                return false;
//            }
//            if (faceQuality.getRightEyeOcclusion() <= Constants.Threshold.OCCLUSION) {
//                logger.d(TAG, "右眼遮挡. track_id: " + faceData.getTrackId());
//                setOcclusionMessage(faceData);
//                return false;
//            }
//            if (faceQuality.getLeftEyeBrewOcclusion() <= Constants.Threshold.OCCLUSION) {
//                logger.d(TAG, "左眉毛遮挡. track_id: " + faceData.getTrackId());
//                setOcclusionMessage(faceData);
//                return false;
//            }
//            if (faceQuality.getRightEyeBrewOcclusion() <= Constants.Threshold.OCCLUSION) {
//                logger.d(TAG, "右眉毛遮挡. track_id: " + faceData.getTrackId());
//                setOcclusionMessage(faceData);
//                return false;
//            }
            info.setFaceQualityMessage(null);
            faceDetectProcessor.updateTrackFace(faceData);
        } catch (FaceQualityException fqe) {
            Logger.e(TAG, "checkFaceQuality: 人脸质量不满足要求: " + faceQualityToString(fqe.getFaceQuality()));
            info.setFaceQualityMessage("请摆正脸");
            try {
                faceDetectProcessor.updateTrackFace(faceData);
            } catch (Exception ignore) {
            }
            return false;
        } catch (Exception e) {
            Logger.e(TAG, "checkFaceQuality: 人脸质量检测失败", e);
            return false;
        }
        return true;
    }

    private void setOcclusionMessage(FaceData faceData) throws FaceException {
        FaceDataExtraInfo info = (FaceDataExtraInfo) faceData.getTag();
        if (info == null) {
            info = new FaceDataExtraInfo();
            faceData.setTag(info);
        }

        info.setFaceQualityMessage("请勿遮挡面部");
        faceDetectProcessor.updateTrackFace(faceData);
    }

    protected String faceQualityToString(FaceQuality faceQuality) {
        if (faceQuality == null) {
            return "NULL";
        }
        return "roll: " + faceQuality.getRoll() +
                ", yaw: " + faceQuality.getYaw() +
                ", pitch: " + faceQuality.getPitch() +
                ", blur: " + faceQuality.getBlur() +
                ", mouthOcclusion: " + faceQuality.getMouthOcclusion() +
                ", leftEyeOcclusion: " + faceQuality.getLeftEyeOcclusion() +
                ", rightEyeOcclusion: " + faceQuality.getRightEyeOcclusion() +
                ", noseOcclusion" + faceQuality.getNoseOcclusion() +
                ", leftEyeBrewOcclusion" + faceQuality.getLeftEyeBrewOcclusion() +
                ", rightEyeBrewOcclusion" + faceQuality.getRightEyeBrewOcclusion();
    }
}
