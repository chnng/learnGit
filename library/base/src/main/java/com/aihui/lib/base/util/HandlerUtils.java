package com.aihui.lib.base.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * Created by 胡一鸣 on 2018/6/20.
 */
public final class HandlerUtils {
    private static Handler mHandler;

    public static Handler getUIHandler() {
        if (mHandler == null) {
            synchronized (HandlerUtils.class) {
                if (mHandler == null) {
                    mHandler = new Handler(Looper.getMainLooper(), getCallback());
                }
            }
        }
        return mHandler;
    }

    @NonNull
    private static Handler.Callback getCallback() {
        return msg -> {
            switch (msg.what) {
                default:
                    break;
            }
            return false;
        };
    }
}
