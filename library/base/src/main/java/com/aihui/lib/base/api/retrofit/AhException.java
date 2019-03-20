package com.aihui.lib.base.api.retrofit;

import android.util.Log;

/**
 * Created by 胡一鸣 on 2019/1/28.
 */
public class AhException extends Exception {

    // 错误码
    private int code;
    // 异常类型
    private int type;

    public AhException(int code, String message) {
        this(code, message, Log.ERROR);
    }

    public AhException(int code, String message, int type) {
        super(message);
        this.code = code;
        this.type = type;
    }

    @Override
    public String getMessage() {
        return "[code:" + code + "]," + "[type:" + type + "],[msg:" + super.getMessage() + "]";
    }

    /**
     * @return 2019/1/28 用于标记是否需要上传日志，true上传日志，false则是其他用途的exception
     */
    public boolean isError() {
        return type == Log.ERROR;
    }
}
