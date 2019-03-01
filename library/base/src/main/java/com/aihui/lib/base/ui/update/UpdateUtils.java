package com.aihui.lib.base.ui.update;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.aihui.lib.base.api.retrofit.BaseObserver;
import com.aihui.lib.base.api.retrofit.RetrofitManager;
import com.aihui.lib.base.app.BaseApplication;
import com.aihui.lib.base.cons.App;
import com.aihui.lib.base.model.common.request.CheckUpdateBody;
import com.aihui.lib.base.model.common.response.CheckUpdateBean;
import com.aihui.lib.base.util.ApplicationUtils;
import com.aihui.lib.base.util.SharePreferenceUtils;

import androidx.annotation.NonNull;

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
    static boolean checkLocalFile() {
        Context context = BaseApplication.getContext();
        boolean isDownloadSuccess = SharePreferenceUtils.contains(context, SharePreferenceUtils.SP_APK_PATH);
        if (isDownloadSuccess) {
            String apkPath = SharePreferenceUtils.get(context, SharePreferenceUtils.SP_APK_PATH, "");
            if (!TextUtils.isEmpty(apkPath)) {
                int downloadVersionCode = ApplicationUtils.getVersionCode(context, apkPath);
                if (downloadVersionCode != 0) {
                    int nativeVersionCode = ApplicationUtils.getVersionCode(context);
                    if (downloadVersionCode > nativeVersionCode) {
                        context.startActivity(getApkInstallIntent(apkPath));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static void queryUpdateInfo() {
        Context context = BaseApplication.getContext();
        RetrofitManager.newHttpBaseServer().checkUpdate(new CheckUpdateBody(App.APP_CODE))
                .map(RetrofitManager.parseResponse())
                .compose(RetrofitManager.switchScheduler())
                .subscribe(new BaseObserver<CheckUpdateBean>() {
                    @Override
                    public void onNext(@NonNull CheckUpdateBean bean) {
                        int severVersionCode = Integer.parseInt(bean.v_code);
                        int nativeVersionCode = ApplicationUtils.getVersionCode(context);
                        if (severVersionCode > nativeVersionCode) {
                            //更新本地版本
                            context.startActivity(getUpdateIntent()
                                    .putExtra("checkUpdate", bean));
                        }
                    }
                });
    }

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
