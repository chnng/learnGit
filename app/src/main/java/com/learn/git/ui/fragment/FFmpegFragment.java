package com.learn.git.ui.fragment;

import android.view.View;

import com.aihui.lib.base.util.LogUtils;
import com.aihui.lib.ffmpeg.FFmpegNativeBridge;
import com.learn.git.R;
import com.learn.git.ui.common.MyFragment;

import java.io.File;

import butterknife.OnClick;

/**
 * Created by 胡一鸣 on 2018/8/23.
 */
public class FFmpegFragment extends MyFragment {
    @Override
    public int getContentViewId() {
        return R.layout.fragment_test;
    }

    @OnClick({R.id.button_request, R.id.button_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_request:
                FFmpegNativeBridge.setDebug(true);
                break;
            case R.id.button_cancel:
                new Thread(() -> {
                    LogUtils.e("ffmpeg--- start");
                    String output = "/storage/emulated/0/Download/ff_test.mp4";
                    File file = new File(output);
                    file.delete();
                    FFmpegNativeBridge.runCommand("ffmpeg",
                            "-i", "/storage/emulated/0/Download/test.mp4",
                            "-y",
                            "-c:v", "libx264",
                            "-c:a", "aac",
                            "-vf", "scale=480:-2",
                            "-preset", "ultrafast",
                            "-crf", "28",
                            "-b:a", "128k",
                            output);
//                        FFmpegNativeBridge.runCommand("ffmpeg",
//                                "-d",
//                                "-ss",
//                                "00:00:01",
//                                "-t",
//                                "00:00:02",
//                                "-i",
//                                "/storage/emulated/0/Download/test.mp4",
//                                "-vcodec",
//                                "copy",
//                                "-acodec",
//                                "copy",
//                                output);

//                        FFmpegNativeBridge.runCommand("ffmpeg",
//                                "-i", "/storage/emulated/0/Download/test.mp4",
//                                "-vcodec", "copy",
//                                "-acodec", "copy",
//                                output);

                    LogUtils.e("ffmpeg--- end");
                }).start();
                break;
        }
    }

}
