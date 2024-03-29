package com.czt.mp3recorder;

import android.media.AudioRecord;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.czt.mp3recorder.util.LameUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DataEncodeThread extends Thread implements AudioRecord.OnRecordPositionUpdateListener {
    public static final int PROCESS_STOP = 1;
    private final File file;
    private StopHandler mHandler;
    private byte[] mMp3Buffer;
    private FileOutputStream mFileOutputStream;

    private CountDownLatch mHandlerInitLatch = new CountDownLatch(1);

    public static final int ACTION_RESET = 1;
    public static final int ACTION_STOP_AND_NEXT = 2;
    public static final int ACTION_STOP_ONLY = 3;

    /**
     * @author buihong_ha
     * @see https://groups.google.com/forum/?fromgroups=#!msg/android-developers/1aPZXZG6kWk/lIYDavGYn5UJ
     */
    static class StopHandler extends Handler {

        WeakReference<DataEncodeThread> encodeThread;

        StopHandler(DataEncodeThread encodeThread) {
            this.encodeThread = new WeakReference<>(encodeThread);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PROCESS_STOP) {
                DataEncodeThread threadRef = encodeThread.get();
                //处理缓冲区中的数据
                while (threadRef.processData() > 0) ;
                // Cancel any event left in the queue
                removeCallbacksAndMessages(null);
                threadRef.flushAndRelease(msg.arg1);
                getLooper().quit();
            }
            super.handleMessage(msg);
        }
    }

    ;

    /**
     * Constructor
     *
     * @param file file
     * @param bufferSize bufferSize
     * @throws FileNotFoundException exception
     */
    DataEncodeThread(File file, int bufferSize) throws FileNotFoundException {
        this.file = file;
        this.mFileOutputStream = new FileOutputStream(this.file);
        mMp3Buffer = new byte[(int) (7200 + (bufferSize * 2 * 1.25))];
    }

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new StopHandler(this);
        mHandlerInitLatch.countDown();
        Looper.loop();
    }

    /**
     * Return the handler attach to this thread
     *
     * @return Handler
     */
    public Handler getHandler() {
        try {
            mHandlerInitLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mHandler;
    }

    @Override
    public void onMarkerReached(AudioRecord recorder) {
        // Do nothing
    }

    @Override
    public void onPeriodicNotification(AudioRecord recorder) {
        processData();
    }

    /**
     * 从缓冲区中读取并处理数据，使用lame编码MP3
     *
     * @return 从缓冲区中读取的数据的长度
     * 缓冲区中没有数据时返回0
     */
    private int processData() {
        if (mTasks.size() > 0) {
            Task task = mTasks.remove(0);
            short[] buffer = task.getData();
            short[] rightData;
            int readSize = task.getReadSize();
            if (task.getRightData() != null && task.getRightData().length > 0) {
                rightData = task.getRightData();
            } else {
                rightData = task.getData();
            }
            int encodedSize = LameUtil.encode(buffer, rightData, readSize, mMp3Buffer);
            if (encodedSize > 0) {
                try {
                    mFileOutputStream.write(mMp3Buffer, 0, encodedSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return readSize;
        }
        return 0;
    }


    /**
     * Flush all data left in lame buffer to file
     * @param action action
     */
    private void flushAndRelease(int action) {
        switch (action) {
            case ACTION_STOP_ONLY:
                //将MP3结尾信息写入buffer中
                final int flushResult = LameUtil.flush(mMp3Buffer);
//                Log.d("flushAndRelease:", flushResult + "    :" + mMp3Buffer.length);
                if (flushResult > 0) {
                    if (mOnMp3BufferListener != null) {
                        mOnMp3BufferListener.onBuffer(mMp3Buffer, flushResult);
                    }
                    try {
                        mFileOutputStream.write(mMp3Buffer, 0, flushResult);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        close();
                    }
                    if (mOnMp3BufferListener != null) {
                        mOnMp3BufferListener.onBuffer(file);
                    }
                }
                break;
            default:
                close();
                break;
        }
    }

    private void close() {
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.close();
                mFileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LameUtil.close();
    }

    private List<Task> mTasks = Collections.synchronizedList(new ArrayList<Task>());

    public void addTask(short[] rawData, int readSize) {
        mTasks.add(new Task(rawData, readSize));
    }

    public void addTask(short[] rawData, short[] rightData, int readSize) {
        mTasks.add(new Task(rawData, rightData, readSize));
    }

    private class Task {
        private short[] rawData;
        private int readSize;
        private short[] rightData;

        Task(short[] rawData, int readSize) {
            this.rawData = rawData.clone();
            this.readSize = readSize;
        }

        Task(short[] leftData, short[] rightData, int readSize) {
            this.rawData = leftData.clone();
            this.rightData = rightData.clone();
            this.readSize = readSize;
        }

        public short[] getData() {
            return rawData;
        }

        short[] getRightData() {
            return rightData;
        }

        int getReadSize() {
            return readSize;
        }
    }

    private OnMp3BufferListener mOnMp3BufferListener;

    public void setOnMp3BufferListener(OnMp3BufferListener onMp3BufferListener) {
        mOnMp3BufferListener = onMp3BufferListener;
    }

    public interface OnMp3BufferListener {

        void onBuffer(byte[] buffer, int length);

        void onBuffer(File file);
    }
}
