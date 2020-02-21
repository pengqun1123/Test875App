package com.baselibrary.util.dialogUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;

import com.baselibrary.R;
/**
 * Created By pq
 * on 2019/9/25
 */
public class AppDialog {

    public static Dialog gmDialog(Activity activity, View view,Boolean isCancel) {
        return DialogUtils.instance()
                .setDialogDecoeViewBg(R.drawable.dialog_bg)
                .setRateW(0.6f)
                .setIsCancel(isCancel)
                .setHasMargin(true)
                .setDialogAnimStyle(com.baselibrary.R.style.dialog)
                .setGravity(Gravity.CENTER)
                .setView(view)
                .gMDialog(activity);
    }

//    public static void showAskDialog(Context context, Integer msgRes) {
//        DialogUtils.instance()
//                .setDialogCallBack()
//                .showNormalAlertDialog(context, msgRes);
//    }
}
