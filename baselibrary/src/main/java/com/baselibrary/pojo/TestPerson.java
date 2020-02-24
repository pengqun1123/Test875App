package com.baselibrary.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import com.baselibrary.dao.db.DaoSession;
import com.baselibrary.dao.db.TestIdCardDao;
import com.baselibrary.dao.db.TestPersonDao;

@Entity
public class TestPerson {
    @Id(autoincrement = true)
    private Long personId;
    private String personName;
    @ToOne(joinProperty = "personId")
    private TestIdCard testIdCard;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1581041979)
    private transient TestPersonDao myDao;
    @Generated(hash = 1630703387)
    public TestPerson(Long personId, String personName) {
        this.personId = personId;
        this.personName = personName;
    }
    @Generated(hash = 378096043)
    public TestPerson() {
    }
    public Long getPersonId() {
        return this.personId;
    }
    public void setPersonId(Long personId) {
        this.personId = personId;
    }
    public String getPersonName() {
        return this.personName;
    }
    public void setPersonName(String personName) {
        this.personName = personName;
    }
    @Generated(hash = 2064566288)
    private transient Long testIdCard__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1678855771)
    public TestIdCard getTestIdCard() {
        Long __key = this.personId;
        if (testIdCard__resolvedKey == null
                || !testIdCard__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TestIdCardDao targetDao = daoSession.getTestIdCardDao();
            TestIdCard testIdCardNew = targetDao.load(__key);
            synchronized (this) {
                testIdCard = testIdCardNew;
                testIdCard__resolvedKey = __key;
            }
        }
        return testIdCard;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 603895463)
    public void setTestIdCard(TestIdCard testIdCard) {
        synchronized (this) {
            this.testIdCard = testIdCard;
            personId = testIdCard == null ? null : testIdCard.getCardId();
            testIdCard__resolvedKey = personId;
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
    @Generated(hash = 189468774)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTestPersonDao() : null;
    }

    @Override
    public String toString() {
        return "TestPerson{" +
                "personId=" + personId +
                ", personName='" + personName + '\'' +
                ", testIdCard=" + testIdCard +
                ", daoSession=" + daoSession +
                ", myDao=" + myDao +
                ", testIdCard__resolvedKey=" + testIdCard__resolvedKey +
                '}';
    }
}
