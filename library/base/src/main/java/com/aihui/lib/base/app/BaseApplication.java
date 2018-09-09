package com.aihui.lib.base.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.aihui.lib.base.api.eventbus.EventMessage;
import com.aihui.lib.base.api.eventbus.EventTag;
import com.aihui.lib.base.util.ApplicationUtils;
import com.aihui.lib.base.util.HandlerUtils;
import com.wanjian.cockroach.Cockroach;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by 路传涛 on 2017/5/24.
 */

public abstract class BaseApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        if (!ApplicationUtils.isMainProcess(this)) {
            return;
        }
        initData();
//        if (App.AUTH) {
            installCockroach();
//        }
    }

    protected abstract void initData();

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Cockroach.uninstall();//卸载 Cockroach
    }

    /**
     * 装载Cockroach
     */
    private void installCockroach() {
        Cockroach.install((thread, throwable) -> {
            //开发时使用Cockroach可能不容易发现bug，所以建议开发阶段在handlerException中用Toast谈个提示框，
            //由于handlerException可能运行在非ui线程中，Toast又需要在主线程，所以new了一个new Handler(Looper.getMainLooper())，
            //所以千万不要在下面的run方法中执行耗时操作，因为run已经运行在了ui线程中。
            //new Handler(Looper.getMainLooper())只是为了能弹出个toast，并无其他用途
            HandlerUtils.getUIHandler().post(() -> {
                try {
                    //建议使用下面方式在控制台打印异常，这样就可以在Error级别看到红色log
                    Log.e("AndroidRuntime", "--->CockroachException:" + thread + "<---", throwable);
                    //正式发布版本时注销该 toast
                    //Toast.makeText(BaseApplication.this, "Exception Happend\n" + thread + "\n" + throwable.toString(), Toast.LENGTH_SHORT).show();
                    //扫码出现异常时关闭扫码页面
                    EventBus.getDefault().post(new EventMessage<>(EventTag.CLOSE_SCAN_ACTIVITY, null));
                } catch (Throwable e) {

                }
            });
        });
    }
}
