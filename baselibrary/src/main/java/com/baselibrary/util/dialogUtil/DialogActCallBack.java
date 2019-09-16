package com.baselibrary.util.dialogUtil;

import android.support.v7.app.AlertDialog;

/**
 * Created By pq
 * on 2019/4/25
 * 对话框的执行回调
 */
public interface DialogActCallBack {

    void cancelActCallBack(AlertDialog alertDialog);

    void positionActCallBack(AlertDialog alertDialog);

}
