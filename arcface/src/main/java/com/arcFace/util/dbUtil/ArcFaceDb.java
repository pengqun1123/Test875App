package com.arcFace.util.dbUtil;

import android.support.annotation.NonNull;

import com.arcFace.callBack.CheckOutCallBack;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.dao.db.FaceDao;
import com.baselibrary.pojo.ArcFace;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * 虹软数据库操作类
 */
public class ArcFaceDb {

    public static void updateFaceUser(@NonNull ArcFace arcFace) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        dbUtil.insertOrReplace(arcFace);
    }

    /**
     * 异步查询
     *
     * @param condition 条件
     * @return ArcFace
     */
    public static void checkFace(String condition, CheckOutCallBack callBack) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        WhereCondition eq = FaceDao.Properties.Name.eq(condition);
        dbUtil.queryAsync(ArcFace.class, eq);
        dbUtil.setDbCallBack(new DbCallBack<ArcFace>() {
            @Override
            public void onSuccess(ArcFace result) {
                if (callBack != null)
                    callBack.checkOutCallBack(result);
            }

            @Override
            public void onSuccess(List<ArcFace> result) {
                if (callBack != null)
                    callBack.checkOutCallBack(result.get(0));
            }

            @Override
            public void onFailed() {
                if (callBack != null)
                    callBack.checkOutCallBack(null);
            }

            @Override
            public void onNotification(boolean result) {

            }
        });
    }

}
