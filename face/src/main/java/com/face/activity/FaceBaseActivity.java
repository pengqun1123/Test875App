package com.face.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.FrameLayout;

import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.constant.AppConstant;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.service.factory.FingerFactory;
import com.baselibrary.util.AnimatorUtils;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.util.ToastUtils;
import com.face.R;
import com.face.service.FaceService;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * Created by wangyu on 2019/10/15.
 */

public abstract  class FaceBaseActivity extends BaseActivity{

    protected ArrayList<Finger6> fingerList;

    protected AppCompatImageView gear1, gear2, gear3, gear4;

    protected FrameLayout face_jump;

    protected ObjectAnimator gear1Anim, gear2Anim, gear3Anim, gear4Anim;
    @Override
    protected void initView() {
        gear1 = bindViewWithClick(R.id.gear1, true);
        gear2 = bindViewWithClick(R.id.gear2, true);
        gear3 = bindViewWithClick(R.id.gear3, true);
        gear4 = bindViewWithClick(R.id.gear4, true);
        face_jump = bindViewWithClick(R.id.fl,true);

        gear1Anim = AnimatorUtils.rotateAnim(gear1, 3900L, 359F);
        gear2Anim = AnimatorUtils.rotateAnim(gear2, 3100L, -359F);
        gear3Anim = AnimatorUtils.rotateAnim(gear3, 3400L, 359F);
        gear4Anim = AnimatorUtils.rotateAnim(gear4, 2800L, -359F);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gear1Anim != null) {
            AnimatorUtils.resumeAnim(gear1Anim);
        }
        if (gear2Anim != null) {
            AnimatorUtils.resumeAnim(gear2Anim);
        }
        if (gear3Anim != null) {
            AnimatorUtils.resumeAnim(gear3Anim);
        }
        if (gear4Anim != null) {
            AnimatorUtils.resumeAnim(gear4Anim);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gear1Anim != null) {
            AnimatorUtils.cancelAnim(gear1Anim);
        }
        if (gear2Anim != null) {
            AnimatorUtils.cancelAnim(gear2Anim);
        }
        if (gear3Anim != null) {
            AnimatorUtils.cancelAnim(gear3Anim);
        }
        if (gear4Anim != null) {
            AnimatorUtils.cancelAnim(gear4Anim);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gear1Anim != null) {
            AnimatorUtils.pauseAnim(gear1Anim);
        }
        if (gear2Anim != null) {
            AnimatorUtils.pauseAnim(gear2Anim);
        }
        if (gear3Anim != null) {
            AnimatorUtils.pauseAnim(gear3Anim);
        }
        if (gear4Anim != null) {
            AnimatorUtils.pauseAnim(gear4Anim);
        }
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
        if (view.getId() == R.id.homeMenu) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(AppConstant.FINGER_DATA_LIST, fingerList);
            ARouterUtil.navigation(ARouterConstant.MENU_ACTIVITY, bundle);
            finish();

        }
    /*    if (view.getId() == R.id.face_animator) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(AppConstant.FINGER_DATA_LIST, fingerList);
            ARouterUtil.navigation(ARouterConstant.MENU_ACTIVITY,bundle);
            finish();
       }*/
    }
}
