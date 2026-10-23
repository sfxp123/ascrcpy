# Third-party notices

## Tailcat 0.7.0

AScrcpy bundles the official Tailcat v0.7.0 Linux ARM64, ARMv7 and x86_64 static executables for
Android controller devices. They are stored under `app/src/main/jniLibs` with a `.so` name so
Android extracts them to an executable native library directory; they are run as separate processes.

- Project and corresponding source: <https://github.com/tailscale/tailcat/tree/v0.7.0>
- License: BSD 3-Clause, reproduced in [`licenses/tailcat-LICENSE`](licenses/tailcat-LICENSE)
- Release: <https://github.com/tailscale/tailcat/releases/tag/v0.7.0>
- ARM64 archive SHA-256: `bbb1ab50f24f00effe1e1fd86d0501803fb80793a90785a2a16ff3428f03d8ef`
- ARMv7 archive SHA-256: `cad3994b1f336b67e8a3a5273a9cecb44331d14d57eb32bfe7d0919adeab22b7`
- x86_64 archive SHA-256: `23c0b1887a5ec422f0d18a9c52b4f5357815febdaae738a1eb54036d10bd9ee6`
- x86_64 bundled binary SHA-256: `75da81231e01f2a52005f26f4eca6df4bce71cb694a42fa467a30e93c3d7a5b9`
- ARM64 bundled binary SHA-256: `1f8e877f9080ab0436eaf2bb0d0712f190f56d30d3e78ece8d0680b280f1f955`
- ARMv7 bundled binary SHA-256: `930870d0ae44f766f70bcb74aa3f4e3b394dbe50350ddebbcc3fa45bf523258e`

Tailcat's upstream repository documents its other dependencies and their licenses. AScrcpy uses
Tailcat only as a loopback TCP forwarder; the ADB and scrcpy implementations remain separate.

## scrcpy-server 5.0.1

AScrcpy redistributes the unmodified `scrcpy-server` version 5.0.1 binary from the application assets.
The file is byte-identical to the artifact published by the scrcpy project.

- Project: <https://github.com/Genymobile/scrcpy>
- Tag: `v5.0.1`
- Copyright: Copyright (C) 2018 Genymobile, Copyright (C) 2018-2026 Romain Vimont, and scrcpy contributors
- License: Apache License, Version 2.0
- License text: <https://github.com/Genymobile/scrcpy/blob/v5.0.1/LICENSE>
- Corresponding source: <https://github.com/Genymobile/scrcpy/tree/v5.0.1>
- Binary SHA-256: `764eb6f79811d5211fe9df341120882ba9994c7a61b897d7bf3fb662e53bc536`

The whole scrcpy repository, client and server alike, is licensed under the Apache License 2.0.
Redistributing this binary therefore requires keeping the copyright and license notices above, and
offering the corresponding source, which this file and the project repository do.

### Scope of reuse

Only the server binary is reused, and only to run on the target device. The controller side of
AScrcpy - the ADB host implementation, the stream protocol handling, the `MediaCodec` decoder, the
control-message encoder and the entire user interface - is an independent implementation written for
this project. It contains no scrcpy client source code.

AScrcpy speaks the scrcpy wire protocol, which is an internal protocol of the scrcpy project and is
versioned together with it. The bundled server and the client must therefore always be the same
scrcpy version; see `SERVER_VERSION` in `ScrcpyClient.kt`.

## Design reference

The visual language of the floating remote follows the Tencent WeUI design language. WeUI is an open
source project maintained by the WeChat design team and is distributed under its own license; no
WeUI source code, trademark, or product asset is copied into this repository.
