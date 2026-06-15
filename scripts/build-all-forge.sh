#!/usr/bin/env bash
#
# Build all Forge mod artifacts (1.7.10 through 1.20.1).
#
# Modern modules (1.16.5, 1.20.1) use the monorepo root Gradle wrapper (JDK 17).
# Legacy modules (1.7.10, 1.8.9, 1.12.2) use independent gradlew wrappers and
# require JDK 8 — set JAVA_HOME before running if your default Java is newer.
#
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

GRADLE="./gradlew"
if [[ "${OS:-}" == "Windows_NT" ]]; then
  GRADLE="./gradlew.bat"
fi

find_java8() {
  if [[ -n "${JAVA_HOME:-}" ]] && "$JAVA_HOME/bin/java" -version 2>&1 | grep -qE 'version "1\.8'; then
    return 0
  fi
  local candidates=(
    "/c/Program Files/Zulu/zulu-8"
    "/c/Program Files/Java/jdk1.8.0"
    "/c/Program Files/Eclipse Adoptium/jdk-8"
    "/usr/lib/jvm/java-8-openjdk-amd64"
    "/usr/lib/jvm/java-8-openjdk"
  )
  for dir in "${candidates[@]}"; do
    if [[ -x "$dir/bin/java" ]] && "$dir/bin/java" -version 2>&1 | grep -qE 'version "1\.8'; then
      export JAVA_HOME="$dir"
      return 0
    fi
  done
  echo "ERROR: JDK 8 required for Forge 1.7.10 / 1.8.9 / 1.12.2 builds." >&2
  echo "Set JAVA_HOME to a Java 8 install and re-run." >&2
  return 1
}

run_legacy_forge() {
  local module="$1"
  echo "==> Building $module (JDK 8)"
  find_java8
  echo "    JAVA_HOME=$JAVA_HOME"
  if [[ "${OS:-}" == "Windows_NT" ]]; then
    (cd "$module" && ./gradlew.bat build --console=plain --no-daemon)
  else
    (cd "$module" && ./gradlew build --console=plain --no-daemon)
  fi
}

echo "==> Building Forge 1.16.5 + 1.20.1 (root Gradle, JDK 17)"
"$GRADLE" :deathnote-forge-1.16.5:build :deathnote-forge-1.20.1:build --console=plain

run_legacy_forge deathnote-forge-1.12.2
run_legacy_forge deathnote-forge-1.8.9
run_legacy_forge deathnote-forge-1.7.10

echo
echo "==> Done. Forge artifacts:"
ls -1 deathnote-forge-1.20.1/build/libs/*.jar 2>/dev/null | grep -v sources || true
ls -1 deathnote-forge-1.16.5/build/libs/*.jar 2>/dev/null | grep -v sources || true
ls -1 deathnote-forge-1.12.2/build/libs/*.jar 2>/dev/null | grep -v sources || true
ls -1 deathnote-forge-1.8.9/build/libs/*.jar 2>/dev/null | grep -v sources || true
ls -1 deathnote-forge-1.7.10/build/libs/*.jar 2>/dev/null | grep -v sources || true
