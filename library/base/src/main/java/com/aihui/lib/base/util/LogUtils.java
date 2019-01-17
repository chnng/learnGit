package com.aihui.lib.base.util;

import android.text.TextUtils;
import android.util.Log;

import com.aihui.lib.base.app.BaseApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import androidx.annotation.NonNull;
import okio.BufferedSink;
import okio.Okio;

/**
 * Created by 路传涛 on 2017/6/2.
 */

public final class LogUtils {
    private static final int MAX_LOG_FILE_SIZE = 15 * 1024 * 1024;
    private static boolean mIsDebug = true;
    private static boolean mIsEnableWriteFile;
    private static File mLogFile;

    /**
     * 是否打印log日志
     * 建议开发阶段打印，发布时关闭
     *
     * @param debug
     */
    public static void debug(boolean debug) {
//        mIsDebug = debug;
    }

    public static void setEnableWriteFile(boolean enableWriteFile) {
        mIsEnableWriteFile = enableWriteFile;
    }

    // 华为平板系统禁止information等级之下的日志输出
//    public static void d(String log) {
//        if (mIsDebug) {
//            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
//            log(Log.DEBUG, stackTraceElement, log);
//        }
//    }

    public static void i(Object log) {
        if (mIsDebug) {
            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
            log(Log.INFO, stackTraceElement, log);
        }
    }

    public static void w(Object log) {
        if (mIsDebug) {
            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
            log(Log.WARN, stackTraceElement, log);
        }
    }

    public static void e(Object log) {
        if (mIsDebug) {
            StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
            log(Log.ERROR, stackTraceElement, log);
        }
    }

    public static void http(Object log) {
        if (mIsDebug) {
            Log.i("#CH#OkHttp", getLogThread() + log);
            writeLogFile(TimeUtils.sdf0.format(System.currentTimeMillis()) + " #AH#OkHttp " + getLogThread() + log + '\n');
        }
    }

    private static void log(int level, StackTraceElement stackTraceElement, Object msg) {
        String fileName = stackTraceElement.getFileName();
        String methodName = stackTraceElement.getMethodName();
        int methodIndex = methodName.lastIndexOf('$') + 1;

        String logTag = "#AH#" + (TextUtils.isEmpty(fileName) ? "" : fileName.substring(0, fileName.lastIndexOf('.')));
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
        writeLogFile(TimeUtils.sdf0.format(System.currentTimeMillis()) + ' ' + logTag + ' ' + logMsg + '\n');
    }

    @NonNull
    private static String getLogThread() {
        return "main".equals(Thread.currentThread().getName()) ? "" : "[T:" + Thread.currentThread().getId() + "] ";
    }

    @NonNull
    private static File getLogFile() {
        if (mLogFile == null) {
            File file = FileUtils.getIndividualCacheDirectory(BaseApplication.getContext(), FileUtils.DIR_LOG);
            mLogFile = new File(file.getAbsolutePath(), "log.txt");
        }
        return mLogFile;
    }

    public static void deleteLogFile() {
        File file = getLogFile();
        if (file.exists()) {
            file.delete();
        }
    }

    private static void writeLogFile(String log) {
        if (!mIsEnableWriteFile) {
            return;
        }
        File file = getLogFile();
        if (file.length() > MAX_LOG_FILE_SIZE) {
            file.delete();
        }
        try (BufferedSink sink = Okio.buffer(Okio.appendingSink(file))) {
            sink.write(log.getBytes());
            sink.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
