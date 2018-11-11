package com.aihui.lib.base.model.common.response;

/**
 * Created by 路传涛 on 2017/6/2.
 */

public class BaseResponseBean<T> {
    /**
     * MessageType : 0
     * ErrorMessage : null
     * LogMessage : 查询成功
     * Result : T
     */

    public int MessageType;
    public Object ErrorMessage;
    public String LogMessage;
    public T Result;

    @Override
    public String toString() {
        return "BaseResponseBean{" +
                "MessageType=" + MessageType +
                ", ErrorMessage=" + ErrorMessage +
                ", LogMessage='" + LogMessage + '\'' +
                ", Result=" + Result +
                '}';
    }
}
