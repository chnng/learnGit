package com.aihui.lib.base.model.getui.request;

import androidx.annotation.NonNull;

/**
 * Created by 胡一鸣 on 2018/9/19.
 */
public class PushMessageInfo {
    /**
     * 推送类型,如{@link com.aihui.lib.base.api.getui.GetuiManager#MSG_TYPE_PATROL_START}
     */
    public int type;
    /**
     * 1.推送消息内容标题
     */
    public String title;
    /**
     * 1.视频巡房频道号;
     * 2.推送消息内容
     */
    public String content;
    /**
     * 1.视频巡房唯一标识(用于调试);
     * 2.推送消息类型,如{@link com.aihui.lib.base.cons.HttpConstant#PUSH_TYPE_TEXT}
     */
    public String key;
    /**
     * 1.视频巡房超时时间;
     * 2.定时事件推送消息持续时长(秒)
     * 3.普通推送消息患者签名页url
     */
    public String extra;
    /**
     * 1.推送消息回调地址
     * https://hl.smartsky-tech.com:8094/api/pushinfo/pushcallback?accessKey=1d0b680de5019e95c70ec6a193cbe3ff&PushId=1114505
     */
    public String extra2;

    @NonNull
    @Override
    public String toString() {
        return "PushMessage{" +
                "type=" + type +
                ", title=" + title +
                ", content='" + content + '\'' +
                ", key='" + key + '\'' +
                ", extra='" + extra + '\'' +
                ", extra2='" + extra2 + '\'' +
                '}';
    }
}
