package com.baselibrary.service.routerTest;

import android.graphics.Bitmap;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.baselibrary.callBack.*;

import java.io.File;

/**
 * Created by wangyu on 2019/9/26.
 */

public interface IdCardService extends IProvider {
    //身份证信息验证
    void verify_IdCard(CardInfoListener cardInfoListener);

    //身份证信息注册
    void register_IdCard(CardInfoListener cardInfoListener);

    //获取身份证照片
    Bitmap getBitmap();

    //获取存储的身份证图片
    File getIdCardFile();

    //销毁idcardReader
    void destroyIdCard();
}
