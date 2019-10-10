package com.baselibrary.callBack;

/**
 * Created by wangyu on 2019/10/9.
 */

public interface FaceInitListener {
    void initFail(String error);
    void initSuccess();
}
