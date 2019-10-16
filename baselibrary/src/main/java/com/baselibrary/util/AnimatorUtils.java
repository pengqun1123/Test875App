package com.baselibrary.util;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.animation.LinearInterpolator;


/**
 * Created By pq
 * on 2019/8/9
 * 动画工具类
 */
public class AnimatorUtils {

    private static final int magnify = 10000;

    //对象旋转
    public static <T extends View> ObjectAnimator objRotateAnim(T target/*, long animateTime*/) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(target,
                "rotation", 0f, 720f * magnify);
//        if (animateTime != 0) {
        rotation.setDuration(1200 * magnify * 100);
//        }
        //动画重复次数，无限循环
        rotation.setRepeatCount(ValueAnimator.INFINITE);
        //设置动画的循环模式
        rotation.setRepeatMode(ValueAnimator.RESTART);
        //设置匀速插值器
        rotation.setInterpolator(new LinearInterpolator());
        rotation.start();
        return rotation;
    }

    //无限旋转动画
    public static <T extends View> ObjectAnimator rotateAnim(T target,long time, float angle) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(target,
                "rotation", 0f, angle);//359f
        rotation.setRepeatCount(ObjectAnimator.INFINITE);
        //设置动画的循环模式
//        rotation.setRepeatMode(ValueAnimator.RESTART);
        //设置匀速插值器
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setDuration(time);
        rotation.start();
        return rotation;
    }

    //取消动画
    public static void cancelAnim(ObjectAnimator animator) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void pauseAnim(ObjectAnimator animator) {
        if (animator != null && animator.isPaused()) {
            animator.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void resumeAnim(ObjectAnimator animator){
        if (animator != null && animator.isPaused()) {
            animator.resume();
        }
    }
}
