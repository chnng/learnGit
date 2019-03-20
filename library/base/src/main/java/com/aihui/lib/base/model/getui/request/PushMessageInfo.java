package com.aihui.lib.base.model.getui.request;

/**
 * Created by 胡一鸣 on 2018/9/19.
 */
public class PushMessageInfo {
    public int type;
    public String content;
    public String extra;
    // 唯一标识
    public String key;

    @Override
    public String toString() {
        return "PushMessage{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", extra='" + extra + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
