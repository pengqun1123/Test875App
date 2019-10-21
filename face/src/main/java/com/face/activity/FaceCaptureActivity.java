package com.face.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.pojo.Face;
import com.baselibrary.util.ToastUtils;
import com.face.R;
import com.face.common.FaceConfig;
import com.face.ui.FaceRecBoxView;
import com.face.utils.FaceUtils;
import com.face.utils.FileHelper;
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
import com.zqzn.android.face.model.FaceSearchLibrary;
import com.zqzn.android.face.processor.FaceDetectProcessor;
import com.face.ui.FaceRecView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 人脸检测及采集示例
 * <p>
 * 主要用于人脸采集过程
 */
@Route(path = ARouterConstant.FACE_RIGSTER_ACTIVITY)
public class FaceCaptureActivity extends AppCompatActivity implements FaceDetectProcessor.FaceDetectCallback {

    private static final String TAG = FaceCaptureActivity.class.getSimpleName();
    private boolean isExtractFeature=false;
    /**
     * 人脸图片存储目录
     */
    private File faceImageDir;
    private FaceRecView visCameraView;
    private FaceRecBoxView faceRecBoxView;
    private FaceCamera visCamera;
    private FaceDetectProcessor faceDetectProcessor;
    private FaceQualityDetector faceQualityDetector;
    private String name;
    private FaceSearchLibrary faceSearchLibrary;
    private File confirmedFaceImage;
    private Face face;
    private Long faceId;
    private TextView warning;

    private int time=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_activity_face_capture);
        visCameraView = (FaceRecView) findViewById(R.id.camera_view);
        faceRecBoxView = (FaceRecBoxView) findViewById(R.id.camera_mask_view);
        faceRecBoxView.bringToFront();
        warning = ((TextView) findViewById(R.id.tv_warning));
        warning.setText(time+"");
        initCamera();
        initData();



    }


    private void initData() {
        name = getIntent().getStringExtra("name");
        faceId = getIntent().getLongExtra("faceId",-1);
        try {
            faceQualityDetector = FaceConfig.getInstance().getFaceSDK().getFaceQualityDetector();
        } catch (FaceException e) {
            Logger.e(TAG, "获取人脸质量检测器失败", e);
        }


        faceSearchLibrary = FaceConfig.getInstance().getFaceSDK().getFaceSearchLibrary();
        faceImageDir = new File(FaceConfig.getInstance().getAppRootDir(), "face_image");
        faceImageDir.mkdirs();


        handler.sendEmptyMessageDelayed(0x001,1000);
    }



    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            time--;
            warning.setText(time+"");
            if (time==0){
                warning.setVisibility(View.GONE);
            }
            if (time==-1){
                if (face==null) {
                    Face face1 = new Face();
                    EventBus.getDefault().post(face1);
                    finish();
                }
            }
            handler.sendEmptyMessageDelayed(0x001,1000);
        }
    };

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
        if (time == 0) {
            if (isExtractFeature) {
                return;
            }
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
                    confirmedFaceImage = new File(faceImageDir, "" + System.currentTimeMillis() + ".jpg");
                    Tool.saveToJpeg(faceBitmap, confirmedFaceImage);

                    saveUser(faceBitmap);


                }
            } catch (FaceQualityException e) {
                Logger.e(TAG, "onFaceDetected: 人脸质量检测不符合要求: " + faceQualityToString(e.getFaceQuality()));
            } catch (FaceException e) {
                Logger.e(TAG, "onFaceDetected: 人脸质量检测失败", e);
            }
        }
    }

    private void stopPreview() {
        try {
            visCamera.stopPreview();

        } catch (IOException e) {
            Logger.e(TAG, "摄像头关闭预览失败", e);
            Toast.makeText(FaceCaptureActivity.this, "摄像头关闭预览失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        try {
            visCamera.release();
        } catch (IOException e) {
            Logger.e(TAG, "释放可见光摄像头异常", e);
        }
    }



    private void saveUser(Bitmap bitmap) {
        //抽取特征
        float[] feature = FaceUtils.extractFeatureBybitmap(bitmap);
        if (feature == null) {
            ToastUtils.showSingleToast(this,"特征提取失败");
            return;
        }
        isExtractFeature=true;
        //持久化用户信息
        try {
            persistentUser(name, feature);
        } catch (IOException e) {
            runOnUiThread(() -> {
                ToastUtils.showSingleToast(this, "保存人员失败: " + e.getMessage());
            });
        }
    }


    /**
     * 持久化用户信息
     *
     * @param name
     * @param feature
     */
    private void persistentUser(String name, float[] feature) throws IOException {
        //  user.setImagePath(confirmedFaceImage.getAbsolutePath());
        //将用户信息写入数据库
        if (face!=null){
            return;
        }
        face = new Face();
        if (faceId!=-1){
            face.setUId(faceId);
        }
        face.setName(name);
        face.setFeature(feature);
        face.setImagePath(confirmedFaceImage.getAbsolutePath());

        try {
            DBUtil dbUtil = BaseApplication.getDbUtil();
            dbUtil.insertOrReplace(face);
            //写数据成功，将用户信息加载到离线1：N搜索库中
            boolean addSearchLibraryRet = addUserToSearchLibrary(faceSearchLibrary,face);
            if (addSearchLibraryRet) {
                EventBus.getDefault().post(face);
                finish();
            }
        }catch (Exception e){
            ToastUtils.showSingleToast(FaceCaptureActivity.this, "增加失败");
        }

        }


    /**
     * 将新加的用户信息加载到离线1：N搜索库中
     *
     * @param face
     * @return
     */
    private boolean addUserToSearchLibrary(FaceSearchLibrary faceSearchLibrary,Face face) {
        try {
            //确保之前在搜索库中的用户信息已经被移除
            faceSearchLibrary.removePersons(new long[]{face.getUId()});
        } catch (FaceException ignore) {
        }
        try {
            //将用户特征信息加载到离线1：N搜索库中
            FaceUtils.addToSearchLibrary(face);
            Logger.d(TAG, "加载用户到缓存成功：" + face.getUId() + "," + face.getName());
            //runOnUiThread(() -> ToastUtils.showSingleToast(this, "增加成功"));
            return true;
        } catch (FaceException e) {
            Logger.e(TAG, "加载用户到缓存失败：" +face.getUId() + "," + face.getName(), e);
            runOnUiThread(() -> {
                ToastUtils.showSingleToast(this, "写入离线1：N搜索库失败: " + e.getMessage());
            });
            return false;
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
            if (handler!=null) {
                handler.removeMessages(0x001);
                handler=null;
            }
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
