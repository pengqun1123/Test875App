package com.testApp.callBack;

import android.os.Parcelable;

import com.baselibrary.pojo.User;

/**
 * Created By pq
 * on 2019/10/19
 */
public interface SearchDeleteUser extends Parcelable {
    void searchDeleteUser(User user);
}
