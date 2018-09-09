package com.learn.git.api.okhttp;

import com.aihui.lib.base.util.LogUtils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpClientHelper {

    private static volatile OkHttpClient mOkHttpClient;

    public static OkHttpClient get() {
        if (mOkHttpClient == null) {
            synchronized (OkHttpClientHelper.class) {
                if (mOkHttpClient == null) {
                    mOkHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(new HttpLoggingInterceptor(LogUtils::http)
                                    .setLevel(HttpLoggingInterceptor.Level.BODY))
                            .build();
                }
            }
        }
        return mOkHttpClient;
    }
}
