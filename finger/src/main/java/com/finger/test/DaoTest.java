package com.finger.test;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.dao.db.PwDao;
import com.baselibrary.dao.db.UserDao;
import com.baselibrary.pojo.Pw;
import com.baselibrary.pojo.User;
import com.orhanobut.logger.Logger;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * Created By pq
 * on 2019/9/24
 * 数据库使用示例
 * <p>
 * 可参考：https://www.jianshu.com/p/3ee00bd99593
 * 数据库操作尽量使用异步，性能高
 */
public class DaoTest {

    /**
     * 一对一关联表
     */
    public static void oneToOne() {
        DBUtil dbUtil = BaseApplication.getDbUtil();

        Pw pw = new Pw();
        pw.setUId(100L);
        pw.setPassword("123456");

        User user = new User();
        user.setName("里哈哈");
        user.setSection("小子部门");
        user.setWorkNum("100");
        user.setPwId(100L);

        PwDao pwDao = dbUtil.getDaoSession().getPwDao();
        UserDao userDao = dbUtil.getDaoSession().getUserDao();
        pwDao.insertOrReplace(pw);
        userDao.insertOrReplace(user);

        QueryBuilder<User> lihaha = userDao.queryBuilder()
                .where(UserDao.Properties.Name.eq("里哈哈"));
        for (User user1 : lihaha.list()) {
            Pw pw1 = user1.getPw();
            Logger.d("user1:" + user1.toString());
        }
    }

    /**
     * GreenDao异步查询数据   方法可进一步按照泛型抽取
     * 条件查询
     */
    public static void asncSingle() {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        /*
         * 以下条件意为：查询User表中属性名称值等于"里哈哈"的数据集合
         */
        WhereCondition whereCondition = UserDao.Properties.Name.eq("里哈哈");
        dbUtil.setDbCallBack(new DbCallBack<User>() {
            @Override
            public void onSuccess(User result) {

            }

            @Override
            public void onSuccess(List<User> result) {
                //返回的结果
            }

            @Override
            public void onFailed() {
                //查询失败
            }

            @Override
            public void onNotification(boolean result) {
                //true 查询成功的通知  false  查询失败的通知

            }
        }).queryAsync(User.class, whereCondition);
    }

    /**
     * 泛型抽取  未测试
     *
     * @param clazz 要查询的Dao类型
     * @param <T>   泛型类型
     */
    public static <T> void syncQueryAll(Class<T> clazz) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        QueryBuilder<T> queryBuilder = dbUtil.getQueryBuilder(clazz);
        dbUtil.setDbCallBack(new DbCallBack<T>() {
            @Override
            public void onSuccess(T result) {

            }

            @Override
            public void onSuccess(List<T> result) {

            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onNotification(boolean result) {

            }
        }).queryAsyncAll(clazz, queryBuilder);
    }

    /**
     * 未抽取的情况  已测试
     */
    public static void syncQueryAll() {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        QueryBuilder<User> userQueryBuilder = dbUtil.getDaoSession().getUserDao().queryBuilder();
        dbUtil.setDbCallBack(new DbCallBack<User>() {
            @Override
            public void onSuccess(User result) {

            }

            @Override
            public void onSuccess(List<User> result) {

            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onNotification(boolean result) {

            }
        }).queryAsyncAll(User.class, userQueryBuilder);
    }

    /**
     * 批量插入数据，已存在的数据将会被替换
     *
     * @param users 批量数据
     */
    public static void asyncInsert(List<User> users) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        dbUtil.setDbCallBack(new DbCallBack<User>() {
            @Override
            public void onSuccess(User result) {

            }

            @Override
            public void onSuccess(List<User> result) {

            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onNotification(boolean result) {

            }
        }).insertAsyncBatch(User.class, users);
    }

}
