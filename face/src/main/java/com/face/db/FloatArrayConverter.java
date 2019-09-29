package com.face.db;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by wangyu on 2019/9/24.
 */

public class FloatArrayConverter implements PropertyConverter<Float[],String> {
    @Override
    public Float[] convertToEntityProperty(String databaseValue) {
        if (databaseValue!=null){
            String[] strings = databaseValue.split(" ");
            float[] fs = new float[strings.length];
            for (int i = 0; i < strings.length; i++) {
                fs[i] = Float.parseFloat(strings[i]);
            }
        }
        return new Float[0];
    }

    @Override
    public String convertToDatabaseValue(Float[] entityProperty) {
        if (entityProperty!=null){
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < entityProperty.length; i++) {
                stringBuilder.append(entityProperty[i] + " ");
            }
            String s = stringBuilder.toString();
            return  s;
        }
        return null;
    }
}
