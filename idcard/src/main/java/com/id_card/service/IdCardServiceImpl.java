package com.id_card.service;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.callBack.CardInfoListener;
import com.baselibrary.service.IdCardService;

import java.io.File;

/**
 * Created by wangyu on 2019/9/26.
 */

@Route(path = ARouterConstant.SERVICE_IdCARD)
public class IdCardServiceImpl implements IdCardService {

    private IdController instance;

    @Override
    public void verify_IdCard(CardInfoListener cardInfoListener) {
        if (instance!=null) {
            instance.verify_IdCard(cardInfoListener);
        }
    }

    @Override
    public void register_IdCard(CardInfoListener cardInfoListener,Long idCardId) {
        if (instance!=null) {
            instance.register_IdCard(cardInfoListener,idCardId);
        }
    }

    @Override
    public Bitmap getBitmap() {
        if (instance!=null) {
            return instance.getBitmap();
        }
        return  null;
    }

    @Override
    public File getIdCardFile() {
        if(instance!=null) {
            return instance.getFile();
        }
        return null;
    }

    @Override
    public void destroyIdCard() {
        if (instance!=null) {
            instance.closeIdCard();
        }
    }

    @Override
    public void init(Context context) {
        Log.d("777","初始化");
        instance = IdController.getInstance(context);
    }
}
