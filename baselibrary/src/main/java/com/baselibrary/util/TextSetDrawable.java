package com.baselibrary.util;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;


public class TextSetDrawable {

    public static void setTextTopDrawable(Activity activity, TextView tv, int resDrawable) {
        Drawable drawable = ActivityCompat.getDrawable(activity, resDrawable);
        assert drawable != null;
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv.setCompoundDrawables(null, drawable, null, null);
    }
}
