# deathnote-forge-1.7.10

Forge 1.7.10 product line for the Death Note mod.

## Features

- Custom item `deathnote:death_note` via `GameRegistry.registerItem` + `setTextureName`
- NBT HMAC signing (`item_type`, `signature`, `created_at`, `created_by`, `uses`)
- Custom `GuiScreen` editor (first-line name input, Done submits to server)
- `SimpleNetworkWrapper` packet validation + `DeathNoteService.process`
- Kill strategies via command manager (`kill`) + `attackEntityFrom` + `setHealth(0)`
- Shared logic from `deathnote-core`, `deathnote-platform-api`, `deathnote-config-common`

## Toolchain

- **Java 8** (required — Gradle 4.x / ForgeGradle 1.2 will not run on JDK 17+)
- **Gradle 4.10.3** (bundled `gradlew`)
- **ForgeGradle 1.2** ([anatawa12 fork](https://github.com/anatawa12/ForgeGradle-1.2))
- Minecraft **1.7.10-10.13.4.1614**

This module has its **own** `gradlew` and is intentionally **not** wired into the monorepo root build (different toolchain).

## Build

```bat
cd deathnote-forge-1.7.10
gradlew.bat build
```

Output: `build/libs/deathnote-forge-1.7.10-1.0.0-SNAPSHOT.jar`

Set `JAVA_HOME` to a JDK 8 install if your default Java is newer.

## Commands

- `/deathnote` or `/dn` — `give`, `reload`, `info`, `recipe`, `help`

## Config

Copied on first run to `config/deathnote/` (`config.yml`, `messages.yml`, `recipe.yml`).
