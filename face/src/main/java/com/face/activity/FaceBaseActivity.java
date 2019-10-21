package com.face.activity;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.service.factory.FingerFactory;
import com.baselibrary.util.ActManager;
import com.baselibrary.util.AnimatorUtils;
import com.baselibrary.util.FingerListManager;
import com.face.R;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wangyu on 2019/10/15.
 */

public abstract  class FaceBaseActivity extends BaseActivity{

    protected ArrayList<Finger6> fingerList;

    protected AppCompatImageView gear1, gear2, gear3, gear4;

    private final long flagTime = 4000;
    private long maxTime = 1000;
    private long lastTime = 0;
    private int count = 0;

    protected ObjectAnimator gear1Anim, gear2Anim, gear3Anim, gear4Anim;
    private long timeInMillis;

    @Override
    protected void initView() {
        gear1 = bindViewWithClick(R.id.gear1, true);
        gear2 = bindViewWithClick(R.id.gear2, true);
        gear3 = bindViewWithClick(R.id.gear3, true);
        gear4 = bindViewWithClick(R.id.gear4, true);
           bindViewWithClick(R.id.defaultOut, true);

        gear1Anim = AnimatorUtils.rotateAnim(gear1, 3000L, 359F);
        gear2Anim = AnimatorUtils.rotateAnim(gear2, 2600L, -359F);
        gear3Anim = AnimatorUtils.rotateAnim(gear3, 2800, 359F);
        gear4Anim = AnimatorUtils.rotateAnim(gear4, 2500, -359F);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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


    }

    @Override
    protected void onViewClick(View view) {
        if (view.getId() == R.id.homeMenu) {
            ARouterUtil.navigation(ARouterConstant.MENU_ACTIVITY);
            finish();
        } else if (view.getId()==R.id.defaultOut) {
    /*        long timeInMillis = Calendar.getInstance().getTimeInMillis();
            long l = timeInMillis - lastTime;
            if (((l) < flagTime && count > 0) || count == 0) {
                count++;
                lastTime = timeInMillis;
            }
            if (count == 5) {
                count = 0;
                //退出应用
                ActManager.getInstance().exitApp();
            }*/
         count++;
         if (count==1){
             timeInMillis = new Date().getTime();
             return;
         }
            Log.d("yuio",count+"：count---"+timeInMillis+":timeInMillis---"+"now:"+new Date().getTime());
         if (count==5){
             if (( new Date().getTime()-timeInMillis)<flagTime){
                 ActManager.getInstance().exitApp();
             }else {
                 count=1;
                 timeInMillis =new Date().getTime();
             }
         }
        }
    }
}
