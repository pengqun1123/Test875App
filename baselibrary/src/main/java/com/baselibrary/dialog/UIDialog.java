package com.baselibrary.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baselibrary.R;
import com.baselibrary.util.ToastUtils;
import com.baselibrary.util.dialogUtil.AppDialog;
import com.baselibrary.util.dialogUtil.DialogUtils;

import java.util.Objects;

import io.reactivex.annotations.NonNull;

/**
 * dialog的展示工具类
 */
public class UIDialog {

    public static void showFaceTestDialog(@NonNull Activity activity, DialogCallBack callBack) {
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.face_test_item_dialog, null);
        AppCompatEditText name = dialogView.findViewById(R.id.name);
        AppCompatTextView noBtn = dialogView.findViewById(R.id.noBtn);
        AppCompatTextView yesBtn = dialogView.findViewById(R.id.yesBtn);
        Dialog dialog = AppDialog.gmDialog(activity, dialogView, false);
        noBtn.setOnClickListener(v -> dialog.dismiss());
        yesBtn.setOnClickListener(v -> {
            String nameValue = Objects.requireNonNull(name.getText()).toString().trim();
            if (TextUtils.isEmpty(nameValue)) {
                ToastUtils.showShortToast(activity, "请填写姓名");
                return;
            }
            if (callBack != null)
                callBack.yesCallBack(nameValue);
        });
    }

    //WaitDialog
    public static Dialog showWaitDialog(@NonNull Activity activity, String tip) {
        View view = LayoutInflater.from(activity).inflate(R.layout.loading_view, null);
        RelativeLayout loadingParent = view.findViewById(R.id.loadingParent);
        // AppCompatImageView loadingView = view.findViewById(R.id.loadingView);
//        TextView upProgressTv = view.findViewById(R.id.upProgressTv);
        TextView loadingTv = view.findViewById(R.id.loadingTv);
        if (!TextUtils.isEmpty(tip)) {
            loadingTv.setText(tip);
        }
        loadingParent.setVisibility(View.VISIBLE);
        return DialogUtils.instance()
                .setGravity(Gravity.CENTER)
                .setView(view)
                .setDialogDecoeViewBg(R.drawable.loading_dialog_white_alpha_bg)
                .setDialogStyle(R.style.showDialog)
                .setIsCancel(false)
                .setHasMargin(true)
                .setWequalH(true)
                .setRateW(0.1F)
                .setRateH(0.1F)
                .gMDialog(activity);
    }


    public static void disMiss(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
