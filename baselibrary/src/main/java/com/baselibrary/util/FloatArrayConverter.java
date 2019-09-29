package com.baselibrary.util;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by wangyu on 2019/9/24.
 */

public class FloatArrayConverter implements PropertyConverter<float[],String> {
    @Override
    public float[] convertToEntityProperty(String databaseValue) {
        if (databaseValue!=null){
            String[] strings = databaseValue.split(", ");
            float[] fs = new float[strings.length];
            for (int i = 0; i < strings.length; i++) {
                fs[i] = Float.parseFloat(strings[i]);
            }
            return fs;
        }
        return new float[0];
    }

    @Override
    public String convertToDatabaseValue(float[] entityProperty) {
        if (entityProperty!=null){
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < entityProperty.length; i++) {
                if (i==0){
                    stringBuilder.append(entityProperty[i]);
                }else {
                    stringBuilder.append( ", "+entityProperty[i] );
                }
            }
            String s = stringBuilder.toString();
            return  s;
        }
        return null;
    }
}
