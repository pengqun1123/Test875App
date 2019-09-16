package com.baselibrary.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created By pq
 * on 2019/8/27
 * 控制软键盘得工具类
 */
public class SoftInputKeyboardUtils {

    /**
     * 展示软键盘
     *
     * @param view:
     *            1、第一个参数，最好是 EditText 或者它的子类。
     *
     * 考虑到软键盘就是为了输入，EditText 就是一个接收输入的控件。而这不是绝对的，如果不是一个
     *           EditText ，就必须要求这个 View 有两个属性，分别是：android:focusable="true"
     *            和android:focusableInTouchMode="true"。
     *
     * 2、第一个参数，必须是可获取焦点的，并且当前已经获取到焦点。
     *
     * EditText 默认是允许获取焦点的，但是假如布局中，存在多个可获取焦点的控件，就需要提前让我们
     *            传递进去的 View 获取到焦点。获取焦点可以使用 requestFocus() 方法。
     *
     * 3、布局必须加载完成。
     *
     * 在 onCreate() 中，如果立即调用 showSoftInput() 是不会生效的。想要在页面一启动的时候
     *            就弹出键盘，可以在 Activity 上，设置 android:windowSoftInputMode 属性来
     *            完成，或者做一个延迟加载，View.postDelayed() 也是一个解决方案。
     */
    public static void showKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            view.requestFocus();
            inputMethodManager.showSoftInput(view, 0);
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public static void hiddenKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
