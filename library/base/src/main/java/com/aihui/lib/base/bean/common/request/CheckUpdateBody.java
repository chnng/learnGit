package com.aihui.lib.base.bean.common.request;

/**
 * Created by 路传涛 on 2017/7/12.
 */

public class CheckUpdateBody {
    public String code;

    public CheckUpdateBody(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CheckUpdateBody{" +
                "code='" + code + '\'' +
                '}';
    }
}
