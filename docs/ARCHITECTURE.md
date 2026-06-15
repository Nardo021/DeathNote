# Architecture

DeathNote is a **hybrid, multi-artifact monorepo**, not a single universal jar.
This document explains why, and how the pieces fit together.

## Layered design

```
                +-----------------------------+
                |     deathnote-platform-api  |   pure Java, no game deps
                |  DNPlayer, DeathNotePlatform|
                |  *Bridge, KillRequest, ...  |
                +--------------+--------------+
                               ^
                               | depends on
                +--------------+--------------+
                |        deathnote-core       |   pure Java, no game deps
                |  DeathNoteService, Config,  |
                |  NameValidator, Cooldown,   |
                |  SignatureService, KillStrategy, Audit
                +--------------+--------------+
                               ^
        +----------------------+-----------------------+
        |                                              |
+-------+--------------------+              +----------+-----------------+
|  deathnote-bukkit-common   |              |  mod adapters (scaffold)   |
|  Bukkit platform + listeners|             |  forge-1.7.10 ... neoforge |
|  command, config, recipe    |             |  each: own toolchain       |
+-------+-------------+-------+              +----------------------------+
        ^             ^
+-------+----+  +-----+-------+
| bukkit-    |  | bukkit-     |
| legacy     |  | modern      |   <- final loadable plugin jars
| (1.8-1.12) |  | (1.13+)     |
+------------+  +-------------+
```

- **deathnote-platform-api** - the contract. Pure Java. No Bukkit/Forge/MC.
- **deathnote-core** - all the game-agnostic logic. Pure Java. No Bukkit/Forge/MC.
  Depends only on the platform-api. This is where the rules actually live, and
  it is fully unit-tested.
- **deathnote-bukkit-common** - shared Bukkit code (platform impl, listeners,
  command, config loading, recipe layout, item base class). Compiled against an
  old Spigot API so the shared bytecode runs on every supported server.
- **deathnote-bukkit-legacy / -modern** - thin final artifacts that plug in the
  version-specific bits (materials, signing storage, recipe construction) and
  ship a loadable plugin jar.
- **mod adapters** - one module per Forge/Fabric/NeoForge target. Scaffolded.

## Why Bukkit cannot truly add a new item ID

Bukkit/Spigot/Paper are **server-side** APIs. The set of item IDs is defined by
the **client** (the vanilla item registry). A server plugin cannot register a
new client-recognised item type, because the client would have no idea how to
render or network an unknown ID. There is no server-only "create item type" API.

Therefore the plugin line disguises a **real vanilla item** - a writable book
(`BOOK_AND_QUILL` on legacy, `WRITABLE_BOOK` on modern) - and marks it as a
Death Note using:

- a display name + lore,
- a verifiable **HMAC signature** (hidden lore on legacy, PersistentDataContainer
  on modern),
- optional **CustomModelData** + a resource pack to give it a unique look.

The book GUI is vanilla, which is exactly why we use it for input: the player
writes a name and clicks *Done*, and we read it via `PlayerEditBookEvent`.

## Why mod platforms use a real custom item

Forge/Fabric/NeoForge run on **both client and server** and ship their own
assets to the client. They *can* register a brand-new item
(`deathnote:death_note`) with its own model, texture and recipe, because the mod
is present on the client too. So the mod line implements a genuine custom item
rather than a disguised book.

## Why "full compatibility" means multiple artifacts

The toolchains and APIs are mutually incompatible:

- Bukkit 1.7.10 vs 1.8 changed primitive signatures (`getHealth()` int->double,
  `getOnlinePlayers()` array->collection).
- The 1.13 "flattening" rewrote the Material enum and removed data values.
- Forge 1.7.10 / 1.8.9 / 1.12.2 / 1.16.5 / 1.20.1 each use different
  ForgeGradle versions, mappings, registration APIs and metadata files.
- Fabric and NeoForge are entirely separate toolchains.

A single jar physically cannot satisfy all of these. The honest, maintainable
answer is: **share the logic once (core), adapt per platform, ship many jars.**

## Data / control flow (Bukkit)

1. Player crafts or is given a Death Note (a signed writable book).
2. Player right-clicks -> vanilla book GUI opens.
3. Player types the target name on the first line, clicks *Done*.
4. `DeathNoteBookEditListener` verifies the item's signature, reads the first
   line, sanitizes the book, and schedules processing on the next sync tick.
5. `DeathNoteService` (core) validates the name, checks permission, self-target,
   immunity and cooldown, then asks the platform to execute the kill.
6. `BukkitPlatform.executeKill` tries the strategies in priority order using the
   **validated** name only, on the main thread.
7. An `AuditEntry` is always written; the actor receives a result message.
