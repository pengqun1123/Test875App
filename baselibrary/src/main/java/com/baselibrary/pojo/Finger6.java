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
    Long finger6Id;
    @Property(nameInDb = "finger6")
    byte[] finger6Feature;

    @Generated(hash = 470613142)
    public Finger6(Long finger6Id, byte[] finger6Feature) {
        this.finger6Id = finger6Id;
        this.finger6Feature = finger6Feature;
    }

    @Generated(hash = 1398466695)
    public Finger6() {
    }

    public Long getFinger6Id() {
        return this.finger6Id;
    }

    public void setFinger6Id(Long finger6Id) {
        this.finger6Id = finger6Id;
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
                "finger6Id=" + finger6Id +
                ", finger6Feature=" + Arrays.toString(finger6Feature) +
                '}';
    }
}
