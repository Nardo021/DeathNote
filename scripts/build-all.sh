#!/usr/bin/env bash
#
# Build everything that is currently implemented and run the core tests.
#
set -euo pipefail

cd "$(dirname "$0")/.."

GRADLE="./gradlew"
if [[ "${OS:-}" == "Windows_NT" ]]; then
  GRADLE="./gradlew.bat"
fi

echo "==> Running core unit tests"
"$GRADLE" :deathnote-core:test --console=plain

echo "==> Building Bukkit plugin jars (shaded)"
"$GRADLE" :deathnote-bukkit-modern:shadowJar :deathnote-bukkit-legacy:shadowJar --console=plain

echo "==> Building all Forge mods"
"$(dirname "$0")/build-all-forge.sh"

echo
echo "==> Done. Artifacts:"
ls -1 deathnote-bukkit-modern/build/libs/*.jar 2>/dev/null || true
ls -1 deathnote-bukkit-legacy/build/libs/*.jar 2>/dev/null || true
ls -1 deathnote-forge-*/build/libs/*.jar 2>/dev/null | grep -v sources || true
