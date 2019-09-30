package com.face.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baselibrary.base.BaseActivity;
import com.face.R;
import com.face.callback.FaceListener;
import com.face.db.User;
import com.face.db.UserManager;
import com.face.service.FaceService;
import com.orhanobut.logger.Logger;
import com.zqzn.android.face.camera.FaceCamera;
import com.zqzn.android.face.camera.FaceCameraView;
import com.zqzn.android.face.data.FaceData;
import com.zqzn.android.face.data.FaceDetectData;
import com.zqzn.android.face.data.SearchedPerson;
import com.zqzn.android.face.exceptions.FaceException;
import com.zqzn.android.face.jni.Tool;
import com.zqzn.android.face.model.FaceSDK;
import com.zqzn.android.face.model.FaceSearchLibrary;
import com.zqzn.android.face.processor.BaseFaceRecProcessor;
import com.zqzn.android.face.processor.FaceDetectProcessor;
import com.zqzn.android.face.processor.FaceRecBoxView;
import com.face.common.FaceConfig;
import com.face.ui.FaceRecView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.zqzn.android.face.processor.BaseFaceRecProcessor.extractFeatureStep;
import static com.zqzn.android.face.processor.BaseFaceRecProcessor.faceSearchStep;
import static com.zqzn.android.face.processor.BaseFaceRecProcessor.livenessDetectStep;

public class V3FaceRecActivity extends BaseActivity implements BaseFaceRecProcessor.FaceRecCallback, FaceSDK.InitCallback, FaceListener {

    private static final String TAG = V3FaceRecActivity.class.getSimpleName();

    private FaceRecView visCameraView;
    private FaceRecBoxView faceRecBoxView;
    private FaceCamera visCamera;
    private FaceCamera nirCamera;
    private BaseFaceRecProcessor faceRecProcessor;
    private BaseFaceRecProcessor.FaceRecConfig faceRecConfig;
    private File logFilePath;
    private SurfaceView nirPreview;
    private String serialNumber="VG9D-QVA7-956E-RE97 ";

    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private FaceSearchLibrary faceSearchLibrary;
    private ProgressBar pb;

    @Override
    protected Integer contentView() {
        return R.layout.face_activity_v3_face_rec;
    }

    @Override
    protected void initView() {
        visCameraView = (FaceRecView) findViewById(R.id.camera_view);
        faceRecBoxView = (FaceRecBoxView) findViewById(R.id.camera_mask_view);
        faceRecBoxView.bringToFront();
        // nirPreview = (SurfaceView) findViewById(R.id.nir_preview);
        pb = ((ProgressBar) findViewById(R.id.pb));
        //    nirPreview.bringToFront();
        //  visCamera = FaceConfig.getInstance().getVisCamera();
        nirCamera = FaceConfig.getInstance().getNirCamera();
        //  logFilePath = new File(String.format("%s/face_rec_log", FaceConfig.getInstance().getAppRootDir()));
        //noinspection ResultOfMethodCallIgnored
        // logFilePath.mkdirs();
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initData() {
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        }else {
            //  FaceConfig.getInstance().init(this, serialNumber.toUpperCase(), this);
            // initCamera();
            //   FaceService.getInstance().initCamera(visCameraView,faceRecBoxView,nirCamera,this);
            // initNirPreview();
            FaceService.getInstance().initCamera(visCameraView,faceRecBoxView,nirCamera,this);
            // initNirPreview();
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
               // FaceConfig.getInstance().init(this, serialNumber.toUpperCase(), this);
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
       Log.d("555","onFaceChanged:"+faceDetectData);
        runOnUiThread(() -> faceRecBoxView.sendFaceData(faceDetectData));
    }

    @Override
    public void onFaceRecCompleted(FaceDetectData faceDetectData, FaceData faceData, BaseFaceRecProcessor.FaceTrackData faceTrackData) {
        //faceRecConfig.livenessDetectMode = BaseFaceRecProcessor.LivenessDetectMode.NIR_LIVENESS;
        Log.d("555","onFaceRecCompleted:"+faceTrackData.searchedPerson.getFaceId());
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ").format(new Date());
        try  {
            //后面根据recId保存识别相关记录
            String recId = Long.toString(System.currentTimeMillis());
            String livenessScore = "不判断";
            livenessScore = Float.toString(faceTrackData.livenessScore);
            long t = System.currentTimeMillis() - faceTrackData.createTime;
           // String subDir = "pass";
            StringBuilder log = new StringBuilder(String.format("%s(rec_id=%s, 耗时=%d ms): ", time, recId, t));
            //判断搜索次数
            if (faceTrackData.searchTimes.get() > 0) {
                //判断活体检测结果
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
            }
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

    @Override
    public void onInitSuccess() {
        pb.setVisibility(View.VISIBLE);
        FaceService.getInstance().loadUserToSearchLibrary(this,this);
    }

    @Override
    public void onInitFailed(Throwable throwable) {

    }

    private void loadUserToSearchLibrary() {

        //获取用户数据管理器
        UserManager userManager = FaceConfig.getInstance().getUserManager();
        //获取离线1：N搜索库
        faceSearchLibrary = FaceConfig.getInstance().getFaceSDK().getFaceSearchLibrary();
        int offset = 0;
        int limit = 10;
        while (true) {
            List<User> users = userManager.find(limit, offset);
            if (users == null || users.isEmpty()) {
                break;
            }
            for (User user : users) {
                //将用户特征加载到1：N离线搜索库中
                addUserToSearchLibrary(user);
            }
            offset += users.size();
            if (users.size() < limit) {
                Log.i(TAG, "loadUserToSearchLibrary: 没有更多的用户数据需要加载");
                break;
            }
        }
        Logger.i(TAG, "用户加载完成，总数：" + offset);
        final int finalCount = offset;
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "用户加载完成，总数：" + finalCount, Toast.LENGTH_SHORT).show());
    }

    private void addUserToSearchLibrary(User user) {
        try {
            user.addToSearchLibrary(faceSearchLibrary);
            Logger.d(TAG, "加载用户到缓存成功：" + user.getId() + ", " + user.getName());
        } catch (FaceException e) {
            Logger.e(TAG, "加载用户到缓存失败：" + user.getId() + "," + user.getName(), e);
        }
    }

    @Override
    public void onLoadDataListener() {
        pb.setVisibility(View.GONE);
        FaceService.getInstance().initCamera(visCameraView,faceRecBoxView,nirCamera,this);
        //initNirPreview();
    }
}
