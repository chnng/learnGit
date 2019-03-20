package com.aihui.lib.base.model.getui.request;

/**
 * Created by 胡一鸣 on 2018/9/18.
 * {
 "sign": "42ff43507192c71ec1f80e3a6c3ec08370dd06e74eafea31fa9d523c74b87f9c",
 "timestamp": "1537233277000",
 "appkey": "iOtlP70DwKA2ZWjWVAGvj6"
 }
 */
public class QueryGetuiAuthBody {
    // sha256(appkey+timestamp+mastersecret) mastersecret为注册应用时生成
    public String sign;
    // 时间戳，毫秒级别
    public String timestamp;
    // 注册应用生成的appkey
    public String appkey;

    @Override
    public String toString() {
        return "QueryGetuiAuthBody{" +
                "sign='" + sign + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", appkey='" + appkey + '\'' +
                '}';
    }
}
