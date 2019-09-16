package com.baselibrary.util.dialogUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.baselibrary.R;
import com.orhanobut.logger.Logger;

/**
 * Created by **
 * on 2018/10/10.
 * 所有的dialog集中在这里
 */

public class DialogUtils {
    @SuppressLint("StaticFieldLeak")
    private static DialogUtils mDialogUtils;
//    private Activity activity;
//    private Context context;

    public static DialogUtils instance(/*Activity activity*/) {
        if (mDialogUtils == null) {
            synchronized (DialogUtils.class) {
                if (mDialogUtils == null) {
                    mDialogUtils = new DialogUtils(/*activity*/);
                }
            }
        }
        return mDialogUtils;
    }

    private DialogUtils(/*Activity activity*/) {
//        this.activity = activity;
//        this.context = activity;
    }

    //设置dialog的布局
    private View mView;

    public DialogUtils setView(View view) {
        mView = view;
        return this;
    }

    private Integer mGravity = Gravity.CENTER;

    //设置dialog的位置
    public DialogUtils setGravity(Integer gravity) {
        mGravity = gravity;
        return this;
    }

    //设置dialog的动画属性
    private Integer mDialogAnimStyle;

    public DialogUtils setDialogAnimStyle(Integer dialogAnimStyle) {
        mDialogAnimStyle = dialogAnimStyle;
        return this;
    }

    private Float mDialogH;

    public DialogUtils setDialogH(Float dialogH) {
        mDialogH = dialogH;
        return this;
    }

    //设置点击dialog外围是否取消
    private Boolean mIsCancel = false;

    public DialogUtils setIsCancel(Boolean isCancel) {
        mIsCancel = isCancel;
        return this;
    }

    //设置dialog的属性
    private Integer mDialogStyle;

    public DialogUtils setDialogStyle(Integer dialogStyle) {
        mDialogStyle = dialogStyle;
        return this;
    }

    //设置是否有边距
    private Boolean mHasMargin = false;
    //对话框站窗口宽度的比例
    private float rateW = 4 / 5;

    public DialogUtils setRateW(float newRateW) {
        rateW = newRateW;
        return this;
    }

    public DialogUtils setHasMargin(Boolean hasMargin) {
        mHasMargin = hasMargin;
        return this;
    }

    private Integer mRes;

    public DialogUtils setDialogDecoeViewBg(Integer res) {
        mRes = res;
        return this;
    }

    public Dialog gMDialog(Activity activity) {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
        if (mDialogStyle != null) {
            builder = new AlertDialog.Builder(activity, mDialogStyle);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        alertDialog = builder.create();
        //给dialog设置布局
        alertDialog.setView(mView);
        Window window = alertDialog.getWindow();
        assert window != null;
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager windowManager = activity.getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        defaultDisplay.getMetrics(metrics);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        if (mHasMargin) {
            lp.width = (int) (metrics.widthPixels * rateW);
        } else {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        }
        window.getDecorView().setPadding(0, 0, 0, 0);
        //从Android 4.1开始向上兼容，对下不兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (mRes == null) {
                window.getDecorView().setBackground(activity.getResources().getDrawable(R.drawable.dialog_top_bg));
            } else {
                window.getDecorView().setBackground(activity.getResources().getDrawable(mRes));
            }
        }
        //设置位置
        window.setGravity(mGravity);
        lp.gravity = mGravity;
        //设置window动画
        if (mDialogAnimStyle != null) {
            window.setWindowAnimations(mDialogAnimStyle);
        }
        window.setAttributes(lp);
        //设置点击外围取消
        alertDialog.setCanceledOnTouchOutside(mIsCancel);
        alertDialog.setCancelable(mIsCancel);
        //展示dialog
        if (!activity.isFinishing()) {
            Logger.d("展示Dialog");
            alertDialog.show();
        }
        return alertDialog;
    }

    //无边距
    public AlertDialog showAlertDialog(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        Point outSize = new Point();
        defaultDisplay.getSize(outSize);
        AlertDialog.Builder builder;
        if (mDialogStyle != null) {
            builder = new AlertDialog.Builder(activity, mDialogStyle);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        AlertDialog dialog = builder.create();
        dialog.setView(mView);
        Window win = dialog.getWindow();
        assert win != null;
        View decorView = win.getDecorView();
        decorView.setPadding(0, 0, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setBackground(activity.getResources().getDrawable(R.drawable.dialog_top_bg));
        }
        if (mGravity != null) {
            win.setGravity(mGravity);
        }
        if (mDialogAnimStyle != null) {
            win.setWindowAnimations(mDialogAnimStyle);
        }
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = outSize.x;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        if (!activity.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    private DialogCallBack mDialogCallBack;

    public DialogUtils setDialogCallBack(DialogCallBack dialogCallBack) {
        mDialogCallBack = dialogCallBack;
        return this;
    }

    public void showNormalAlertDialog(Context context, Integer msg) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
                    if (mDialogCallBack != null) {
                        mDialogCallBack.positiveClick(dialog);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> {
                    if (mDialogCallBack != null) {
                        mDialogCallBack.negativeClick(dialog);
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
