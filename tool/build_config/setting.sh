# ndk 环境
NDK=~/Downloads/android-ndk-r10e
SYSROOT=$NDK/platforms/android-14/arch-arm/
TOOLCHAIN=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/darwin-x86_64
# cpu 架构平台，若要编译 x86 则指定 x86
# 目前开启了硬编码，所以只支持 armv7a
CPU=armv7-a