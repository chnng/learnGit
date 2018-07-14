package com.learn.git.server.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by 胡一鸣 on 2018/7/13.
 *  [
 {
 "id": 12997,
 "hospital_code": "1000000",
 "hospital_name": "旻慧企业管理系统",
 "dept_code": "01020800",
 "bednumber": "1813",
 "identify_code": "14393",
 "estate": "0",
 "create_time": "2018-06-27T16:01:18.573",
 "remark": "前台 测试平板 10090",
 "user_id": 1,
 "user_name": "上海爱汇健康科技有限公司",
 "sort": 4
 },
 {
 "id": 16358,
 "hospital_code": "1000000",
 "hospital_name": "旻慧企业管理系统",
 "dept_code": "01020800",
 "bednumber": "1816",
 "identify_code": "27376",
 "estate": "0",
 "create_time": "2018-06-27T16:01:35.95",
 "remark": "冷阳测",
 "user_id": 1,
 "user_name": "上海爱汇健康科技有限公司",
 "sort": 7
 }
 ]
 */
public class QueryMissionListBean implements Parcelable {
    public int id;
    public String hospital_code;
    public String hospital_name;
    public String dept_code;
    public String bednumber;
    public String identify_code;
    public String estate;
    public String create_time;
    public String remark;
    public int user_id;
    public String user_name;
    public int sort;
    public List<QueryMissionListItemBean> itemList;

    protected QueryMissionListBean(Parcel in) {
        id = in.readInt();
        hospital_code = in.readString();
        hospital_name = in.readString();
        dept_code = in.readString();
        bednumber = in.readString();
        identify_code = in.readString();
        estate = in.readString();
        create_time = in.readString();
        remark = in.readString();
        user_id = in.readInt();
        user_name = in.readString();
        sort = in.readInt();
        itemList = in.createTypedArrayList(QueryMissionListItemBean.CREATOR);
    }

    public static final Creator<QueryMissionListBean> CREATOR = new Creator<QueryMissionListBean>() {
        @Override
        public QueryMissionListBean createFromParcel(Parcel in) {
            return new QueryMissionListBean(in);
        }

        @Override
        public QueryMissionListBean[] newArray(int size) {
            return new QueryMissionListBean[size];
        }
    };

    @Override
    public String toString() {
        return "QueryMissionListBean{" +
                "id=" + id +
                ", hospital_code='" + hospital_code + '\'' +
                ", hospital_name='" + hospital_name + '\'' +
                ", dept_code='" + dept_code + '\'' +
                ", bednumber='" + bednumber + '\'' +
                ", identify_code='" + identify_code + '\'' +
                ", estate='" + estate + '\'' +
                ", create_time='" + create_time + '\'' +
                ", remark='" + remark + '\'' +
                ", user_id=" + user_id +
                ", user_name='" + user_name + '\'' +
                ", sort=" + sort +
                ", itemList size=" + (itemList != null ? itemList.size() : 0) +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(hospital_code);
        dest.writeString(hospital_name);
        dest.writeString(dept_code);
        dest.writeString(bednumber);
        dest.writeString(identify_code);
        dest.writeString(estate);
        dest.writeString(create_time);
        dest.writeString(remark);
        dest.writeInt(user_id);
        dest.writeString(user_name);
        dest.writeInt(sort);
        dest.writeTypedList(itemList);
    }
}
