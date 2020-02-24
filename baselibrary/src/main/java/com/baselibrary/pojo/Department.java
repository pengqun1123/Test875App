package com.baselibrary.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.baselibrary.dao.db.DaoSession;
import com.baselibrary.dao.db.UserDao;
import com.baselibrary.dao.db.DepartmentDao;

/**
 *  部门的数据对象
 */
@Entity
public class Department implements Parcelable {
    @Id
    private Long departmentId;
    @NotNull
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "count")
    private int count;
    @ToMany(referencedJoinProperty = "userId")//关联多的一方的外键，这里对应user中的userId;
    private List<User> userList;

    protected Department(Parcel in) {
        if (in.readByte() == 0) {
            departmentId = null;
        } else {
            departmentId = in.readLong();
        }
        name = in.readString();
        count = in.readInt();
        userList = in.createTypedArrayList(User.CREATOR);
    }

    @Generated(hash = 1910931699)
    public Department(Long departmentId, @NotNull String name, int count) {
        this.departmentId = departmentId;
        this.name = name;
        this.count = count;
    }

    @Generated(hash = 355406289)
    public Department() {
    }

    public static final Creator<Department> CREATOR = new Creator<Department>() {
        @Override
        public Department createFromParcel(Parcel in) {
            return new Department(in);
        }

        @Override
        public Department[] newArray(int size) {
            return new Department[size];
        }
    };
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 550071597)
    private transient DepartmentDao myDao;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (departmentId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(departmentId);
        }
        dest.writeString(name);
        dest.writeInt(count);
        dest.writeTypedList(userList);
    }

    public Long getDepartmentId() {
        return this.departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1145217109)
    public List<User> getUserList() {
        if (userList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            List<User> userListNew = targetDao
                    ._queryDepartment_UserList(departmentId);
            synchronized (this) {
                if (userList == null) {
                    userList = userListNew;
                }
            }
        }
        return userList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1517531020)
    public synchronized void resetUserList() {
        userList = null;
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
    @Generated(hash = 393361921)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDepartmentDao() : null;
    }
}
