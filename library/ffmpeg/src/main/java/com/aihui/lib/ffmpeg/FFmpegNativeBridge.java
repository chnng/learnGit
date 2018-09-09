package com.aihui.lib.ffmpeg;

/**
 * FFmpeg native 层的 bridge
 *
 * @author qigengxin
 * @since 2017-06-17 14:45
 */


public class FFmpegNativeBridge {

    static {
        System.loadLibrary("ffmpeg-lib");
    }

    public static int runCommand(String format, Object... args){
        return innerRunCommand(String.format(format, args).split(" "));
    }


    /**
     * 设置是否处于调试状态
     * @param debug
     */
    public synchronized static native void setDebug(boolean debug);

    /**
     * 执行指令
     * @param command
     * @return 命令返回结果
     */
    private synchronized static native int innerRunCommand(String[] command);
}
