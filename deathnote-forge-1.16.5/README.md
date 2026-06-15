# DeathNote Forge 1.16.5

Forge 1.16.5 mod for the Death Note item.

## Custom item

- Item id: `deathnote:death_note`
- JSON model + texture
- JSON data recipe (`data/deathnote/recipes/death_note.json`)
- Right-click opens a book-like input GUI; *Done* submits first page first line

## Toolchain

- Java 8 (compiled with Java 8 compatibility)
- ForgeGradle 6 / Forge 1.16.5-36.2.x
- MCP snapshot mappings (`20210309-1.16.5`)

## Build

From the monorepo root:

```bash
./gradlew :deathnote-forge-1.16.5:build
```

Output: `deathnote-forge-1.16.5/build/libs/deathnote-forge-1.16.5-1.0.0-SNAPSHOT.jar`

## Shared logic

Reuses `deathnote-core`, `deathnote-platform-api`, and `deathnote-config-common`
(embedded in the mod jar).

## Commands

- `/deathnote` (alias `/dn`) — `give`, `reload`, `info`, `recipe`, `help`
