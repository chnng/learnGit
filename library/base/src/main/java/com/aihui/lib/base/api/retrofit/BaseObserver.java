package com.aihui.lib.base.api.retrofit;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by 胡一鸣 on 2018/10/25.
 * 只关心数据
 */
public abstract class BaseObserver<T> extends DisposableObserver<T> {

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        // java.net.SocketTimeoutException: failed to connect to /10.65.200.11 (port 8094) after 5000ms
    }

    @Override
    public void onComplete() {
    }
}
