package com.baselibrary.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by wangyu on 2019/9/17.
 */
@Entity
public class IdCard {
    @Id(autoincrement = true)
    private Long uId;

    private String name;
    private String nation;
    private String id;
    private String sex;
    private String birthday;
    @Generated(hash = 2072250835)
    public IdCard(Long uId, String name, String nation, String id, String sex,
            String birthday) {
        this.uId = uId;
        this.name = name;
        this.nation = nation;
        this.id = id;
        this.sex = sex;
        this.birthday = birthday;
    }
    @Generated(hash = 1500073048)
    public IdCard() {
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
    public String getNation() {
        return this.nation;
    }
    public void setNation(String nation) {
        this.nation = nation;
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "IdCard{" +
                "uId=" + uId +
                ", name='" + name + '\'' +
                ", nation='" + nation + '\'' +
                ", id='" + id + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }
}
