package com.face.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.face.R;
import com.face.common.FaceConfig;
import com.orhanobut.logger.Logger;
import com.zqzn.android.face.camera.FaceCamera;
import com.zqzn.android.face.camera.FaceCameraView;
import com.zqzn.android.face.data.FaceData;
import com.zqzn.android.face.data.FaceDetectData;
import com.zqzn.android.face.data.FaceQuality;
import com.zqzn.android.face.data.FaceRect;
import com.zqzn.android.face.exceptions.FaceException;
import com.zqzn.android.face.exceptions.FaceQualityException;
import com.zqzn.android.face.jni.Tool;
import com.zqzn.android.face.model.FaceQualityDetector;
import com.zqzn.android.face.processor.FaceDetectProcessor;
import com.zqzn.android.face.processor.FaceRecBoxView;
import com.face.ui.FaceRecView;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 人脸检测及采集示例
 * <p>
 * 主要用于人脸采集过程
 */
public class FaceCaptureActivity extends AppCompatActivity implements FaceDetectProcessor.FaceDetectCallback {

    private static final String TAG = FaceCaptureActivity.class.getSimpleName();

    /**
     * 人脸图片存储目录
     */
    private File faceImageDir;
    private File faceImageFile;
    private FaceRecView visCameraView;
    private FaceRecBoxView faceRecBoxView;
    private FaceCamera visCamera;
    private FaceDetectProcessor faceDetectProcessor;
    private FaceQualityDetector faceQualityDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_activity_face_capture);
        visCameraView = (FaceRecView) findViewById(R.id.camera_view);
        faceRecBoxView = (FaceRecBoxView) findViewById(R.id.camera_mask_view);
        faceRecBoxView.bringToFront();
        initCamera();
        try {
            faceQualityDetector = FaceConfig.getInstance().getFaceSDK().getFaceQualityDetector();
        } catch (FaceException e) {
            Logger.e(TAG, "获取人脸质量检测器失败", e);
        }
        faceImageDir = new File(FaceConfig.getInstance().getAppRootDir(), "face_image");
        faceImageDir.mkdirs();
        faceImageFile = new File(faceImageDir, "temp_face.jpg");
    }

    private void initCamera() {
        visCamera = FaceConfig.getInstance().getVisCamera();
        visCamera.setAsync(true);
        try {
            visCameraView.setCamera(visCamera);
            visCameraView.setViewMode(FaceCameraView.ViewMode.TEXTURE_VIEW);
            visCameraView.setFaceDataView(faceRecBoxView);
        } catch (IOException e) {
            Logger.i(TAG, "开启可见光摄像头失败");
        }

        //初始化人脸检测器
        faceDetectProcessor = new FaceDetectProcessor(visCamera, null);
        faceDetectProcessor.setFaceDetector(FaceConfig.getInstance().getFaceSDK().getVisFaceDetector());
        faceDetectProcessor.setFaceDetectCallback(this);
    }


    /**
     * 人脸检测回调
     *
     * @param faceDetectData
     */
    @Override
    public void onFaceDetected(FaceDetectData faceDetectData) {
        //人脸框绘制
        runOnUiThread(() -> faceRecBoxView.sendFaceData(faceDetectData));

        FaceData[] faceList = faceDetectData.getFaceList();
        if (faceList == null || faceList.length == 0) {
            return;
        }
        //获取最大人脸
        FaceData maxFace = FaceData.getMaxFace(faceList);
        try {
            //检测人脸质量
            FaceQuality faceQuality = faceQualityDetector.qualityDetect(faceDetectData.getImage(), maxFace);
            Logger.i(TAG, "onFaceDetected: 人脸质量检测结果：" + faceQualityToString(faceQuality));
            //头部姿态
            float roll = faceQuality.getRoll();
            float yaw = faceQuality.getYaw();
            float pitch = faceQuality.getPitch();
            //模糊值
            float blur = faceQuality.getBlur();
            if (Math.abs(roll) <= 10 && Math.abs(yaw) <= 10 && Math.abs(pitch) <= 10) {
                //质量符合要求
                FaceRect faceRect = maxFace.getFaceRect();
                //截取人脸图，最后一个参数rectScale的意思是基于检测出来的人脸大小，放大多少倍来截取原始图片中的人脸
                Bitmap faceBitmap = faceDetectData.getImage().cropBitmap(faceRect.getLeft(), faceRect.getTop(),
                        faceRect.getRight(), faceRect.getBottom(), 2.0f);

                //存储图片
                Tool.saveToJpeg(faceBitmap, faceImageFile);

                //将截取的图片地址返回给调用Activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("face_image", faceImageFile.getAbsolutePath());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        } catch (FaceQualityException e) {
            Logger.e(TAG, "onFaceDetected: 人脸质量检测不符合要求: " + faceQualityToString(e.getFaceQuality()));
        } catch (FaceException e) {
            Logger.e(TAG, "onFaceDetected: 人脸质量检测失败", e);
        }
    }

    @Override
    public void onLivenessDetected(FaceDetectData faceDetectData) {

    }

    @Override
    public void onFaceChanged(FaceDetectData faceDetectData, List<FaceData> list, List<FaceData> list1) {
        //人脸框绘制
        runOnUiThread(() -> faceRecBoxView.sendFaceData(faceDetectData));
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
        }
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
