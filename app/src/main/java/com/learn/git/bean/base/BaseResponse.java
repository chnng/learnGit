package com.learn.git.bean.base;

/**
 * {
 "MessageType": 0,
 "ErrorMessage": null,
 "LogMessage": "查询成功",
 "Result": "123456"
 }
 */
public class BaseResponse<T> {
    public int MessageType;
    public String ErrorMessage;
    public String LogMessage;
    public T Result;

    @Override
    public String toString() {
        return "BaseResponse{" +
                "MessageType=" + MessageType +
                ", ErrorMessage='" + ErrorMessage + '\'' +
                ", LogMessage='" + LogMessage + '\'' +
                ", t=" + Result +
                '}';
    }
}
