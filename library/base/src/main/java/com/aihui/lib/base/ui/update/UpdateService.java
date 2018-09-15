package com.aihui.lib.base.ui.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.aihui.lib.base.R;
import com.aihui.lib.base.api.eventbus.EventMessage;
import com.aihui.lib.base.api.eventbus.EventTag;
import com.aihui.lib.base.api.retrofit.download.DownloadManager;
import com.aihui.lib.base.api.retrofit.download.OnProgressListener;
import com.aihui.lib.base.util.FileUtils;
import com.aihui.lib.base.util.LogUtils;
import com.aihui.lib.base.util.NotificationUtils;
import com.aihui.lib.base.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by 胡一鸣 on 2018/8/16.
 */
public class UpdateService extends Service {

    private NotificationCompat.Builder builder;
    private NotificationManager manager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new UpdateBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO
//        if (UpdateUtils.checkLocalFile()) {
//            return super.onStartCommand(intent, flags, startId);
//        } else {
//            UpdateUtils.queryUpdateInfo();
//        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class UpdateBinder extends Binder {
        public void download(String url) {
            LogUtils.e("download:" + url);
            downloadApk(url);
        }

        public void showNotification() {
            LogUtils.e("showNotification");
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            builder = NotificationUtils.getUpdateBuilder(UpdateService.this);
            manager.notify(NotificationUtils.ID_UPDATE, builder.build());

        }
    }

    /**
     * 下载apk文件
     *
     * @param url
     * @param url
     */
    private void downloadApk(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String fileDir = FileUtils.getCacheDirectory(this).getAbsoluteFile()
                + File.separator + FileUtils.DIR_DOWNLOAD;
        String fileName = "mobileNursing.apk";
        DownloadManager.downloadFile(url, fileDir, fileName, new OnProgressListener() {
            @Override
            public void onProgress(int progress) {
                if (builder != null) {
                    builder.setContentText(progress + "%");
                    builder.setProgress(100, progress, false);
                    manager.notify(NotificationUtils.ID_UPDATE, builder.build());
                } else {
                    EventBus.getDefault().post(new EventMessage<>(EventTag.EVENT_UPDATE_DOWNLOAD_PROGRESS, progress));
                }
            }

            @Override
            public void onSuccess(@NonNull File file) {
                String apkPath = file.getAbsolutePath();
//                SharePreferenceUtils.put(UpdateService.this, App.APK_PATH, apkPath);
                if (builder != null) {
                    builder.setContentTitle(getString(R.string.click_install));
                    //设置点击事件
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            UpdateService.this, 0, UpdateUtils.getApkInstallIntent(apkPath), PendingIntent.FLAG_ONE_SHOT);
                    builder.setContentIntent(pendingIntent);
                    //设置点击取消通知栏显示
                    Notification notification = builder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                    //更新通知栏
                    manager.notify(NotificationUtils.ID_UPDATE, notification);
                    builder = null;
                } else {
                    EventBus.getDefault().post(new EventMessage<>(EventTag.EVENT_UPDATE_DOWNLOAD_RESULT, apkPath));
                }
                stopSelf();
            }

            @Override
            public void onFailure(@NonNull Exception e, @NonNull File file) {
//                SharePreferenceUtils.remove(UpdateService.this, App.APK_PATH);
                if (builder != null) {
                    builder.setContentTitle(getString(R.string.update_fail));
                    builder.setContentText(e.getMessage());
                    manager.notify(NotificationUtils.ID_UPDATE, builder.build());
                    builder = null;
                } else {
                    EventBus.getDefault().post(new EventMessage<>(EventTag.EVENT_UPDATE_DOWNLOAD_RESULT, null));
                }
                //更新失败，删除已下载的文件
                //暂不处理断点续传
                FileUtils.delFile(file, false);
                ToastUtils.toast(R.string.update_fail);
            }
        });
    }
}