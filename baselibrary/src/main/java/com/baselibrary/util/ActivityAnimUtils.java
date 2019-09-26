package com.baselibrary.util;

import android.app.Activity;
import android.content.Intent;

/**
 * Create By Administrator
 * on 2019/5/20
 * activity渐入渐出动画
 */
public class ActivityAnimUtils<T extends Activity> {

    private static ActivityAnimUtils activityAnimUtils = null;

    public static ActivityAnimUtils instance() {
        if (activityAnimUtils == null) {
            synchronized (ActivityAnimUtils.class) {
                if (activityAnimUtils == null) {
                    activityAnimUtils = new ActivityAnimUtils();
                }
            }
        }
        return activityAnimUtils;
    }

    //activity进入的动画
    public void activityIn(Activity currentAct, Class<Activity> clazz) {
        currentAct.startActivity(new Intent(currentAct, clazz));
        currentAct.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        currentAct.overridePendingTransition(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
    }

    //activity进入的动画,携带数据
    public void activityInData(Activity currentAct,Intent intent) {
        currentAct.startActivity(intent);
        currentAct.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        currentAct.overridePendingTransition(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
    }

    //activity退出的动画
    public void activityOut(T currentAct) {
        currentAct.finish();
        currentAct.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        currentAct.overridePendingTransition(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
    }

}
