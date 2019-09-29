package com.baselibrary.pojo;

import com.baselibrary.util.FloatArrayConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

import java.util.Arrays;

/**
 * Created by wangyu on 2019/9/24.
 */

@Entity
public class Face {
    @Id(autoincrement = true)
    private Long uId;
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
    @Convert(columnType = String.class,converter = FloatArrayConverter.class)
    private float[] feature;

    @Generated(hash = 2078179342)
    public Face(Long uId, String name, String imagePath, float[] feature) {
        this.uId = uId;
        this.name = name;
        this.imagePath = imagePath;
        this.feature = feature;
    }

    @Generated(hash = 601504354)
    public Face() {
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

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public float[] getFeature() {
        return this.feature;
    }

    public void setFeature(float[] feature) {
        this.feature = feature;
    }

    @Override
    public String toString() {
        return "Face{" +
                "uId=" + uId +
                ", name='" + name + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", feature=" + Arrays.toString(feature) +
                '}';
    }
}
