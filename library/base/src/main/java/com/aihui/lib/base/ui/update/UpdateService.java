package com.aihui.lib.base.ui.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.aihui.lib.base.R;
import com.aihui.lib.base.api.eventbus.EventMessage;
import com.aihui.lib.base.api.eventbus.EventTag;
import com.aihui.lib.base.api.retrofit.download.DownloadManager;
import com.aihui.lib.base.api.retrofit.download.OnProgressListener;
import com.aihui.lib.base.util.FileUtils;
import com.aihui.lib.base.util.LogUtils;
import com.aihui.lib.base.util.NotificationUtils;
import com.aihui.lib.base.util.SharePreferenceUtils;
import com.aihui.lib.base.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * Created by 胡一鸣 on 2018/8/16.
 */
public class UpdateService extends Service {

    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new UpdateBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (UpdateUtils.checkLocalFile()) {
            return super.onStartCommand(intent, flags, startId);
        } else {
            UpdateUtils.queryUpdateInfo();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class UpdateBinder extends Binder {
        void download(String url) {
            LogUtils.e("download:" + url);
            downloadApk(url);
        }

        void showNotification() {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationBuilder = NotificationUtils.getUpdateBuilder(UpdateService.this);
            mNotificationManager.notify(NotificationUtils.ID_UPDATE, mNotificationBuilder.build());

        }
    }

    /**
     * 下载apk文件
     *
     * @param url url
     */
    private void downloadApk(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String fileDir = FileUtils.getIndividualCacheDirectory(this, FileUtils.DIR_DOWNLOAD).getAbsolutePath();
        String fileName = "mobileNursing.apk";
        DownloadManager.downloadFile(url, fileDir, fileName, new OnProgressListener() {
            @Override
            public void onProgress(int progress) {
                if (mNotificationBuilder != null) {
                    mNotificationBuilder.setContentText(progress + "%");
                    mNotificationBuilder.setProgress(100, progress, false);
                    mNotificationManager.notify(NotificationUtils.ID_UPDATE, mNotificationBuilder.build());
                } else {
                    EventBus.getDefault().post(new EventMessage(EventTag.EVENT_UPDATE_DOWNLOAD_PROGRESS, progress));
                }
            }

            @Override
            public void onSuccess(@NonNull File file) {
                String apkPath = file.getAbsolutePath();
                SharePreferenceUtils.put(UpdateService.this, SharePreferenceUtils.SP_APK_PATH, apkPath);
                if (mNotificationBuilder != null) {
                    mNotificationBuilder.setContentTitle(getString(R.string.click_install));
                    //设置点击事件
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            UpdateService.this, 0, UpdateUtils.getApkInstallIntent(apkPath), PendingIntent.FLAG_ONE_SHOT);
                    mNotificationBuilder.setContentIntent(pendingIntent);
                    //设置点击取消通知栏显示
                    Notification notification = mNotificationBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                    //更新通知栏
                    mNotificationManager.notify(NotificationUtils.ID_UPDATE, notification);
                    mNotificationBuilder = null;
                } else {
                    EventBus.getDefault().post(new EventMessage(EventTag.EVENT_UPDATE_DOWNLOAD_RESULT, apkPath));
                }
                stopSelf();
            }

            @Override
            public void onFailure(@NonNull Exception e, @NonNull File file) {
                ToastUtils.toast(R.string.update_fail);
                SharePreferenceUtils.remove(UpdateService.this, SharePreferenceUtils.SP_APK_PATH);
                if (mNotificationBuilder != null) {
                    mNotificationBuilder.setContentTitle(getString(R.string.update_fail));
                    mNotificationBuilder.setContentText(e.getMessage());
                    mNotificationManager.notify(NotificationUtils.ID_UPDATE, mNotificationBuilder.build());
                    mNotificationBuilder = null;
                } else {
                    EventBus.getDefault().post(new EventMessage(EventTag.EVENT_UPDATE_DOWNLOAD_RESULT, null));
                }
                //更新失败，删除已下载的文件
                //暂不处理断点续传
                file.delete();
            }
        });
    }
}
