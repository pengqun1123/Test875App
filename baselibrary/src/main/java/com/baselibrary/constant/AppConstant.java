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

    //注册时标记必须注册的验证模式
    public static final String PW_VERIFY_MODEL= "pw_verify";
    public static final String FINGER_VERIFY_MODEL = "finger_verify";
    public static final String FACE_VERIFY_MODEL = "face_verify";
    public static final String CARD_VERIFY_MODEL = "card_verify";
    //更新库中模板的阈值分数
    public static final Integer UPDATE_THRESHOLDBOOLEAN = 75;

    //注册验证的类型
    public static final Integer REGISTER_FINGER = 0xf1;


}
