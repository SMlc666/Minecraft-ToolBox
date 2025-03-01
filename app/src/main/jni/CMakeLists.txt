cmake_minimum_required(VERSION 3.30)
project(mc)
find_package(shadowhook REQUIRED CONFIG)
set(MC_INCLUDE_DIRS
        include
        src
)
include_directories(mc PRIVATE ${MC_INCLUDE_DIRS})
file(GLOB MC_SRC_FILES
        "mc-init.cpp"
        "src/*/*.cpp"
)
add_library(mc SHARED
        ${MC_SRC_FILES}
)
target_link_libraries(mc shadowhook::shadowhook)