package com.aihui.lib.base.api.retrofit.download;

import android.accounts.NetworkErrorException;
import android.support.annotation.NonNull;

import com.aihui.lib.base.api.retrofit.RetrofitManager;
import com.aihui.lib.base.util.FileUtils;
import com.aihui.lib.base.util.LogUtils;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

/**
 * Created by 路传涛 on 2017/6/14.
 */

public class DownloadManager {
    private static int temp = 0;


    /**
     * 通过URL下载文件
     *
     * @param fileDir  下载的文件存储目录
     * @param fileName 下载的文件名称
     * @param url      下载文件的URL
     * @param listener 下载结果监听器
     */
    public static void downloadFile(@NonNull String url,
                                    @NonNull String fileDir,
                                    @NonNull String fileName,
                                    OnProgressListener listener) {
        downloadFile(url, new File(fileDir, fileName), listener);
    }

    /**
     * 通过URL下载文件
     *
     * @param url      下载文件的URL
     * @param file     下载的文件
     * @param listener 下载结果监听器
     */
    public static void downloadFile(@NonNull String url,
                                    @NonNull File file,
                                    OnProgressListener listener) {
        //注册下载进度 监听器
        ProgressManager.getInstance().addResponseListener(url, new me.jessyan.progressmanager.ProgressListener() {
            @Override
            public void onProgress(ProgressInfo progressInfo) {
                double currentLength = progressInfo.getCurrentbytes();
                double contentLength = progressInfo.getContentLength();
                int percent = (int) Math.floor(currentLength / contentLength * 100);
                if (temp != percent) {
                    temp = percent;
                    safeProgress(listener, percent);
                    LogUtils.i("文件下载进度：" + percent + "%");
                }
            }

            @Override
            public void onError(long id, Exception e) {
                e.printStackTrace();
                safeFailure(listener, e, file);
            }
        });

        //开始下载
        Request request = new Request.Builder().url(url).build();
        RetrofitManager.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                safeFailure(listener, e, file);
                LogUtils.i(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (!response.isSuccessful()) {
                    safeFailure(listener, new NetworkErrorException("response failed!"), file);
                    return;
                }
                ResponseBody delegateBody = response.body();
                if (delegateBody == null) {
                    safeFailure(listener, new NullPointerException("response delegateBody is null!"), file);
                    return;
                }
//                new ProgressResponseBody(delegateBody, file, listener);
                try (BufferedSink sink = Okio.buffer(Okio.sink(FileUtils.createFile(file)))) {
                    //写入SD卡
                    sink.writeAll(delegateBody.source());
                    LogUtils.i(file.getName() + " 下载完成");
                    //写入完成
                    safeSuccess(listener, file);
                } catch (IOException e) {
                    e.printStackTrace();
                    safeFailure(listener, e, file);
                }
            }
        });
    }

    /**
     * 通过URL上传文件
     * {@link com.aihui.lib.base.api.retrofit.server.HttpServer#upload(MultipartBody.Part, String)}
     *
     * @param file     下载的文件
     * @param listener 下载结果监听器
     */
    public static Observable<String> uploadFile(@NonNull String uploadType,
                                                @NonNull File file,
                                                String fileType,
                                                OnProgressListener listener) {
        RequestBody delegateBody = RequestBody.create(MediaType.parse(fileType), file);
        RequestBody requestBody = new ProgressRequestBody(delegateBody, listener);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        return RetrofitManager.newHttpServer().upload(body, uploadType);
    }

    private static void safeProgress(OnProgressListener listener, int progress) {
        if (listener != null) {
            listener.onProgress(progress);
        }
    }

    private static void safeSuccess(OnProgressListener listener, @NonNull File file) {
        if (listener != null) {
            listener.onSuccess(file);
        }
    }

    private static void safeFailure(OnProgressListener listener, @NonNull Exception e, @NonNull File file) {
        if (listener != null) {
            listener.onFailure(e, file);
        }
    }
}
