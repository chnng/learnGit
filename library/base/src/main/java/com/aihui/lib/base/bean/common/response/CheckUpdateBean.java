package com.aihui.lib.base.bean.common.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 路传涛 on 2017/6/13.
 */

public class CheckUpdateBean implements Parcelable {
    /**
     * id : 1
     * name : 测试医院
     * code : A20170619
     * v_code : 123123
     * v_name : v1.0.0
     * etype : 1
     * down_url  : 1
     * remark  : 1
     * estate  : 1
     * create_time : 2017-05-19T13:20:19.15
     */

    public int id;
    public String name;
    public String code;
    public String v_code;
    public String v_name;
    public String etype;
    public String down_url;
    public String remark;
    public String estate;
    public String create_time;

    private CheckUpdateBean(Parcel in) {
        id = in.readInt();
        name = in.readString();
        code = in.readString();
        v_code = in.readString();
        v_name = in.readString();
        etype = in.readString();
        down_url = in.readString();
        remark = in.readString();
        estate = in.readString();
        create_time = in.readString();
    }

    public static final Creator<CheckUpdateBean> CREATOR = new Creator<CheckUpdateBean>() {
        @Override
        public CheckUpdateBean createFromParcel(Parcel in) {
            return new CheckUpdateBean(in);
        }

        @Override
        public CheckUpdateBean[] newArray(int size) {
            return new CheckUpdateBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(code);
        dest.writeString(v_code);
        dest.writeString(v_name);
        dest.writeString(etype);
        dest.writeString(down_url);
        dest.writeString(remark);
        dest.writeString(estate);
        dest.writeString(create_time);
    }

    @Override
    public String toString() {
        return "CheckUpdateBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", v_code='" + v_code + '\'' +
                ", v_name='" + v_name + '\'' +
                ", etype='" + etype + '\'' +
                ", down_url='" + down_url + '\'' +
                ", remark='" + remark + '\'' +
                ", estate='" + estate + '\'' +
                ", create_time='" + create_time + '\'' +
                '}';
    }
}
