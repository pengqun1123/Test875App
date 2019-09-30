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
import com.baselibrary.dao.db.Finger6Dao;
import com.baselibrary.dao.db.Finger3Dao;

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
    @Property(nameInDb = "finger3Id")
    Long finger3Id;
    @ToOne(joinProperty = "finger3Id")
    Finger3 finger3;
    @Property(nameInDb = "finger6Id")
    Long finger6Id;
    @ToOne(joinProperty = "finger6Id")
    Finger6 finger6;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;
    @Generated(hash = 574549119)
    public User(Long uId, String name, String age, String sex, String phone,
            String organizName, String section, String workNum, Long pwId,
            Long finger3Id, Long finger6Id) {
        this.uId = uId;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.phone = phone;
        this.organizName = organizName;
        this.section = section;
        this.workNum = workNum;
        this.pwId = pwId;
        this.finger3Id = finger3Id;
        this.finger6Id = finger6Id;
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
    public Long getFinger3Id() {
        return this.finger3Id;
    }
    public void setFinger3Id(Long finger3Id) {
        this.finger3Id = finger3Id;
    }
    public Long getFinger6Id() {
        return this.finger6Id;
    }
    public void setFinger6Id(Long finger6Id) {
        this.finger6Id = finger6Id;
    }
    @Generated(hash = 534339714)
    private transient Long pw__resolvedKey;
    /** To-one relationship, resolved on first access. */
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
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1209702503)
    public void setPw(Pw pw) {
        synchronized (this) {
            this.pw = pw;
            pwId = pw == null ? null : pw.getUId();
            pw__resolvedKey = pwId;
        }
    }
    @Generated(hash = 1149056736)
    private transient Long finger3__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 196059806)
    public Finger3 getFinger3() {
        Long __key = this.finger3Id;
        if (finger3__resolvedKey == null || !finger3__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            Finger3Dao targetDao = daoSession.getFinger3Dao();
            Finger3 finger3New = targetDao.load(__key);
            synchronized (this) {
                finger3 = finger3New;
                finger3__resolvedKey = __key;
            }
        }
        return finger3;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 309724030)
    public void setFinger3(Finger3 finger3) {
        synchronized (this) {
            this.finger3 = finger3;
            finger3Id = finger3 == null ? null : finger3.getUId();
            finger3__resolvedKey = finger3Id;
        }
    }
    @Generated(hash = 585799691)
    private transient Long finger6__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1266515420)
    public Finger6 getFinger6() {
        Long __key = this.finger6Id;
        if (finger6__resolvedKey == null || !finger6__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            Finger6Dao targetDao = daoSession.getFinger6Dao();
            Finger6 finger6New = targetDao.load(__key);
            synchronized (this) {
                finger6 = finger6New;
                finger6__resolvedKey = __key;
            }
        }
        return finger6;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 657162331)
    public void setFinger6(Finger6 finger6) {
        synchronized (this) {
            this.finger6 = finger6;
            finger6Id = finger6 == null ? null : finger6.getUId();
            finger6__resolvedKey = finger6Id;
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
    /** called by internal mechanisms, do not call yourself. */
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
                ", finger3Id=" + finger3Id +
                ", finger3=" + finger3 +
                ", finger6Id=" + finger6Id +
                ", finger6=" + finger6 +
                ", daoSession=" + daoSession +
                ", myDao=" + myDao +
                ", pw__resolvedKey=" + pw__resolvedKey +
                ", finger3__resolvedKey=" + finger3__resolvedKey +
                ", finger6__resolvedKey=" + finger6__resolvedKey +
                '}';
    }
}
