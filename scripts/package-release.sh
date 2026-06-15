#!/usr/bin/env bash
#
# Package the implemented artifacts into a dist/ folder for release, alongside
# the resource pack. Builds first via build-all.sh.
#
set -euo pipefail

cd "$(dirname "$0")/.."

VERSION="$(grep -E '^version' gradle.properties | sed 's/.*=\s*//' | tr -d '[:space:]')"
[[ -z "$VERSION" ]] && VERSION="dev"

DIST="dist/deathnote-${VERSION}"
echo "==> Packaging release ${VERSION} into ${DIST}"

bash scripts/build-all.sh

rm -rf "$DIST"
mkdir -p "$DIST/plugins" "$DIST/resourcepack"

# Plugin jars
cp -f deathnote-bukkit-modern/build/libs/DeathNote-Bukkit-Modern-*.jar "$DIST/plugins/" 2>/dev/null || true
cp -f deathnote-bukkit-legacy/build/libs/DeathNote-Bukkit-Legacy-*.jar "$DIST/plugins/" 2>/dev/null || true

# Resource pack (zip the contents so it can be dropped into resourcepacks/)
( cd deathnote-assets/resourcepack && zip -r -q "../../$DIST/resourcepack/DeathNote-ResourcePack.zip" . ) \
  || echo "   (zip not available; copying resourcepack folder instead)" \
  && cp -rf deathnote-assets/resourcepack "$DIST/resourcepack/source" 2>/dev/null || true

# Docs
cp -f README.md "$DIST/" 2>/dev/null || true
cp -rf docs "$DIST/docs" 2>/dev/null || true

echo "==> Release packaged:"
find "$DIST" -type f | sed 's/^/   /'
