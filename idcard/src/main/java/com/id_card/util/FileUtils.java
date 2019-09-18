package com.id_card.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by wangyu on 2019/9/17.
 */

public class FileUtils {
    /**
     * 通过文件通道实现文件复制
     */
    private static void copyFile(String path_in ,String path_out) {
        try {
            // 创建一个输入文件通道
            FileChannel fcIn = new FileInputStream(path_in).getChannel();
            // 创建一个输出文件通道
            FileChannel fcOut = new FileOutputStream(path_out).getChannel();

            ByteBuffer buf = ByteBuffer.allocate(1024);
            while(fcIn.read(buf) != -1) {
                buf.flip();
                fcOut.write(buf);
                buf.clear();// 清空缓冲区
            }

            fcOut.close();
            fcIn.close();
            System.out.println("复制成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// copyFile
}
