package com.face.service;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.callBack.FaceInitListener;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.pojo.Face;
import com.baselibrary.util.ToastUtils;
import com.face.callback.FaceListener;
import com.face.common.FaceConfig;
import com.face.ui.FaceRecBoxView;
import com.face.ui.FaceRecView;
import com.face.utils.FaceUtils;
import com.orhanobut.logger.Logger;
import com.zqzn.android.face.camera.FaceCamera;
import com.zqzn.android.face.camera.FaceCameraView;
import com.zqzn.android.face.exceptions.FaceException;
import com.zqzn.android.face.exceptions.SDKAuthException;
import com.zqzn.android.face.exceptions.SDKAuthExpireException;
import com.zqzn.android.face.exceptions.SDKNotAuthException;
import com.zqzn.android.face.model.FaceSDK;
import com.zqzn.android.face.model.FaceSearchLibrary;
import com.zqzn.android.face.processor.BaseFaceRecProcessor;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.zqzn.android.face.processor.BaseFaceRecProcessor.extractFeatureStep;
import static com.zqzn.android.face.processor.BaseFaceRecProcessor.faceSearchStep;
import static com.zqzn.android.face.processor.BaseFaceRecProcessor.livenessDetectStep;
import static com.zqzn.android.face.processor.BaseFaceRecProcessor.qualityDetectStep;

/**
 * Created by wangyu on 2019/9/24.
 */

public class FaceService {

    private static  volatile  FaceService instance;

    private FaceSearchLibrary faceSearchLibrary;
    private  static  String TAG="FaceService";
    private final ExecutorService service;
    private FaceCamera visCamera;
    private FaceCamera nirCamera;
    private BaseFaceRecProcessor.FaceRecConfig faceRecConfig;
    private BaseFaceRecProcessor faceRecProcessor;

    private FaceService(){
        service = Executors.newCachedThreadPool();
    }

    public static FaceService getInstance(){
        if (instance==null){
            synchronized (FaceService.class){
                if (instance==null){
                    instance=new FaceService();
                }
            }
        }
        return instance;
    }

    public void  initFace(String code,Context mContext,FaceInitListener listener){
        FaceConfig.getInstance().init(mContext, code, new FaceSDK.InitCallback() {
            @Override
            public void onInitSuccess() {
                listener.initSuccess();
            }

            @Override
            public void onInitFailed(Throwable throwable) {
                String error;
                if (throwable instanceof SDKAuthExpireException) {
                    error="授权已过期: " + throwable.getMessage();
                } else if (throwable instanceof SDKNotAuthException) {
                    error="未授权: " + throwable.getMessage();
                } else if (throwable instanceof SDKAuthException) {
                    error="授权失败: " + throwable.getMessage();
                } else {
                    error="授权异常：" + throwable.getMessage();
                }
                listener.initFail(error);
            }
        });
    }
    //初始化摄像头
    public void initCamera(FaceRecView visCameraView, FaceRecBoxView faceRecBoxView, FaceCamera nirCamera, BaseFaceRecProcessor.FaceRecCallback faceRecCallback) {
            visCamera = FaceConfig.getInstance().getVisCamera();
            visCamera.setAsync(true);

        try {
            visCameraView.setCamera(visCamera);
            visCameraView.setViewMode(FaceCameraView.ViewMode.TEXTURE_VIEW);
            visCameraView.setFaceDataView(faceRecBoxView);
        } catch (IOException e) {
            Logger.i(TAG, "开启可见光摄像头失败");
        }
        if (nirCamera != null) {
            nirCamera.setAsync(true);
        }

        try {
            faceRecConfig = new BaseFaceRecProcessor.FaceRecConfig(new BaseFaceRecProcessor.FaceRecStep[]{qualityDetectStep,livenessDetectStep,extractFeatureStep, faceSearchStep, });
            //设置为近红外活体检测
            faceRecConfig.maxSearchFailTimes=2;
            faceRecConfig.livenessDetectMode = BaseFaceRecProcessor.LivenessDetectMode.NIR_LIVENESS;
            faceRecProcessor = new BaseFaceRecProcessor(FaceConfig.getInstance().getFaceSDK(), visCamera, nirCamera, faceRecConfig);
            faceRecProcessor.setFaceDetectCallback(faceRecCallback);
        } catch (FaceException e) {
            Logger.e(TAG, "创建人脸识别器失败", e);

        }
        openCloseNirCamera(true , nirCamera);
    }


    private void openCloseNirCamera(boolean open,FaceCamera  nirCamera) {
        if (nirCamera != null) {
            try {
                if (open) {
                    if (!nirCamera.isOpen()) {
                        nirCamera.open();
                        nirCamera.startPreview();
                    }
                } else if (nirCamera.isOpen()) {
                    nirCamera.release();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public    boolean addUserToSearchLibrary(FaceSearchLibrary faceSearchLibrary,Face face) {
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
            return false;
        }
    }

    //加载模板数据
    public void loadUserToSearchLibrary( Context context,FaceListener faceListener) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                //获取用户数据管理器
                DBUtil dbUtil = BaseApplication.getDbUtil();
                //    UserManager userManager = FaceConfig.getInstance().getUserManager();
                //获取离线1：N搜索库
                faceSearchLibrary = FaceConfig.getInstance().getFaceSDK().getFaceSearchLibrary();
                int offset = 0;
                int limit = 10;
                while (true) {
                    List<Face> faces = dbUtil.getDaoSession().queryBuilder(Face.class).offset(offset).limit(limit).build().list();
                  //  List<User> users = userManager.find(limit, offset);
                    if (faces == null || faces.isEmpty()) {
                        break;
                    }
                    for (Face face : faces) {
                        //将用户特征加载到1：N离线搜索库中
                        try {
                            FaceUtils.addToSearchLibrary(face);
                        } catch (FaceException e) {
                            e.printStackTrace();
                        }
                    }
                    offset += faces.size();
                    if (faces.size() < limit) {
                        Log.i(TAG, "loadUserToSearchLibrary: 没有更多的用户数据需要加载");
                        break;
                    }
                }
                Logger.i(TAG, "用户加载完成，总数：" + offset);
                faceListener.onLoadDataListener();
//                if (context instanceof Activity){
//                    ((Activity) context) .runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            faceListener.onLoadDataListener();
//                        }
//                    });
//                }

            }
        });

    }

}