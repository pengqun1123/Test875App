package com.baselibrary.util.dialogUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.baselibrary.R;

/**
 * Created by **
 * on 2018/9/11.
 */

public class MyDialogUtil {
    private static MyDialogUtil sMyDialogUtil;

    public static MyDialogUtil getInstance() {
        if (sMyDialogUtil == null) {
            synchronized (MyDialogUtil.class) {
                if (sMyDialogUtil == null) {
                    sMyDialogUtil = new MyDialogUtil();
                }
            }
        }
        return sMyDialogUtil;
    }

    private static DialogCallBack mDialogCallBack;

    public MyDialogUtil setDialogCallBack(DialogCallBack dialogCallBack) {
        mDialogCallBack = dialogCallBack;
        return this;
    }

    /**
     * 通用的dialog
     *
     * @param view     自定义Dialog的布局
     * @param activity 上下文
     * @return 返回的dialog
     */
    public Dialog showNormalDialog(View view, Activity activity) {
        return DialogUtils.instance().setView(view)
                .setIsCancel(true)
                .setHasMargin(true)
                .setDialogStyle(R.style.dialog)
                .setGravity(Gravity.BOTTOM)
                .setDialogDecoeViewBg(R.drawable.dialog_top_bg)
                .gMDialog(activity);
    }

    public void showPermissionDialog(Context context, String permissionTip) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(permissionTip)
                .setPositiveButton(context.getString(R.string.resume), (dialog, which) -> {
                    //确定，调用确定的回调
                    if (mDialogCallBack != null) {
                        mDialogCallBack.positiveClick(dialog);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> {
                    if (mDialogCallBack != null) {
                        mDialogCallBack.negativeClick(dialog);
                    }
                    dialog.dismiss();
                    //退出应用
                    //activity.onBackPressed();
                })
                .create();
        alertDialog.show();
    }

    @SuppressLint("ObsoleteSdkInt")
    public static void showBottomDynamicDialog(Activity activity, Context context, View view,
                                               boolean touchCancel, int res, int dialogStyle,
                                               DialogActCallBack actCallBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, dialogStyle);
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                window.getDecorView().setBackground(ActivityCompat.getDrawable(context, res));
            }
            WindowManager manager = activity.getWindowManager();
            Display display = manager.getDefaultDisplay();
            WindowManager.LayoutParams lp = alertDialog.getWindow()
                    .getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = display.getWidth();
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(lp);
            alertDialog.setCancelable(touchCancel);
            alertDialog.setCanceledOnTouchOutside(touchCancel);
            window.setWindowAnimations(R.style.alertDialogStyle01);
            if (actCallBack != null)
                actCallBack.cancelActCallBack(alertDialog);
        }
        alertDialog.show();
    }
}
