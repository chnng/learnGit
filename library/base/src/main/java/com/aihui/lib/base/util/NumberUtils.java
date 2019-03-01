package com.aihui.lib.base.util;

/**
 * Created by 胡一鸣 on 2018/8/21.
 */
public final class NumberUtils {
    public static int str2Int(String src) {
        int dest = 0;
        try {
            dest = Integer.parseInt(src);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return dest;
    }

    public static long str2Long(String src) {
        long dest = 0;
        try {
            dest = Long.parseLong(src);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return dest;
    }

    public static float str2Float(String src) {
        float dest = 0;
        try {
            dest = Float.parseFloat(src);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return dest;
    }
}
