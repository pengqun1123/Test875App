package com.baselibrary.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created By pq
 * on 2019/9/23
 * 通过uId与User唯一对应
 */
@Entity
public class Pw implements Parcelable {
    @Id
    private Long pwId;
    @Property(nameInDb = "password")
    private String password;

    protected Pw(Parcel in) {
        if (in.readByte() == 0) {
            pwId = null;
        } else {
            pwId = in.readLong();
        }
        password = in.readString();
    }

    @Generated(hash = 742592676)
    public Pw(Long pwId, String password) {
        this.pwId = pwId;
        this.password = password;
    }

    @Generated(hash = 1671265915)
    public Pw() {
    }

    public static final Creator<Pw> CREATOR = new Creator<Pw>() {
        @Override
        public Pw createFromParcel(Parcel in) {
            return new Pw(in);
        }

        @Override
        public Pw[] newArray(int size) {
            return new Pw[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (pwId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(pwId);
        }
        dest.writeString(password);
    }

    public Long getPwId() {
        return this.pwId;
    }

    public void setPwId(Long pwId) {
        this.pwId = pwId;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
