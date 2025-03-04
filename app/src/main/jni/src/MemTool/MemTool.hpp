//
// Created by qq103 on 2025/3/1.
//

#ifndef MBLOADER_MEMTOOL_HPP
#define MBLOADER_MEMTOOL_HPP
#include <string>
#include <shadowhook.h>
namespace MemTool {
class [[maybe_unused]] Hook {
public:
  Hook() = default;
  template <typename T>
  [[maybe_unused]] Hook(void *funcAddr, T *newAddr, bool autoDestroy = true)
      : mAutoDestroy(autoDestroy), mNewAddr(reinterpret_cast<void *>(newAddr)),
        mFuncAddr(reinterpret_cast<void *>(funcAddr)) {
    if (mFuncAddr == nullptr || mNewAddr == nullptr) {
      throw std::runtime_error("oldAddr or newAddr is nullptr");
    }
    mStub = shadowhook_hook_func_addr(mFuncAddr, mNewAddr, &mOldAddr);
    if (mStub == nullptr) {
      throw std::runtime_error("hook failed");
    }
  }
  template <typename T>
  [[maybe_unused]] Hook(const std::string &libName, const std::string &symName, T *newAddr,
                        bool autoDestroy = true)
      : mAutoDestroy(autoDestroy), mNewAddr(reinterpret_cast<void *>(newAddr)) {
    if (newAddr == nullptr) {
      throw std::runtime_error("newAddr is nullptr");
    }
    mStub = shadowhook_hook_sym_name(libName.c_str(), symName.c_str(), mNewAddr, &mOldAddr);
    if (mStub == nullptr) {
      throw std::runtime_error("hook failed");
    }
  }
  ~Hook();
  template <typename T, typename... Args> [[maybe_unused]] T callOld(Args... args) {
    return reinterpret_cast<T (*)(Args...)>(mOldAddr)( // NOLINT(*-pro-type-reinterpret-cast)
        args...);
  }
  Hook(const Hook &) = delete;
  Hook &operator=(const Hook &) = delete;
  Hook(Hook &&) = default;
  Hook &operator=(Hook &&) = default;
  template <typename T = void *> T getOldAddr() const {
    return reinterpret_cast<T>(mOldAddr); // NOLINT(*-pro-type-reinterpret-cast)
  }
  template <typename T = void **> T getNewAddr() const {
    return reinterpret_cast<T>(mNewAddr); // NOLINT(*-pro-type-reinterpret-cast)
  }
  template <typename T = void *> T getFuncAddr() const {
    return reinterpret_cast<T>(mFuncAddr); // NOLINT(*-pro-type-reinterpret-cast)
  }

private:
  void *mOldAddr = nullptr;
  [[maybe_unused]] void *mNewAddr = nullptr;
  void *mFuncAddr = nullptr;
  bool mAutoDestroy = true;
  void *mStub = nullptr;
};

} // namespace MemTool

#endif //MBLOADER_MEMTOOL_HPP
