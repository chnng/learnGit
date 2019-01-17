package com.aihui.lib.base.api.retrofit.download;

import com.aihui.lib.base.util.LogUtils;

import java.io.File;

import androidx.annotation.NonNull;

/**
 * Created by 胡一鸣 on 2018/9/4.
 */
public abstract class OnProgressListener {

    /**
     * 传输进度
     * @param currentBytes 当前进度
     * @param contentLength 文件总长
     */
    protected void onProgress(long currentBytes, long contentLength) {
        int progress = (int) Math.floor((float) currentBytes / contentLength * 100);
        LogUtils.i("文件传输进度：" + progress + "%" + " current:" + currentBytes + " content:" + contentLength);
        onProgress(progress);
    }

    /**
     * 传输进度
     *
     * @param progress 0-100
     */
    protected void onProgress(int progress) {
    }


    /**
     * 文件传输成功
     *
     * @param file file
     */
    protected void onSuccess(@NonNull File file) {
    }

    /**
     * 文件传输失败
     *
     * @param e    错误信息
     * @param file file
     */
    protected void onFailure(@NonNull Exception e, @NonNull File file) {
    }
}
