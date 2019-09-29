package com.face.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.baselibrary.pojo.Face;
import com.face.callback.FaceListener;
import com.face.common.FaceConfig;
import com.face.db.User;
import com.face.db.UserManager;
import com.orhanobut.logger.Logger;
import com.zqzn.android.face.data.AddedPerson;
import com.zqzn.android.face.data.FaceData;
import com.zqzn.android.face.data.FaceItem;
import com.zqzn.android.face.exceptions.FaceException;
import com.zqzn.android.face.image.RGBImage;
import com.zqzn.android.face.jni.Tool;
import com.zqzn.android.face.model.FaceDetector;
import com.zqzn.android.face.model.FaceFeatureExtractor;
import com.zqzn.android.face.model.FaceSearchLibrary;
import com.zqzn.android.zqznfacesdk.ZqznFaceSDK;

import java.io.File;
import java.util.List;

/**
 * Created by wangyu on 2019/9/25.
 */

public class FaceUtils {
    public static int imgWidth = 102;
    public static int imgHeight = 126;

    /**
     * 将用户及其特征加载到指定的离线1：N搜索库中
     *
     * @param face
     * @throws FaceException
     */
    public static void addToSearchLibrary( Face face) throws FaceException {
        FaceSearchLibrary faceSearchLibrary = FaceConfig.getInstance().getFaceSDK().getFaceSearchLibrary();
        //SDK支持每个人使用多个人脸特征，此demo中每个人仅录入了一张人脸图作为底库
        FaceItem[] faceItem = new FaceItem[1];
        //人员ID(personId)与人脸ID(faceId)是两个不同的概念，personId为人员的唯一ID，faceId为人脸的唯一ID，也就是说一个用户使用多张人脸作为底库的时候，有多个faceId
        faceItem[0] = new FaceItem(face.getUId(), face.getFeature());
        AddedPerson addedPerson = new AddedPerson(face.getUId(), face.getName(), faceItem);
        faceSearchLibrary.addPerson(addedPerson);
    }

    public static float[] extractFaceFeature(File file) {
        float[] feature = null;
        ZqznFaceSDK faceSDK = FaceConfig.getInstance().getFaceSDK();
        FaceDetector faceDetector = faceSDK.getVisFaceDetector();
        FaceFeatureExtractor faceFeatureExtractor = faceSDK.getFaceFeatureExtractor();
        try {
            //将本地图片文件转换成RGBImage
            RGBImage faceImage = RGBImage.bitmapToRGBImage(Tool.loadBitmap(file));
            //检测图片中最大人脸
            FaceData faceData = faceDetector.detectMaxFace(faceImage, true);
            if (faceData == null) {

                Logger.d( "照片不符合要求，请重新拍摄");
                return null;
            }
            //对检测出来的人脸进行特征抽取
            feature = faceFeatureExtractor.extractFaceFeature(faceImage, faceData);
        } catch (FaceException e) {
            Logger.e("onClick: 人脸特征抽取失败", e);
            return null;
            }
        if (feature == null) {
            Logger.w("onClick: 特征抽取失败");
            return null;
        }
        return feature;
    }


    //通过图片进行提取特征 用于认证合一
    public static float[] extractFeatureBybitmap(Bitmap bitmap) {
        ZqznFaceSDK faceSDK = FaceConfig.getInstance().getFaceSDK();
        FaceDetector faceDetector = faceSDK.getVisFaceDetector();
        FaceFeatureExtractor faceFeatureExtractor = faceSDK.getFaceFeatureExtractor();
        float[] feature = null;
        //将图片转
        //换成RGBImage
        RGBImage img = RGBImage.bitmapToRGBImage(bitmap);
            //检测图片中的最大人脸
            FaceData faceData = null;
            try {
                faceData = faceDetector.detectMaxFace(img, true);

                if (faceData == null) {
                    Logger.d("未检测到人脸");
                    return null;
                }
                //抽取人脸特征
                feature = faceFeatureExtractor.extractFaceFeature(img, faceData);
                if (feature == null) {
                    Logger.d("抽取特征失败，可能照片质量较低");
                    return null;
                }

            } catch (FaceException e) {
                e.printStackTrace();
            }
        return feature;
    }

    public static float[] extractFeatureByBGR(byte[] bgrBuff) {
        //bgrBuff
        ZqznFaceSDK faceSDK = FaceConfig.getInstance().getFaceSDK();
        FaceDetector faceDetector = faceSDK.getVisFaceDetector();
        FaceFeatureExtractor faceFeatureExtractor = faceSDK.getFaceFeatureExtractor();
        float[] feature = null;
        //将图片转
        //换成RGBImage
        int[] toArgb = Tool.bgrImageToArgb(bgrBuff, imgWidth, imgHeight);
        byte[] bytes = Tool.argbImageToRgb(toArgb, imgWidth, imgHeight);

        RGBImage img =new RGBImage(bytes,imgWidth,imgHeight);
        //检测图片中的最大人脸
        FaceData faceData = null;
        try {
            faceData = faceDetector.detectMaxFace(img, true);

            if (faceData == null) {
                Logger.d("未检测到人脸");
                return null;
            }
            //抽取人脸特征
            feature = faceFeatureExtractor.extractFaceFeature(img, faceData);
            if (feature == null) {
                Logger.d("抽取特征失败，可能照片质量较低");
                return null;
            }

        } catch (FaceException e) {
            e.printStackTrace();
        }
        return feature;
    }

}
