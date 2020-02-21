package com.arcFace.util.face;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.LivenessInfo;

/**
 * 人脸处理回调
 */
public interface FaceListener {
    /**
     * 当出现异常时执行
     *
     * @param e 异常信息
     */
    void onFail(Exception e);


    /**
     * 请求人脸特征后的回调
     *
     * @param faceFeature 人脸特征数据
     * @param requestId   请求码
     * @param errorCode   错误码
     */
    void onFaceFeatureInfoGet(@Nullable FaceFeature faceFeature,
                              Integer requestId, Integer errorCode,int gender);

    /**
     * 请求活体检测后的回调
     *
     * @param livenessRGBInfo RGB活体检测结果
     * @param requestId       请求码
     * @param errorCode       错误码
     */
    void onFaceLivenessInfoGet(@Nullable LivenessInfo livenessRGBInfo,
                               Integer requestId, Integer errorCode,
                               LivenessType livenessType,long time);

    /**
     * 请求双重活体检测后的回调
     *
     * @param isAlive
     * @param livenessRGBInfo
     * @param livenessIRInfo
     * @param requestId
     * @param errorCode
     * @param livenessType
     */
    void onFaceDoubleLivenessInfo(Boolean isAlive,
                                  @Nullable LivenessInfo livenessRGBInfo,
                                  @NonNull LivenessInfo livenessIRInfo,
                                  Integer requestId, Integer errorCode,
                                  LivenessType livenessType,long time);
}
