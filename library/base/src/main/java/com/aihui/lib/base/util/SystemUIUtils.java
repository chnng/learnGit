package com.aihui.lib.base.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.Window;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by 胡一鸣 on 2018/8/19.
 */
public final class SystemUIUtils {

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    public static void hideNavigationBar(Activity activity) {
        hideNavigationBar(activity, activity.getWindow());
    }

    public static void hideNavigationBar(Dialog dialog) {
        hideNavigationBar(dialog.getContext(), dialog.getWindow());
    }

    public static void showNavigationBar(Window window) {
        View decorView = window.getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private static void hideNavigationBar(Context context, Window window) {
        if (checkDeviceHasNavigationBar(context)) {
            if (ApplicationUtils.isKitkat()) {
                View decorView = window.getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        ;
                decorView.setSystemUiVisibility(uiOptions);
            } else { // lower api
                View v = window.getDecorView();
                v.setSystemUiVisibility(View.GONE);
            }
        }
    }

    private static int navigationBarFlag = -1;
    public static boolean navigationBarHide = false;

    /**
     * 判断是否存在虚拟按键
     *
     * @return
     */
    private static boolean checkDeviceHasNavigationBar(Context context) {
        if (navigationBarFlag == -1) {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                boolean hasNavigationBar = rs.getBoolean(id);
                if (hasNavigationBar) {
                    navigationBarFlag = 1;
                    return true;
                } else {
                    navigationBarFlag = 0;
                }
            }
            try {
                Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
                Method m = systemPropertiesClass.getMethod("get", String.class);
                String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
                if ("1".equals(navBarOverride)) {
                    navigationBarFlag = 0;
                } else if ("0".equals(navBarOverride)) {
                    navigationBarFlag = 1;
                }
            } catch (Exception ignore) {

            }
        }
        return navigationBarFlag == 1;
    }
}
