package com.aihui.lib.base.api.retrofit.download;

import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.aihui.lib.base.util.FileUtils;
import com.aihui.lib.base.util.HandlerUtils;
import com.aihui.lib.base.util.LogUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by 胡一鸣 on 2018/9/4.
 */
class ProgressResponseBody {
//    private ResponseBody delegate;
    private long lastRefreshTime = 0L;  //最后一次刷新的时间
    private long mRefreshTime = 150;

    ProgressResponseBody(ResponseBody body, @NonNull File file, OnProgressListener listener) {
//        this.delegate = body;
        byte[] buf = new byte[8 * 1024];
        int len;
        long currentBytes = 0;
        long contentLength = body.contentLength();
        try (BufferedSource source = Okio.buffer(Okio.source(body.byteStream()));
             BufferedSink sink = Okio.buffer(Okio.sink(FileUtils.createFile(file)))) {
            while ((len = source.read(buf)) != -1) {
                currentBytes += len;
                sink.write(buf, 0, len);
                LogUtils.i("write current:" + currentBytes + " content:" + contentLength);
                if (listener != null) {
                    long curTime = SystemClock.elapsedRealtime();
                    if (curTime - lastRefreshTime >= mRefreshTime || currentBytes == contentLength) {
                        lastRefreshTime = curTime;
                        long finalCurrentBytes = currentBytes;
                        HandlerUtils.getUIHandler().post(() -> listener.onProgress(finalCurrentBytes, contentLength));
                    }
                }
            }
            if (listener != null) {
                HandlerUtils.getUIHandler().post(() -> listener.onSuccess(file));
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                HandlerUtils.getUIHandler().post(() -> listener.onFailure(e, file));
            }
        }
    }

//    @Override
//    public MediaType contentType() {
//        return delegate.contentType();
//    }
//
//    @Override
//    public long contentLength() {
//        return delegate.contentLength();
//    }
//
//    @Override
//    public BufferedSource source() {
//        return delegate.source();
//    }
//
//    /**
//     * 读取，回调进度接口
//     *
//     * @param source Source
//     * @return Source
//     */
//    private Source source(Source source) {
//
//        return new ForwardingSource(source) {
//
//            long lastRefreshTime = 0L;  //最后一次刷新的时间
//            long mRefreshTime = ProgressManager.DEFAULT_REFRESH_TIME;
//            //当前读取字节数
//            long currentBytes = 0L;
//            long contentLength = 0L;
//
//            @Override
//            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
//                long bytesRead = super.read(sink, byteCount);
//                LogUtils.e("byteCount:" + byteCount + " bytesRead:" + bytesRead);
//                if (contentLength == 0) {
//                    contentLength = contentLength();
//                }
//                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
//                currentBytes += bytesRead != -1 ? bytesRead : 0;
//                if (onProgressListener != null) {
//                    long curTime = SystemClock.elapsedRealtime();
//                    if (curTime - lastRefreshTime >= mRefreshTime || currentBytes == contentLength) {
//                        lastRefreshTime = curTime;
//                        long currentBytes = this.currentBytes;
//                        long contentLength = this.contentLength;
//                        HandlerUtils.getUIHandler().post(() ->
//                                onProgressListener.onProgress(currentBytes, contentLength));
//                    }
//                }
//                return bytesRead;
//            }
//        };
//    }
}
