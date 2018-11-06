package com.aihui.lib.base.api.db.userinfo;

/**
 * Created by 胡一鸣 on 2018/6/21.
 */
public class DBUserInfoBean {
    public String hospitalCode;
    public String account;
    public String pwd;
    public int uid;
    public int accountType;
    public String icon;
    public String name;

    @Override
    public String toString() {
        return "DBUserInfoBean{" +
                "hospitalCode='" + hospitalCode + '\'' +
                ", account='" + account + '\'' +
                ", pwd='" + pwd + '\'' +
                ", uid=" + uid +
                ", accountType=" + accountType +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
