package com.baselibrary.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.baselibrary.dao.db.DaoSession;
import com.baselibrary.dao.db.Finger6Dao;
import com.baselibrary.dao.db.PwDao;
import com.baselibrary.dao.db.IdCardDao;
import com.baselibrary.dao.db.ArcFaceDao;
import com.baselibrary.dao.db.DepartmentDao;
import com.baselibrary.dao.db.UserDao;

/**
 * Created By pq
 * on 2019/9/9
 */
@Entity
public class User implements Parcelable {
    @Id(autoincrement = true)
    private Long uId;//主键
    //外键
    private Long userId;
    @NotNull
    @Property(nameInDb = "username")
    private String name;
    @NotNull
    @Unique
    @Property(nameInDb = "jobNumber")
    private String jobNumber;
    //一对一部门
    private Long departmentId;
    @ToOne(joinProperty = "departmentId")//对应一的一方的主键,这里是部门的主键
    private Department department;
    //拥有的权限类型
    private int permissionType;
    //一对一人脸
    private Long faceId;
    @ToOne(joinProperty = "faceId")//关联的是对应人脸的主键
    private ArcFace arcFace;
    //一对多指静脉
    @ToMany(referencedJoinProperty = "userId")
    private List<Finger6> finger6List;//删除时，先验证再删除；放置的时候规定位置对应手指:012:左食中无;345:右食中无
    //一对一IC卡
    //关联一的一方的主键，这里是关联ICCard表中的主键
    private Long cardId;
    @ToOne(joinProperty = "cardId")
    private IdCard idCard;
    //一对一密码
    //关联密码表中的主键
    private Long pwId;
    @ToOne(joinProperty = "pwId")
    private Pw pw;

    protected User(Parcel in) {
        if (in.readByte() == 0) {
            uId = null;
        } else {
            uId = in.readLong();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readLong();
        }
        name = in.readString();
        jobNumber = in.readString();
        if (in.readByte() == 0) {
            departmentId = null;
        } else {
            departmentId = in.readLong();
        }
        department = in.readParcelable(Department.class.getClassLoader());
        permissionType = in.readInt();
        if (in.readByte() == 0) {
            faceId = null;
        } else {
            faceId = in.readLong();
        }
        arcFace = in.readParcelable(ArcFace.class.getClassLoader());
        finger6List = in.createTypedArrayList(Finger6.CREATOR);
        if (in.readByte() == 0) {
            cardId = null;
        } else {
            cardId = in.readLong();
        }
        idCard = in.readParcelable(IdCard.class.getClassLoader());
        if (in.readByte() == 0) {
            pwId = null;
        } else {
            pwId = in.readLong();
        }
        pw = in.readParcelable(Pw.class.getClassLoader());
    }

    @Generated(hash = 761627882)
    public User(Long uId, Long userId, @NotNull String name,
            @NotNull String jobNumber, Long departmentId, int permissionType,
            Long faceId, Long cardId, Long pwId) {
        this.uId = uId;
        this.userId = userId;
        this.name = name;
        this.jobNumber = jobNumber;
        this.departmentId = departmentId;
        this.permissionType = permissionType;
        this.faceId = faceId;
        this.cardId = cardId;
        this.pwId = pwId;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (uId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(uId);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(userId);
        }
        dest.writeString(name);
        dest.writeString(jobNumber);
        if (departmentId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(departmentId);
        }
        dest.writeParcelable(department, flags);
        dest.writeInt(permissionType);
        if (faceId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(faceId);
        }
        dest.writeParcelable(arcFace, flags);
        dest.writeTypedList(finger6List);
        if (cardId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(cardId);
        }
        dest.writeParcelable(idCard, flags);
        if (pwId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(pwId);
        }
        dest.writeParcelable(pw, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Long getUId() {
        return this.uId;
    }

    public void setUId(Long uId) {
        this.uId = uId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobNumber() {
        return this.jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public Long getDepartmentId() {
        return this.departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public int getPermissionType() {
        return this.permissionType;
    }

    public void setPermissionType(int permissionType) {
        this.permissionType = permissionType;
    }

    public Long getFaceId() {
        return this.faceId;
    }

    public void setFaceId(Long faceId) {
        this.faceId = faceId;
    }

    public Long getCardId() {
        return this.cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getPwId() {
        return this.pwId;
    }

    public void setPwId(Long pwId) {
        this.pwId = pwId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1998261213)
    public Department getDepartment() {
        Long __key = this.departmentId;
        if (department__resolvedKey == null
                || !department__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DepartmentDao targetDao = daoSession.getDepartmentDao();
            Department departmentNew = targetDao.load(__key);
            synchronized (this) {
                department = departmentNew;
                department__resolvedKey = __key;
            }
        }
        return department;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1370855047)
    public void setDepartment(Department department) {
        synchronized (this) {
            this.department = department;
            departmentId = department == null ? null : department.getDepartmentId();
            department__resolvedKey = departmentId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 291236660)
    public ArcFace getArcFace() {
        Long __key = this.faceId;
        if (arcFace__resolvedKey == null || !arcFace__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ArcFaceDao targetDao = daoSession.getArcFaceDao();
            ArcFace arcFaceNew = targetDao.load(__key);
            synchronized (this) {
                arcFace = arcFaceNew;
                arcFace__resolvedKey = __key;
            }
        }
        return arcFace;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 71662849)
    public void setArcFace(ArcFace arcFace) {
        synchronized (this) {
            this.arcFace = arcFace;
            faceId = arcFace == null ? null : arcFace.getFaceId();
            arcFace__resolvedKey = faceId;
        }
    }

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
    @Generated(hash = 1040497985)
    public void setIdCard(IdCard idCard) {
        synchronized (this) {
            this.idCard = idCard;
            cardId = idCard == null ? null : idCard.getCardId();
            idCard__resolvedKey = cardId;
        }
    }

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
    @Generated(hash = 1501920934)
    public void setPw(Pw pw) {
        synchronized (this) {
            this.pw = pw;
            pwId = pw == null ? null : pw.getPwId();
            pw__resolvedKey = pwId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1050831755)
    public List<Finger6> getFinger6List() {
        if (finger6List == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            Finger6Dao targetDao = daoSession.getFinger6Dao();
            List<Finger6> finger6ListNew = targetDao._queryUser_Finger6List(uId);
            synchronized (this) {
                if (finger6List == null) {
                    finger6List = finger6ListNew;
                }
            }
        }
        return finger6List;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1477032160)
    public synchronized void resetFinger6List() {
        finger6List = null;
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
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;
    @Generated(hash = 340684718)
    private transient Long department__resolvedKey;
    @Generated(hash = 1844672042)
    private transient Long arcFace__resolvedKey;
    @Generated(hash = 602561657)
    private transient Long idCard__resolvedKey;
    @Generated(hash = 534339714)
    private transient Long pw__resolvedKey;
}
