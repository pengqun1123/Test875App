package com.baselibrary.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Arrays;

/**
 * Created By pq
 * on 2019/9/29
 */
@Entity
public class Finger3 {
    @Id(autoincrement = true)
    Long uId;
    @Property(nameInDb = "feature")
    byte[] finger3Feature;
    @Generated(hash = 1729757169)
    public Finger3(Long uId, byte[] finger3Feature) {
        this.uId = uId;
        this.finger3Feature = finger3Feature;
    }
    @Generated(hash = 250175537)
    public Finger3() {
    }
    public Long getUId() {
        return this.uId;
    }
    public void setUId(Long uId) {
        this.uId = uId;
    }
    public byte[] getFinger3Feature() {
        return this.finger3Feature;
    }
    public void setFinger3Feature(byte[] finger3Feature) {
        this.finger3Feature = finger3Feature;
    }


    @Override
    public String toString() {
        return "Finger3{" +
                "uId=" + uId +
                ", finger3Feature=" + Arrays.toString(finger3Feature) +
                '}';
    }
}
