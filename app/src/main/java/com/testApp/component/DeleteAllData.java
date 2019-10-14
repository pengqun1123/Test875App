package com.testApp.component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;

import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.pojo.Pw;
import com.baselibrary.pojo.User;
import com.baselibrary.util.ToastUtils;

import java.util.List;

/**
 * Created By pq
 * on 2019/10/11
 * 测试和重置使用：删除所有数据
 */
public class DeleteAllData {

    private Activity mActivity;
    @SuppressLint("StaticFieldLeak")
    private static DeleteAllData deleteAllData = null;

    private DeleteAllData(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public static synchronized DeleteAllData getInstance(Activity activity) {
        if (deleteAllData == null)
            deleteAllData = new DeleteAllData(activity);
        return deleteAllData;
    }

    public void deleteAllUser(DBUtil dbUtil) {
        dbUtil.setDbCallBack(new DbCallBack<User>() {
            @Override
            public void onSuccess(User result) {
                ToastUtils.showSingleToast(mActivity, "删除成功");
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
        }).deleteAsyncAll(User.class);
    }

    public void deleteAllFinger(DBUtil dbUtil) {
        dbUtil.setDbCallBack(new DbCallBack<Finger6>() {
            @Override
            public void onSuccess(Finger6 result) {
                ToastUtils.showSingleToast(mActivity, "删除成功");
            }

            @Override
            public void onSuccess(List<Finger6> result) {

            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onNotification(boolean result) {

            }
        }).deleteAsyncAll(Finger6.class);
    }

    public void deleteAllPw(DBUtil dbUtil) {
        dbUtil.setDbCallBack(new DbCallBack<Pw>() {
            @Override
            public void onSuccess(Pw result) {
                ToastUtils.showSingleToast(mActivity, "删除成功");
            }

            @Override
            public void onSuccess(List<Pw> result) {

            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onNotification(boolean result) {

            }
        }).deleteAsyncAll(Finger6.class);
    }
}
