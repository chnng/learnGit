package com.aihui.lib.getui;

import android.content.Context;

import com.aihui.lib.base.util.LogUtils;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.message.BindAliasCmdMessage;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.igexin.sdk.message.UnBindAliasCmdMessage;

/**
 * Created by 胡一鸣 on 2018/9/17.
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public abstract class GetuiIntentService extends GTIntentService {

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        LogUtils.e("onReceiveServicePid -> " + "pid = " + pid);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        LogUtils.e("onReceiveMessageData -> " + GetuiUtils.transmitMessageToString(msg));
//        String appid = msg.getAppid();
//        String taskid = msg.getTaskId();
//        String messageid = msg.getMessageId();
//        byte[] payload = msg.getPayload();
//        String pkg = msg.getPkgName();
//        String cid = msg.getClientId();
//
//        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
//        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
//        LogUtils.e("call sendFeedbackMessage = " + (result ? "success" : "failed"));
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        LogUtils.e("onReceiveClientId -> " + "clientid = " + clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        LogUtils.e("onReceiveOnlineState -> " + "online = " + online);
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        int action = cmdMessage.getAction();
        LogUtils.e("onReceiveCommandResult -> " + "action = " + action);
        int text;
        if (action == PushConsts.SET_TAG_RESULT) {
            text = GetuiUtils.setTagResult((SetTagCmdMessage) cmdMessage);
            LogUtils.e("onReceiveCommandResult -> setTag result, text = " + getResources().getString(text));
        } else if(action == PushConsts.BIND_ALIAS_RESULT) {
            text = GetuiUtils.bindAliasResult((BindAliasCmdMessage) cmdMessage);
            LogUtils.e("onReceiveCommandResult -> bindAlias result, text = " + getResources().getString(text));
        } else if (action == PushConsts.UNBIND_ALIAS_RESULT) {
            text = GetuiUtils.unbindAliasResult((UnBindAliasCmdMessage) cmdMessage);
            LogUtils.e("onReceiveCommandResult -> unbindAlias result, text = " + getResources().getString(text));
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            GetuiUtils.feedbackResult((FeedbackCmdMessage) cmdMessage);
        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
        LogUtils.e("onReceiveOnlineState -> " + GetuiUtils.notificationMessageToString(msg));
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
        LogUtils.e("onNotificationMessageClicked -> " + GetuiUtils.notificationMessageToString(msg));
    }
}
