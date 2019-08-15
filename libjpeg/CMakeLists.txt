
#设置 最小CMake版本

cmake_minimum_required(VERSION 3.4.1)

# 构建动态库
add_library(
        # 库名字.
        NativeJpeg
        # 构建动态库.
        SHARED
        # 源文件的相对路径
        src/main/cpp/NativeJpeg.cpp)

# 引入头文件
include_directories(src/main/cpp/include)

#导入静态库
add_library(
        turbojpeg
        STATIC
        IMPORTED )

#对应so目录 这里为了方便写死的路径，只编译armeabi-v7a包
set_target_properties(
        turbojpeg

        PROPERTIES IMPORTED_LOCATION
        # 静态库所在的路径，以cmakeList.txt所在的路径起
        ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libturbojpeg.a)


find_library(
        log-lib

        log)

target_link_libraries(
        #自己的需要生成的动态库
        NativeJpeg
        #将所需的库和自己要生成的动态库连接起来
        turbojpeg
        #这是一个很小的库，展示一个稳定的，基于C语言的接口，使本机代码安全地访问Java对象的像素缓冲区的位图
        jnigraphics
        ${log-lib})