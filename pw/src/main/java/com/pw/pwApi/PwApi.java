package com.pw.pwApi;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.callBack.PwCallBack;
import com.baselibrary.custom.CEditText;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.listener.OnceClickListener;
import com.baselibrary.pojo.Pw;
import com.baselibrary.util.ToastUtils;
import com.baselibrary.util.dialogUtil.AppDialog;
import com.orhanobut.logger.Logger;
import com.pw.R;

import java.util.List;

/**
 * Created By pq
 * on 2019/9/27
 */
public class PwApi {

    public static void pwRegister(Activity activity, PwCallBack pwCallBack) {
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.input_pw_view, null);
        CEditText inputPw = dialogView.findViewById(R.id.inputPw);
        AppCompatImageView dismissBtn = dialogView.findViewById(R.id.dismissBtn);
        LinearLayout btnParent = dialogView.findViewById(R.id.btnParent);
        AppCompatButton nextBtn = dialogView.findViewById(R.id.nextBtn);
        AppCompatButton cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        AppCompatButton positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        AppCompatTextView inputPwTitle = dialogView.findViewById(R.id.inputPwTitle);
        final String[] pw1 = new String[1];
        nextBtn.setOnClickListener(new OnceClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                String pw = inputPw.getText().toString().trim();
                if (pw.length() < 6) {
                    ToastUtils.showSquareTvToast(
                            activity, activity.getString(R.string.please_input_6_pw));
                    return;
                }
                pw1[0] = pw;
                inputPw.getText().clear();
                inputPwTitle.setText(activity.getString(R.string.check_pw));
                nextBtn.setVisibility(View.GONE);
                btnParent.setVisibility(View.VISIBLE);
            }
        });
        Dialog dialog = AppDialog.gmDialog(activity, dialogView, false);
        dismissBtn.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                pw1[0] = null;
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                pw1[0] = null;
                dialog.dismiss();
            }
        });
        positiveBtn.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String pw = inputPw.getText().toString().trim();
                if (pw.length() < 6) {
                    ToastUtils.showSquareTvToast(activity, activity.getString(R.string.please_input_6_pw));
                    return;
                }
                if (pw.equals(pw1[0])) {
                    //存储密码，注册成功
//                    pwId = System.currentTimeMillis();
                    Pw pwData = new Pw();
                    pwData.setPassword(pw);
                    pwCallBack.pwCallBack(pwData);
//                    insertOrReplacePw(pwData, pwCallBack);
                    pw1[0] = null;
                    dialog.dismiss();
                } else {
                    ToastUtils.showSquareImgToast(activity, activity.getString(R.string.pw_register_no_eq),
                            ActivityCompat.getDrawable(activity, R.drawable.cry_icon)
                    );
                }
            }
        });
    }

    private static void insertOrReplacePw(Pw pw, PwCallBack pwCallBack) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        dbUtil.setDbCallBack(new DbCallBack<Pw>() {
            @Override
            public void onSuccess(Pw result) {
                Logger.d("Pw成功插入：" + result);
                if (pwCallBack != null)
                    pwCallBack.pwCallBack(result);
            }

            @Override
            public void onSuccess(List<Pw> result) {

            }

            @Override
            public void onFailed() {
                Logger.d("Pw插入失败：");
            }

            @Override
            public void onNotification(boolean result) {
                Logger.d("插入Pw成功:" + result);
            }
        }).insertAsyncSingle(pw);
    }

}
