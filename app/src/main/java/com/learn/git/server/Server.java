package com.learn.git.server;

import com.learn.git.server.bean.base.BaseResponse;
import com.learn.git.server.bean.request.QueryMissionListBody;
import com.learn.git.server.bean.request.QueryMissionListItemBody;
import com.learn.git.server.bean.request.TokenRequest;
import com.learn.git.server.bean.response.QueryMissionListBean;
import com.learn.git.server.bean.response.QueryMissionListItemBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Server {
    @POST("api/Index/GetSSToken")
    Observable<BaseResponse<String>> getToken(@Body TokenRequest tokenRequest);

    /**
     * 效果评估列表
     * @param queryMissionListBody
     * @return
     */
    @POST("api/BedIdentify/GetByWhere")
    Observable<BaseResponse<List<QueryMissionListBean>>> queryMissionList(@Body QueryMissionListBody queryMissionListBody);

    /**
     * 效果评估列表
     * @param queryMissionListItemBody
     * @return
     */
    @POST("api/PushInfo/GetByWhere")
    Observable<BaseResponse<List<QueryMissionListItemBean>>> queryMissionListItem(@Body QueryMissionListItemBody queryMissionListItemBody);
}
