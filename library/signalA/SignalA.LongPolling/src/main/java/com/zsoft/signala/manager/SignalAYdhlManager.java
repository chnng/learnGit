package com.zsoft.signala.manager;

import android.content.Context;
import android.text.TextUtils;

import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 胡一鸣 on 2019/1/16.
 */
public class SignalAYdhlManager extends SignalAManager {

    private static volatile SignalAYdhlManager mInstance;
    private String mConnectParam;
    private Set<OnConnectListener> mListenerSet;

    public static SignalAYdhlManager getInstance() {
        if (mInstance == null) {
            synchronized (SignalAYdhlManager.class) {
                if (mInstance == null) {
                    mInstance = new SignalAYdhlManager();
                }
            }
        }
        return mInstance;
    }

    private SignalAYdhlManager() {
        super();
        mListenerSet = new HashSet<>();
    }

    @Override
    protected String getUrl() {
        return SignalAUtils.HUB_URL;
    }

    @Override
    protected String getHubName() {
        return "nurseHub";
    }

    public void connect(Context context,
                        String hospitalCode,
                        String deptId,
                        String bedNo,
                        String patientId,
                        String nurseCode,
                        int state) {
        mConnectParam = getConnectParam(hospitalCode, deptId, bedNo, patientId, nurseCode, state);
        if (TextUtils.isEmpty(mConnectParam)) {
            return;
        }
        connect(context, new ConnectionListener() {

            @Override
            void onConnected() {
                super.onConnected();
                addNurseConnection();
            }

            @Override
            void onError(Exception exception) {
                super.onError(exception);
                for (OnConnectListener listener : mListenerSet) {
                    listener.onError(exception);
                }
            }
        });
    }

    @Override
    protected void onRegisterMethod() {
        On("pushMsgByCode", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                SignalAUtils.printArgument("pushMsgByCode", args);
                for (OnConnectListener listener : mListenerSet) {
                    int code = Integer.parseInt(args.opt(0).toString());
                    String msg = args.opt(1).toString();
                    listener.onPushMsg(code, msg);
                }
            }
        });
    }

    // {"hospitalCode":"1000000","deptId":"01020800","bedNo":"1807","patientId":"966616","nurseCode":"0059","state":0}
    private String getConnectParam(String hospitalCode,
                                   String deptId,
                                   String bedNo,
                                   String patientId,
                                   String nurseCode,
                                   int state) {
        String param = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("hospitalCode", hospitalCode);
            jsonObject.put("deptId", deptId);
            jsonObject.put("bedNo", bedNo);
            jsonObject.put("patientId", patientId);
            jsonObject.put("nurseCode", nurseCode);
            jsonObject.put("state", state);
            param = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return param;
    }

    // addNurseConnection(string hospital_code, string ward_code)
    private void addNurseConnection() {
        List<String> args = new ArrayList<>();
        args.add(mConnectParam);
        SignalAUtils.log("addNurseConnection:" + args);
        // 往服务器传参数
        Invoke("addNurseConnection", args, new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                SignalAUtils.log("addNurseConnection:" + succeeded + " 返回结果:" + response);
                if (!succeeded) {
                    for (OnConnectListener listener : mListenerSet) {
                        listener.onError(new IllegalStateException(response));
                    }
                }
            }

            @Override
            public void OnError(Exception ex) {
                SignalAUtils.log("addNurseConnection发送信息失败！");
            }
        });
    }

    public abstract static class OnConnectListener {

        protected abstract void onPushMsg(int code, String msg);

        protected void onError(Exception e) {
            e.printStackTrace();
        }
    }

    public static void addOnConnectListener(OnConnectListener listener) {
        if (listener != null) {
            getInstance().mListenerSet.add(listener);
        }
    }

    public static void removeOnConnectListener(OnConnectListener listener) {
        getInstance().mListenerSet.remove(listener);
    }

    public static void removeAllOnConnectListeners() {
        getInstance().mListenerSet.clear();
    }
}
