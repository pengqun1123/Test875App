package com.baselibrary.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 虹软人脸实体类
 * GreenDAO  存储对象   https://blog.csdn.net/sinat_29384657/article/details/80627308
 */
@Entity
public class ArcFace {

    @Id(autoincrement = true)
    Long uId;
    /**
     * 用户姓名
     */
    String name;
//    /**
//     * 虹软人脸Id
//     */
//    Integer trackId;
    /**
     * 人脸特征
     */
    byte[] faceFeature;
    /**
     * 人脸头像
     */
    byte[] headImg;
    @Generated(hash = 1125874263)
    public ArcFace(Long uId, String name, byte[] faceFeature, byte[] headImg) {
        this.uId = uId;
        this.name = name;
        this.faceFeature = faceFeature;
        this.headImg = headImg;
    }
    @Generated(hash = 1904168999)
    public ArcFace() {
    }
    public Long getUId() {
        return this.uId;
    }
    public void setUId(Long uId) {
        this.uId = uId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public byte[] getFaceFeature() {
        return this.faceFeature;
    }
    public void setFaceFeature(byte[] faceFeature) {
        this.faceFeature = faceFeature;
    }
    public byte[] getHeadImg() {
        return this.headImg;
    }
    public void setHeadImg(byte[] headImg) {
        this.headImg = headImg;
    }


}
