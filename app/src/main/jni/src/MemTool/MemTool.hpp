//
// Created by qq103 on 2025/3/1.
//

#ifndef MBLOADER_MEMTOOL_HPP
#define MBLOADER_MEMTOOL_HPP
#include <string>
namespace MemTool {
    class Hook {
        public:
            Hook();
            Hook(void* oldAddr,void** newAddr);
            Hook(std::string &libName,std::string &symName,void** newAddr);
            ~Hook();
            template<typename T,typename... Args>
            inline T callOld(Args... args){
                return reinterpret_cast<T(*)(Args...)>(mOldAddr)(args...);
            }
        private:
            void* mOldAddr;
            void** mNewAddr;

    };

}// namespace MemTool


#endif //MBLOADER_MEMTOOL_HPP
