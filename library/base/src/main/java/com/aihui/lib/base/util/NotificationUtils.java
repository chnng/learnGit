package com.aihui.lib.base.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.aihui.lib.base.R;
import com.aihui.lib.base.app.BaseApplication;

import androidx.core.app.NotificationCompat;

/**
 * Created by 胡一鸣 on 2018/8/15.
 */
public final class NotificationUtils {
    private static final String CHANNEL_SERVICE = "channel_service";
    private static final String CHANNEL_MESSAGE = "channel_message";
    public static final int ID_UPDATE = 1;
    public static final int ID_PATROL = 2;
    static {
        if (ApplicationUtils.isOreo()) {
            NotificationManager manager = (NotificationManager) BaseApplication.getContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                NotificationChannel channelUpdate = new NotificationChannel(CHANNEL_SERVICE, "服务",
                        NotificationManager.IMPORTANCE_LOW);
                channelUpdate.setShowBadge(false);
                manager.createNotificationChannel(channelUpdate);
                NotificationChannel channelPatrol = new NotificationChannel(CHANNEL_MESSAGE, "消息",
                        NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channelPatrol);
            }
        }
    }

    public static NotificationCompat.Builder getUpdateBuilder(Context context) {
        return new NotificationCompat.Builder(context, CHANNEL_SERVICE)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle(context.getString(R.string.updating))
                .setProgress(100, 0, false);
    }

    public static NotificationCompat.Builder getPatrolBuilder(Context context) {
        return new NotificationCompat.Builder(context, CHANNEL_SERVICE);
    }
}
