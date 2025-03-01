add_rules("mode.debug", "mode.release")

target("mc")
set_kind("shared")
-- 源文件配置（保持与CMake相同的通配逻辑）
add_files("mc-init.cpp", "src/*/*.cpp")

-- 头文件包含配置
add_includedirs("include", "src")

-- shadowhook依赖配置
add_links("shadowhook")
-- 如果头文件/库不在系统路径，需要手动指定路径：
-- add_linkdirs("/path/to/shadowhook/lib")
-- add_includedirs("/path/to/shadowhook/include")
