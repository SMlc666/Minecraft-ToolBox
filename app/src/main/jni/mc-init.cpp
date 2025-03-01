#include <jni.h>
#include <string>
#include <dlfcn.h>
#include "Init/Init.hpp"
#include "Log/Log.hpp"
#include <android/native_activity.h>
#include <thread>
static void (*android_main_minecraft)(struct android_app *app);
static void (*ANativeActivity_onCreate_minecraft)(ANativeActivity *activity, void *savedState,
                                                  size_t savedStateSize);

extern "C" void android_main(struct android_app *app) {
  android_main_minecraft(app);
}

extern "C" void ANativeActivity_onCreate(ANativeActivity *activity, void *savedState,
                                         size_t savedStateSize) {
  ANativeActivity_onCreate_minecraft(activity, savedState, savedStateSize);
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
  std::this_thread::sleep_for(std::chrono::milliseconds(1000)); //TODO: for debug
  void *handle = dlopen("libminecraftpe.so", RTLD_LAZY);
  android_main_minecraft = (void (*)(struct android_app *))(dlsym(handle, "android_main"));
  ANativeActivity_onCreate_minecraft =
      (void (*)(ANativeActivity *, void *, size_t))(dlsym(handle, "ANativeActivity_onCreate"));
  std::thread([]() {
    for (auto mInit : InitList) {
      try {
        mInit.second();
      } catch (const std::exception &e) {
        TOOLBOX_LOG_LOGE("Init failed: %s", e.what());
        std::terminate();
      }
    }
  }).detach();
  return JNI_VERSION_1_6;
}