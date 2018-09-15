package com.aihui.lib.base.ui.update;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.aihui.lib.base.app.BaseApplication;

/**
 * Created by 胡一鸣 on 2018/8/16.
 */
public class UpdateUtils {

    public static void checkUpdate() {
        BaseApplication.getContext().startService(new Intent(BaseApplication.getContext(), UpdateService.class));
    }

    /**
     * 检查更新之前，先检查本地是否存在 已下载的最新版本 但是没有安装
     * 如果存在 提示安装
     * 否则 检查更新
     *
     * @return false检查更新  true安装新版本
     */
//    static boolean checkLocalFile() {
//        Context context = BaseApplication.getContext();
//        boolean isDownloadSuccess = SharePreferenceUtils.contains(context, App.APK_PATH);
//        if (isDownloadSuccess) {
//            String apkPath = (String) SharePreferenceUtils.get(context, App.APK_PATH, "");
//            if (!TextUtils.isEmpty(apkPath)) {
//                int downloadVersionCode = ApplicationUtils.getVersionCode(context, apkPath);
//                if (downloadVersionCode != 0) {
//                    int nativeVersionCode = ApplicationUtils.getVersionCode(context);
//                    if (downloadVersionCode > nativeVersionCode) {
//                        context.startActivity(getApkInstallIntent(apkPath));
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    static void queryUpdateInfo() {
//        Context context = BaseApplication.getContext();
//        RetrofitManager.newHttpServer().checkUpdate(new CheckUpdateBody(App.APP_CODE))
//                .map(RetrofitManager.parseResponse())
//                .compose(RetrofitManager.switchScheduler())
//                .subscribe(new BaseObserver<CheckUpdateBean>() {
//                    @Override
//                    public void onNext(CheckUpdateBean checkUpdateBean) {
//                        int severVersionCode = Integer.parseInt(checkUpdateBean.v_code);
//                        int nativeVersionCode = ApplicationUtils.getVersionCode(context);
//                        if (severVersionCode > nativeVersionCode) {
//                            //更新本地版本
//                            context.startActivity(getUpdateIntent()
//                                    .putExtra("checkUpdate", checkUpdateBean));
//                        }
//                    }
//                });
//    }

    static Intent getApkInstallIntent(String apkPath) {
        return getUpdateIntent()
                .putExtra("apkPath", apkPath);
    }

    @NonNull
    private static Intent getUpdateIntent() {
        return new Intent(BaseApplication.getContext(), UpdateActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}