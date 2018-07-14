package com.learn.git.server.bean.request;

/**
 * Created by 胡一鸣 on 2018/7/13.
 */
public class QueryMissionListBody {
    public String HospitalCode = "1000000";
    public String DeptCode = "01020800";
    public int Estate;
    public int ISPush = 1;

    @Override
    public String toString() {
        return "QueryMissionListBody{" +
                "HospitalCode='" + HospitalCode + '\'' +
                ", DeptCode='" + DeptCode + '\'' +
                ", Estate=" + Estate +
                ", ISPush=" + ISPush +
                '}';
    }
}
