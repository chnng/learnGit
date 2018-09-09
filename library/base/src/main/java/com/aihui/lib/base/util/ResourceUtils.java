package com.aihui.lib.base.util;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.util.TypedValue;

import com.aihui.lib.base.app.BaseApplication;

import java.lang.reflect.Field;

/**
 * Created by 胡一鸣 on 2018/7/16.
 */
public final class ResourceUtils {

    public static int getMipmapId(String mipmapName) {
        Context context = BaseApplication.getContext();
        return context.getResources().getIdentifier(mipmapName, "mipmap", context.getPackageName());
    }

    public static float getDpValue(@DimenRes int resId) {
        Context context = BaseApplication.getContext();
        TypedValue value = new TypedValue();
        context.getResources().getValue(resId, value, true);
        return TypedValue.complexToFloat(value.data);
    }

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
}
