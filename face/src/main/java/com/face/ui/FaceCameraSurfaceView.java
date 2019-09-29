package com.face.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zqzn.android.face.camera.FaceCamera;
import com.face.device.CameraHelper;

public class FaceCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private final CameraHelper cameraHelper = new CameraHelper();

    public FaceCameraSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public FaceCameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    public FaceCameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FaceCameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getHolder().addCallback(this);
    }

    public void setFaceCamera(FaceCamera faceCamera) {
        cameraHelper.setFaceCamera(faceCamera);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        cameraHelper.openCamera(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraHelper.closeCamera();
    }
}
