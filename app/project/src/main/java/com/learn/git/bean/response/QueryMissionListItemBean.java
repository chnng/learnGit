package com.learn.git.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 胡一鸣 on 2018/7/13.
 * {
 "ID": 5495,
 "ServiceID": 1,
 "ServiceName": null,
 "AccessID": 2,
 "AccessName": null,
 "HospitalCode": "39",
 "DeptCode": "6",
 "BedNumber": "27376",
 "PushType": 3,
 "Content": "http://192.168.88.15:8094/uploadfile/source/missionize/20171218184654.pdf",
 "PushTime": "2018-02-05T11:12:30.437",
 "Estate": 1,
 "CreateTime": "2017-11-11T13:40:32.127",
 "UserID": 1002,
 "AccountInfo": null,
 "pyKey": null,
 "Name": "智能天护系统使用说明书",
 "ReadState": 0,
 "EraseStatus": 0,
 "ReadEndTime": "2018-02-05T11:12:28.853",
 "Result": null
 },
 {
 "ID": 5436,
 "ServiceID": 1,
 "ServiceName": null,
 "AccessID": 2,
 "AccessName": null,
 "HospitalCode": "39",
 "DeptCode": "6",
 "BedNumber": "27376",
 "PushType": 3,
 "Content": "https://hl.smartsky-tech.com:8094/uploadfile/source/missionize/20171111214445.html",
 "PushTime": "2018-02-02T10:17:18.353",
 "Estate": 1,
 "CreateTime": "2017-11-11T13:40:32.127",
 "UserID": 1002,
 "AccountInfo": null,
 "pyKey": null,
 "Name": "CZ-胰岛素注射方法",
 "ReadState": 0,
 "EraseStatus": 0,
 "ReadEndTime": "2018-02-02T10:17:14.807",
 "Result": null
 }
 */
public class QueryMissionListItemBean implements Parcelable{
    public int ID;
    public int ServiceID;
    public String ServiceName;
    public String AccessID;
    public String AccessName;
    public String HospitalCode;
    public String DeptCode;
    public String BedNumber;
    public int PushType;
    public String Content;
    public String PushTime;
    public int Estate;
    public String CreateTime;
    public int UserID;
    public String AccountInfo;
    public String pyKey;
    public String Name;
    public int ReadState;
    public int EraseStatus;
    public String ReadEndTime;
    public String Result;

    protected QueryMissionListItemBean(Parcel in) {
        ID = in.readInt();
        ServiceID = in.readInt();
        ServiceName = in.readString();
        AccessID = in.readString();
        AccessName = in.readString();
        HospitalCode = in.readString();
        DeptCode = in.readString();
        BedNumber = in.readString();
        PushType = in.readInt();
        Content = in.readString();
        PushTime = in.readString();
        Estate = in.readInt();
        CreateTime = in.readString();
        UserID = in.readInt();
        AccountInfo = in.readString();
        pyKey = in.readString();
        Name = in.readString();
        ReadState = in.readInt();
        EraseStatus = in.readInt();
        ReadEndTime = in.readString();
        Result = in.readString();
    }

    public static final Creator<QueryMissionListItemBean> CREATOR = new Creator<QueryMissionListItemBean>() {
        @Override
        public QueryMissionListItemBean createFromParcel(Parcel in) {
            return new QueryMissionListItemBean(in);
        }

        @Override
        public QueryMissionListItemBean[] newArray(int size) {
            return new QueryMissionListItemBean[size];
        }
    };

    @Override
    public String toString() {
        return "QueryMissionListItemBean{" +
                "ID=" + ID +
                ", ServiceID=" + ServiceID +
                ", ServiceName='" + ServiceName + '\'' +
                ", AccessID='" + AccessID + '\'' +
                ", AccessName='" + AccessName + '\'' +
                ", HospitalCode='" + HospitalCode + '\'' +
                ", DeptCode='" + DeptCode + '\'' +
                ", BedNumber='" + BedNumber + '\'' +
                ", PushType=" + PushType +
                ", Content='" + Content + '\'' +
                ", PushTime='" + PushTime + '\'' +
                ", Estate=" + Estate +
                ", CreateTime='" + CreateTime + '\'' +
                ", UserID=" + UserID +
                ", AccountInfo='" + AccountInfo + '\'' +
                ", pyKey='" + pyKey + '\'' +
                ", Name='" + Name + '\'' +
                ", ReadState=" + ReadState +
                ", EraseStatus=" + EraseStatus +
                ", ReadEndTime='" + ReadEndTime + '\'' +
                ", Result='" + Result + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeInt(ServiceID);
        dest.writeString(ServiceName);
        dest.writeString(AccessID);
        dest.writeString(AccessName);
        dest.writeString(HospitalCode);
        dest.writeString(DeptCode);
        dest.writeString(BedNumber);
        dest.writeInt(PushType);
        dest.writeString(Content);
        dest.writeString(PushTime);
        dest.writeInt(Estate);
        dest.writeString(CreateTime);
        dest.writeInt(UserID);
        dest.writeString(AccountInfo);
        dest.writeString(pyKey);
        dest.writeString(Name);
        dest.writeInt(ReadState);
        dest.writeInt(EraseStatus);
        dest.writeString(ReadEndTime);
        dest.writeString(Result);
    }
}
