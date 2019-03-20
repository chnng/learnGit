package com.aihui.lib.base.api.getui;

import android.content.ComponentCallbacks;
import android.content.Context;

import com.aihui.lib.base.R;
import com.aihui.lib.base.api.retrofit.RetrofitManager;
import com.aihui.lib.base.app.BaseApplication;
import com.aihui.lib.base.model.getui.request.QueryGetuiAuthBody;
import com.aihui.lib.base.model.getui.request.QueryGetuiPushBody;
import com.aihui.lib.base.model.getui.response.QueryGetuiAuthBean;
import com.aihui.lib.base.model.getui.response.QueryGetuiPushBean;
import com.aihui.lib.base.util.NumberUtils;
import com.aihui.lib.base.util.StringUtils;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by 胡一鸣 on 2018/9/17.
 */
public final class GetuiManager {

    // 视频巡房开启
    public static final int MSG_TYPE_PATROL_START = 1;
    // 视频巡房关闭
    public static final int MSG_TYPE_PATROL_CLOSE = 2;
    // 床旁护理开启
    public static final int MSG_TYPE_YDHL_START = 3;
    // 床旁护理关闭
    public static final int MSG_TYPE_YDHL_CLOSE = 4;
    private static QueryGetuiAuthBean mGetuiAuthBean;

    /**
     * 推送个推透传
     *
     * @param callbacks           callbacks
     * @param alias               用户别名
     * @param transmissionContent 透传内容
     * @return Observable
     */
    public static Observable<QueryGetuiPushBean> getPushTransmissionObservable(ComponentCallbacks callbacks, @NonNull String alias, String transmissionContent) {
        Context context = BaseApplication.getContext();
        String appId = context.getString(R.string.getui_app_id);
        String appKey = context.getString(R.string.getui_app_key);
        String timestamp = String.valueOf(System.currentTimeMillis());
        return Observable.just(context)
                .flatMap((Function<Context, ObservableSource<QueryGetuiAuthBean>>) ctx -> {
                    if (mGetuiAuthBean != null && NumberUtils.str2Long(mGetuiAuthBean.expire_time) > System.currentTimeMillis()) {
                        return Observable.just(mGetuiAuthBean);
                    } else {
                        QueryGetuiAuthBody body = new QueryGetuiAuthBody();
                        body.appkey = appKey;
                        body.timestamp = timestamp;
                        // sha256(appkey+timestamp+mastersecret) mastersecret为注册应用时生成
                        String masterSecret = ctx.getString(R.string.getui_master_secret);
                        body.sign = StringUtils.sha256(body.appkey + body.timestamp + masterSecret);
                        return RetrofitManager.newGeituiServer().authSign(appId, body)
                                .compose(RetrofitManager.switchSchedulerWith(callbacks))
                                .map(bean -> mGetuiAuthBean = bean);
                    }
                })
                .flatMap((Function<QueryGetuiAuthBean, ObservableSource<QueryGetuiPushBean>>) bean -> {
                    QueryGetuiPushBody pushBody = new QueryGetuiPushBody();
                    pushBody.alias = alias;
                    pushBody.requestid = "trans" + timestamp.substring(0, timestamp.length() - 3);
                    pushBody.message = pushBody.new Message();
                    pushBody.message.appkey = appKey;
                    pushBody.message.is_offline = false;
                    pushBody.message.msgtype = "transmission";
                    pushBody.transmission = pushBody.new Transmission();
                    pushBody.transmission.transmission_type = false;
                    pushBody.transmission.transmission_content = transmissionContent;
                    return RetrofitManager.newGeituiServer().pushSingle(appId, bean.auth_token, pushBody)
                            .compose(RetrofitManager.switchSchedulerWith(callbacks));
                });
    }

    public static String getMessagePatrolStart(String msg) {
        return getMessageByType(MSG_TYPE_PATROL_START, msg);
    }

    public static String getMessagePatrolClose() {
        return getMessageByType(MSG_TYPE_PATROL_CLOSE, null);
    }

    private static String getMessageByType(int type, String content) {
        return "{"
                + "\"type\":" + type
                + ",\"content\":\"" + content + "\""
                + "}";
    }
}
