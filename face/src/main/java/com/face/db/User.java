package com.face.db;

import android.support.annotation.NonNull;

import com.zqzn.android.face.data.AddedPerson;
import com.zqzn.android.face.data.FaceItem;
import com.zqzn.android.face.exceptions.FaceException;
import com.zqzn.android.face.model.FaceSearchLibrary;
import com.face.utils.BlobHelper;

import java.io.Serializable;

/**
 * 用户信息类
 */
public class User implements Serializable {

    /**
     * 用户ID，数据库主键-自动生成
     */
    private long id;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 图片存储路径
     */
    private String imagePath;

    /**
     * 用户人脸特征
     */
    private float[] feature;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public float[] getFeature() {
        return feature;
    }

    public void setFeature(float[] feature) {
        this.feature = feature;
    }

    public void setFeatureString(String featureStr) {
        if (featureStr == null || featureStr.isEmpty()) {
            feature = null;
            return;
        }
        String[] data = featureStr.split(",");
        this.feature = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            feature[i] = Float.parseFloat(data[i]);
        }
    }

    public String getFeatureString() {
        if (feature != null && feature.length > 0) {
            StringBuilder strBuilder = new StringBuilder();
            for (float f : feature) {
                strBuilder.append(f);
                strBuilder.append(",");
            }
            return strBuilder.substring(0, strBuilder.length() - 1);
        }
        return null;
    }

    public void setFeatureBytes(byte[] featureBytes) {
        if (featureBytes == null || featureBytes.length == 0) {
            feature = null;
            return;
        }
        feature = BlobHelper.byteArrayToFloatArray(featureBytes);
    }

    public byte[] getFeatureBytes() {
        if (feature == null) {
            return null;
        }
        return BlobHelper.floatArrayToByteArray(feature);
    }

    /**
     * 将用户及其特征加载到指定的离线1：N搜索库中
     *
     * @param faceSearchLibrary
     * @throws FaceException
     */
    public void addToSearchLibrary(FaceSearchLibrary faceSearchLibrary) throws FaceException {
        //SDK支持每个人使用多个人脸特征，此demo中每个人仅录入了一张人脸图作为底库
        FaceItem[] faceItem = new FaceItem[1];
        //人员ID(personId)与人脸ID(faceId)是两个不同的概念，personId为人员的唯一ID，faceId为人脸的唯一ID，也就是说一个用户使用多张人脸作为底库的时候，有多个faceId
        faceItem[0] = new FaceItem(id, feature);
        AddedPerson addedPerson = new AddedPerson(id, name, faceItem);
        faceSearchLibrary.addPerson(addedPerson);
    }
}
