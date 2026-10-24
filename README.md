# AScrcpy

[![License: Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2026.03-4285F4.svg)](https://developer.android.com/jetpack/compose)

[English](README.md) | [简体中文](README.zh-CN.md)

Mirror and control one Android device from another Android device. No PC, no root, nothing installed
on the target. AScrcpy connects straight to the target's `adbd`, pushes the scrcpy server, decodes the
H.264 stream with `MediaCodec`, and turns touches and key presses into scrcpy control messages.

This repository is a fork of [Ernest-su/ascrcpy](https://github.com/Ernest-su/ascrcpy) that tracks a
newer scrcpy server and adds mirror options: resolution and bit rate presets, virtual-display mirroring
with a chosen start app, and a floating remote with rotate and minimize keys.

## Built on

Everything below is upstream work this project depends on. The bundled binaries are redistributed
unmodified; see [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) for checksums, licences and source links.

| Project | What AScrcpy uses | Licence |
|---|---|---|
| [Genymobile/scrcpy](https://github.com/Genymobile/scrcpy) | The unmodified `scrcpy-server` 5.0.1 binary and the device-side wire protocol | Apache-2.0 |
| [Ernest-su/ascrcpy](https://github.com/Ernest-su/ascrcpy) | The original Android controller this fork builds on | Apache-2.0 |
| [Ernest-su/adb](https://github.com/Ernest-su/adb) | The reusable ADB host library (also published on JitPack) | Apache-2.0 |
| [tailscale/tailcat](https://github.com/tailscale/tailcat) | Tailcat 0.7.0 ARM64/ARMv7/x86_64 binaries for the Tailcat connection mode | BSD-3-Clause |
| [zxing/zxing](https://github.com/zxing/zxing) | QR code generation for wireless-debugging pairing | Apache-2.0 |
| [AndroidX / Compose / Material 3](https://developer.android.com/jetpack/compose) | UI toolkit and runtime | Apache-2.0 |
| [WeUI](https://github.com/Tencent/weui) | Visual language of the floating remote (design reference only) | Own licence |

No scrcpy client source code is included: the ADB host, stream and control handling, the decoder and
the whole Compose UI are independent implementations. Only the server binary and the protocol are
scrcpy's.

## Features

- TCP ADB, Tailcat and Tailcat wireless pairing, Android 11+ wireless debugging (code or QR), and USB
  Host connections, with a persistent RSA identity.
- Bundled scrcpy-server 5.0.1, started and stopped with the session.
- Mirror options: resolution limited by the **short edge** (original, 1080, 720, 540, 480), bit rate
  presets (1–32 Mbit/s), screen-off while mirroring, and mirroring into a **virtual display**.
- Virtual display: optional width/height/DPI and the app to start on it. A virtual display renders
  nothing until an app is started, so the app is started automatically; pick a package from the
  installed-app list or type one (blank uses the device launcher).
- Low-latency H.264 decoding into a `SurfaceView` via `MediaCodec`, single- and multi-pointer touch
  forwarding with pressure and correct coordinate mapping.
- Draggable floating remote: drag it away from the edge to open the full panel, drop it near an edge to
  dock it as a small icon. The panel's top row holds minimize, fullscreen, rotate and power.
- Device management: shell commands, installed apps (clear data, disable, enable, uninstall) and remote
  file browsing with download and confirmed deletion.
- Persistent connection history, English and Simplified Chinese UI including Android 13 per-app
  language selection.

## Requirements

- **Controller**: Android 8.0 (API 26) or newer.
- **Target**: a reachable ADB endpoint — `adb tcpip 5555`, Tailcat, wireless debugging, or USB.
- Virtual display requires **Android 10 (API 29)** or newer on the target. On Android 10–12 the
  virtual display has no system decorations, so the started app provides the whole picture.
- Legacy TCP ADB is plaintext; use it only on a trusted network.

## Usage

1. Install AScrcpy on the controller.
2. Pick a connection method and connect. For wireless pairing, enter the temporary pairing endpoint
   and code; the app then finds and connects to the separate debugging port. Paired devices are saved
   by GUID and can be reconnected without a new code.
3. Tap **Start mirroring**. The preview opens full screen automatically.
4. Use the preview for touch input and the floating remote for navigation keys, rotate and power.
   Long-press the docked icon to switch between the full-screen preview and the main screen.
5. Open **Device management** from the main screen for shell, app and file actions.

Mirror options take effect when mirroring starts; stop and restart the session to apply a change.
The installed-app list used by the virtual display can only be read while mirroring is stopped, because
shell commands must not run in the middle of a session.

## Build

```shell
./gradlew :scrcpy:testDebugUnitTest :app:testDebugUnitTest :app:assembleDebug
```

Requires JDK 17. Install with the Android CLI:

```shell
android run --device=<serial> --activity=ernest.ascrcpy.MainActivity \
  --apks=app/build/outputs/apk/debug/app-debug.apk
```

Instrumented tests need a device and stay out of CI:

```shell
./gradlew :app:connectedDebugAndroidTest
```

### Versions and CI

Every push to `main` builds and tests through GitHub Actions; the debug APK is uploaded as a build
artifact. Version numbers follow `MAJOR.MINOR.PATCH` with `versionCode = MAJOR*10000 + MINOR*100 + PATCH`.
Local builds use `1.4.0` (10400); CI stamps `1.4.<run number>` so each artifact is identifiable.

The debug keystore is cached between runs, which keeps the debug APK signature stable, so a newer
build installs over the previous one instead of requiring an uninstall. Release builds need the four
signing secrets described in [docs/releasing.md](docs/releasing.md); pushing a `vX.Y.Z` tag publishes
a signed APK as a GitHub release.

## Modules

| Module | Contents |
|---|---|
| `app` | Compose controller UI, Android lifecycle, assets, dependency wiring |
| `adb` | reusable ADB host library (external, see above); its public API never exposes the TCP implementation |
| `scrcpy` | scrcpy server lifecycle, stream protocol, `MediaCodec` decoder, control messages |

Allowed dependency direction is `app -> scrcpy -> adb`, plus `app -> adb` for direct connection and
diagnostics. All project-owned packages start with `ernest.ascrcpy`.

## Known limitations

- Audio, clipboard, HID input and recording are not implemented.
- No foreground service, so a session is not guaranteed to survive backgrounding or UI recreation.
- H.264 over the ADB tunnel has no pacing: after a network or decoder stall the buffered frames are
  played back quickly, which looks like fast-forwarding. Lowering the bit rate or resolution helps.

## License

Copyright 2026 Ernest-su and AScrcpy contributors. Released under the Apache License 2.0; see
[LICENSE](LICENSE) and [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).
