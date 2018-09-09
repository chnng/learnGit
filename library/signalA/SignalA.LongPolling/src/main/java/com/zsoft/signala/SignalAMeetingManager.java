package com.zsoft.signala;

import android.content.Context;
import android.content.OperationApplicationException;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.text.TextUtils;

import com.zsoft.signala.hubs.HubConnection;
import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;
import com.zsoft.signala.hubs.IHubProxy;
import com.zsoft.signala.transport.StateBase;
import com.zsoft.signala.transport.longpolling.ConnectedState;
import com.zsoft.signala.transport.longpolling.LongPollingTransport;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by 胡一鸣 on 2018/6/29.
 * SignalA连接
 */
public class SignalAMeetingManager {
    public static String HUB_URL = "http://10.65.200.11:8190/signalr/hubs";
//    private final static String HUB_URL = "http://192.168.10.190:8190/signalr/hubs";
//    private final static String HUB_URL = "http://192.168.10.190:8071/signalr/hubs";
//    private final static String HUB_URL = "https://192.168.10.190:8072/signalr/hubs";

    private static final long DELAY_RECONNECT = TimeUnit.SECONDS.toMillis(30);
//    private static final long DELAY_RECONNECTION = TimeUnit.SECONDS.toMillis(10);

    private static SignalAMeetingManager mInstance ;

    private int patientId, expertId, meetingId;
    private String channelName, mUserInfoJson;

    private Context mContext;
    private Handler mHandler;
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
    private String mUserId; // 当前登录用户ID
    private int mUserType; // 用户类型 1：患者  2：专家
    //呼叫声音
    private MediaPlayer mediaPlayer;

    public static SignalAMeetingManager getInstance() {
        if (mInstance == null) {
            synchronized (SignalAMeetingManager.class) {
                if (mInstance == null) {
                    mInstance = new SignalAMeetingManager() ;
                }
            }
        }
        return mInstance ;
    }

    private SignalAMeetingManager() {
        mHandler = new Handler();
    }

    public void addConnection(Context context, String userId, int userType,
                              OnSignalAManagerListener listener) {
        mContext = context.getApplicationContext();
        mOnSignalAManagerListener = listener;
        mUserId = userId;
        mUserType = userType;
        beginConnect();
        SignalAManager.log("addConnection userId:" + mUserId + " userType:" + userType);
    }

    private void beginConnect() {
        if (conn != null) {
            // 判断连接状态
//            LogUtil.logE("beginConnect:" + conn.getCurrentState().getClass().getSimpleName());
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
        hub.On("passiveNotificationCall", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                printArgument("passiveNotificationCall", args);
                if (mOnSignalAManagerListener == null) {
                    return;
                }
//                mIsPositive = false;
                int patientId = getIntArgAt(0, args);
                int expertId = getIntArgAt(1, args);
                int userType = getIntArgAt(2, args);
                String channelName = getStringArgAt(3, args);
                meetingId = getIntArgAt(4, args);
                String userInfoJson = getStringArgAt(5, args);
                if (mOnSignalAManagerListener.passiveNotificationCall(patientId, expertId, userType, channelName, userInfoJson)) {
                    startRing(false);
                }
            }
        });
        hub.On("passiveStartMeeting", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                printArgument("passiveStartMeeting", args);
                stopRing();
//                if (mOnSignalAManagerListener != null) {
//                    mOnSignalAManagerListener.passiveStartMeeting(
//                            getIntArgAt(0, args),
//                            getIntArgAt(1, args),
//                            getIntArgAt(2, args),
//                            getStringArgAt(3, args));
//                }
            }
        });
        hub.On("passiveStopMeeting", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                printArgument("passiveStopMeeting", args);
                stopRing();
//                if (mAlertDialog != null && mAlertDialog.isShowing()) {
//                    mAlertDialog.cancel();
//                }
                if (mOnSignalAManagerListener != null) {
                    mOnSignalAManagerListener.passiveStopMeeting(
                            getIntArgAt(0, args),
                            getIntArgAt(1, args),
                            getIntArgAt(2, args),
                            getStringArgAt(3, args));
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
                SignalAManager.log("OnError=" + exception.getMessage());
                reconnect();
            }

            @Override
            public void OnMessage(String message) {
//                LogUtil.logE("message=" + message);
            }

            @Override
            public void OnStateChanged(StateBase oldState, StateBase newState) {
                SignalAManager.log("OnStateChanged=" + oldState.getState() + " -> " + newState.getState());
                switch (newState.getState()) {
                    case Disconnected:
                        SignalAManager.log("未连接！");
                        reconnect();
                        break;
                    case Connecting:
                        SignalAManager.log("正在连接!");
                        break;
                    case Connected:
                        SignalAManager.log("连接成功!");
                        removeReconnectRunnable();
                        addBedConnection();
                        break;
                    case Reconnecting:
                        SignalAManager.log("重新连接！");
                        break;
                    case Disconnecting:
                        SignalAManager.log("断开连接！");
                        reconnect();
                        break;
                    default:
                        break;
                }
            }

            private void reconnect() {
                if (mHandler != null) {
                    removeReconnectRunnable();
                    mHandler.postDelayed(mReconnectRunnable, DELAY_RECONNECT);
                }
            }
        };
    }

    private Runnable mReconnectRunnable = () -> {
        SignalAManager.log("mReconnectRunnable");
        if (TextUtils.isEmpty(mUserId) || mUserType == -1) {
            return;
        }
        if (conn == null) {
            beginConnect();
        } else {
            conn.Start();
        }
        removeReconnectRunnable();
    };

    private void removeReconnectRunnable() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mReconnectRunnable);
        }
    }

    private void addBedConnection() {
        SignalAManager.log("addBedConnection mUserId:" + mUserId + " mUserType:" + mUserType);
        List<Object> args = new ArrayList<>();
        args.add(mUserId);
        args.add(mUserType);
        if (hub == null) {
            return;
        }
//        ToastUtil.toast("addBedConnection" + mUserId);
//        LogUtil.logE("addConnection:" + mUserId);
        //网服务器传参数
        hub.Invoke("addConnection", args, new HubInvokeCallback() {

//            private Runnable reconnectionRunnable = () -> addBedConnection();

            @Override
            public void OnResult(boolean succeeded, String response) {
//                LogUtil.logE("是否成功:" + succeeded + " 返回结果:" + response);
//                if (mHandler != null) {
//                    mHandler.removeCallbacks(reconnectionRunnable);
//                }
            }

            @Override
            public void OnError(Exception ex) {
//                LogUtil.logE("发送信息失败！");
//                if (mHandler != null) {
//                    // 重传参数给服务端
//                    mHandler.removeCallbacks(reconnectionRunnable);
//                    mHandler.postDelayed(reconnectionRunnable, DELAY_RECONNECTION);
//                }
            }
        });
    }

    /// <summary>
    /// 患者/专家发起呼叫
    /// </summary>
    /// <param name="patientId">患者ID</param>
    /// <param name="expertId">专家ID</param>
    /// <param name="userType">用户类型 1：患者  2：专家</param>
    /// <param name="channlename">声网频道ID</param>
//        public void call(string patientId,string expertId, int userType,string channlename)
    public void call(int patientId, int expertId, int userType, String channelName, int meetingId, String userJson) {
        SignalAManager.log("call patientId:" + patientId + " expertId:" + expertId
                + " userType:" + userType + " channelName:" + channelName + " meetingId:" + meetingId + " user:" + userJson);
        if (hub == null) {
            return;
        }
        startRing(true);
        setArguments(patientId, expertId, channelName, userJson);
        this.meetingId = meetingId;
        List<Object> args = new ArrayList<>();
        args.add(patientId);
        args.add(expertId);
        args.add(userType);
        args.add(channelName);
        args.add(meetingId);
        args.add(userJson);
        //网服务器传参数
        hub.Invoke("call", args, new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                SignalAManager.log("OnResult call:" + succeeded + " res:" + response);
                if (mOnSignalAManagerListener != null && !succeeded) {
                    stopRing();
                    mOnSignalAManagerListener.passiveStopMeeting(patientId, expertId, userType, channelName);
                }
            }

            @Override
            public void OnError(Exception ex) {
                SignalAManager.log("OnError call:" + ex.getMessage());
                if (mOnSignalAManagerListener != null) {
                    mOnSignalAManagerListener.passiveStopMeeting(patientId, expertId, userType, channelName);
                }
            }
        });
    }

    private void setArguments(int patientId, int expertId, String channelName, String userJson) {
        this.patientId = patientId;
        this.expertId = expertId;
        this.channelName = channelName;
        this.mUserInfoJson = userJson;
    }

    /// <summary>
    /// 接听
    /// </summary>
    /// <param name="patientId">患者ID</param>
    /// <param name="expertId">专家ID</param>
    /// <param name="userType">用户类型 1：患者  2：专家</param>
    /// <param name="channlename">声网频道ID</param>
//    public void startMeeting(string patientId, string expertId, int userType, string channlename)
    public void startMeeting(int patientId, int expertId, String channelName) {
        SignalAManager.log("startMeeting patientId:" + patientId + " expertId:" + expertId
                + " userType:" + mUserType + " channelName:" + channelName + " meetingId:" + meetingId);
        if (hub == null) {
            return;
        }
        setArguments(patientId, expertId, channelName, mUserInfoJson);
        List<Object> args = new ArrayList<>();
        args.add(patientId);
        args.add(expertId);
//        args.add(mUserType);
        args.add(channelName);
        //网服务器传参数
        hub.Invoke("startMeeting", args, new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                SignalAManager.log("OnResult startMeeting:" + succeeded + " res:" + response);
            }

            @Override
            public void OnError(Exception ex) {
                SignalAManager.log("OnError startMeeting:" + ex.getMessage());
            }
        });
    }

    public void stopMeeting() {
        stopMeeting(patientId, expertId, channelName);
    }

    /// <summary>
    /// 挂断
    /// </summary>
    /// <param name="patientId">患者ID</param>
    /// <param name="expertId">专家ID</param>
    /// <param name="userType">用户类型 1：患者  2：专家</param>
    /// <param name="channlename">声网频道ID</param>
//    public void startMeeting(string patientId, string expertId, int userType, string channlename)
    public void stopMeeting(int patientId, int expertId, String channelName) {
        SignalAManager.log("stopMeeting patientId:" + patientId + " expertId:" + expertId
                + " userType:" + mUserType + " channelName:" + channelName + " meetingId:" + meetingId);
        stopRing();
        if (hub == null) {
            return;
        }
        List<Object> args = new ArrayList<>();
        args.add(patientId);
        args.add(expertId);
        args.add(mUserType);
        args.add(channelName);
        //网服务器传参数
        hub.Invoke("stopMeeting", args, new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                SignalAManager.log("OnResult stopMeeting:" + succeeded + " res:" + response);
            }

            @Override
            public void OnError(Exception ex) {
                SignalAManager.log("OnError stopMeeting:" + ex.getMessage());
            }
        });
    }

    private String getStringArgAt(int position, JSONArray array) {
        if (array != null && array.length() > position) {
            try {
                Object obj = array.get(position);
                if (obj != null) {
                    return obj.toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private int getIntArgAt(int position, JSONArray array) {
        try {
            return Integer.parseInt(getStringArgAt(position, array));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void printArgument(String method, JSONArray args) {
//        LogUtil.logE("method:" + method + " args:" + args.toString());
        SignalAManager.log(method + " " + args);
    }

    public interface OnSignalAManagerListener {
        boolean passiveNotificationCall(int patientId, int expertId, int userType, String channelName, String userInfoJson);

//        void passiveStartMeeting(int patientId, int expertId, int userType, String channelName);

        void passiveStopMeeting(int patientId, int expertId, int userType, String channelName);

        @RawRes
        int getRingResId(boolean isPositive);
    }

    public int getMeetingId() {
        return meetingId;
    }

    private void startRing(boolean isPositive) {
        stopRing();
        if (mOnSignalAManagerListener == null) {
            return;
        }
        mediaPlayer = MediaPlayer.create(mContext, mOnSignalAManagerListener.getRingResId(isPositive));
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void stopRing() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void disconnect() {
        mInstance = null;
        mContext = null;
        mUserId = mUserInfoJson = null;
        patientId = expertId = mUserType = meetingId = -1;
        mHandler.removeCallbacksAndMessages(null);
        if (conn != null) {
            conn.Stop();
        }
        stopRing();
    }
}
