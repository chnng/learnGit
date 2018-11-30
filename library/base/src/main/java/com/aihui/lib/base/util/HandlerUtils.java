package com.aihui.lib.base.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 胡一鸣 on 2018/6/20.
 */
public final class HandlerUtils {
    private static HandlerUtils mInstance;
    private Handler mHandler;
    private List<Handler.Callback> mCallbackList;

    private static Handler getUIHandler() {
        if (mInstance == null) {
            synchronized (HandlerUtils.class) {
                if (mInstance == null) {
                    mInstance = new HandlerUtils();
                }
            }
        }
        return mInstance.mHandler;
    }

    private HandlerUtils() {
        mHandler = new Handler(Looper.getMainLooper(), getCallback());
    }

    public static void post(Runnable r) {
        getUIHandler().post(r);
    }

    public static void postDelayed(Runnable r, long delayMillis) {
        getUIHandler().postDelayed(r, delayMillis);
    }

    @NonNull
    private Handler.Callback getCallback() {
        return msg -> {
            if (mCallbackList != null) {
                for (Handler.Callback callback : mCallbackList) {
                    callback.handleMessage(msg);
                }
            }
            return false;
        };
    }

    public void addCallback(@NonNull Handler.Callback callback) {
        if (mCallbackList == null) {
            mCallbackList = new ArrayList<>();
        }
        mCallbackList.add(callback);
    }

    public void removeCallback(@NonNull Handler.Callback callback) {
        if (mCallbackList != null) {
            mCallbackList.remove(callback);
        }
    }
}
