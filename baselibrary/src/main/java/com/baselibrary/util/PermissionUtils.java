package com.baselibrary.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.baselibrary.activity.PermissionsActivity;
import com.baselibrary.callBack.PermissionC;
import com.baselibrary.callBack.PermissionResultCallBack;
import com.orhanobut.logger.Logger;

/**
 * Created By pq
 * on 2019/7/11
 * 权限申请工具类
 */
public class PermissionUtils {

    private static PermissionUtils permissionUtils = null;

    public static PermissionUtils instance() {
        if (permissionUtils == null) {
            synchronized (PermissionUtils.class) {
                if (permissionUtils == null)
                    permissionUtils = new PermissionUtils();
            }
        }
        return permissionUtils;
    }

    private PermissionResultCallBack permissionResultCallBack;

    /**
     * 申请文件的读写权限
     */
    public void requestPermission(Activity activity, String tip, String[] permissions,
                                  PermissionResultCallBack permissionResultCallBack) {
        this.permissionResultCallBack = permissionResultCallBack;
        Intent intent = new Intent(/*activity,PermissionsActivity.class*/);
        Bundle bundle = new Bundle();
        intent.setAction("com.baselibrary.activity.permissionActivity");
        intent.addCategory("android.intent.category.DEFAULT");
        bundle.putString(PermissionC.TIP, tip);
        bundle.putStringArray(PermissionC.init_permis, permissions);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void callBack() {
        if (this.permissionResultCallBack != null) permissionResultCallBack.permissionCallBack();

    }

}
