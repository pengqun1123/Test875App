package com.baselibrary.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * Created By pq
 * on 2019/9/26
 * <p>
 * 清空EditText
 */
public class EtClearUtil {


    public static void clearEt(@NonNull AppCompatEditText et, @NonNull AppCompatImageView btn
            , EtLengthCallBack etLengthCallBack) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                if (length > 0) {
                    btn.setVisibility(View.VISIBLE);
                } else {
                    btn.setVisibility(View.GONE);
                }
                etLengthCallBack.etLengthCallBack(length);
            }
        });
        btn.setOnClickListener(view -> et.getText().clear());
    }

    public interface EtLengthCallBack {
        void etLengthCallBack(Integer length);
    }

}
