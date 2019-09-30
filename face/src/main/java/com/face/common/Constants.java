package com.face.common;

import com.zqzn.android.face.data.FaceQuality;

public interface Constants {

    interface Threshold {
        /**
         * 活检检测阈值，高于此阈值才判断为活体
         */
        float LIVENESS = 0.9F;

        /**
         * 高人脸识别识别精度相似度阈值，高于此阈值才算同一个人
         */
        float SEARCH_ACCURACY_HIGH = 0.9F;
        /**
         * 中人脸识别识别精度相似度阈值，高于此阈值才算同一个人
         */
        float SEARCH_ACCURACY_MIDDLE = 0.85F;
        /**
         * 低人脸识别识别精度相似度阈值，高于此阈值才算同一个人
         */
        float SEARCH_ACCURACY_LOW = 0.8F;

        /**
         * 低人脸识别识别精度相似度阈值，高于此阈值才算同一个人
         */
        float SEARCH_ACCURACY_IDCARD = 0.75F;
        /**
         * 人脸模糊度阈值，人脸质量{@link FaceQuality#getBlur()}高于这个值表示人脸清晰
         */
        float BLUR = 0.65F;

        /**
         * 人脸遮挡阈值，人脸质量检测结果中涉及到遮挡的值，高于这个阈值才表示无遮挡
         */
        float OCCLUSION = 0.5F;
    }

    interface Url {
        /**
         * 生产环境设备授权激活URL
         */
        String PRODUCT_ENV_ACTIVATION_URL = "https://open-device.yskplus.com/open-serialno-front/1.0/sn_activation";
        /**
         * 测试环境设备授权激活URL
         */
        String TEST_ENV_ACTIVATION_URL = "http://test.cloud.zqauto.com/open-serialno-front/1.0/sn_activation";
    }

    int DEFAULT_MIN_DETECT_FACE_SIZE = 80;
}
