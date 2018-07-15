package com.learn.git.ui.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class BaseApplication extends Application {
    private static Context mContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext = this;
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Context getContext() {
        return mContext;
    }
}
