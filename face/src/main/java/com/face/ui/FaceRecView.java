package com.face.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.zqzn.android.face.camera.FaceCameraView;
import com.zqzn.android.face.camera.Size;

import java.io.IOException;

public class FaceRecView extends FaceCameraView {
    private static final String TAG = "FaceRecDemoView";
    View faceDataView;

    public FaceRecView(Context context) {
        super(context);
    }

    public FaceRecView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceRecView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setLayoutMargin(int left, int top, int right, int bottom) {
        super.setLayoutMargin(0, 0, 0, 0);
        if (faceDataView != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) faceDataView.getLayoutParams();
            params.setMargins(left, top, right, bottom);
            faceDataView.setLayoutParams(params);
        }
    }

    @Override
    public synchronized void openCamera() throws IOException {
        super.openCamera();
    }


    public View getFaceDataView() {
        return faceDataView;
    }

    public void setFaceDataView(View faceDataView) {
        this.faceDataView = faceDataView;
    }

    @Override
    protected Size getPreviewViewSize() {
        Size size = super.getPreviewViewSize();
        double wr = (double) getWidth() / size.getWidth(), hr = (double) getHeight() / size.getHeight();
        double r = wr > hr ? hr : wr;
        return new Size((int) (size.getWidth() * r), (int) (size.getHeight() * r));
    }
}
