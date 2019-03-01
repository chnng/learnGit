package com.aihui.lib.base.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import androidx.annotation.NonNull;

/**
 * Created by 路传涛 on 2017/6/5.
 */

public final class NetworkUtils {
    /**
     * 判断是否有网络连接
     *
     * @param context context
     * @return
     */
    public static boolean isNetworkConnected(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return true;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo == null || networkInfo.isAvailable();
    }

    /**
     * 判断WIFI网络是否可用
     *
     * @param context context
     * @return
     */
    public static boolean isWifiConnected(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return true;
        }
        NetworkInfo networkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo == null || networkInfo.isAvailable();
    }

    /**
     * 判断MOBILE网络是否可用
     *
     * @param context context
     * @return
     */
    public boolean isMobileConnected(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return true;
        }
        NetworkInfo mMobileNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return mMobileNetworkInfo == null || mMobileNetworkInfo.isAvailable();
    }

    private static String mMacAddress = null;
    public static String macAddress() {
        if (!TextUtils.isEmpty(mMacAddress)) {
            return mMacAddress;
        }
        try {
            // 把当前机器上的访问网络接口的存入 Enumeration集合中
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netWork = interfaces.nextElement();
                // 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
                byte[] by = netWork.getHardwareAddress();
                if (by == null || by.length == 0) {
                    continue;
                }
                StringBuilder builder = new StringBuilder();
                for (byte b : by) {
                    builder.append(String.format("%02X:", b));
                }
                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                String mac = builder.toString();
                Log.d("mac", "interfaceName=" + netWork.getName() + ", mac=" + mac);
                // 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
                if (netWork.getName().equals("wlan0")) {
                    Log.d("mac", " interfaceName =" + netWork.getName() + ", mac=" + mac);
                    mMacAddress = mac;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return mMacAddress;
    }
}
