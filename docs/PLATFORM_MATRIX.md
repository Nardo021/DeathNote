# Platform Matrix

Status legend: **implemented** = builds + works; **scaffold** = structure,
README, adapter skeleton + TODO present, not buildable yet; **planned** = not
started.

| Module                     | Status      | Java | Minecraft            | Item mechanism                          | Notes |
|----------------------------|-------------|------|----------------------|-----------------------------------------|-------|
| deathnote-platform-api     | implemented | 8    | n/a                  | n/a (abstraction)                       | Pure Java, no game deps. |
| deathnote-core             | implemented | 8    | n/a                  | n/a (logic)                             | Pure Java, fully unit-tested. |
| deathnote-config-common    | implemented | 8    | n/a                  | n/a (YAML/messages)                     | SnakeYAML config loader, shared by Bukkit + Forge. |
| deathnote-bukkit-common    | implemented | 8    | 1.8 - latest (shared)| disguised writable book                 | Compiled vs Spigot 1.8.8 API. |
| deathnote-bukkit-modern    | implemented | 17   | 1.13 - latest        | WRITABLE_BOOK + PDC + CustomModelData    | Shaded plugin jar. NamespacedKey recipe. |
| deathnote-bukkit-legacy    | implemented | 8    | 1.8.x - 1.12.2       | BOOK_AND_QUILL + hidden lore HMAC        | Shaded plugin jar. See 1.7.10 caveat below. |
| deathnote-forge-1.7.10     | implemented | 8    | 1.7.10               | real `deathnote:death_note` item        | Independent `gradlew`, ForgeGradle 1.2, FML. |
| deathnote-forge-1.8.9      | implemented | 8    | 1.8.9                | real `deathnote:death_note` item        | Independent `gradlew`, ForgeGradle 2.1. |
| deathnote-forge-1.12.2     | implemented | 8    | 1.12.2               | real `deathnote:death_note` item        | Independent `gradlew`, JSON data recipes. |
| deathnote-forge-1.16.5     | implemented | 8    | 1.16.5               | real `deathnote:death_note` item        | Root Gradle, ForgeGradle 6, MCP mappings. |
| deathnote-forge-1.20.1     | implemented | 17   | 1.20.1               | real `deathnote:death_note` item        | Root Gradle, ForgeGradle 6, Mojang mappings. |
| deathnote-fabric-modern    | scaffold    | 17+  | 1.20.x / 1.21.x      | real `deathnote:death_note` item        | Loom, Fabric API, `fabric.mod.json`. |
| deathnote-neoforge-modern  | scaffold    | 21   | 1.20.4+ / 1.21.x     | real `deathnote:death_note` item        | NeoGradle, `neoforge.mods.toml`. |

## Legacy 1.7.10 caveat (Bukkit line)

The `deathnote-bukkit-legacy` artifact is compiled against the Spigot 1.8.8 API
and Java 8, and its **runtime target is 1.8.x - 1.12.2**. True 1.7.10 support is
*not* claimed by the single legacy jar because of two well-known Bukkit breaks:

- `Damageable.getHealth()` / `setHealth()` / `damage()` changed `int` <-> `double`
  between 1.7.10 and 1.8.
- `Server.getOnlinePlayers()` changed from `Player[]` to a `Collection`.

The shared code already avoids `getHealth()` and `getOnlinePlayers()` to widen
compatibility, but `damage(double)` / `setHealth(double)` still differ on a real
1.7.10 server. A dedicated 1.7.10 Bukkit adapter (or reflection isolated behind
`BukkitPlatform`) is the documented path to add genuine 1.7.10 support.

The Forge **mod** line covers 1.7.10 separately via `deathnote-forge-1.7.10`.

## Forge build notes

| Module | Build command | JDK |
|--------|---------------|-----|
| 1.20.1, 1.16.5 | `./gradlew :deathnote-forge-1.20.1:build` (from repo root) | 17 |
| 1.12.2, 1.8.9, 1.7.10 | `cd deathnote-forge-<ver> && ./gradlew build` | 8 |

Build all Forge modules: `scripts/build-all-forge.sh`
