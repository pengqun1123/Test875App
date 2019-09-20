package com.baselibrary.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created By pq
 * on 2019/9/9
 */
@Entity
public class User {
    @Id(autoincrement = true)
    Long uId;

    String workNum;
    String name;
    String section;
    @Generated(hash = 2052126289)
    public User(Long uId, String workNum, String name, String section) {
        this.uId = uId;
        this.workNum = workNum;
        this.name = name;
        this.section = section;
    }
    @Generated(hash = 586692638)
    public User() {
    }
    public Long getUId() {
        return this.uId;
    }
    public void setUId(Long uId) {
        this.uId = uId;
    }
    public String getWorkNum() {
        return this.workNum;
    }
    public void setWorkNum(String workNum) {
        this.workNum = workNum;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSection() {
        return this.section;
    }
    public void setSection(String section) {
        this.section = section;
    }

    @Override
    public String toString() {
        return "User{" +
                "uId=" + uId +
                ", workNum='" + workNum + '\'' +
                ", name='" + name + '\'' +
                ", section='" + section + '\'' +
                '}';
    }
}
