package com.aihui.lib.nurse.util;

import android.content.Context;

import com.aihui.lib.base.app.BaseApplication;
import com.aihui.lib.nurse.R;
import com.aihui.lib.nurse.manager.AccountManager;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by 胡一鸣 on 2018/11/4.
 */
public final class BuglyUtils {

    public static void initBugly(Context context) {
        context = context.getApplicationContext();
//        // 获取当前包名
//        String packageName = context.getPackageName();
//        // 获取当前进程名
//        String processName = ApplicationUtils.getProcessName(android.os.Process.myPid());
//        // 设置是否为上报进程
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
//        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(context, /*注册时申请的APPID*/context.getString(R.string.bugly_app_id), false);
    }

    public static void setCrashReportInfo() {
//        String userId = mHospitalUserBean.hospital_code + " "
//                + getDeptCode() + " "
//                + mHospitalUserBean.name + " "
//                + getLoginUid();
//        LogUtils.e("setCrashReportInfo userId:" + userId);
        CrashReport.setUserId(String.valueOf(AccountManager.getLoginUid()));
        CrashReport.putUserData(BaseApplication.getContext(), "hospitalCode", AccountManager.getLoginAccount().hospital_code);
        CrashReport.putUserData(BaseApplication.getContext(), "deptCode", AccountManager.getDeptCode());
        CrashReport.putUserData(BaseApplication.getContext(), "name", AccountManager.getLoginAccount().name);
//        HandlerUtils.getUIHandler().postDelayed(CrashReport::testJavaCrash,3000);
    }
}
