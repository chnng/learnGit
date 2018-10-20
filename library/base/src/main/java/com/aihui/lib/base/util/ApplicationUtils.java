package com.aihui.lib.base.util;

import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * 主要功能:获取App应用版本信息
 * Created by 路传涛 on 2017/6/13.
 */

public final class ApplicationUtils {

    /**
     * @return 4.4
     */
    public static boolean isKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * @return 5.0
     */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * @return 8.0
     */
    public static boolean isOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * 获取应用程序名称
     */
    public static synchronized String getAppName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int labelRes = applicationInfo.labelRes;
        return labelRes == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(labelRes);
    }

    /**
     * 获取该应用版本号
     *
     * @param context
     * @return 0 表示获取失败
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取该应用版本名
     *
     * @param context
     * @return 0 表示获取失败
     */
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.30.0";
    }

    /**
     * 获取未安装的APK版本号(versionCode)
     *
     * @param apkPath 安装包路径
     * @return 0 表示获取失败
     */
    public static int getVersionCode(Context context, String apkPath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo pi = packageManager.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        int versionCode = 0;
        if (pi != null) {
            versionCode = pi.versionCode;
        }
        return versionCode;
    }

    /**
     * 判断是否为主进程
     */
    public static boolean isMainProcess(Context context) {
        Context application = context.getApplicationContext();
        ActivityManager manager = (ActivityManager) application.getSystemService
                (Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        int pid = android.os.Process.myPid();
        String processName = "";
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return application.getPackageName().equals(processName);
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isLandScreen(Context context) {
        int ori = context.getResources().getConfiguration().orientation; // 获取屏幕方向
        return ori == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 模拟返回键
     */
    public static void simulationBackKey() {
        Executors.newCachedThreadPool().execute(() -> {
            try {
                Instrumentation instrumentation = new Instrumentation();
                instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
