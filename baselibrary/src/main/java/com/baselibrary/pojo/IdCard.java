package com.baselibrary.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by wangyu on 2019/9/17.
 */
@Entity
public class IdCard implements Parcelable {
    @Id
    private Long cardId;//主键,同userId
    //外键
//    private Long userId;
    private String name;
    private String nation;
    private String cardNo;//身份证号/IC卡号
    private String sex;
    private String birthday;


    protected IdCard(Parcel in) {
        if (in.readByte() == 0) {
            cardId = null;
        } else {
            cardId = in.readLong();
        }
        name = in.readString();
        nation = in.readString();
        cardNo = in.readString();
        sex = in.readString();
        birthday = in.readString();
    }

    @Generated(hash = 1260622775)
    public IdCard(Long cardId, String name, String nation, String cardNo,
            String sex, String birthday) {
        this.cardId = cardId;
        this.name = name;
        this.nation = nation;
        this.cardNo = cardNo;
        this.sex = sex;
        this.birthday = birthday;
    }

    @Generated(hash = 1500073048)
    public IdCard() {
    }

    public static final Creator<IdCard> CREATOR = new Creator<IdCard>() {
        @Override
        public IdCard createFromParcel(Parcel in) {
            return new IdCard(in);
        }

        @Override
        public IdCard[] newArray(int size) {
            return new IdCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (cardId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(cardId);
        }
        dest.writeString(name);
        dest.writeString(nation);
        dest.writeString(cardNo);
        dest.writeString(sex);
        dest.writeString(birthday);
    }

    public Long getCardId() {
        return this.cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNation() {
        return this.nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getCardNo() {
        return this.cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
