package com.baselibrary.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created By pq
 * on 2019/9/30
 */
@Entity
public class Manager {

    @Id(autoincrement = true)
    Long mId;
    @Property(nameInDb = "manage_pw")
    String manage_pw;
    @Generated(hash = 1213820683)
    public Manager(Long mId, String manage_pw) {
        this.mId = mId;
        this.manage_pw = manage_pw;
    }
    @Generated(hash = 2029850664)
    public Manager() {
    }
    public Long getMId() {
        return this.mId;
    }
    public void setMId(Long mId) {
        this.mId = mId;
    }
    public String getManage_pw() {
        return this.manage_pw;
    }
    public void setManage_pw(String manage_pw) {
        this.manage_pw = manage_pw;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "mId=" + mId +
                ", manage_pw='" + manage_pw + '\'' +
                '}';
    }
}
