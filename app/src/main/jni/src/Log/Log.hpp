//
// Created by qq103 on 2025/3/1.
//

#ifndef MBLOADER_LOG_HPP
#define MBLOADER_LOG_HPP
#include <android/log.h>
#ifndef TOOLBOX_LOG_LOG_TAG
#define TOOLBOX_LOG_LOG_TAG "ToolBox"
#endif
#define TOOLBOX_LOG_LOGI(...) __android_log_print(ANDROID_LOG_INFO,TOOLBOX_LOG_LOG_TAG, __VA_ARGS__)
#define TOOLBOX_LOG_LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TOOLBOX_LOG_LOG_TAG, __VA_ARGS__)
#define TOOLBOX_LOG_LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TOOLBOX_LOG_LOG_TAG, __VA_ARGS__)
#define TOOLBOX_LOG_LOGW(...) __android_log_print(ANDROID_LOG_WARN,TOOLBOX_LOG_LOG_TAG, __VA_ARGS__)
#define TOOLBOX_LOG_LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TOOLBOX_LOG_LOG_TAG, __VA_ARGS__)
#endif //MBLOADER_LOG_HPP
