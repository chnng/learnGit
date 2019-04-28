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
public class SignalAMnManager extends SignalAManager {

    private static volatile SignalAMnManager mInstance;

    public static SignalAMnManager getInstance() {
        if (mInstance == null) {
            synchronized (SignalAMnManager.class) {
                if (mInstance == null) {
                    mInstance = new SignalAMnManager();
                }
            }
        }
        return mInstance;
    }

    private SignalAMnManager() {
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

    private String mConnectParam;
    private Set<OnConnectListener> mListenerSet;

    public void connect(Context context, String hospitalCode, String wardCode, int... codes) {
        if (codes.length == 0) {
            disconnect();
            return;
        }
        mConnectParam = setConnectParam(hospitalCode, wardCode, codes);
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

    private String setConnectParam(String hospitalCode, String wardCode, int[] codes) {
        String param = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("hospital_code", hospitalCode);
            jsonObject.put("ward_code", wardCode);
            jsonObject.put("app_code", SignalAUtils.APP_CODE);
            jsonObject.put("mac", SignalAUtils.MAC);
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("request_codes", jsonArray);
            for (int code : codes) {
                jsonArray.put(code);
            }
            param = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return param;
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

    public static void removeAllOnConnectListener() {
        getInstance().mListenerSet.clear();
    }
}
