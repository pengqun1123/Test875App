package com.baselibrary.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by **
 * on 2018/9/10.
 */

public class SkipActivityUtil {

    //跳转activity
    public static void skipActivity(Activity activity,Class clazz){
        ActivityAnimUtils.instance().activityIn(activity,clazz);
//        activity.startActivity(new Intent(activity,clazz));
    }
    //跳转并返回结果
    public static void skipActivityForResult(Activity activity,Class clazz,Integer requestCode){
        activity.startActivityForResult(new Intent(activity,clazz),requestCode);
    }
    //携带数据跳转
    public static void skipDataActivity(Activity activity, Class clazz, Bundle bundle){
        Intent intent = new Intent(activity,clazz);
        intent.putExtras(bundle);
        ActivityAnimUtils.instance().activityInData(activity,intent);
    }

    //携带数据跳转
    public static void skipIntentDataActivity(Activity activity,Intent intent){
        ActivityAnimUtils.instance().activityInData(activity,intent);
    }





}
