# DeathNote Forge 1.12.2

Standalone Forge mod for Minecraft **1.12.2** (Java 8).

## Build

Requires **JDK 8** (`JAVA_HOME` must point to Java 8 — ForgeGradle 2.3 does not run on JDK 17+).

```bash
cd deathnote-forge-1.12.2
./gradlew build          # Unix
gradlew.bat build        # Windows
```

Output: `build/libs/deathnote-forge-1.12.2-1.0.0-SNAPSHOT.jar`

Shared logic is compiled from the monorepo via `gradle/composite/` project stubs
(`deathnote-core`, `deathnote-platform-api`, `deathnote-config-common`).

## Runtime

- Config directory: `config/deathnote/` (`config.yml`, `messages.yml`, `recipe.yml`)
- Item: `deathnote:death_note` with HMAC-signed NBT
- Commands: `/deathnote` and `/dn` (`give`, `reload`, `info`, `recipe`, `help`)
- Right-click item → book GUI → Done → server validates signature → `DeathNoteService.process`

## Toolchain

- Gradle 4.9 (local wrapper)
- [anatawa12/ForgeGradle-2.3](https://github.com/anatawa12/ForgeGradle-2.3) 2.3-1.0.8
- Forge `1.12.2-14.23.5.2847`
