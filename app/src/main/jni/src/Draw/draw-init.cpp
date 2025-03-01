//
// Created by qq103 on 2025/3/1.
//
#include "draw-init.hpp"
#include <EGL/egl.h>
#include "Init/Init.hpp"
#include "MemTool/MemTool.hpp"
static MemTool::Hook eglSwapBuffers_;
EGLBoolean eglSwapBuffers_new(EGLDisplay dpy, EGLSurface surface) {

  return eglSwapBuffers_.callOld<EGLBoolean>(dpy, surface);
}
[[maybe_unused]] Init drawInit("DrawInit", []() {
  eglSwapBuffers_ = MemTool::Hook("libEGL.so", "eglSwapBuffers", &eglSwapBuffers_new, false);
});