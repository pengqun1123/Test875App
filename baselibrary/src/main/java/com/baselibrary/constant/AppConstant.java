package com.baselibrary.constant;

/**
 * Created By pq
 * on 2019/9/25
 */
public final class AppConstant {

    public static final Integer FINGER_MODEL = 0x10;
    public static final Integer FACE_MODEL = 0x11;
    public static final Integer IDCARD_MODEL = 0x12;
    public static final Integer PW_MODEL = 0x13;

    //标记是否启用人脸
    public static final String OPEN_FACE = "open_face";
    //是否设置管理员密码
    public static final String MANAGER_PWD = "manager_pwd";
    //可注册的最大管理员数量
    public static final String MAX_MANAGER = "max_manager";
    //更新库中模板的阈值分数
    public static final Integer UPDATE_THRESHOLDBOOLEAN = 75;

    //注册验证的类型
    public static final Integer REGISTER_FINGER = 0xf1;


}
