package com.face.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.constant.AppConstant;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.service.factory.FingerFactory;
import com.baselibrary.util.ToastUtils;
import com.face.R;
import com.face.service.FaceService;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * Created by wangyu on 2019/10/15.
 */

public abstract   class FaceBaseActivity extends BaseActivity{

    protected ArrayList<Finger6> fingerList;

    @Override
    protected void initView() {
        bindViewWithClick(R.id.face_animator,true);

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                fingerList = bundle.getParcelableArrayList(AppConstant.FINGER_DATA_LIST);
                Logger.d("DefaultActivity 1 指静脉模板数量：" + fingerList.size());
                FingerFactory.getInstance().setFingerData(fingerList);
            }
        }
    }

    @Override
    protected void onViewClick(View view) {
        if (view.getId() == R.id.face_animator) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(AppConstant.FINGER_DATA_LIST, fingerList);
            ARouterUtil.navigation(ARouterConstant.MENU_ACTIVITY,bundle);
            finish();
       }
    }
}
