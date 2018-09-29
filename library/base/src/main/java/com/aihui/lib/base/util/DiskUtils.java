package com.aihui.lib.base.util;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by 胡一鸣 on 2018/9/21.
 */
public final class DiskUtils {
    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param file 文件路径
     */
    public static String getFileSize(@NonNull File file) throws IOException {
        if (!file.exists()) {
            return "0";
        }
        long fileSize;
        if (file.isDirectory()) {
            fileSize = getDirectorySize(file);
        } else {
            fileSize = getSingleFileSize(file);
        }
        return formatFileSize(fileSize);
    }

    /**
     * 获取指定文件夹
     *
     * @param src
     * @return
     * @throws IOException
     */
    private static long getDirectorySize(File src) throws IOException {
        long size = 0;
        for (File file : src.listFiles()) {
            if (file.isDirectory()) {
                size += getDirectorySize(file);
            } else {
                size += getSingleFileSize(file);
            }
        }
        return size;
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws IOException
     */
    private static long getSingleFileSize(File file) throws IOException {
        if (!file.exists()) {
            return 0;
        }
        try (FileInputStream in = new FileInputStream(file)) {
            return in.available();
        }
    }

    /**
     * 转换文件大小
     *
     * @param size
     * @return
     */
    public static String formatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String formatSize;
        if (size < 1024) {
            formatSize = df.format((double) size) + "B";
        } else if (size < 1048576) {
            formatSize = df.format((double) size / 1024) + "KB";
        } else if (size < 1073741824) {
            formatSize = df.format((double) size / 1048576) + "MB";
        } else {
            formatSize = df.format((double) size / 1073741824) + "GB";
        }
        return formatSize;
    }
}
