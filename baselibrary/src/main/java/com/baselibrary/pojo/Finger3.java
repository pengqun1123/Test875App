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
    Long finger3Id;
    @Property(nameInDb = "finger3")
    byte[] finger3Feature;

    @Generated(hash = 303168316)
    public Finger3(Long finger3Id, byte[] finger3Feature) {
        this.finger3Id = finger3Id;
        this.finger3Feature = finger3Feature;
    }

    @Generated(hash = 250175537)
    public Finger3() {
    }

    public Long getFinger3Id() {
        return this.finger3Id;
    }

    public void setFinger3Id(Long finger3Id) {
        this.finger3Id = finger3Id;
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
                "finger3Id=" + finger3Id +
                ", finger3Feature=" + Arrays.toString(finger3Feature) +
                '}';
    }
}
