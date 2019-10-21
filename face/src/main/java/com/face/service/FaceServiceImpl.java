package com.face.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.service.*;
import com.face.common.FaceConfig;
import com.zqzn.android.face.exceptions.FaceException;
import com.zqzn.android.face.model.FaceSearchLibrary;

/**
 * Created by wangyu on 2019/10/21.
 */

@Route(path = ARouterConstant.FACE_SERVICE)
public class FaceServiceImpl implements com.baselibrary.service.FaceService {
    @Override
    public void removeFace(Long faceId) throws Exception {
        FaceSearchLibrary faceSearchLibrary = FaceConfig.getInstance().getFaceSDK().getFaceSearchLibrary();
        faceSearchLibrary.removePersons(new long[]{faceId});
    }

    @Override
    public void init(Context context) {

    }
}
