package com.face.utils;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class FileHelper {

    private static final String TAG = FileHelper.class.getSimpleName();

    public static String fileToBase64String(String filePath) {
        File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
            byte[] buff = new byte[1024];
            int len = -1;
            while ((len = fis.read(buff)) != -1) {
                bout.write(buff, 0, len);
            }
            return Base64.encodeToString(bout.toByteArray(), Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e("utils", "fileToBase64String: read file ERROR: " + filePath, e);
        }
        return null;
    }

    public static String readTextFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder result = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
    }

    public static void writeTextFile(File file, String content) throws Exception {
        if (file.isDirectory()) {
            throw new Exception("file is directory");
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(content);
            writer.flush();
        }
    }

    public static boolean ensureDirectory(File parentFile) {
        if (parentFile == null) {
            return false;
        }
        if (parentFile.exists()) {
            return parentFile.isDirectory();
        } else {
            return parentFile.mkdirs();
        }

    }

    public static boolean inputStreamToFile(InputStream ins, File file) throws Exception {
        try (FileOutputStream fout = new FileOutputStream(file)) {
            byte[] buff = new byte[1024];
            int ret = -1;
            while ((ret = ins.read(buff)) != -1) {
                fout.write(buff, 0, ret);
            }
            return true;
        }
    }

    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        try (FileInputStream fin = new FileInputStream(sourceFile);
             FileOutputStream fout = new FileOutputStream(targetFile, false)) {
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = fin.read(buff)) > 0) {
                fout.write(buff, 0, len);
            }
        }
    }

    public static boolean deleteIfExists(File file) {
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

}
