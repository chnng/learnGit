package com.zsoft.signala.manager;

import android.content.Context;
import android.media.MediaPlayer;

import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RawRes;

/**
 * Created by 胡一鸣 on 2018/6/29.
 * SignalA连接
 */
public class SignalAMeetingManager extends SignalAManager {

    private static volatile SignalAMeetingManager mInstance;

    private int patientId, expertId, meetingId;
    private String channelName, mUserInfoJson;
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
                    mInstance = new SignalAMeetingManager();
                }
            }
        }
        return mInstance;
    }

    private SignalAMeetingManager() {
        super();
    }

    @Override
    protected String getHubName() {
        return "ChatHub";
    }

    @Override
    protected String getUrl() {
        return SignalAUtils.HUB_MEETING_URL;
    }

    public void addConnection(Context context,
                              String userId,
                              int userType,
                              OnSignalAManagerListener listener) {
        mOnSignalAManagerListener = listener;
        mUserId = userId;
        mUserType = userType;
        SignalAUtils.log("addConnection userId:" + mUserId + " userType:" + userType);
        connect(context, new ConnectionListener() {
            @Override
            void onConnected() {
                super.onConnected();
                addBedConnection();
            }
        });
    }

    private void addBedConnection() {
        SignalAUtils.log("addBedConnection mUserId:" + mUserId + " mUserType:" + mUserType);
        List<Object> args = new ArrayList<>();
        args.add(mUserId);
        args.add(mUserType);
        //网服务器传参数
        Invoke("addConnection", args, new HubInvokeCallback() {

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

    @Override
    protected void onRegisterMethod() {
        On("passiveNotificationCall", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                SignalAUtils.printArgument("passiveNotificationCall", args);
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
        On("passiveStartMeeting", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                SignalAUtils.printArgument("passiveStartMeeting", args);
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
        On("passiveStopMeeting", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                SignalAUtils.printArgument("passiveStopMeeting", args);
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
    }


//    private Runnable mReconnectRunnable = () -> {
//        SignalAUtils.log("mReconnectRunnable");
//        if (TextUtils.isEmpty(mUserId) || mUserType == -1) {
//            return;
//        }
//        if (conn == null) {
//            beginConnect();
//        } else {
//            conn.Start();
//        }
//        removeReconnectRunnable();
//    };


    /// <summary>
    /// 患者/专家发起呼叫
    /// </summary>
    /// <param name="patientId">患者ID</param>
    /// <param name="expertId">专家ID</param>
    /// <param name="userType">用户类型 1：患者  2：专家</param>
    /// <param name="channlename">声网频道ID</param>
//        public void call(string patientId,string expertId, int userType,string channlename)
    public void call(int patientId, int expertId, int userType, String channelName, int meetingId, String userJson) {
        SignalAUtils.log("call patientId:" + patientId + " expertId:" + expertId
                + " userType:" + userType + " channelName:" + channelName + " meetingId:" + meetingId + " user:" + userJson);
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
        Invoke("call", args, new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                SignalAUtils.log("OnResult call:" + succeeded + " res:" + response);
                if (mOnSignalAManagerListener != null && !succeeded) {
                    stopRing();
                    mOnSignalAManagerListener.passiveStopMeeting(patientId, expertId, userType, channelName);
                }
            }

            @Override
            public void OnError(Exception ex) {
                SignalAUtils.log("OnError call:" + ex.getMessage());
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
        SignalAUtils.log("startMeeting patientId:" + patientId + " expertId:" + expertId
                + " userType:" + mUserType + " channelName:" + channelName + " meetingId:" + meetingId);
        stopRing();
        setArguments(patientId, expertId, channelName, mUserInfoJson);
        List<Object> args = new ArrayList<>();
        args.add(patientId);
        args.add(expertId);
        args.add(mUserType);
        args.add(channelName);
        //网服务器传参数
        Invoke("startMeeting", args, new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                SignalAUtils.log("OnResult startMeeting:" + succeeded + " res:" + response);
            }

            @Override
            public void OnError(Exception ex) {
                SignalAUtils.log("OnError startMeeting:" + ex.getMessage());
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
        SignalAUtils.log("stopMeeting patientId:" + patientId + " expertId:" + expertId
                + " userType:" + mUserType + " channelName:" + channelName + " meetingId:" + meetingId);
        stopRing();
        List<Object> args = new ArrayList<>();
        args.add(patientId);
        args.add(expertId);
        args.add(mUserType);
        args.add(channelName);
        //网服务器传参数
        Invoke("stopMeeting", args, new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                SignalAUtils.log("OnResult stopMeeting:" + succeeded + " res:" + response);
            }

            @Override
            public void OnError(Exception ex) {
                SignalAUtils.log("OnError stopMeeting:" + ex.getMessage());
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
        mediaPlayer = MediaPlayer.create(getContext(), mOnSignalAManagerListener.getRingResId(isPositive));
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void stopRing() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void disconnect() {
        super.disconnect();
        mInstance = null;
//        mUserId = mUserInfoJson = null;
//        patientId = expertId = mUserType = meetingId = -1;
        stopRing();
    }
}
