//
// Created by 胡一鸣 on 2018/9/8.
//

#ifndef LEARNGIT_FFMPEG_LIB_H
#define LEARNGIT_FFMPEG_LIB_H

#include <android/log.h>

#ifdef ANDROID

#define TAG "TAG"

#define ALOGE(fmt, ...) __android_log_vprint(ANDROID_LOG_ERROR, TAG, fmt, ##__VA_ARGS__)
#define ALOGI(fmt, ...) __android_log_vprint(ANDROID_LOG_INFO, TAG, fmt, ##__VA_ARGS__)
#define ALOGD(fmt, ...) __android_log_vprint(ANDROID_LOG_DEBUG, TAG, fmt, ##__VA_ARGS__)
#define ALOGW(fmt, ...) __android_log_vprint(ANDROID_LOG_WARN, TAG, fmt, ##__VA_ARGS__)
#define ALOGV(fmt, ...) __android_log_vprint(ANDROID_LOG_VERBOSE, TAG, fmt, ##__VA_ARGS__)

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, ##__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, ##__VA_ARGS__)

#else
#define ALOGE printf
#define ALOGI printf
#define ALOGD printf
#define ALOGW printf
#endif

#endif //LEARNGIT_FFMPEG_LIB_H
