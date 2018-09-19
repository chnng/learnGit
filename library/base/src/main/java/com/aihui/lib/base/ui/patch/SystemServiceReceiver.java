package com.aihui.lib.base.ui.patch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.aihui.lib.base.util.LogUtils;

/**
 * Created by 胡一鸣 on 2018/6/30.
 * 网络状况广播
 */
public class SystemServiceReceiver extends BroadcastReceiver {
    private Context mContext;
    private ConnectivityManager mConnectivityManager;
    private OnSystemServiceListener mOnSystemServiceListener;

    public SystemServiceReceiver(Context context, OnSystemServiceListener onSystemServiceListener) {
        mContext = context;
        mOnSystemServiceListener = onSystemServiceListener;
        context.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mOnSystemServiceListener == null) {
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case ConnectivityManager.CONNECTIVITY_ACTION:
                if (mConnectivityManager == null) {
                    mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                }
                if (mConnectivityManager == null) {
                    break;
                }
                NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
                LogUtils.e("CONNECTIVITY_ACTION:" + networkInfo);
                if (networkInfo != null) {
                    NetworkInfo.State state = networkInfo.getState();
                    if (state == NetworkInfo.State.CONNECTED) {
                        mOnSystemServiceListener.onNetworkInfoState(true);
                    } else if (state == NetworkInfo.State.DISCONNECTED) {
                        mOnSystemServiceListener.onNetworkInfoState(false);
                    }
                }
                break;
            default:
                break;
        }
    }

    public void unregister() {
        mContext.unregisterReceiver(this);
    }

    public abstract static class OnSystemServiceListener {
        protected void onNetworkInfoState(boolean isConnected) {

        }
    }
}
