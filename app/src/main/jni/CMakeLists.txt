cmake_minimum_required(VERSION 3.30)
project(mc)
find_package(shadowhook REQUIRED CONFIG)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

set(MC_INCLUDE_DIRS
        include/
        src/
)
set(MC_LIBRARY_LIST
        shadowhook::shadowhook
        log
        EGL
)
include_directories(mc PRIVATE ${MC_INCLUDE_DIRS})
file(GLOB MC_SRC_FILES
        "mc-init.cpp"
        "src/*/*.cpp"
)
add_library(mc SHARED
        ${MC_SRC_FILES}
)
target_link_libraries(mc ${MC_LIBRARY_LIST})