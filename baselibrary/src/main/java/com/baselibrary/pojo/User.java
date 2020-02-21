package com.baselibrary.pojo;

import android.os.Parcel;
import android.os.Parcelable;

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
import com.baselibrary.dao.db.IdCardDao;
import com.baselibrary.dao.db.FaceDao;

/**
 * Created By pq
 * on 2019/9/9
 */
@Entity
public class User implements Parcelable {
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
    @Property(nameInDb = "faceId")
    Long faceId;
    @Property(nameInDb = "arcFaceId")
    Long arcFaceId;
    @ToOne(joinProperty = "faceId")
    Face face;
    @Property(nameInDb = "cardId")
    Long cardId;
    @ToOne(joinProperty = "cardId")
    IdCard idCard;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;
    @Generated(hash = 311753857)
    public User(Long uId, String name, String age, String sex, String phone,
            String organizName, String section, String workNum, Long pwId,
            Long finger3Id, Long finger6Id, Long faceId, Long arcFaceId,
            Long cardId) {
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
        this.faceId = faceId;
        this.arcFaceId = arcFaceId;
        this.cardId = cardId;
    }
    @Generated(hash = 586692638)
    public User() {
    }

    protected User(Parcel in) {
        if (in.readByte() == 0) {
            uId = null;
        } else {
            uId = in.readLong();
        }
        name = in.readString();
        age = in.readString();
        sex = in.readString();
        phone = in.readString();
        organizName = in.readString();
        section = in.readString();
        workNum = in.readString();
        if (in.readByte() == 0) {
            pwId = null;
        } else {
            pwId = in.readLong();
        }
        if (in.readByte() == 0) {
            finger3Id = null;
        } else {
            finger3Id = in.readLong();
        }
        if (in.readByte() == 0) {
            finger6Id = null;
        } else {
            finger6Id = in.readLong();
        }
        finger6 = in.readParcelable(Finger6.class.getClassLoader());
        if (in.readByte() == 0) {
            faceId = null;
        } else {
            faceId = in.readLong();
        }
        if (in.readByte() == 0) {
            arcFaceId = null;
        } else {
            arcFaceId = in.readLong();
        }
        if (in.readByte() == 0) {
            cardId = null;
        } else {
            cardId = in.readLong();
        }
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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
    public Long getFaceId() {
        return this.faceId;
    }
    public void setFaceId(Long faceId) {
        this.faceId = faceId;
    }
    public Long getArcFaceId() {
        return this.arcFaceId;
    }
    public void setArcFaceId(Long arcFaceId) {
        this.arcFaceId = arcFaceId;
    }
    public Long getCardId() {
        return this.cardId;
    }
    public void setCardId(Long cardId) {
        this.cardId = cardId;
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
    @Generated(hash = 495600469)
    private transient Long face__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1564146146)
    public Face getFace() {
        Long __key = this.faceId;
        if (face__resolvedKey == null || !face__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FaceDao targetDao = daoSession.getFaceDao();
            Face faceNew = targetDao.load(__key);
            synchronized (this) {
                face = faceNew;
                face__resolvedKey = __key;
            }
        }
        return face;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1271567982)
    public void setFace(Face face) {
        synchronized (this) {
            this.face = face;
            faceId = face == null ? null : face.getUId();
            face__resolvedKey = faceId;
        }
    }
    @Generated(hash = 602561657)
    private transient Long idCard__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1790411946)
    public IdCard getIdCard() {
        Long __key = this.cardId;
        if (idCard__resolvedKey == null || !idCard__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            IdCardDao targetDao = daoSession.getIdCardDao();
            IdCard idCardNew = targetDao.load(__key);
            synchronized (this) {
                idCard = idCardNew;
                idCard__resolvedKey = __key;
            }
        }
        return idCard;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1274803049)
    public void setIdCard(IdCard idCard) {
        synchronized (this) {
            this.idCard = idCard;
            cardId = idCard == null ? null : idCard.getUId();
            idCard__resolvedKey = cardId;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (uId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(uId);
        }
        dest.writeString(name);
        dest.writeString(age);
        dest.writeString(sex);
        dest.writeString(phone);
        dest.writeString(organizName);
        dest.writeString(section);
        dest.writeString(workNum);
        if (pwId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(pwId);
        }
        if (finger3Id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(finger3Id);
        }
        if (finger6Id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(finger6Id);
        }
        dest.writeParcelable(finger6, flags);
        if (faceId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(faceId);
        }
        if (arcFaceId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(arcFaceId);
        }
        if (cardId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(cardId);
        }
    }
}
