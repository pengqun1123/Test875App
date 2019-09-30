package com.baselibrary.util;

import com.baselibrary.constant.AppConstant;

/**
 * Created By pq
 * on 2019/9/16
 */
public class SPUtil {

    public static final String DEFAULT_STR = "";

    private static SharedpreferencesUtil instance() {
        return SharedpreferencesUtil.getInstance();
    }

    //设置是否使用人脸
    public static void putOpenFace(Boolean isOpenFace) {
        instance().putBoolean(AppConstant.OPEN_FACE, isOpenFace);
    }

    //获取是否启用人脸
    public static Boolean getOpenFace() {
        return instance().getBoolean(AppConstant.OPEN_FACE, false);
    }
}
