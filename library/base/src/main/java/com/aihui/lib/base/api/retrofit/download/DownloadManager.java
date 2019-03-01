package com.aihui.lib.base.api.retrofit.download;

import android.accounts.NetworkErrorException;

import com.aihui.lib.base.api.retrofit.RetrofitManager;
import com.aihui.lib.base.api.retrofit.server.HttpBaseServer;
import com.aihui.lib.base.util.FileUtils;
import com.aihui.lib.base.util.LogUtils;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
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
        downloadFile(url, null, fileDir, fileName, listener);
    }


    /**
     * 通过URL下载文件
     *
     * @param url      下载文件的URL
     * @param headers  请求头
     * @param fileDir  下载的文件存储目录
     * @param fileName 下载的文件名称
     * @param listener 下载结果监听器
     */
    public static void downloadFile(@NonNull String url,
                                    @Nullable Headers headers,
                                    @NonNull String fileDir,
                                    @NonNull String fileName,
                                    OnProgressListener listener) {
        downloadFile(url, headers, new File(fileDir, fileName), listener);
    }

    /**
     * 通过URL下载文件
     *
     * @param url      下载文件的URL
     * @param headers  请求头
     * @param file     下载的文件
     * @param listener 下载结果监听器
     */
    public static void downloadFile(@NonNull String url,
                                    @Nullable Headers headers,
                                    @NonNull File file,
                                    OnProgressListener listener) {
        //注册下载进度 监听器
        ProgressManager.getInstance().addResponseListener(url, new me.jessyan.progressmanager.ProgressListener() {
            int temp = 0;

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
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null) {
            builder.headers(headers);
        }
        Request request = builder.build();
        RetrofitManager.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                safeFailure(listener, e, file);
                LogUtils.i(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (!response.isSuccessful()) {
                    switch (response.code()) {
                        case 304:
                            break;
                        case 404:
                            safeFailure(listener, new NetworkErrorException("file not found!"), file);
                            break;
                        default:
                            safeFailure(listener, new NetworkErrorException("response failed!"), file);
                            break;
                    }
                    return;
                }
                ResponseBody body = response.body();
                if (listener != null) {
                    Headers headers = response.headers();
                    listener.onHeaders(headers);
                }
                if (body == null) {
                    safeFailure(listener, new NullPointerException("response body is null!"), file);
                    return;
                }
                try (BufferedSink sink = Okio.buffer(Okio.sink(FileUtils.createFile(file)))) {
                    //写入SD卡
                    sink.writeAll(body.source());
                    sink.flush();
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
     * {@link HttpBaseServer#upload(MultipartBody.Part, String)}
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
        return RetrofitManager.newHttpBaseServer().upload(body, uploadType);
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
