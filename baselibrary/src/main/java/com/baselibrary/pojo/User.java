package com.baselibrary.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;

import com.baselibrary.dao.db.DaoSession;
import com.baselibrary.dao.db.PwDao;
import com.baselibrary.dao.db.UserDao;

/**
 * Created By pq
 * on 2019/9/9
 */
@Entity
public class User {
    @Id(autoincrement = true)
    Long uId;
    @Property(nameInDb = "name")
    String name;
    @Property(nameInDb = "age")
    String age;
    @Property(nameInDb = "sex")
    String sex;
    @Property(nameInDb = "phone")
    String phone;
    @Property(nameInDb = "organizName")
    String organizName;
    @Property(nameInDb = "section")
    String section;
    @Property(nameInDb = "workNum")
    String workNum;
    @Property(nameInDb = "pwId")
    Long pwId;
    @ToOne(joinProperty = "pwId")
    Pw pw;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;

    @Generated(hash = 472694883)
    public User(Long uId, String name, String age, String sex, String phone,
                String organizName, String section, String workNum, Long pwId) {
        this.uId = uId;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.phone = phone;
        this.organizName = organizName;
        this.section = section;
        this.workNum = workNum;
        this.pwId = pwId;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return this.age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrganizName() {
        return this.organizName;
    }

    public void setOrganizName(String organizName) {
        this.organizName = organizName;
    }

    public String getSection() {
        return this.section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getWorkNum() {
        return this.workNum;
    }

    public void setWorkNum(String workNum) {
        this.workNum = workNum;
    }

    public Long getPwId() {
        return this.pwId;
    }

    public void setPwId(Long pwId) {
        this.pwId = pwId;
    }

    @Generated(hash = 534339714)
    private transient Long pw__resolvedKey;

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 1794411686)
    public Pw getPw() {
        Long __key = this.pwId;
        if (pw__resolvedKey == null || !pw__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PwDao targetDao = daoSession.getPwDao();
            Pw pwNew = targetDao.load(__key);
            synchronized (this) {
                pw = pwNew;
                pw__resolvedKey = __key;
            }
        }
        return pw;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1209702503)
    public void setPw(Pw pw) {
        synchronized (this) {
            this.pw = pw;
            pwId = pw == null ? null : pw.getUId();
            pw__resolvedKey = pwId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }


    @Override
    public String toString() {
        return "User{" +
                "uId=" + uId +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", sex='" + sex + '\'' +
                ", phone='" + phone + '\'' +
                ", organizName='" + organizName + '\'' +
                ", section='" + section + '\'' +
                ", workNum='" + workNum + '\'' +
                ", pwId=" + pwId +
                ", pw=" + pw +
                ", daoSession=" + daoSession +
                ", myDao=" + myDao +
                ", pw__resolvedKey=" + pw__resolvedKey +
                '}';
    }
}
