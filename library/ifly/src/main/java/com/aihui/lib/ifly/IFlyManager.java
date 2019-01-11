package com.aihui.lib.ifly;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by 胡一鸣 on 2018/9/20.
 * 2019/1/9 1.1134
 */
public final class IFlyManager {
    public static void init(Context context, boolean debug) {
        mIsDebug = debug;
        setContext(context);
        // 应用程序入口处调用，避免手机内存过小，杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 如在Application中调用初始化，需要在Mainifest中注册该Applicaiton
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用半角“,”分隔。
        // 设置你申请的应用appid,请勿在'='与appid之间添加空格及空转义符

        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误

        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=" + context.getString(R.string.app_id));
        // 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
        Setting.setShowLog(debug);
    }

    static void log(String msg) {
        if (!mIsDebug) {
            return;
        }
        Log.i("IFlyManager", msg);
    }

    private static volatile IFlyManager mInstance;
    private static boolean mIsDebug;
    private Context mContext;
    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 语音听写对象
    private SpeechRecognizer mIat;

    private static IFlyManager getInstance() {
        if (mInstance == null) {
            synchronized (IFlyManager.class) {
                if (mInstance == null) {
                    mInstance = new IFlyManager();
                }
            }
        }
        return mInstance;
    }

    private static void setContext(Context context) {
        getInstance().mContext = context;
    }

    public static void destroy() {
        stopSpeaking();
        stopListening(true);
    }

    // TODO speaking

    public static void startSpeaking(String text) {
        startSpeaking(text, null);
    }

    public static void startSpeaking(String text, SynthesizerListener listener) {
        IFlyManager manager = getInstance();
        SpeechSynthesizer tts = manager.mTts;
        if (tts == null) {
            manager.mTts = SpeechSynthesizer.createSynthesizer(manager.mContext, code -> {
                if (code != ErrorCode.SUCCESS) {
                    // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
                    log("初始化失败,错误码：" + code);
                } else {
                    // 初始化成功，之后可以调用startSpeaking方法
                    // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                    // 正确的做法是将onCreate中的startSpeaking调用移至这里
                    internalStartSpeaking(text, listener);
                }
            });
        } else {
            internalStartSpeaking(text, listener);
        }
    }

    private static void internalStartSpeaking(String text, SynthesizerListener listener) {
        SpeechSynthesizer tts = getInstance().mTts;
        if (tts == null) {
            return;
        }
        setSpeakingParam(tts);
        /**
         * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
         * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
         */
        int code = tts.startSpeaking(text, listener);
        if (code != ErrorCode.SUCCESS) {
            log("语音合成失败,错误码: " + code);
        }
    }

    /**
     * 参数设置
     *
     * @return
     */
    private static void setSpeakingParam(SpeechSynthesizer tts) {
        // 清空参数
        tts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        tts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        tts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
        // 设置在线合成发音人
        tts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        //设置合成语速
        tts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        tts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        tts.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        tts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        tts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        if (getInstance().mContext.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED) {
            // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
            tts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
            tts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.pcm");
        }
    }

    public static void stopSpeaking() {
        SpeechSynthesizer tts = getInstance().mTts;
        if (tts != null) {
            tts.stopSpeaking();
            // 退出时释放连接
            tts.destroy();
            getInstance().mTts = null;
        }
    }

    // TODO listening

    public static void startListening(RecognizerListener listener) {
        IFlyManager manager = getInstance();
        if (manager.mIat == null) {
            manager.mIat = SpeechRecognizer.createRecognizer(manager.mContext, code -> {
                if (code != ErrorCode.SUCCESS) {
                    log("初始化失败，错误码：" + code);
                } else {
                    internalStartListening(listener);
                }
            });
        } else {
            internalStartListening(listener);
        }
    }

    private static void internalStartListening(RecognizerListener listener) {
        SpeechRecognizer iat = getInstance().mIat;
        if (iat == null) {
            return;
        }
        setListeningParam(iat);
        int code = iat.startListening(listener);
        if (code != ErrorCode.SUCCESS) {
            log("听写失败,错误码：" + code);
        }
    }

    /**
     * 参数设置
     *
     * @return
     */
    public static void setListeningParam(SpeechRecognizer iat) {
        // 清空参数
        iat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        iat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        iat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语言
        iat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        iat.setParameter(SpeechConstant.ACCENT, "mandarin");
        //此处用于设置dialog中不显示错误码信息
        //iat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        iat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        iat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        iat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        iat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        iat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    public static void stopListening() {
        stopListening(false);
    }

    private static void stopListening(boolean destroy) {
        SpeechRecognizer iat = getInstance().mIat;
        if (null != iat) {
            if (destroy) {
                // 退出时释放连接
                iat.cancel();
                iat.destroy();
                getInstance().mIat = null;
            } else {
                iat.stopListening();
            }
        }
    }
}
