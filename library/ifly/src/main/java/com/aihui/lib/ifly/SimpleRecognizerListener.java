package com.aihui.lib.ifly;

import android.os.Bundle;

import com.aihui.lib.ifly.util.JsonParser;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by 胡一鸣 on 2019/1/9.
 */
public abstract class SimpleRecognizerListener implements RecognizerListener {

    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();

    @Override
    public void onVolumeChanged(int volume, byte[] data) {

    }

    @Override
    public void onBeginOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onResult(RecognizerResult results, boolean isLast) {
        String result = printResult(results);
        onResult(result);
    }

    @Override
    public void onError(SpeechError error) {

    }

    @Override
    public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

    }

    protected abstract void onResult(String result);

    private String printResult(RecognizerResult results) {
        // {"sn":1,"ls":false,"bg":0,"ed":0,"ws":[{"bg":1,"cw":[{"sc":0.0,"w":"喂"}]}]}
        String resultString = results.getResultString();
        IFlyManager.log(resultString);
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(resultString);
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String text = JsonParser.parseIatResult(resultString);
        mIatResults.put(sn, text);

        StringBuilder resultBuilder = new StringBuilder();
        for (String key : mIatResults.keySet()) {
            resultBuilder.append(mIatResults.get(key));
        }
        return resultBuilder.toString();
    }
}
