# AScrcpy

[![License: Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2026.03-4285F4.svg)](https://developer.android.com/jetpack/compose)

[English](README.md) | [简体中文](README.zh-CN.md)

用一台 Android 设备镜像并控制另一台 Android 设备：不需要电脑，不需要 root，被控设备上也不留下
任何安装。AScrcpy 直连目标设备的 `adbd`，推送 scrcpy server，用 `MediaCodec` 解码 H.264 码流，再把
触摸和按键转换成 scrcpy 控制消息。

本仓库是 [Ernest-su/ascrcpy](https://github.com/Ernest-su/ascrcpy) 的 fork，跟随更新的 scrcpy
server，并增加了投屏选项：分辨率与码率预设、可指定启动应用的虚拟屏镜像，以及带旋转和最小化的
悬浮遥控器。

## 依赖的上游项目

以下都是本项目依赖的上游成果。内置二进制均未修改地转发；校验和、许可证全文与源码链接见
[THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。

| 项目 | 本项目使用的内容 | 许可证 |
|---|---|---|
| [Genymobile/scrcpy](https://github.com/Genymobile/scrcpy) | 未经修改的 `scrcpy-server` 5.0.1 与设备端通信协议 | Apache-2.0 |
| [Ernest-su/ascrcpy](https://github.com/Ernest-su/ascrcpy) | 本 fork 所基于的原 Android 控制端 | Apache-2.0 |
| [Ernest-su/adb](https://github.com/Ernest-su/adb) | 可复用的 ADB Host 库（同时发布在 JitPack） | Apache-2.0 |
| [tailscale/tailcat](https://github.com/tailscale/tailcat) | Tailcat 0.7.0 的 ARM64/ARMv7/x86_64 二进制，用于 Tailcat 连接模式 | BSD-3-Clause |
| [zxing/zxing](https://github.com/zxing/zxing) | 无线调试配对用的二维码生成 | Apache-2.0 |
| [AndroidX / Compose / Material 3](https://developer.android.com/jetpack/compose) | 界面框架与运行时 | Apache-2.0 |
| [WeUI](https://github.com/Tencent/weui) | 悬浮遥控器的视觉语言（仅设计参考） | 其自有许可证 |

本项目不含 scrcpy 客户端源码：ADB Host、串流与控制处理、解码器和全部 Compose 界面均为独立实现，
只有 server 二进制和协议来自 scrcpy。

## 功能

- TCP ADB、Tailcat 与 Tailcat 无线配对、Android 11+ 无线调试（配对码或二维码）、USB 主机连接，
  使用持久化 RSA 身份。
- 内置 scrcpy-server 5.0.1，随会话启动和停止。
- 投屏选项：按**短边**限制的分辨率（原始、1080、720、540、480）、码率预设（1–32 Mbit/s）、
  投屏期间息屏，以及镜像到**虚拟显示屏**。
- 虚拟屏：可指定宽/高/DPI 和启动应用。虚拟屏在启动应用前不渲染任何内容，因此会为你自动启动；
  可从已安装应用列表中选择包名或手动填写（留空则启动设备桌面）。
- 通过 `MediaCodec` 将 H.264 低延迟解码到 `SurfaceView`，单指与多指触摸转发，带压力值和正确的
  坐标映射。
- 可拖动悬浮遥控器：拖离边缘展开完整面板，松手靠近左右边缘则收成小图标停靠。面板顶栏依次是
  最小化、全屏、旋转和电源。
- 设备管理：Shell 命令、已安装应用（清空数据、停用、启用、卸载），以及远程文件浏览、下载和
  需确认的删除。
- 持久化连接历史；中英文界面，支持 Android 13 应用独立语言选择。

## 环境要求

- **控制端**：Android 8.0（API 26）及以上。
- **被控端**：可访问的 ADB 端点——`adb tcpip 5555`、Tailcat、无线调试或 USB。
- 虚拟屏要求被控端为 **Android 10（API 29）** 及以上。Android 10–12 的虚拟屏没有系统装饰，
  画面完全来自所启动的应用。
- legacy TCP ADB 是明文传输，请只在可信网络使用。

## 使用

1. 在控制端安装 AScrcpy。
2. 选择连接方式并连接。无线配对时输入临时配对端点和配对码，应用会自动发现并连接独立的调试
   端口；已配对设备按 GUID 保存，无需新配对码即可重连。
3. 点击 **开始镜像**，预览自动全屏打开。
4. 在预览画面上触摸操作，用悬浮遥控器发送导航键、旋转与电源。长按停靠图标可在全屏预览与
   主界面之间切换。
5. 需要 Shell、应用或文件操作时，从主界面进入**设备管理**。

投屏选项在开始投屏时生效；修改后停止并重新开始投屏即可应用。虚拟屏所用的应用列表只能在未
投屏时读取，因为投屏进行中不能执行 shell 命令。

## 构建

```shell
./gradlew :scrcpy:testDebugUnitTest :app:testDebugUnitTest :app:assembleDebug
```

需要 JDK 17。使用 Android CLI 安装到已连接设备：

```shell
android run --device=<serial> --activity=ernest.ascrcpy.MainActivity \
  --apks=app/build/outputs/apk/debug/app-debug.apk
```

instrumentation 测试需要真机，不进入 CI：

```shell
./gradlew :app:connectedDebugAndroidTest
```

### 版本与 CI

每次推送到 `main` 都由 GitHub Actions 构建并测试，debug APK 作为构建产物上传。版本号遵循
`MAJOR.MINOR.PATCH`，`versionCode = MAJOR*10000 + MINOR*100 + PATCH`。本地构建为 `1.4.0`（10400），
CI 构建为 `1.4.<run number>`，便于区分每个产物。

CI 会缓存 debug keystore，使各次构建的 debug APK 签名保持一致，新版本可以直接覆盖安装，无需
先卸载。正式发布需要 [docs/releasing.md](docs/releasing.md) 中所述的四个签名 secrets；推送
`vX.Y.Z` tag 会自动构建已签名 APK 并发布 Release。

## 模块

| 模块 | 内容 |
|---|---|
| `app` | Compose 控制端界面、Android 生命周期、assets、依赖装配 |
| `adb` | 外部 ADB Host 库（见上）；其公开 API 不暴露 TCP 实现 |
| `scrcpy` | scrcpy server 生命周期、串流协议、`MediaCodec` 解码、控制消息 |

允许的依赖方向为 `app -> scrcpy -> adb`，此外 `app -> adb` 用于直连和诊断。所有项目自有包均以
`ernest.ascrcpy` 开头。

## 已知限制

- 尚未实现音频、剪贴板、HID 输入与录制。
- 没有前台服务，会话在后台或界面重建后不保证存活。
- H.264 走 ADB 隧道没有流控：网络或解码卡顿后，缓冲的帧会被快速播放完毕，看起来像快进。
  降低码率或分辨率可缓解。

## 许可证

Copyright 2026 Ernest-su and AScrcpy contributors。基于 Apache License 2.0 发布，详见
[LICENSE](LICENSE) 与 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。
