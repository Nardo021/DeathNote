# deathnote-forge-1.8.9

Forge 1.8.9 product line for the Death Note mod.

## Custom item

- Item id: `deathnote:death_note`
- Custom model/texture (`ModelLoader.setCustomModelResourceLocation`)
- Shaped recipe via `GameRegistry.addRecipe` (hard preset WNW/EBE/ITI)
- Right-click opens a book-like input GUI; *Done* submits first page first line

## Toolchain

- Java 8 (required for Gradle + Minecraft 1.8.9 decompilation)
- Independent `./gradlew` with ForgeGradle 2.1-SNAPSHOT (Gradle 4.9)
- Minecraft 1.8.9-11.15.1.2318-1.8.9, mappings `stable_22`

Shared pure-Java sources from `deathnote-platform-api`, `deathnote-core`, and
`deathnote-config-common` are compiled into this module directly (no root Gradle
required).

## Build

```bash
cd deathnote-forge-1.8.9
# JAVA_HOME must point to JDK 8
./gradlew build
```

Output: `build/libs/deathnote-forge-1.8.9-1.0.0-SNAPSHOT.jar` (includes shared logic + Forge mod code).

## Shared logic

Reuses `deathnote-core` for validation, cooldowns, signature, audit and `KillStrategy` ordering. Config/messages loaded via `deathnote-config-common`.

## Commands

- `/deathnote` (alias `/dn`) — `give`, `reload`, `info`, `recipe`, `help`
