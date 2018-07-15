package com.learn.git.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * Created by 胡一鸣 on 2018/6/20.
 */
public class HandlerUtil {
    private static volatile Handler mUIHandler;
    private static volatile Handler mWorkHandler;

    public static Handler getUIHandler() {
        if (mUIHandler == null) {
            synchronized (HandlerUtil.class) {
                if (mUIHandler == null) {
                    mUIHandler = new Handler(Looper.getMainLooper(), getCallback());
                }
            }
        }
        return mUIHandler;
    }

    public static Handler getWorkHandler() {
        if (mWorkHandler == null) {
            synchronized (HandlerUtil.class) {
                if (mWorkHandler == null) {
                    HandlerThread handlerThread = new HandlerThread("WorkHandler");
                    handlerThread.start();
                    mWorkHandler = new Handler(handlerThread.getLooper(), getCallback());
                }
            }
        }
        return mWorkHandler;
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

    public static void clear() {
        if (mUIHandler != null) {
            mUIHandler.removeCallbacksAndMessages(null);
        }
        if (mWorkHandler != null) {
            mWorkHandler.removeCallbacksAndMessages(null);
            mWorkHandler.getLooper().quit();
            mWorkHandler = null;
        }
    }
}
