package org.linphone;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.wanjian.cockroach.Cockroach;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
    private static Context mContext;
    private static volatile boolean mIsLogFileLoading;

    // 2018-07-13T16:21:12.079
    public final static SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.CHINA);

    public static void init(Context context) {
        mContext = context;
        setEnableWriteFile(true);
        LogUtils.e("--->LogUtils<---" + context.toString());
        installCockroach();
    }


    /**
     * 装载Cockroach
     */
    private static void installCockroach() {
        Cockroach.install((thread, throwable) -> {
            //开发时使用Cockroach可能不容易发现bug，所以建议开发阶段在handlerException中用Toast谈个提示框，
            //由于handlerException可能运行在非ui线程中，Toast又需要在主线程，所以new了一个new Handler(Looper.getMainLooper())，
            //所以千万不要在下面的run方法中执行耗时操作，因为run已经运行在了ui线程中。
            //new Handler(Looper.getMainLooper())只是为了能弹出个toast，并无其他用途
            throwable.printStackTrace();
            try {
                //建议使用下面方式在控制台打印异常，这样就可以在Error级别看到红色log
                StringBuilder builder = new StringBuilder();
                StackTraceElement[] stackElements = throwable.getStackTrace();
                if (stackElements != null) {
                    for (StackTraceElement stackElement : stackElements) {
                        builder.append(stackElement.getClassName()).append('\t');
                        builder.append(stackElement.getFileName()).append('\t');
                        builder.append(stackElement.getLineNumber()).append('\t');
                        builder.append(stackElement.getMethodName()).append('\n');
                        builder.append("-----------------------------------").append('\n');
                    }
                }
                LogUtils.e("--->CockroachException<---" + builder.toString());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

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

    public static boolean isEnableWriteFile() {
        return mIsEnableWriteFile;
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
            Log.i("#AH#OkHttp", getLogThread() + log);
            writeLogFile(sdf0.format(System.currentTimeMillis()) + " #AH#OkHttp " + getLogThread() + log + '\n');
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
        writeLogFile(sdf0.format(System.currentTimeMillis()) + ' ' + logTag + ' ' + logMsg + '\n');
    }

    @NonNull
    private static String getLogThread() {
        return "main".equals(Thread.currentThread().getName()) ? "" : "[T:" + Thread.currentThread().getId() + "] ";
    }

    @NonNull
    private static File getLogFile() {
        if (mLogFile == null) {
            File file = FileUtils.getIndividualCacheDirectory(mContext, FileUtils.DIR_LOG);
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
        if (!mIsEnableWriteFile || mIsLogFileLoading) {
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
