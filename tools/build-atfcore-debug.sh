#!/usr/bin/env bash
set -euo pipefail

repo="$(cd "$(dirname "$0")/.." && pwd)"
prefix="$(cd "${1:?informe o prefixo Android arm64 do EasyRPG}" && pwd)"
if ! find "$prefix" -iname 'liblcf*config.cmake' -print -quit | grep -q .; then
    printf 'liblcf para Android ausente em %s\n' "$prefix" >&2
    exit 1
fi

exec bash "$repo/builds/android/gradlew" -p "$repo/builds/android" :app:assembleDebug \
    "-PtoolchainDirs=$prefix" -PABI_FILTERS_DEBUG=arm64-v8a
