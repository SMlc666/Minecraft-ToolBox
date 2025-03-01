//
// Created by qq103 on 2025/3/1.
//

#include "MemTool.hpp"
#include "shadowhook/shadowhook.h"
#include "Init/Init.hpp"
[[maybe_unused]] Init init_MemTool("init_MemTool", []() {
  int code = shadowhook_init(shadowhook_mode_t::SHADOWHOOK_MODE_UNIQUE, false);
  if (code != SHADOWHOOK_ERRNO_OK) {
    throw std::runtime_error("shadowhook init failed");
  }
});
[[maybe_unused]] MemTool::Hook::Hook(void *funcAddr, void *newAddr, bool autoDestroy)
    : mAutoDestroy(autoDestroy), mNewAddr(newAddr), mFuncAddr(funcAddr) {
  if (funcAddr == nullptr || newAddr == nullptr) {
    throw std::runtime_error("oldAddr or newAddr is nullptr");
  }
  mStub = shadowhook_hook_func_addr(funcAddr, newAddr, &mOldAddr);
  if (mStub == nullptr) {
    throw std::runtime_error("hook failed");
  }
}
[[maybe_unused]] MemTool::Hook::Hook(std::string &libName, std::string &symName, void *newAddr,
                                     bool autoDestroy)
    : mAutoDestroy(autoDestroy), mNewAddr(newAddr) {
  if (newAddr == nullptr) {
    throw std::runtime_error("newAddr is nullptr");
  }
  mStub = shadowhook_hook_sym_name(libName.c_str(), symName.c_str(), newAddr, &mOldAddr);
  if (mStub == nullptr) {
    throw std::runtime_error("hook failed");
  }
}
MemTool::Hook::~Hook() {
  if (mAutoDestroy) {
    shadowhook_unhook(mStub);
  }
}