package com.aihui.lib.base.api.retrofit;

import com.aihui.lib.base.util.LogUtils;

import androidx.annotation.NonNull;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by 胡一鸣 on 2018/10/25.
 * 只关心数据
 */
public abstract class BaseObserver<T> extends DisposableObserver<T> {
    private static long mTimestamp;

    @Override
    public abstract void onNext(@NonNull T t);

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
        // java.net.SocketTimeoutException: failed to connect to /10.65.200.11 (port 8094) after 5000ms
        if (LogUtils.isEnableWriteFile()
                && (!(e instanceof AhException) || ((AhException) e).isError())) {
            long currentTimeMillis = System.currentTimeMillis();
            // 控制频率最快30s上传一次
            if (currentTimeMillis - mTimestamp > 30000) {
                mTimestamp = currentTimeMillis;
                LogUtils.uploadLogFile();
            }
        }
    }

    @Override
    public void onComplete() {
    }
}
