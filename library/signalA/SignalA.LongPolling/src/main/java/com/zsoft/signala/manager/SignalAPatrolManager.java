package com.zsoft.signala.manager;

import android.content.Context;
import android.text.TextUtils;

import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by 胡一鸣 on 2018/6/29.
 * SignalA连接
 */
public class SignalAPatrolManager extends SignalAManager {
//    private final static String HUB_URL = "http://192.168.10.190:8071/signalr/hubs";
//    private final static String HUB_URL = "https://192.168.10.190:8072/signalr/hubs";

//    private static final long DELAY_RECONNECT = TimeUnit.SECONDS.toMillis(30);
//    private static final long DELAY_RECONNECTION = TimeUnit.SECONDS.toMillis(10);

    private Context mContext;
    private OnSignalAManagerListener mOnSignalAManagerListener;
    //    private Handler mHandler;
    private String mChannelName, mPatrolId; // 1000000010208001807
    private boolean isCallVideo;

    public SignalAPatrolManager(Context context, String channelName,
                                OnSignalAManagerListener listener) {
        this(context, channelName, null, listener);
    }

    public SignalAPatrolManager(Context context, String channelName, String patrolId,
                                OnSignalAManagerListener listener) {
        mContext = context;
        mOnSignalAManagerListener = listener;
        mChannelName = channelName;
        mPatrolId = patrolId;
        isCallVideo = TextUtils.isEmpty(patrolId);
        SignalAUtils.log("channelName:" + mChannelName + " patrolId:" + patrolId);
    }

    @Override
    protected String getUrl() {
        return SignalAUtils.HUB_URL;
    }

    @Override
    protected String getHubName() {
        return "ChatHub";
    }

    public void beginConnect() {
        connect(mContext, new ConnectionListener() {
            @Override
            void onConnected() {
                super.onConnected();
                if (isCallVideo) {
                    addBedConnection();
                } else {
                    addTHConnection();
                }
            }
        });
    }

    @Override
    protected void onRegisterMethod() {
        On("BedJoinChannel", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                SignalAUtils.printArgument("BedJoinChannel", args);
                if (args != null && mOnSignalAManagerListener != null) {
                    Object arg = args.opt(0);
                    if (arg != null) {
                        mOnSignalAManagerListener.onBedJoinChannel(arg.toString());
                    }
                }
            }
        });
//        // 离开房间又声网房间状态控制
//        On("BedLeaveChannel", new HubOnDataCallback() {
//            @Override
//            public void OnReceived(JSONArray args) {
//                printArgument("BedLeaveChannel", args);
////                mContext.sendBroadcast(new Intent(PatrolRoomService.ACTION_VIDEO_CLOSE));
//            }
//        });
    }

    private void addBedConnection() {
        List<String> args = new ArrayList<>();
        args.add(mChannelName);
        SignalAUtils.log("addBedConnection:" + mChannelName);
        // 往服务器传参数
        Invoke("addBedConnection", args, new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                SignalAUtils.log("addBedConnection:" + succeeded + " 返回结果:" + response);
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
                SignalAUtils.log("addBedConnection发送信息失败！");
            }
        });
    }

    private void addTHConnection() {
        SignalAUtils.log("addTHConnection:" + mChannelName + " id:" + mPatrolId);
        // 往服务器传参数
        Invoke("addTHConnection", getPatrolArgList(), new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                SignalAUtils.log("addTHConnection是否成功:" + succeeded + " 返回结果:" + response);
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
                SignalAUtils.log("addTHConnection发送信息失败！");
                if (mOnSignalAManagerListener != null) {
                    mOnSignalAManagerListener.onError();
                }
            }
        });
    }

    private void bedJoinChannel() {
        SignalAUtils.log("bedJoinChannel:" + mChannelName + " id:" + mPatrolId);
        // 往服务器传参数
        Invoke("bedJoinChannel", getPatrolArgList(), new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                SignalAUtils.log("bedJoinChannel是否成功:" + succeeded + " 返回结果:" + response);
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
                SignalAUtils.log("bedJoinChannel发送信息失败！");
                if (mOnSignalAManagerListener != null) {
                    mOnSignalAManagerListener.onError();
                }
            }
        });
    }

    public void bedLeaveChannel() {
        SignalAUtils.log("bedLeaveChannel:" + mChannelName + " id:" + mPatrolId);
        if (TextUtils.isEmpty(mChannelName) || TextUtils.isEmpty(mPatrolId)) {
            if (mOnSignalAManagerListener != null) {
                mOnSignalAManagerListener.bedLeaveChannel();
            }

        } else {
            // 往服务器传参数
            Invoke("bedLeaveChannel", getPatrolArgList(), new HubInvokeCallback() {
                @Override
                public void OnResult(boolean succeeded, String response) {
                    SignalAUtils.log("bedLeaveChannel是否成功:" + succeeded + " 返回结果:" + response);
                    if (mOnSignalAManagerListener != null) {
                        mOnSignalAManagerListener.bedLeaveChannel();
                    }
                }

                @Override
                public void OnError(Exception ex) {
                    SignalAUtils.log("bedLeaveChannel发送信息失败！");
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
}
