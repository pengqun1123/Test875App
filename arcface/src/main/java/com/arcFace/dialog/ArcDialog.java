package com.arcFace.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.arcFace.R;
import com.arcFace.callBack.ProgressDialogCallBack;
import com.baselibrary.util.SoftInputKeyboardUtils;
import com.baselibrary.util.VerifyResultUi;
import com.baselibrary.util.dialogUtil.AppDialog;
import com.baselibrary.util.dialogUtil.EtCallBack;

/**
 * 虹软人脸组件中的Dialog
 */
public class ArcDialog {

    /**
     * EtDialog
     */
    public static void showEtDialog(@NonNull Activity activity, EtCallBack etCallBack) {
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.arc_et_dialog_view, null);
        AppCompatEditText nameEt = dialogView.findViewById(R.id.nameEt);
        AppCompatTextView cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        AppCompatTextView positionBtn = dialogView.findViewById(R.id.positionBtn);
        Dialog dialog = AppDialog.gmDialog(activity, dialogView, false);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoftInputKeyboardUtils.hiddenKeyboard(nameEt);
                dialog.dismiss();
            }
        });
        positionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEt.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    VerifyResultUi.showVerifyFail(activity,
                            "注册姓名不能为空", false);
                    dialog.dismiss();
                    return;
                }
                if (etCallBack != null)
                    etCallBack.etContent(name, dialog);
                dialog.dismiss();
                SoftInputKeyboardUtils.hiddenKeyboard(nameEt);
            }
        });
    }

    /**
     * 等待对话框
     * @param activity   activity
     * @param progressDialog   progressDialog
     */
    public static void showWaitDialog(@NonNull Activity activity, ProgressDialogCallBack progressDialog) {
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.arc_wait_dialog_view, null);
        ContentLoadingProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(activity, R.color.blue_10)
                        , PorterDuff.Mode.MULTIPLY);
        Dialog dialog = AppDialog.gmDialog(activity, dialogView, false);
        if (progressDialog != null)
            progressDialog.progressDialog(dialog);
    }

}
