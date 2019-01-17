package com.aihui.lib.base.api.retrofit.download;

import android.os.SystemClock;

import com.aihui.lib.base.util.HandlerUtils;
import com.aihui.lib.base.util.LogUtils;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by 胡一鸣 on 2018/9/4.
 */
public class ProgressRequestBody extends RequestBody {
    private RequestBody delegate;
    private OnProgressListener onProgressListener;

    ProgressRequestBody(RequestBody body, OnProgressListener listener) {
        delegate = body;
        onProgressListener = listener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return delegate.contentLength();
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        if (sink instanceof Buffer) {
            /**
             * {@link okhttp3.logging.HttpLoggingInterceptor#intercept}
             * will read the delegate body, do nothing here
             */
        } else {
            BufferedSink bufferedSink = Okio.buffer(sink(sink));
            //写入
            delegate.writeTo(bufferedSink);
            //刷新
            bufferedSink.flush();
        }
    }

    private Sink sink(BufferedSink sink) {

        return new ForwardingSink(sink) {

            long lastRefreshTime = 0L;  //最后一次刷新的时间
            long mRefreshTime = 150;
            long currentBytes = 0L;
            long contentLength = 0L;

            @Override
            public void write(@NonNull Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                currentBytes += byteCount;
                LogUtils.i("hash" + hashCode() + " write current0:" + currentBytes + " content:" + contentLength);
                if (onProgressListener != null) {
                    long curTime = SystemClock.elapsedRealtime();
                    if (curTime - lastRefreshTime >= mRefreshTime || currentBytes == contentLength) {
                        lastRefreshTime = curTime;
                        final long currentBytes = this.currentBytes;
                        final long contentLength = this.contentLength;
                        LogUtils.i("write current1:" + currentBytes + " content:" + contentLength);
                        HandlerUtils.post(() ->
                                onProgressListener.onProgress(currentBytes, contentLength));
                    }
                }
            }
        };
    }
}
