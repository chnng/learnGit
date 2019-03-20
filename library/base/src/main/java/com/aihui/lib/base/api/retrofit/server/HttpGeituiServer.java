package com.aihui.lib.base.api.retrofit.server;

import com.aihui.lib.base.model.getui.request.QueryGetuiAuthBody;
import com.aihui.lib.base.model.getui.request.QueryGetuiPushBody;
import com.aihui.lib.base.model.getui.response.QueryGetuiAuthBean;
import com.aihui.lib.base.model.getui.response.QueryGetuiPushBean;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by 胡一鸣 on 2018/9/18.
 */
public interface HttpGeituiServer {

    /**
     * 个推透传消息
     *
     * @param body body
     * @return data
     */
    @POST("https://restapi.getui.com/v1/{appid}/auth_sign")
    Observable<QueryGetuiAuthBean> authSign(@Path("appid") String appId, @Body QueryGetuiAuthBody body);


    /**
     * 个推透传消息
     *
     * @param body body
     * @return data
     */
    @POST("https://restapi.getui.com/v1/{appid}/push_single")
    Observable<QueryGetuiPushBean> pushSingle(@Path("appid") String appId, @Header("authtoken") String authtoken, @Body QueryGetuiPushBody body);
}
