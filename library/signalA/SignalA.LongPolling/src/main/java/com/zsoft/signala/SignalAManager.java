package com.zsoft.signala;

import android.content.Context;
import android.content.OperationApplicationException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.zsoft.signala.hubs.HubConnection;
import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;
import com.zsoft.signala.hubs.IHubProxy;
import com.zsoft.signala.transport.StateBase;
import com.zsoft.signala.transport.longpolling.ConnectedState;
import com.zsoft.signala.transport.longpolling.LongPollingTransport;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 胡一鸣 on 2018/6/29.
 * SignalA连接
 */
public class SignalAManager {
    public static String HUB_URL = "http://10.65.200.11:8094/signalr/hubs";
//    private final static String HUB_URL = "http://192.168.10.190:8071/signalr/hubs";
//    private final static String HUB_URL = "https://192.168.10.190:8072/signalr/hubs";

//    private static final long DELAY_RECONNECT = TimeUnit.SECONDS.toMillis(30);
//    private static final long DELAY_RECONNECTION = TimeUnit.SECONDS.toMillis(10);

    private Context mContext;
    /**
     * hub代理 panderman 2013-10-25
     */
    private IHubProxy hub = null;

    /**
     * hub链接
     */
    private HubConnection conn;
    private OnSignalAManagerListener mOnSignalAManagerListener;
//    private Handler mHandler;
    private String mChannelName, mPatrolId; // 1000000010208001807
    private boolean isCallVideo;

    public SignalAManager(Context context, String channelName,
                          OnSignalAManagerListener listener) {
        this(context, channelName, null, listener);
    }

    public SignalAManager(Context context, String channelName, String patrolId,
                          OnSignalAManagerListener listener) {
        mContext = context;
        mOnSignalAManagerListener = listener;
        mChannelName = channelName;
        mPatrolId = patrolId;
        isCallVideo = TextUtils.isEmpty(patrolId);
        log("channelName:" + mChannelName + " patrolId:" + patrolId);
    }

    public void beginConnect() {
        if (conn != null) {
            // 判断连接状态
            log("beginConnect:" + conn.getCurrentState().getClass().getSimpleName());
            if (conn.getCurrentState() instanceof ConnectedState) {
                return;
            }
        }
        removeReconnectRunnable();
        initHub();
    }

    private void initHub() {
        if (conn != null) {
            conn.Stop();
        }
        conn = getHubConnection();
        try {
            hub = conn.CreateHubProxy("ChatHub");
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        hub.On("BedJoinChannel", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                printArgument("BedJoinChannel", args);
                if (args != null && mOnSignalAManagerListener != null) {
                    Object arg = args.opt(0);
                    if (arg != null) {
                        mOnSignalAManagerListener.onBedJoinChannel(arg.toString());
                    }
                }
            }
        });
//        // 离开房间又声网房间状态控制
//        hub.On("BedLeaveChannel", new HubOnDataCallback() {
//            @Override
//            public void OnReceived(JSONArray args) {
//                printArgument("BedLeaveChannel", args);
////                mContext.sendBroadcast(new Intent(PatrolRoomService.ACTION_VIDEO_CLOSE));
//            }
//        });
        conn.Start();
    }

    @NonNull
    private HubConnection getHubConnection() {
        return new HubConnection(HUB_URL, mContext, new LongPollingTransport()) {

            @Override
            public void OnError(Exception exception) {
                log("OnError=" + exception.getMessage());
                reconnect();
            }

            @Override
            public void OnMessage(String message) {
                log("message=" + message);
            }

            @Override
            public void OnStateChanged(StateBase oldState, StateBase newState) {
                log("OnStateChanged=" + oldState.getState() + " -> " + newState.getState());
                switch (newState.getState()) {
                    case Disconnected:
                        log("未连接！");
                        reconnect();
                        break;
                    case Connecting:
                        log("正在连接!");
                        break;
                    case Connected:
                        log("连接成功!");
                        removeReconnectRunnable();
                        if (isCallVideo) {
                            addBedConnection();
                        } else {
                            addTHConnection();
                        }
                        break;
                    case Reconnecting:
                        log("重新连接！");
                        break;
                    case Disconnecting:
                        log("断开连接！");
                        reconnect();
                        break;
                    default:
                        break;
                }
            }

            private void reconnect() {
//                if (mHandler != null) {
//                    removeReconnectRunnable();
//                    mHandler.postDelayed(mReconnectRunnable, DELAY_RECONNECT);
//                }
            }
        };
    }

//    private Runnable mReconnectRunnable = () -> conn.Start();

    private void removeReconnectRunnable() {
//        if (mHandler != null) {
//            mHandler.removeCallbacks(mReconnectRunnable);
//        }
    }

    private void addBedConnection() {
        List<String> args = new ArrayList<>();
        args.add(mChannelName);
        log("addBedConnection:" + mChannelName);
        // 往服务器传参数
        hub.Invoke("addBedConnection", args, new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                log("addBedConnection:" + succeeded + " 返回结果:" + response);
                if (mOnSignalAManagerListener != null) {
                    if (succeeded) {
                        mOnSignalAManagerListener.onAddBedConnection();
                    } else {
                        mOnSignalAManagerListener.onError();
                    }
                }
            }

            @Override
            public void OnError(Exception ex) {
                log("addBedConnection发送信息失败！");
            }
        });
    }

    private void addTHConnection() {
        log("addTHConnection:" + mChannelName + " id:" + mPatrolId);
        // 往服务器传参数
        hub.Invoke("addTHConnection", getPatrolArgList(), new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                log("addTHConnection是否成功:" + succeeded + " 返回结果:" + response);
                if (mOnSignalAManagerListener != null) {
                    if (succeeded) {
                        bedJoinChannel();
                    } else {
                        mOnSignalAManagerListener.onError();
                    }
                }
            }

            @Override
            public void OnError(Exception ex) {
                log("addTHConnection发送信息失败！");
                if (mOnSignalAManagerListener != null) {
                    mOnSignalAManagerListener.onError();
                }
            }
        });
    }

    private void bedJoinChannel() {
        log("bedJoinChannel:" + mChannelName + " id:" + mPatrolId);
        // 往服务器传参数
        hub.Invoke("bedJoinChannel", getPatrolArgList(), new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                log("bedJoinChannel是否成功:" + succeeded + " 返回结果:" + response);
                if (mOnSignalAManagerListener != null) {
                    if (succeeded) {
                        mOnSignalAManagerListener.bedJoinChannel();
                    } else {
                        mOnSignalAManagerListener.onError();
                    }
                }
            }

            @Override
            public void OnError(Exception ex) {
                log("bedJoinChannel发送信息失败！");
                if (mOnSignalAManagerListener != null) {
                    mOnSignalAManagerListener.onError();
                }
            }
        });
    }

    public void bedLeaveChannel() {
        log("bedLeaveChannel:" + mChannelName + " id:" + mPatrolId);
        if (TextUtils.isEmpty(mChannelName) || TextUtils.isEmpty(mPatrolId)) {
            if (mOnSignalAManagerListener != null) {
                mOnSignalAManagerListener.bedLeaveChannel();
            }

        } else {
            // 往服务器传参数
            hub.Invoke("bedLeaveChannel", getPatrolArgList(), new HubInvokeCallback() {
                @Override
                public void OnResult(boolean succeeded, String response) {
                    log("bedLeaveChannel是否成功:" + succeeded + " 返回结果:" + response);
                    if (mOnSignalAManagerListener != null) {
                        mOnSignalAManagerListener.bedLeaveChannel();
                    }
                }

                @Override
                public void OnError(Exception ex) {
                    log("bedLeaveChannel发送信息失败！");
                    if (mOnSignalAManagerListener != null) {
                        mOnSignalAManagerListener.bedLeaveChannel();
                    }
                }
            });
        }
    }

    private List<String> mPatrolArgList;

    @NonNull
    private List<String> getPatrolArgList() {
        if (mPatrolArgList == null) {
            mPatrolArgList = new ArrayList<>();
            mPatrolArgList.add(mChannelName);
            mPatrolArgList.add(mPatrolId);
        }
        return mPatrolArgList;
    }


    private void printArgument(String method, JSONArray args) {
        log("method:" + method + " args:" + args.toString());
    }

    public abstract static class OnSignalAManagerListener {
        protected void onAddBedConnection() {
        }
        protected void bedJoinChannel() {
        }
        protected void bedLeaveChannel() {
        }
        protected void onBedJoinChannel(String id) {
        }
        protected void onError() {
        }
    }

    public void disconnect() {
//        if (mHandler != null) {
//            mHandler.removeCallbacksAndMessages(null);
//            mHandler = null;
//        }
        if (conn != null) {
            conn.Stop();
        }
    }

    static void log(Object obj) {
        Log.e(SignalAManager.class.getSimpleName(), "" + obj);
    }
}
