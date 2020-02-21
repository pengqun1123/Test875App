package com.baselibrary.constant;

/**
 * Created By pq
 * on 2019/9/25
 */
public final class AppConstant {

    public static final long FLAG_TIME = 3000L;

    public static final Integer FINGER_MODEL = 0x10;
    public static final Integer FACE_MODEL = 0x11;
    public static final Integer ID_CARD_MODEL = 0x12;
    public static final Integer PW_MODEL = 0x13;

    public static final Integer ADD_NEW_MANAGER = 0x14;
    public static final Integer INIT_ADD_MANAGER = 0x15;


    public static final Integer FINGER6_DATA_SIZE = 6464;

    //标记是否启用人脸
    public static final String OPEN_FACE = "open_face";
    //是否设置管理员密码
    public static final String MANAGER_PWD = "manager_pwd";
    //可注册的最大管理员数量
    public static final String MAX_MANAGER = "max_manager";
    public static final String ARC_FACE_ACTIVATED = "activated";
    //指静脉的数据
    public static final String FINGER_DATA = "finger_data";
    public static final String FACE_ID = "faceId";
    //    public static final String FINGER_DATA_LIST = "finger_list";
    public static final String FINGER_SIZE = "finger_size";
    public static final String ADD_FINGER = "add_finger";
    public static final String DELETE_FINGER = "delete_finger";
    public static final String FINGER_VERIFY_RESULT = "finger_verify_result";
    public static final String VERIFY_RESULT_TYPE = "verify_type";
    public static final String VERIFY_TYPE_ID = "verify_type_id";
    public static final String SEARCH_DELETE = "search_delete";
    public static final String USER = "user";

    //视频激活码
    public static final String FACE_ACTIVITY_CODE =
            "3J7D-BB84-JD8E-UA96";
//            "CG8A-HJ92-7W8E-EJ8F";
    //3J7D-BB84-JD8E-UA96
    /*TWAB-UI9E-AO90-7X8F*/;

    //注册时标记必须注册的验证方式
    public static final String PW_VERIFY_MODEL = "pw_verify";
    public static final String FINGER_VERIFY_MODEL = "finger_verify";
    public static final String FACE_VERIFY_MODEL = "face_verify";
    public static final String CARD_VERIFY_MODEL = "card_verify";
    public static final String FACE_CODE = "faceCode";
    //注册时标记的验证逻辑
    public static final String VERIFY_AND = "verify_and";

    //更新库中模板的阈值分数
    public static final Integer UPDATE_THRESHOLDBOOLEAN = 75;

    //注册验证的类型
    public static final Integer REGISTER_FINGER = 0xf1;

    //广播
    public static final String USER_MANAGER_BROADCAST_RECEIVER = "user_manager_receiver";
    public static final String USER_MENU_RECEIVER = "user_menu_receiver";


}
