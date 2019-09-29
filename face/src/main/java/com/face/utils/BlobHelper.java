package com.face.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author yinsen
 */
public class BlobHelper {

    public static byte[] floatArrayToByteArray(float[] value) {
        if (value == null || value.length == 0) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.allocate(4 * value.length).order(ByteOrder.BIG_ENDIAN);
        for (float v : value) {
            buffer.putFloat(v);
        }
        return buffer.array();
    }

    public static float[] byteArrayToFloatArray(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        if (data.length % 4 != 0) {
            return null;
        }
        int len = data.length / 4;
        float[] value = new float[len];
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);
        for (int i = 0; i < len; i++) {
            value[i] = buffer.getFloat();
        }
        return value;
    }

    public static byte[] doubleArrayToByteArray(double[] value) {
        if (value == null || value.length == 0) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.allocate(8 * value.length).order(ByteOrder.BIG_ENDIAN);
        for (double v : value) {
            buffer.putDouble(v);
        }
        return buffer.array();
    }

    public static double[] byteArrayToDoubleArray(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        if (data.length % 8 != 0) {
            return null;
        }
        int len = data.length / 8;
        double[] value = new double[len];
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);
        for (int i = 0; i < len; i++) {
            value[i] = buffer.getDouble();
        }
        return value;
    }

}
