package com.czt.mp3recorder;

import android.content.Context;
import android.media.AudioManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;

/**
 * Created by hss01248 on 12/29/2015.
 * https://github.com/hss01248/AndroidMP3Recorder
 */
public class Mp3Recorder {
    private int mMaxDuration = (int) TimeUnit.MINUTES.toMillis(5);// 最长录音时间，单位：毫秒
    private String outputFilePath;
    private AudioRecorder audioRecorder = null;
    private int state = State.UNINITIALIZED;
    //private Handler mHandler;
    //private OnMaxDurationReached onMaxDurationReachedListener;
    private Callback mStateListener;
    private AudioManager am;
    private AudioManager.OnAudioFocusChangeListener listener;
    private int stateBeforeFocusChange;

    private class State {
        private static final int UNINITIALIZED = -1;
        private static final int INITIALIZED = 0;
        private static final int PREPARED = 1;
        private static final int RECORDING = 2;
        private static final int PAUSED = 3;
        private static final int STOPPED = 4;
    }

    private static volatile Mp3Recorder mInstance;

    public static Mp3Recorder getInstance(Context context) {
        if (mInstance == null) {
            synchronized (Mp3Recorder.class) {
                if (mInstance == null) {
                    Mp3RecorderUtil.init(context.getApplicationContext(), true);
                    mInstance = new Mp3Recorder();
                }
            }
        }
        return mInstance;
    }

    /*private Runnable r = new Runnable() {

        @Override
        public void run() {
            if(state != State.STOPPED){
                onMaxDurationReachedListener.onMaxDurationReached();
            }
        }
    };*/

    private Mp3Recorder() {
        //mHandler = new Handler(this);
        am = (AudioManager) Mp3RecorderUtil.getContext().getSystemService(Context.AUDIO_SERVICE);
        listener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                    //Log.e("dd"," AUDIOFOCUS_LOSS_TRANSIENT ---------------------");
                    if (state == State.RECORDING) {
                        stateBeforeFocusChange = State.RECORDING;
                    }
                    pause();
                    // Pause playback
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    //Log.e("dd"," AUDIOFOCUS_GAIN ---------------------");
                    if (stateBeforeFocusChange == State.RECORDING) {
                        resume();
                    }

                    // Resume playback
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    //Log.e("dd"," AUDIOFOCUS_LOSS ---------------------");
                    //am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);

                    //instance.stop();
                    // Stop playback
                }

            }
        };
    }

    public int getmMaxDuration() {
        return mMaxDuration;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }


    /**
     * @param maxDurationInSecond 单位为秒
     */
    public Mp3Recorder setMaxDuration(int maxDurationInSecond) {
        this.mMaxDuration = maxDurationInSecond * 1000;
        return this;
    }

    public Mp3Recorder setOutputFile(String path) {
        this.outputFilePath = path;
        return this;
    }

    public Mp3Recorder setCallback(Callback listener) {
        this.mStateListener = listener;
        return this;
    }

    public Mp3Recorder start() {
        return start(null);
    }

    public Mp3Recorder start(DataEncodeThread.OnMp3BufferListener onMp3BufferListener) {
        if (state == State.INITIALIZED ||
                state == State.STOPPED || state == State.PREPARED
                || state == State.UNINITIALIZED) {
            audioRecorder = new AudioRecorder(new File(outputFilePath), this);
            audioRecorder.setOnMp3BufferListener(onMp3BufferListener);
            audioRecorder.setCallback(mStateListener);
            audioRecorder.setMaxDuration(mMaxDuration);
            audioRecorder.start();
            state = State.PREPARED;
            audioRecorder.startRecording();
        } else if (state == State.PAUSED) {
            resume();
        }
        //state = State.RECORDING;
        return this;
    }


    /**
     * 只供AudioRecorder调用,真正的开始
     */
    void onStart() {
        if (state == State.PREPARED) {
            state = State.RECORDING;
            int result = am.requestAudioFocus(listener,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN);
            if (mStateListener != null) {
                mStateListener.onStart();
            }
        }
    }

   /* public void cancel(){
        if(state!=State.UNINITIALIZED || state != State.STOPPED){
           if(audioRecorder!=null){
               audioRecorder.stopRecord();
           }
        }
    }*/

    public Mp3Recorder pause() {
        if (audioRecorder != null && state == State.RECORDING) {
            audioRecorder.pauseRecord();
            state = State.PAUSED;
            if (mStateListener != null) {
                mStateListener.onPause();
            }
        }
        return this;
    }

    public Mp3Recorder resume() {
        if (audioRecorder != null && state == State.PAUSED) {
            audioRecorder.resumeRecord();
            state = State.RECORDING;
            if (mStateListener != null) {
                mStateListener.onResume();
            }
        }
        return this;
    }

    public Mp3Recorder stop() {
        return stop(DataEncodeThread.ACTION_STOP_ONLY);
    }

    public Mp3Recorder stop(int action) {
        if (audioRecorder != null && state == State.RECORDING) {
            audioRecorder.stopRecord(action);
            am.abandonAudioFocus(listener);

            state = State.STOPPED;
            if (mStateListener != null) {
                mStateListener.onStop(action);
            }
        }
        return this;
    }


    public int getRecorderState() {
        return state;
    }

    public void reset() {
        if (null == audioRecorder) {
            // start();
            return;
        }
        if (null != audioRecorder && state != State.STOPPED) {
            stop(DataEncodeThread.ACTION_RESET);
        }
        audioRecorder = null;
        // start();

        if (mStateListener != null) {
            mStateListener.onReset();
        }
    }

    public interface Callback {
        void onStart();

        void onPause();

        void onResume();

        void onStop(int action);

        void onReset();

        /*@Deprecated
        void onRecording(double duration);*/

        /**
         * @param duration 过了多长时间
         * @param volume   这个时间的段的分贝值
         */
        void onRecording(double duration, double volume);

        void onMaxDurationReached();
    }

}
