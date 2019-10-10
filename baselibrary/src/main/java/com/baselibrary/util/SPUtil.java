package com.baselibrary.util;

import android.media.midi.MidiManager;

import com.baselibrary.base.AppConfig;
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

    //存储标记是否设置管理员密码
    public static void putHasManagerPwd(Boolean hasManagerPwd) {
        instance().putBoolean(AppConstant.MANAGER_PWD, hasManagerPwd);
    }

    //获取是否存在管理员密码
    public static Boolean getHasManagerPwd() {
        return instance().getBoolean(AppConstant.MANAGER_PWD, false);
    }

    //可注册的最大的管理员数量
    public static void putMaxManagerNum(Integer maxManagerNum) {
        instance().putInt(AppConstant.MAX_MANAGER, maxManagerNum);
    }

    //获取可注册的最大的管理员的数量
    public static int getMacManagerNum() {
        return instance().getInt(AppConstant.MAX_MANAGER, 10);
    }

    //人脸的注册码
    public static void putFaceActiveCode(String code){
        instance().putString("faceCode", code);
    }

    public static String getFaceActiveCode(){
      return    instance().getString("faceCode",null);
    }
}
