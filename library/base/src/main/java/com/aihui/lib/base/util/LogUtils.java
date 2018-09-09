package com.aihui.lib.base.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by 路传涛 on 2017/6/2.
 */

public final class LogUtils {
    private static boolean DEBUG = true;

    /**
     * 是否打印log日志
     * 建议开发阶段打印，发布时关闭
     *
     * @param b
     */
    public static void debug(boolean b) {
//        DEBUG = b;
    }

    // 华为平板系统禁止information等级之下的日志输出
//    public static void d(String log) {
//        if (DEBUG) {
//            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
//            log(Log.DEBUG, stackTraceElement, log);
//        }
//    }

    public static void i(Object log) {
        if (DEBUG) {
            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
            log(Log.INFO, stackTraceElement, log);
        }
    }

    public static void w(Object log) {
        if (DEBUG) {
            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
            log(Log.WARN, stackTraceElement, log);
        }
    }

    public static void e(Object log) {
        if (DEBUG) {
            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
            log(Log.ERROR, stackTraceElement, log);
        }
    }

    public static void http(Object log) {
        if (DEBUG) {
            Log.i("#CH#OkHttp", getLogThread() + log);
        }
    }

    private static void log(int level, StackTraceElement stackTraceElement, Object msg) {
        String fileName = stackTraceElement.getFileName();
        String methodName = stackTraceElement.getMethodName();
        int methodIndex = methodName.lastIndexOf('$') + 1;

        String logTag = "#CH#" + (TextUtils.isEmpty(fileName) ? "" : fileName.substring(0, fileName.lastIndexOf('.')));
        String logMsg = getLogThread()
                + "[M:" + methodName.substring(methodIndex)
                + "][L:" + stackTraceElement.getLineNumber()
                + "]【" + msg + "】";
        switch (level) {
            case Log.DEBUG:
                Log.d(logTag, logMsg);
                break;
            case Log.INFO:
                Log.i(logTag, logMsg);
                break;
            case Log.WARN:
                Log.w(logTag, logMsg);
                break;
            case Log.ERROR:
                Log.e(logTag, logMsg);
                break;
        }
    }

    @NonNull
    private static String getLogThread() {
        return "main".equals(Thread.currentThread().getName()) ? "" : "[T:" + Thread.currentThread().getId() + "] ";
    }
}
