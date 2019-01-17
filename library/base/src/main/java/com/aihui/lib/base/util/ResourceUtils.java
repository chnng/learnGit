package com.aihui.lib.base.util;

import android.content.Context;
import android.util.TypedValue;

import com.aihui.lib.base.app.BaseApplication;

import androidx.annotation.DimenRes;

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
}
