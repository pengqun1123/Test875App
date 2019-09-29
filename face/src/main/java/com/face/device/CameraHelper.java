package com.face.device;

import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;

import com.orhanobut.logger.Logger;
import com.zqzn.android.face.camera.FaceCamera;


public class CameraHelper {
    private static final String TAG = CameraHelper.class.getSimpleName();

    private FaceCamera faceCamera;
    private boolean useSurfaceTexture = false;

    public CameraHelper() {
    }

    public CameraHelper(FaceCamera faceCamera) {
        this.faceCamera = faceCamera;
    }

    public FaceCamera getFaceCamera() {
        return faceCamera;
    }

    public void setFaceCamera(FaceCamera faceCamera) {
        this.faceCamera = faceCamera;
    }

    public synchronized void openCamera(@NonNull SurfaceTexture surfaceTexture) {
        if (faceCamera == null) {
            Logger.w(TAG, "摄像头为NULL");
            return;
        }

        if (faceCamera.isOpen()) {
            Logger.i(TAG, "摄像头已经开启: " + faceCamera);
            return;
        }
        useSurfaceTexture = true;
        try {
            faceCamera.open();
            Logger.i(TAG, "摄像头打开成功: " + faceCamera);
        } catch (Exception e) {
            Logger.e(TAG, "摄像头打开失败: " + faceCamera, e);
            return;
        }
        try {
            faceCamera.setPreviewTexture(surfaceTexture);
            faceCamera.startPreview();
            Logger.e(TAG, "摄像头启动预览成功: " + faceCamera);
        } catch (Exception e) {
            Logger.e(TAG, "摄像头启动预览失败: " + faceCamera, e);
        }
    }

    public synchronized void openCamera(@NonNull SurfaceHolder surfaceHolder) {
        if (faceCamera == null) {
            Logger.w(TAG, "摄像头为NULL");
            return;
        }

        if (faceCamera.isOpen()) {
            Logger.i(TAG, "摄像头已经开启: " + faceCamera);
            return;
        }
        useSurfaceTexture = false;
        try {
            faceCamera.open();
            Logger.i(TAG, "摄像头打开成功: " + faceCamera);
        } catch (Exception e) {
            Logger.e(TAG, "摄像头打开失败: " + faceCamera, e);
            return;
        }
        try {
            faceCamera.setPreviewDisplay(surfaceHolder);
            faceCamera.startPreview();
            Logger.e(TAG, "摄像头启动预览成功: " + faceCamera);
        } catch (Exception e) {
            Logger.e(TAG, "摄像头启动预览失败: " + faceCamera, e);
        }
    }

    public synchronized void closeCamera() {
        if (faceCamera == null) {
            Logger.w(TAG, "摄像头为NULL");
            return;
        }
        if (!faceCamera.isOpen()) {
            Logger.i(TAG, "摄像头已经关闭: " + faceCamera);
            return;
        }
        try {
            if (useSurfaceTexture) {
                faceCamera.setPreviewTexture(null);
            } else {
                faceCamera.setPreviewDisplay(null);
            }
        } catch (Exception e) {
            Logger.e(TAG, "解绑SurfaceTexture失败: " + faceCamera, e);
        }
        try {
            faceCamera.stopPreview();
            Logger.i(TAG, "摄像头预览关闭成功: " + faceCamera);
        } catch (Exception e) {
            Logger.e(TAG, "摄像头预览关闭失败: " + faceCamera, e);
        }
        try {
            faceCamera.release();
            Logger.i(TAG, "摄像头释放成功: " + faceCamera);
        } catch (Exception e) {
            Logger.e(TAG, "摄像头释放失败: " + faceCamera, e);
        }
    }
}
