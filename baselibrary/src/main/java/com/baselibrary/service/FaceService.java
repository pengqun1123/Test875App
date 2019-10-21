package com.baselibrary.service;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.baselibrary.pojo.Face;

/**
 * Created by wangyu on 2019/10/21.
 */

public interface FaceService extends IProvider {

    //删除人脸库的数据
    void removeFace(Long faceId) throws Exception;

    //数据添加到人脸库
    boolean addFace(Face face);
}
