# DeathNote Forge 1.20.1

Forge 1.20.1 mod for the Death Note item.

## Custom item

- Item id: `deathnote:death_note`
- JSON model + texture
- JSON data recipe (`data/deathnote/recipes/death_note.json`)
- Right-click opens a book-like input GUI; *Done* submits first page first line

## Toolchain

- Java 17 (auto-downloaded via Gradle foojay toolchain if missing)
- ForgeGradle 6 / Forge 1.20.1-47.x
- Mojang official mappings

## Build

From the monorepo root:

```bash
./gradlew :deathnote-forge-1.20.1:build
```

Output: `deathnote-forge-1.20.1/build/libs/deathnote-forge-1.20.1-1.0.0-SNAPSHOT.jar`

## Shared logic

Reuses `deathnote-core`, `deathnote-platform-api`, and `deathnote-config-common`
(embedded in the mod jar).

## Commands

- `/deathnote` (alias `/dn`) — `give`, `reload`, `info`, `recipe`, `help`
