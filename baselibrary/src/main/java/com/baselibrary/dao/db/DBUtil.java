package com.baselibrary.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created By pq
 * on 2019/9/9
 */
public class DBUtil {

    private static final String DB_NAME = "testDB.db";

    private DaoSession daoSession;

    private static DBUtil dbUtil = null;
    private DaoMaster.DevOpenHelper devOpenHelper;

    public static DBUtil instance() {
        if (dbUtil == null) {
            synchronized (DBUtil.class) {
                if (dbUtil == null) {
                    dbUtil = new DBUtil();
                }
            }
        }
        return dbUtil;
    }

    //初始化数据库
    public void initDB(Context context) {
        devOpenHelper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        SQLiteDatabase database = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
    }

    public synchronized DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * 设置DeBug的模式
     *
     * @param flag
     */
    public void setDebug(boolean flag) {
        QueryBuilder.LOG_SQL = flag;
        QueryBuilder.LOG_VALUES = flag;
    }

    private void closeDaoSession() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

    private void closeHelper() {
        if (devOpenHelper != null) {
            devOpenHelper.close();
            devOpenHelper = null;
        }
    }

    public void closeData() {
        closeHelper();
        closeDaoSession();
    }

}
