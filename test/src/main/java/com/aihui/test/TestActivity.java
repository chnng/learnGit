package com.aihui.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button0:
//                Intent intent = new Intent();
//                //hospitalCode:118 patientCode:730 bedCode:1815 deviceId:53777
//                intent.setComponent(new ComponentName("com.aihui.th.push", "com.aihui.project.push.PushService"));
//                intent.putExtra("action", 1);
//                intent.putExtra("hospitalCode", 118);
//                intent.putExtra("patientCode", 730);
//                intent.putExtra("bedCode", 1815);
//                intent.putExtra("deviceId", 53777);
//                startService(intent);
                break;
            case R.id.button1:
//                new Thread(() -> AppPush.push(this)).start();
//                GetuiManager.getPushTransmissionObservable(this, "1000000010208001807", "hello")
//                        .subscribe(new BaseObserver<QueryGetuiPushBean>() {
//                            @Override
//                            public void onNext(QueryGetuiPushBean bean) {
//
//                            }
//                        });
//                showTransmission();
                break;
            case R.id.button2:
//                PushManager.getInstance().stopService(this.getApplicationContext());
//                PushManager.getInstance().bindAlias(this, alias);
//                PushManager.getInstance().unBindAlias(GetuiSdkDemoActivity.this, alias, false);
//                showNotification();
                break;
        }
    }

//
//    /**
//     * {
//     * "action": "pushmessage",
//     * "appkey": "iOtlP70DwKA2ZWjWVAGvj6",
//     * "appid": "lJIA9Tfvwv8WEpa1kKYgf9",
//     * "data": "helloword",
//     * "time": "2018-09-17 20:30:00",
//     * "alias": "1000000010208001807",
//     * "expire": "3600",
//     * "sign": "1000000010208001807",
//     * }
//     */
//    private void showTransmission() {
////        if (isNetworkConnected()) {
////            // !!!!!!注意：以下为个推服务端API1.0接口，仅供测试。不推荐在现网系统使用1.0版服务端接口，请参考最新的个推服务端API接口文档，使用最新的2.0版接口
////            Map<String, Object> param = new HashMap<String, Object>();
////            param.put("action", "pushmessage"); // pushmessage为接口名，注意全部小写
////            /*---以下代码用于设定接口相应参数---*/
////            param.put("appkey", appkey);
////            param.put("appid", appid);
////            // 注：透传内容后面需用来验证接口调用是否成功，假定填写为hello girl~
////            param.put("data", getResources().getString(R.string.push_transmission_data));
////            // 当前请求时间，可选
////            param.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(System.currentTimeMillis())));
////            param.put("clientid", tView.getText().toString()); // 您获取的ClientID
////            param.put("expire", 3600); // 消息超时时间，单位为秒，可选
////            param.put("sign", GetuiSdkHttpPost.makeSign(MASTERSECRET, param));// 生成Sign值，用于鉴权
////
////            GetuiSdkHttpPost.httpPost(param);
////        } else {
////            Toast.makeText(this, R.string.network_invalid, Toast.LENGTH_SHORT).show();
////        }
//        Map<String, String> param = new TreeMap<>();
//        param.put("action", "pushmessage");
//        param.put("appkey", "iOtlP70DwKA2ZWjWVAGvj6");
//        param.put("appid", "lJIA9Tfvwv8WEpa1kKYgf9");
//        param.put("data", "helloword");
//        param.put("time", "2018-09-17 20:30:00");
//        param.put("alias", "1000000010208001807");
//        param.put("clientid", "45aab2906ab0faeed0a79dddd03b5af2");
//        param.put("expire", "3600");
//        param.put("sign", makeSign("s3ffPzkS6h8KGxzYbexHkA", param));
//        String s = RetrofitManager.getGson().toJson(param);
//        LogUtils.e("showTransmission:" + s);
//    }
//
//
//    private void showNotification() {
//        // !!!!!!注意：以下为个推服务端API1.0接口，仅供测试。不推荐在现网系统使用1.0版服务端接口，请参考最新的个推服务端API接口文档，使用最新的2.0版接口
//        Map<String, Object> param = new TreeMap<>();
//        param.put("action", "pushSpecifyMessage"); // pushSpecifyMessage为接口名，注意大小写
//        /*---以下代码用于设定接口相应参数---*/
//        param.put("appkey", "iOtlP70DwKA2ZWjWVAGvj6");
//        param.put("type", 2); // 推送类型： 2为消息
//        param.put("pushTitle", getString(R.string.push_notification_title)); // pushTitle请填写您的应用名称
//
//        // 推送消息类型，有TransmissionMsg、LinkMsg、NotifyMsg三种，此处以LinkMsg举例
//        param.put("pushType", "LinkMsg");
//        param.put("offline", true); // 是否进入离线消息
//        param.put("offlineTime", 72); // 消息离线保留时间
//        param.put("priority", 1); // 推送任务优先级
//
//        List<String> cidList = new ArrayList<>();
//        cidList.add("45aab2906ab0faeed0a79dddd03b5af2"); // 您获取的ClientID
//        param.put("tokenMD5List", cidList);
//        param.put("sign", makeSign("s3ffPzkS6h8KGxzYbexHkA", param));
//        // LinkMsg消息实体
//        Map<String, Object> linkMsg = new HashMap<>();
//        linkMsg.put("linkMsgIcon", "push.png"); // 消息在通知栏的图标
//        linkMsg.put("linkMsgTitle", getResources().getString(R.string.push_notification_msg_title)); // 推送消息的标题
//        linkMsg.put("linkMsgContent", getResources().getString(R.string.push_notification_msg_content)); // 推送消息的内容
//        linkMsg.put("linkMsgUrl", "http://www.igetui.com/"); // 点击通知跳转的目标网页
//        param.put("msg", linkMsg);
//        String s = RetrofitManager.getGson().toJson(param);
//        LogUtils.e("showNotification:" + s);
//    }
//
//    public static String makeSign(String masterSecret, Map<String, ?> params) throws IllegalArgumentException {
//        if (masterSecret == null || params == null) {
//            throw new IllegalArgumentException("masterSecret and params can not be null.");
//        }
//
//        if (!(params instanceof SortedMap)) {
//            params = new TreeMap<>(params);
//        }
//
//        StringBuilder input = new StringBuilder(masterSecret);
//        for (Map.Entry<String, ?> entry : params.entrySet()) {
//            Object value = entry.getValue();
//            if (value instanceof String || value instanceof Integer || value instanceof Long) {
//                input.append(entry.getKey());
//                input.append(entry.getValue());
//            }
//        }
//
//        return getMD5Str(input.toString());
//    }
//
//    private static String getMD5Str(String sourceStr) {
//        byte[] source = sourceStr.getBytes();
//        char hexDigits[] = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
//        java.security.MessageDigest md = null;
//
//        try {
//            md = java.security.MessageDigest.getInstance("MD5");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
//        if (md == null) {
//            return null;
//        }
//
//        md.update(source);
//        byte tmp[] = md.digest();
//        char str[] = new char[16 * 2];
//        int k = 0;
//        for (int i = 0; i < 16; i++) {
//            byte byte0 = tmp[i];
//            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
//            str[k++] = hexDigits[byte0 & 0xf];
//        }
//
//        return new String(str);
//    }
}
