package com.learn.git.api.okhttp;

import com.learn.git.util.LogUtil;

import me.jessyan.progressmanager.ProgressManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpClientHelper {

    private static volatile OkHttpClient mOkHttpClient;

    public static OkHttpClient  get() {
        if (mOkHttpClient == null) {
            synchronized (OkHttpClientHelper.class) {
                if (mOkHttpClient == null) {
                    mOkHttpClient = ProgressManager.getInstance().with(new OkHttpClient.Builder())
                            .addInterceptor(new HttpLoggingInterceptor(message -> LogUtil.d("--->OkHttp log: " + message))
                                    .setLevel(HttpLoggingInterceptor.Level.BODY))
                            .build();
                }
            }
        }
        return mOkHttpClient;
    }
}
