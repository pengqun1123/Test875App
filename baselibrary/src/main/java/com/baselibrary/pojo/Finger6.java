package com.baselibrary.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
/**
 * Created By pq
 * on 2019/9/29
 */
@Entity
public class Finger6 implements Parcelable {
    @Id(autoincrement = true)
    private Long fingerId;//主键
    //外键
    private Long userId;
    @Property(nameInDb = "fingerFeature")
    private byte[] finger6Feature;


    protected Finger6(Parcel in) {
        if (in.readByte() == 0) {
            fingerId = null;
        } else {
            fingerId = in.readLong();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readLong();
        }
        finger6Feature = in.createByteArray();
    }

    @Generated(hash = 231507954)
    public Finger6(Long fingerId, Long userId, byte[] finger6Feature) {
        this.fingerId = fingerId;
        this.userId = userId;
        this.finger6Feature = finger6Feature;
    }

    @Generated(hash = 1398466695)
    public Finger6() {
    }

    public static final Creator<Finger6> CREATOR = new Creator<Finger6>() {
        @Override
        public Finger6 createFromParcel(Parcel in) {
            return new Finger6(in);
        }

        @Override
        public Finger6[] newArray(int size) {
            return new Finger6[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (fingerId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(fingerId);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(userId);
        }
        dest.writeByteArray(finger6Feature);
    }

    public Long getFingerId() {
        return this.fingerId;
    }

    public void setFingerId(Long fingerId) {
        this.fingerId = fingerId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public byte[] getFinger6Feature() {
        return this.finger6Feature;
    }

    public void setFinger6Feature(byte[] finger6Feature) {
        this.finger6Feature = finger6Feature;
    }
}
