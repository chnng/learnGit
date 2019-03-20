package com.aihui.lib.base.model.getui.response;

/**
 * Created by 胡一鸣 on 2018/9/18.
 * {
 "result": "ok",
 "taskid": "RASS_0918_9af79d5879bd480a5f19390951c5affc",
 "status": "successed_online"
 }
 */
public class QueryGetuiPushBean {
    // ok 鉴权成功，见详情 http://docs.getui.com/getui/server/rest/explain/?id=doc-title-2
    public String result;
    // 任务编号，用于标识保存于服务器上的消息公共体
    public String taskid;
    // 用户状态 online在线 offline离线
    public String status;

    @Override
    public String toString() {
        return "QueryGetuiPushBean{" +
                "result='" + result + '\'' +
                ", taskid='" + taskid + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
