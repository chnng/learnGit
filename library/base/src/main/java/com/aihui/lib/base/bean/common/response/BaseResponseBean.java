package com.aihui.lib.base.bean.common.response;

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

    private int MessageType;
    private Object ErrorMessage;
    private String LogMessage;
    private T Result;

    public int getMessageType() {
        return MessageType;
    }

    public void setMessageType(int messageType) {
        MessageType = messageType;
    }

    public Object getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(Object errorMessage) {
        ErrorMessage = errorMessage;
    }

    public String getLogMessage() {
        return LogMessage;
    }

    public void setLogMessage(String logMessage) {
        LogMessage = logMessage;
    }

    public T getResult() {
        return Result;
    }

    public void setResult(T result) {
        Result = result;
    }
}
