package com.zsoft.signala.manager;

import android.util.Log;

import com.zsoft.signala.hubs.HubConnection;

import org.json.JSONArray;

/**
 * Created by 胡一鸣 on 2019/1/16.
 */
public final class SignalAUtils {
    static String APP_CODE = "m_client_n";
    static String HUB_URL = "http://10.65.200.12:8190/signalr/hubs";
    static String MAC;

    public static void init(String appCode, String url, String mac) {
        APP_CODE = appCode;
        HUB_URL = url;
        MAC = mac;
    }

    public static String EnsureEndsWith(String text, String end) {
        if (!text.endsWith(end))
            text += end;

        return text;
    }

    static void log(Object obj) {
        Log.e(SignalAManager.class.getSimpleName(), "" + obj);
    }

    static void printArgument(String method, JSONArray args) {
        log("method:" + method + " args:" + args.toString());
    }

    static void disconnect(HubConnection conn) {
        if (conn != null) {
            conn.Stop();
        }
    }
}
