package com.face.common;

import com.zqzn.android.face.camera.FaceCamera;
import com.zqzn.android.face.camera.FaceImageSource;
import com.zqzn.android.face.exceptions.FaceException;
import com.zqzn.android.face.image.FaceImage;
import com.zqzn.android.face.processor.FaceDetectProcessor;

/**
 * Created by wangyu on 2019/9/27.
 */

public class TestFaceDetectProcessor extends FaceDetectProcessor {
    private boolean isDetect = false;

    public void setDetect(boolean isDetect){
        this.isDetect=isDetect;
    }

    public TestFaceDetectProcessor(FaceImageSource visImageSource, FaceImageSource nirImageSource) {
        super(visImageSource, nirImageSource);
    }

    @Override
    public void onImageFrame(Object sender, Object data) {
        if(isDetect){
        super.onImageFrame(sender, data);}
    }

}
