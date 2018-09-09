//
// Created by 戚耿鑫 on 2017/6/17.
//

#include <jni.h>
#include "ffmpeg.h"
#include "libavcodec/jni.h"
#include "ffmpeg_lib.h"
#include "ffmpeg_thread.h"

// load JNI
jint JNI_OnLoad(JavaVM* vm, void* reserved){
    av_jni_set_java_vm(vm, NULL);
    return JNI_VERSION_1_6;
}

// 是否启用 debug 默认不启用
jboolean FFMPEG_ANDROID_DEBUG = 0;

static void ffmpeg_android_log_callback(void *ptr, int level, const char *fmt, va_list vl){
    if (FFMPEG_ANDROID_DEBUG){
        switch(level) {
            case AV_LOG_DEBUG:
                ALOGD(fmt, vl);
                break;
            case AV_LOG_VERBOSE:
                ALOGV(fmt, vl);
                break;
            case AV_LOG_INFO:
                ALOGI(fmt, vl);
                break;
            case AV_LOG_WARNING:
                ALOGW(fmt, vl);
                break;
            case AV_LOG_ERROR:
                ALOGE(fmt, vl);
                break;
        }
    }
}

JNIEXPORT jint JNICALL
Java_com_aihui_lib_ffmpeg_FFmpegNativeBridge_innerRunCommand(JNIEnv *env, jclass type,
                                                      jobjectArray command) {
    int argc = (*env)->GetArrayLength(env, command);
    char *argv[argc];
    jstring jsArray[argc];
    int i;
    for (i = 0; i < argc; i++) {
        jsArray[i] = (jstring) (*env)->GetObjectArrayElement(env, command, i);
        argv[i] = (char *) (*env)->GetStringUTFChars(env, jsArray[i], 0);
        LOGE("命令行argCmd=%s",argv[i]);
    }
    int ret = ffmpeg_thread_run_command(argc,argv);
    for (i = 0; i < argc; ++i) {
        (*env)->ReleaseStringUTFChars(env, jsArray[i], argv[i]);
    }
    return ret;
}

JNIEXPORT void JNICALL
Java_com_aihui_lib_ffmpeg_FFmpegNativeBridge_setDebug(JNIEnv *env, jclass type, jboolean debug) {
    LOGE("setDebug:%d", debug);
    FFMPEG_ANDROID_DEBUG = debug;
    if (debug){
        av_log_set_callback(ffmpeg_android_log_callback);
    } else {
        av_log_set_callback(NULL);
    }
}
