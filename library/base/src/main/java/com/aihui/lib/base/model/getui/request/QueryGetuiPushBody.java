package com.aihui.lib.base.model.getui.request;

/**
 * Created by 胡一鸣 on 2018/9/18.
 * http://docs.getui.com/getui/server/rest/explain/
 * {
 "message": {
 "appkey": "iOtlP70DwKA2ZWjWVAGvj6",
 "is_offline": true,
 "offline_expire_time": 10000000,
 "msgtype": "transmission"
 },
 "transmission": {
 "transmission_type": true,
 "transmission_content": "{{timestampHeader}}"
 },
 "alias": "1000000010208001807",
 "requestid": "{{timestampHeader}}"
 }
 */
public class QueryGetuiPushBody {

    public Message message;
    public Transmission transmission;
    // App的用户唯一标识
    public String cid;
    // App的用户标识（别名），别名对应用户是一对多的关系
    public String alias;
    // 请求唯一标识号
    public String requestid;

    public class Message {
        // 注册应用时生成的appkey
        public String appkey;
        // 是否离线推送
        public boolean is_offline;
        // 消息离线存储有效期，单位：ms
        public int offline_expire_time;
        // 消息应用类型，可选项：notification、link、notypopload、transmission
        public String msgtype;
    }

    public class Transmission {
        // 收到消息是否立即启动应用，true为立即启动，false则广播等待启动，默认是否
        public boolean transmission_type;
        // 透传内容
        public String transmission_content;
        // 设定展示开始时间，格式为yyyy-MM-dd HH:mm:ss
        public String duration_begin;
        // 设定展示结束时间，格式为yyyy-MM-dd HH:mm:ss
        public String duration_end;
    }

    @Override
    public String toString() {
        return "QueryGetuiPushBody{" +
                "message=" + message +
                "transmission=" + transmission +
                ", cid='" + cid + '\'' +
                ", alias='" + alias + '\'' +
                ", requestid='" + requestid + '\'' +
                '}';
    }
}
