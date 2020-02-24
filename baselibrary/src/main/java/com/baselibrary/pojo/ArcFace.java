package com.baselibrary.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 虹软人脸实体类
 * GreenDAO  存储对象   https://blog.csdn.net/sinat_29384657/article/details/80627308
 */
@Entity
public class ArcFace implements Parcelable {

    @Id
    private Long faceId;//一对一中，主键同多的一方的主键userId
    //外键
//    private Long userId;
    /**
     * 用户姓名
     */
    @NotNull
    private String name;
//    /**
//     * 虹软人脸Id
//     */
//    Integer trackId;
    /**
     * 人脸特征
     */
    @NotNull
    private byte[] faceFeature;
    /**
     * 人脸头像
     */
    @NotNull
    private byte[] headImg;


    protected ArcFace(Parcel in) {
        if (in.readByte() == 0) {
            faceId = null;
        } else {
            faceId = in.readLong();
        }
        name = in.readString();
        faceFeature = in.createByteArray();
        headImg = in.createByteArray();
    }

    @Generated(hash = 1769462826)
    public ArcFace(Long faceId, @NotNull String name, @NotNull byte[] faceFeature,
            @NotNull byte[] headImg) {
        this.faceId = faceId;
        this.name = name;
        this.faceFeature = faceFeature;
        this.headImg = headImg;
    }

    @Generated(hash = 1904168999)
    public ArcFace() {
    }

    public static final Creator<ArcFace> CREATOR = new Creator<ArcFace>() {
        @Override
        public ArcFace createFromParcel(Parcel in) {
            return new ArcFace(in);
        }

        @Override
        public ArcFace[] newArray(int size) {
            return new ArcFace[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (faceId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(faceId);
        }
        dest.writeString(name);
        dest.writeByteArray(faceFeature);
        dest.writeByteArray(headImg);
    }

    public Long getFaceId() {
        return this.faceId;
    }

    public void setFaceId(Long faceId) {
        this.faceId = faceId;
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
