package com.testApp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.custom.CEditText;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.listener.OnceClickListener;
import com.baselibrary.pojo.Manager;
import com.baselibrary.util.SPUtil;
import com.baselibrary.util.ToastUtils;
import com.baselibrary.util.dialogUtil.AppDialog;
import com.orhanobut.logger.Logger;
import com.testApp.R;

import java.util.List;


/**
 * Created By pq
 * on 2019/9/30
 */
public class AskDialog {

    //展示设置管理员密码和启用人脸识别
    public static void showManagerDialog(@NonNull Activity activity, PositiveCallBack callBack) {
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.ask_manager_dialog_view, null);
        AppCompatTextView managerSetTitle = dialogView.findViewById(R.id.managerSetTitle);
        AppCompatTextView managerTip = dialogView.findViewById(R.id.managerTip);
        CEditText inputPw = dialogView.findViewById(R.id.inputPw);
        AppCompatButton nextBtn = dialogView.findViewById(R.id.nextBtn);
        LinearLayout btnParent = dialogView.findViewById(R.id.btnParent);
        LinearLayout openFaceAsk = dialogView.findViewById(R.id.openFaceAsk);
        AppCompatButton cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        AppCompatButton positiveBtn = dialogView.findViewById(R.id.positiveBtn);
        AppCompatCheckBox cbOpenFace = dialogView.findViewById(R.id.cbOpenFace);
        AppCompatImageView dismissBtn = dialogView.findViewById(R.id.dismissBtn);
        managerSetTitle.setText(activity.getString(R.string.manager_set));
        nextBtn.setVisibility(View.VISIBLE);
        btnParent.setVisibility(View.GONE);
        Dialog dialog = AppDialog.gmDialog(activity, dialogView, false);
        final String[] pw1 = new String[2];
        nextBtn.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (pw1[0] == null) {
                    String pw = inputPw.getText().toString().trim();
                    if (pw.length() < 6) {
                        ToastUtils.showSquareTvToast(
                                activity, activity.getString(com.pw.R.string.please_input_6_pw));
                        return;
                    }
                    pw1[0] = pw;
                    managerTip.setText(activity.getString(R.string.please_input_pw_agin));
                    inputPw.getText().clear();
                } else {
                    String pw2 = inputPw.getText().toString().trim();
                    if (pw2.length() < 6) {
                        ToastUtils.showSquareTvToast(
                                activity, activity.getString(com.pw.R.string.please_input_6_pw));
                        return;
                    }
                    pw1[1] = pw2;
//                    if (pw1[0] == null) {
//                        ToastUtils.showSquareTvToast(
//                                activity, activity.getString(com.pw.R.string.please_input_6_pw));
//                        return;
//                    }
                    if (pw1[0].equals(pw1[1])) {
                        nextBtn.setVisibility(View.GONE);
                        btnParent.setVisibility(View.VISIBLE);
                        inputPw.setVisibility(View.GONE);
                        openFaceAsk.setVisibility(View.VISIBLE);
                        managerTip.setText(activity.getString(R.string.please_select_open_face));
                    } else {
                        ToastUtils.showSquareTvToast(
                                activity, activity.getString(R.string.pw_no_eq));
                        inputPw.getText().clear();
                    }
                }
            }
        });
        cbOpenFace.setOnCheckedChangeListener((compoundButton, b) -> SPUtil.putOpenFace(b));
        dismissBtn.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                pw1[0] = null;
                pw1[1] = null;
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                pw1[0] = null;
                pw1[1] = null;
                dialog.dismiss();
            }
        });
        positiveBtn.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (pw1[0].equals(pw1[1])) {
                    Manager manager = new Manager();
                    manager.setManage_pw(pw1[0]);
                    DBUtil dbUtil = BaseApplication.getDbUtil();
                    dbUtil.setDbCallBack(new DbCallBack<Manager>() {
                        @Override
                        public void onSuccess(Manager result) {
                            ToastUtils.showSquareImgToast(activity,
                                    activity.getString(R.string.manager_add_success),
                                    ActivityCompat.getDrawable(activity, R.drawable.ic_emoje));
                            Logger.d("管理员密码存储成功:" + result.toString());
                        }

                        @Override
                        public void onSuccess(List<Manager> result) {

                        }

                        @Override
                        public void onFailed() {
                            ToastUtils.showSquareImgToast(activity,
                                    activity.getString(R.string.manager_add_fail),
                                    ActivityCompat.getDrawable(activity, R.drawable.cry_icon));
                            Logger.d("管理员密码存储失败");
                        }

                        @Override
                        public void onNotification(boolean result) {

                        }
                    }).insertAsyncSingle(manager);
                }
                if (callBack != null)
                    callBack.positiveCallBack();
                dialog.dismiss();
            }
        });
    }

    public interface PositiveCallBack {
        void positiveCallBack();
    }

}
