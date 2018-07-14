package com.learn.git.util;

import android.util.Log;

public class LogUtil {

    public static void d(String msg) {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
        log(Log.DEBUG, stackTraceElement, msg);
    }

    public static void e(String msg) {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
        log(Log.ERROR, stackTraceElement, msg);
    }

    private static void log(int level, StackTraceElement stackTraceElement, String msg) {
        if (AppUtil.DEBUG) {
            String fileName = stackTraceElement.getFileName();
            String methodName = stackTraceElement.getMethodName();
            int methodIndex = methodName.lastIndexOf('$') + 1;

            String logTag = fileName.substring(0, fileName.lastIndexOf('.'));
            String logMsg = "[thread: " + Thread.currentThread().getName()
                    + "] [method: " + methodName.substring(methodIndex)
                    + "] [line: " + stackTraceElement.getLineNumber()
                    + "] 【 " + msg + " 】";
            switch (level) {
                case Log.DEBUG:
                    Log.d(logTag, logMsg);
                    break;
                case Log.ERROR:
                    Log.e(logTag, logMsg);
                    break;
            }
        }
    }
}
