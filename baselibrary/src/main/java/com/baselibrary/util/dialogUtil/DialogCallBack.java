package com.baselibrary.util.dialogUtil;

import android.content.DialogInterface;

/**
 * Created by **
 * on 2018/9/11.
 * dialog确定和取消的回调接口
 */

public interface DialogCallBack {

    void positiveClick(DialogInterface dialog);
    void negativeClick(DialogInterface dialog);

}
