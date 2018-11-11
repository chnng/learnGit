package com.aihui.lib.base.model.module.mn.main.request;

/**
 * Created by 胡一鸣 on 2018/6/25.
 * 上传自定义项目实体
 */
public class CustomProductBody {

    public int TypeID;
    public String HospitalCode;
    public String DeptCode;
    public String ReminderProject;
    public String FrequencyName = "自定义";
    public int Sort;
    public String Remark;

    @Override
    public String toString() {
        return "CustomProductBody{" +
                "TypeID=" + TypeID +
                ", HospitalCode='" + HospitalCode + '\'' +
                ", DeptCode='" + DeptCode + '\'' +
                ", ReminderProject='" + ReminderProject + '\'' +
                ", FrequencyName='" + FrequencyName + '\'' +
                ", Sort=" + Sort +
//                    ", Remark='" + Remark + '\'' +
                '}';
    }
}
