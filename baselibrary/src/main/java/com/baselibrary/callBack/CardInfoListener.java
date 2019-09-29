package com.baselibrary.callBack;

import com.baselibrary.pojo.IdCard;

/**
 * Created by wangyu on 2019/9/18.
 */

public interface CardInfoListener {

    void onGetCardInfo(IdCard idCard);

    void onRegisterResult(boolean result,IdCard idCard);
}
