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
public class Finger6 {
    @Id(autoincrement = true)
    Long uId;
    @Property(nameInDb = "feature")
    byte[] finger6Feature;
    @Generated(hash = 2087254474)
    public Finger6(Long uId, byte[] finger6Feature) {
        this.uId = uId;
        this.finger6Feature = finger6Feature;
    }
    @Generated(hash = 1398466695)
    public Finger6() {
    }
    public Long getUId() {
        return this.uId;
    }
    public void setUId(Long uId) {
        this.uId = uId;
    }
    public byte[] getFinger6Feature() {
        return this.finger6Feature;
    }
    public void setFinger6Feature(byte[] finger6Feature) {
        this.finger6Feature = finger6Feature;
    }


    @Override
    public String toString() {
        return "Finger6{" +
                "uId=" + uId +
                ", finger6Feature=" + Arrays.toString(finger6Feature) +
                '}';
    }
}
