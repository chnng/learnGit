package com.aihui.lib.base.api.eventbus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by 胡一鸣 on 2019/3/11.
 */
public final class EventBusUtils {

    public static void post(int key) {
        EventBus.getDefault().post(new EventMessage(key, null));
    }

    public static void post(int key, Object object) {
        EventBus.getDefault().post(new EventMessage(key, object));
    }
}
