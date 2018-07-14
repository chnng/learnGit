package com.learn.git.server.bean.request;


/**
 * Created by 胡一鸣 on 2018/7/13.
 */
public class QueryMissionListItemBody {
    public String UserID = "1002";
//    public int ReadState;
    public int EraseStatus;
    public int PageSize = 10;
    public int PageNumber = 1;
    public String BedNumber;

    @Override
    public String toString() {
        return "QueryMissionListItemBody{" +
                "UserID='" + UserID + '\'' +
//                ", ReadState=" + ReadState +
                ", EraseStatus=" + EraseStatus +
                ", PageSize=" + PageSize +
                ", PageNumber=" + PageNumber +
                ", BedNumber='" + BedNumber + '\'' +
                '}';
    }
}
