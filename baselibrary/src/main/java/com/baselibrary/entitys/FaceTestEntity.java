package com.baselibrary.entitys;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 人脸测试的实体类
 */
public class FaceTestEntity implements Parcelable {
    //姓名
    private String name;
    //人脸的ID
    private int trackId;
    //是否活体
    private boolean isLiveness;
    //RGB检测的时间
    private int RGB_Tiem;
    //RGB+IR检测的时间
    private int RGB_IR_Time;
    //比对验证的时间
    private int compare_time;
    //比对验证的相似度
    private double compareSimilar;

    public FaceTestEntity() {
    }

    protected FaceTestEntity(Parcel in) {
        name = in.readString();
        trackId = in.readInt();
        isLiveness = in.readByte() != 0;
        RGB_Tiem = in.readInt();
        RGB_IR_Time = in.readInt();
        compare_time = in.readInt();
        compareSimilar = in.readDouble();
    }

    public static final Creator<FaceTestEntity> CREATOR = new Creator<FaceTestEntity>() {
        @Override
        public FaceTestEntity createFromParcel(Parcel in) {
            return new FaceTestEntity(in);
        }

        @Override
        public FaceTestEntity[] newArray(int size) {
            return new FaceTestEntity[size];
        }
    };

    public FaceTestEntity(String name, int trackId, boolean isLiveness, int RGB_Tiem,
                          int RGB_IR_Time, int compare_time, double compareSimilar) {
        this.name = name;
        this.trackId = trackId;
        this.isLiveness = isLiveness;
        this.RGB_Tiem = RGB_Tiem;
        this.RGB_IR_Time = RGB_IR_Time;
        this.compare_time = compare_time;
        this.compareSimilar = compareSimilar;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public boolean isLiveness() {
        return isLiveness;
    }

    public void setLiveness(boolean liveness) {
        isLiveness = liveness;
    }

    public int getRGB_Tiem() {
        return RGB_Tiem;
    }

    public void setRGB_Tiem(int RGB_Tiem) {
        this.RGB_Tiem = RGB_Tiem;
    }

    public int getRGB_IR_Time() {
        return RGB_IR_Time;
    }

    public void setRGB_IR_Time(int RGB_IR_Time) {
        this.RGB_IR_Time = RGB_IR_Time;
    }

    public int getCompare_time() {
        return compare_time;
    }

    public void setCompare_time(int compare_time) {
        this.compare_time = compare_time;
    }

    public double getCompareSimilar() {
        return compareSimilar;
    }

    public void setCompareSimilar(double compareSimilar) {
        this.compareSimilar = compareSimilar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(trackId);
        dest.writeByte((byte) (isLiveness ? 1 : 0));
        dest.writeInt(RGB_Tiem);
        dest.writeInt(RGB_IR_Time);
        dest.writeInt(compare_time);
        dest.writeDouble(compareSimilar);
    }

    @Override
    public String toString() {
        return "FaceTestEntity{" +
                "name='" + name + '\'' +
                ", trackId=" + trackId +
                ", isLiveness=" + isLiveness +
                ", RGB_Tiem=" + RGB_Tiem +
                ", RGB_IR_Time=" + RGB_IR_Time +
                ", compare_time=" + compare_time +
                ", compareSimilar=" + compareSimilar +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
