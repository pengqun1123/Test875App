package com.baselibrary.util;

import android.app.Activity;
import android.os.Process;

import java.util.Stack;

/**
 * Created By pq
 * on 2019/9/11
 * Activity 的管理类
 */
public class ActManager {

    /**
     * 栈：也就是stack
     */
    private static Stack<Activity> activityStack;

    public static ActManager getInstance() {
        return ActManagerHolder.INSTANCE;
    }

    private static class ActManagerHolder {
        private static final ActManager INSTANCE = new ActManager();
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 返回栈顶的Activity
     *
     * @return
     */
    public Activity currentActivity() {
        if (activityStack == null || activityStack.size() == 0 || activityStack.empty())
            return null;
        return activityStack.lastElement();
    }

    /**
     * 移除并结束某个指定的Activity
     *
     * @param activity
     */
    public void removeFinishAct(Activity activity) {
        if (activityStack == null) return;
        if (activity != null && !activity.isFinishing()) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 移除指定类名的Activity
     *
     * @param clazz
     */
    public void removeFinishAct(Class<?> clazz) {
        if (activityStack == null) return;
        for (Activity activity : activityStack) {
            if (clazz.equals(activity.getClass())) {
                removeFinishAct(activity);
            }
        }
    }

    /**
     * 移除指定的Activity
     *
     * @param activity
     */
    public void removeAct(Activity activity) {
        if (activityStack == null) return;
        for (Activity activity1 : activityStack) {
            if (activity.equals(activity1))
                activityStack.remove(activity);
        }
    }

    /**
     * 结束所有的Activity
     */
    public void finishAllAct() {
        if (activityStack == null) return;
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (activityStack.get(i) != null) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void exitApp(){
        try {
            //结束所有的Activity
            finishAllAct();
            //杀死进程
            Process.killProcess(Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

}
