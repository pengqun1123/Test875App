package com.baselibrary.pojo;

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
public class Pw {
    @Id
    Long uId;
    @Property(nameInDb = "password")
    String password;

    @Generated(hash = 576425409)
    public Pw(Long uId, String password) {
        this.uId = uId;
        this.password = password;
    }

    @Generated(hash = 1671265915)
    public Pw() {
    }

    public Long getUId() {
        return this.uId;
    }

    public void setUId(Long uId) {
        this.uId = uId;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Pw{" +
                "uId=" + uId +
                ", password='" + password + '\'' +
                '}';
    }
}
