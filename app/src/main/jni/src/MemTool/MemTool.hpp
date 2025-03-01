//
// Created by qq103 on 2025/3/1.
//

#ifndef MBLOADER_MEMTOOL_HPP
#define MBLOADER_MEMTOOL_HPP
#include <string>
namespace MemTool {
class [[maybe_unused]] Hook {
public:
  Hook() = default;
  [[maybe_unused]] Hook(void *funcAddr, void *newAddr, bool autoDestroy = true);
  [[maybe_unused]] Hook(std::string &libName, std::string &symName, void *newAddr,
                        bool autoDestroy = true);
  ~Hook();
  template <typename T, typename... Args> [[maybe_unused]] T callOld(Args... args) {
    return reinterpret_cast<T (*)(Args...)>(mOldAddr)( // NOLINT(*-pro-type-reinterpret-cast)
        args...);
  }
  Hook(const Hook &) = delete;
  Hook &operator=(const Hook &) = delete;
  Hook(Hook &&) = delete;
  Hook &operator=(Hook &&) = delete;
  template <typename T = void *> T getOldAddr() const {
    return reinterpret_cast<T>(mOldAddr); // NOLINT(*-pro-type-reinterpret-cast)
  }
  template <typename T = void **> T getNewAddr() const {
    return reinterpret_cast<T>(mNewAddr); // NOLINT(*-pro-type-reinterpret-cast)
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
