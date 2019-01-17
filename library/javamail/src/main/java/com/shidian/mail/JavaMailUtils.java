package com.shidian.mail;

import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;

import androidx.annotation.NonNull;

/**
 * Created by Administrator on 2017/4/10.
 */

public class JavaMailUtils {

    //qq
    //    private static final String HOST = "smtp.qq.com";
//    private static final String PORT = "587";
    private static final String HOST = "smtp.ym.163.com";
    private static final String PORT = "25";
//    private static final String FROM_ADD = "269823446@qq.com";
//    private static final String FROM_PSW = "pymreyarwgpncbca";
//    private static final String FROM_ADD = "huyiming@aihuizhongyi.com";
//    private static final String FROM_PSW = "Ah111111";
    private static final String FROM_ADD = "ahims_mail@smartsky-tech.com";
    private static final String FROM_NAME = "上海爱汇健康科技有限公司";
    private static final String FROM_PSW = "minhui@2017";

//    //163
//    private static final String HOST = "smtp.163.com";
//    private static final String PORT = "465"; //或者465  994
//    private static final String FROM_ADD = "teprinciple@163.com";
//    private static final String FROM_PSW = "teprinciple163";
////    private static final String TO_ADD = "2584770373@qq.com";

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor(r -> new Thread(r, "Javamail"));

    public static void send(String address, String subject, String content, File[] files, MailSender.OnSendListener listener) {
        String exceptionMessage = null;
        if (TextUtils.isEmpty(address)) {
            exceptionMessage = "未设置邮箱地址!";
        } else if (TextUtils.isEmpty(subject)) {
            exceptionMessage = "未设置邮件主题!";
        } else if (TextUtils.isEmpty(content)) {
            exceptionMessage = "未设置邮件内容!";
        }
        if (!TextUtils.isEmpty(exceptionMessage)) {
            if (listener != null) {
                listener.onFailure(new MessagingException(exceptionMessage));
            }
            return;
        }
        boolean hasAttachment = false;
        if (files != null) {
            for (File file : files) {
                if (file.exists()) {
                    hasAttachment = true;
                    break;
                }
            }
        }
        boolean finalHasAttachment = hasAttachment;
        EXECUTOR.execute(() -> {
            MailInfo mailInfo = createMail(address, subject, content);
            if (finalHasAttachment) {
                MailSender.sendFileMail(mailInfo, files, listener);
            } else {
                MailSender.sendHtmlMail(mailInfo, listener);
            }
            if (listener != null) {
                listener.onSuccess();
            }
        });
    }

    @NonNull
    private static MailInfo createMail(String address, String subject, String content) {
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost(HOST);
        mailInfo.setMailServerPort(PORT);
        mailInfo.setValidate(true);
        mailInfo.setUserName(FROM_ADD); // 你的邮箱地址
        mailInfo.setPassword(FROM_PSW);// 您的邮箱密码
        String nick = null;
        try {
            nick = MimeUtility.encodeText(FROM_NAME);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mailInfo.setFromAddress(TextUtils.isEmpty(nick) ? FROM_ADD : nick + "<" + FROM_ADD + ">"); // 发送的邮箱
        mailInfo.setToAddress(address); // 发到哪个邮件去
        mailInfo.setSubject(subject); // 邮件主题
        mailInfo.setContent(content); // 邮件文本
        return mailInfo;
    }

}
