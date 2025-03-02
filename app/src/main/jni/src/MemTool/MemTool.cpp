//
// Created by qq103 on 2025/3/1.
//

#include "MemTool.hpp"
#include <shadowhook.h>
#include "Init/Init.hpp"
[[maybe_unused]] Init init_MemTool("init_MemTool", []() {
  int code = shadowhook_init(shadowhook_mode_t::SHADOWHOOK_MODE_UNIQUE, false);
  if (code != SHADOWHOOK_ERRNO_OK) {
    throw std::runtime_error("shadowhook init failed");
  }
});
MemTool::Hook::~Hook() {
  if (mAutoDestroy) {
    shadowhook_unhook(mStub);
  }
}