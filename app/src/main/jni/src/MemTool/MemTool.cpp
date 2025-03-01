//
// Created by qq103 on 2025/3/1.
//

#include "MemTool.hpp"
#include "shadowhook/shadowhook.h"
#include "Init/Init.hpp"
Init init_MemTool("init_MemTool",[](){
    int code = shadowhook_init(shadowhook_mode_t::SHADOWHOOK_MODE_UNIQUE,false);
    if (code!= SHADOWHOOK_ERRNO_OK) {
        throw std::runtime_error("shadowhook init failed");
    }
});
MemTool::Hook::Hook() {}
MemTool::Hook::Hook(void *oldAddr,void **newAddr) {

}
MemTool::Hook::Hook(std::string &libName,std::string &symName,void** newAddr) {

}
MemTool::Hook::~Hook() {}