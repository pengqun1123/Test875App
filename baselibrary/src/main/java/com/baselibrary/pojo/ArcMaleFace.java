package com.baselibrary.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 虹软男性人脸信息
 */
@Entity
public class ArcMaleFace {
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
    @Generated(hash = 261445110)
    public ArcMaleFace(Long uId, String name, byte[] faceFeature, byte[] headImg) {
        this.uId = uId;
        this.name = name;
        this.faceFeature = faceFeature;
        this.headImg = headImg;
    }
    @Generated(hash = 629241570)
    public ArcMaleFace() {
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
