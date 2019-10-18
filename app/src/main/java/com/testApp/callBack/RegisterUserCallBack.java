package com.testApp.callBack;

import android.os.Parcelable;

import com.baselibrary.pojo.User;

/**
 * Created By pq
 * on 2019/10/18
 */
public interface RegisterUserCallBack extends Parcelable {
    void registerUserCallBack(User user);
}
