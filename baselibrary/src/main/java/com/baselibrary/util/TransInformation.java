package com.baselibrary.util;

import android.text.method.ReplacementTransformationMethod;

/**
 * Created By pq
 * on 2019/10/9
 * EditText输入的小写英文字母自动转换成大写
 */
public class TransInformation extends ReplacementTransformationMethod {
    @Override
    protected char[] getOriginal() {
        char[] small = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        return small;
    }

    @Override
    protected char[] getReplacement() {
        char[] big = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        return big;
    }

}
