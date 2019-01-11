package com.aihui.lib.ifly;

import android.os.Bundle;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by 胡一鸣 on 2019/1/9.
 */
public abstract class SimpleSynthesizerListener implements SynthesizerListener {
    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onBufferProgress(int percent, int beginPos, int endPos, String info) {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int percent, int beginPos, int endPos) {

    }

    @Override
    public void onCompleted(SpeechError error) {

    }

    @Override
    public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

    }
}
