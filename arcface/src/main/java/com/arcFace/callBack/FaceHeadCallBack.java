package com.arcFace.callBack;

/**
 * 注册的人脸头像
 */
public interface FaceHeadCallBack {

    void faceHeadCallBack(byte[] faceHead,byte[] arcFaceFeature,String registerName);

}
