package com.zsoft.signala.manager;

import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Handler;

import com.zsoft.signala.hubs.HubConnection;
import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;
import com.zsoft.signala.hubs.IHubProxy;
import com.zsoft.signala.transport.StateBase;
import com.zsoft.signala.transport.longpolling.LongPollingTransport;

import org.json.JSONArray;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

/**
 * Created by 胡一鸣 on 2019/2/26.
 */
public abstract class SignalAManager implements IHubProxy {

    private static final long DELAY_RECONNECT = TimeUnit.SECONDS.toMillis(30);
    private Context mContext;
    /**
     * hub代理 panderman 2013-10-25
     */
    private IHubProxy iHubProxy = null;

    /**
     * hub链接
     */
    private HubConnection mHubConnection;
    private ConnectionListener mConnectionListener;
    private Handler mHandler;
    private boolean mIsAllowConnect = true;

    SignalAManager() {
        mHandler = new Handler();
    }

    protected Context getContext() {
        return mContext;
    }

    protected abstract String getUrl();

    protected abstract String getHubName();

    protected abstract void onRegisterMethod();

    void connect(Context context, ConnectionListener connection) {
        mIsAllowConnect = true;
        mContext = context.getApplicationContext();
        mConnectionListener = connection;
        if (mHubConnection != null) {
            mHubConnection.Stop();
        }
        mHubConnection = getHubConnection();
        try {
            iHubProxy = mHubConnection.CreateHubProxy(getHubName());
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        onRegisterMethod();
        mHubConnection.Start();
        mHandler.removeCallbacksAndMessages(null);
    }

    public void disconnect() {
        mIsAllowConnect = false;
        SignalAUtils.disconnect(mHubConnection);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void On(String eventName, HubOnDataCallback callback) {
        if (iHubProxy != null) {
            iHubProxy.On(eventName, callback);
        }
    }

    @Override
    public void Invoke(String method, Collection<?> args, HubInvokeCallback callback) {
        if (iHubProxy != null) {
            iHubProxy.Invoke(method, args, callback);
        }
    }

    @Override
    public void Invoke(String method, JSONArray args, HubInvokeCallback callback) {
        if (iHubProxy != null) {
            iHubProxy.Invoke(method, args, callback);
        }
    }

    @NonNull
    private HubConnection getHubConnection() {
        return new HubConnection(getUrl(), mContext, new LongPollingTransport()) {

            @Override
            public void OnError(Exception e) {
                SignalAUtils.log("OnError=" + e.getMessage());
                if (mConnectionListener != null) {
                    mConnectionListener.onError(e);
                }
                reconnect();
            }

            @Override
            public void OnMessage(String message) {
                SignalAUtils.log("message=" + message);
                if (mConnectionListener != null) {
                    mConnectionListener.onMessage(message);
                }
            }

            @Override
            public void OnStateChanged(StateBase oldState, StateBase newState) {
                SignalAUtils.log("OnStateChanged=" + oldState.getState() + " -> " + newState.getState());
                if (mConnectionListener != null) {
                    mConnectionListener.onStateChanged(oldState, newState);
                }
                switch (newState.getState()) {
                    case Disconnected:
                        SignalAUtils.log("未连接！");
                        reconnect();
                        break;
                    case Connecting:
                        SignalAUtils.log("正在连接!");
                        break;
                    case Connected:
                        SignalAUtils.log("连接成功!");
                        if (mConnectionListener != null) {
                            mConnectionListener.onConnected();
                        }
                        break;
                    case Reconnecting:
                        SignalAUtils.log("重新连接！");
                        break;
                    case Disconnecting:
                        SignalAUtils.log("断开连接！");
                        break;
                    default:
                        break;
                }
            }

            private void reconnect() {
                if (mIsAllowConnect) {
                    removeReconnectRunnable();
                    mHandler.postDelayed(mReconnectRunnable, DELAY_RECONNECT);
                }
            }

            private Runnable mReconnectRunnable = () -> connect(mContext, mConnectionListener);

            private void removeReconnectRunnable() {
                if (mHandler != null) {
                    mHandler.removeCallbacks(mReconnectRunnable);
                }
            }
        };
    }
}
