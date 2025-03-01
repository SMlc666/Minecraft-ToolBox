cmake_minimum_required(VERSION 3.4.1)
set(MC_SRC_FILES
    mc-init.cpp
)

add_library(mc SHARED
        ${MC_SRC_FILES}
)