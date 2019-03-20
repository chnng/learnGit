package com.aihui.lib.base.model.getui.response;

/**
 * Created by 胡一鸣 on 2018/9/18.
 * {
 "result": "ok",
 "expire_time": "1537254569453",
 "auth_token": "3d4864e8c9da6c6101496747b2a18906fcdb9b1c33f5cf73d56e241bffa75dfb"
 }
 *
 */
public class QueryGetuiAuthBean {
    // ok 鉴权成功，见详情 http://docs.getui.com/getui/server/rest/explain/?id=doc-title-2
    public String result;
    public String expire_time;
    // 权限令牌，推送消息时，需要提供 auth_token有效期默认为1天，过期后无法使用
    public String auth_token;

    @Override
    public String toString() {
        return "QueryGetuiAuthBean{" +
                "result='" + result + '\'' +
                ", expire_time='" + expire_time + '\'' +
                ", auth_token='" + auth_token + '\'' +
                '}';
    }
}
