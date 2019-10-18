package com.baselibrary.ARouter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.security.keystore.StrongBoxUnavailableException;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.orhanobut.logger.Logger;

/**
 * Created By pq
 * on 2019/9/12
 * 路由工具类
 */
public class ARouterUtil {

    /**
     * 注入Activity
     *
     * @param activity 要注入的Activity
     */
    public static void injectActivity(Activity activity) {
        if (activity == null) return;
        ARouter.getInstance().inject(activity);
    }

    /**
     * 注入Fragment
     *
     * @param fragment 要注入的Fragment
     */
    public static void injectFragment(Fragment fragment) {
        if (fragment == null) return;
        ARouter.getInstance().inject(fragment);
    }

    /**
     * 销毁路由资源
     */
    public static void destroyARouter() {
        ARouter.getInstance().destroy();
    }

    /**
     * 简单的页面跳转
     * 未分组
     *
     * @param path 要跳转的页面路径
     */
    public static void navigation(String path) {
        if (TextUtils.isEmpty(path)) return;
        ARouter.getInstance().build(path).navigation();
    }

    /**
     * 简单的界面跳转
     *
     * @param path  要跳转的界面路径
     * @param group 路径所属分组
     */
    public static void navigation(String path, String group) {
        if (TextUtils.isEmpty(path)) return;
        ARouter.getInstance().build(path, group).navigation();
    }

    /**
     * 简单页面跳转
     *
     * @param path     要跳转界面的路径
     * @param context  上下文
     * @param callback 监听路由的过程
     */
    public static void navigation(String path, Context context, NavigationCallback callback) {
        if (TextUtils.isEmpty(path)) return;
        ARouter.getInstance().build(path)
                .navigation(context, callback);
    }

    /**
     * 简单页面跳转
     *
     * @param path       要跳转界面的路径
     * @param activity   当前的activity
     * @param resultCode 回调的标志位
     */
    public static void navigation(String path, Activity activity, int resultCode) {
        if (TextUtils.isEmpty(path)) return;
        ARouter.getInstance().build(path)
                .navigation(activity, resultCode);
    }

    /**
     * 简单的页面跳转
     *
     * @param path       要跳转界面的路径
     * @param activity   当前的activity
     * @param resultCode 回传的标志位
     * @param callback   监听路由的过程
     */
    public static void navigation(String path, Activity activity, int resultCode,
                                  NavigationCallback callback) {
        if (TextUtils.isEmpty(path)) return;
        ARouter.getInstance().build(path)
                .navigation(activity, resultCode, callback);
    }

    /**
     * 简单的页面跳转
     *
     * @param uri uri
     */
    public static void navigation(Uri uri) {
        if (uri == null) return;
        ARouter.getInstance().build(uri).navigation();
    }

    /**
     * 简单的页面跳转
     *
     * @param path      要跳转界面的路径
     * @param bundle    参数
     * @param enterAnim 进入的动画
     * @param exitAnim  退出的动画
     */
    public static void navigation(String path, Bundle bundle, int enterAnim, int exitAnim) {
        if (TextUtils.isEmpty(path)) return;
        if (bundle == null) {
            ARouter.getInstance()
                    .build(path)
                    .withTransition(enterAnim, exitAnim)
                    .navigation();
        } else {
            ARouter.getInstance()
                    .build(path)
                    .with(bundle)
                    .withTransition(enterAnim, exitAnim)
                    .navigation();
        }
    }

    /**
     * 携带参数跳转页面
     * @param path                  path目标界面对应的路径
     * @param bundle                bundle参数
     */
    public static void navigation(String path , Bundle bundle){
        if (path==null || bundle==null){
            return;
        }
        ARouter.getInstance()
                .build(path)
                .with(bundle)
                .navigation();
    }

    public static void navigation(String path ,String group ,Bundle bundle){
        if (path==null || bundle==null){
            return;
        }
        ARouter.getInstance()
                .build(path,group)
                .with(bundle)
                .navigation();
    }

    /**
     * 跨模块实现ForResult返回数据（activity中使用）,在fragment中使用不起作用
     * 携带参数跳转页面
     * @param path                  path目标界面对应的路径
     * @param bundle                bundle参数
     */
    public static void navigation(String path , Bundle bundle , Activity context , int code){
        if (path==null){
            return;
        }
        if (bundle==null){
            ARouter.getInstance()
                    .build(path)
                    .navigation(context,code);
        }else {
            ARouter.getInstance()
                    .build(path)
                    .with(bundle)
                    .navigation(context,code);
        }
    }

    /**
     * 使用绿色通道(跳过所有的拦截器)
     * @param path                  path目标界面对应的路径
     * @param green                 是否使用绿色通道
     */
    public static void navigation(String path , boolean green){
        if (path==null){
            return;
        }
        if (green){
            ARouter.getInstance()
                    .build(path)
                    .greenChannel()
                    .navigation();
        }else {
            ARouter.getInstance()
                    .build(path)
                    .navigation();
        }
    }

    private NavigationCallback getCallback(){
        NavigationCallback callback = new NavCallback() {
            @Override
            public void onArrival(Postcard postcard) {
                Logger.i("ARouterUtils"+"---跳转完了");
            }

            @Override
            public void onFound(Postcard postcard) {
                super.onFound(postcard);
                Logger.i("ARouterUtils"+"---找到了");
            }

            @Override
            public void onInterrupt(Postcard postcard) {
                super.onInterrupt(postcard);
                Logger.i("ARouterUtils"+"---被拦截了");
            }

            @Override
            public void onLost(Postcard postcard) {
                super.onLost(postcard);
                Logger.i("ARouterUtils"+"---找不到了");
                //降级处理
                //DegradeServiceImpl degradeService = new DegradeServiceImpl();
                //degradeService.onLost(Utils.getApp(),postcard);

                //无法找到路径，作替换处理
//                PathReplaceServiceImpl pathReplaceService = new PathReplaceServiceImpl();
//                pathReplaceService.replacePath(ARouterConstant.ACTIVITY_ANDROID_ACTIVITY,ARouterConstant.ACTIVITY_DOU_MUSIC_ACTIVITY);
            }
        };
        return callback;
    }
}
