package com.face.ui;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.TextureView;

import com.zqzn.android.face.camera.FaceCamera;
import com.face.device.CameraHelper;

/**
 * 摄像头关联TextureView
 */
public class FaceCameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private static final String TAG = FaceCameraTextureView.class.getSimpleName();

    private final CameraHelper cameraHelper = new CameraHelper();

    public FaceCameraTextureView(Context context) {
        super(context);
        setSurfaceTextureListener(this);
    }

    public FaceCameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
    }

    public FaceCameraTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSurfaceTextureListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FaceCameraTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setSurfaceTextureListener(this);
    }

    public void setFaceCamera(@NonNull FaceCamera faceCamera) {
        cameraHelper.setFaceCamera(faceCamera);
        if (faceCamera.getCameraParams().isPreviewMirror()) {
            this.setScaleX(-1);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        cameraHelper.openCamera(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        cameraHelper.closeCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
