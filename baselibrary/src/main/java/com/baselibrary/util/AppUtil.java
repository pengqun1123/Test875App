package com.baselibrary.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Application.ActivityLifecycleCallbacks;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Created By pq
 * on 2019/9/11
 */
public class AppUtil {

    private static Application topApplication;
    public static WeakReference<Activity> topWeakReference;
    public static List<Activity> activityList = new LinkedList<>();

    public static void init(@NonNull final Application application) {
        AppUtil.topApplication = application;
        application.registerActivityLifecycleCallbacks(callbacks);
    }

    /**
     * 获取 Application
     *
     * @return Application
     */
    public static Application getApp() {
        if (topApplication != null) return topApplication;
        throw new NullPointerException("you should init first");
    }

    private static ActivityLifecycleCallbacks callbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            setWeakReferenceActivity(activity);
            activityList.add(activity);
            ActManager.getInstance().addActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            setWeakReferenceActivity(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            setWeakReferenceActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            activityList.remove(activity);
            ActManager.getInstance().removeFinishAct(activity);
        }
    };

    private static void setWeakReferenceActivity(final Activity activity) {
        //短暂使用后完毕后马上会销毁的Activity不用赋值给weakReference，例如PermissionActivity
        if (topWeakReference == null || !activity.equals(topWeakReference.get())) {
            topWeakReference = new WeakReference<>(activity);
        }
    }

}
