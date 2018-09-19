package com.aihui.lib.base.api.retrofit.server;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by 路传涛 on 2017/6/1.
 */

public interface HttpBaseServer {

    /**
     * 上传文件
     *
     * @param file 文件
     * @param type 类型
     * @return data
     */
    @Multipart
    @POST("file/upload")
    Observable<String> upload(/*@Part("description") RequestBody description, */
            @Part MultipartBody.Part file, @Query("type") String type);
}
