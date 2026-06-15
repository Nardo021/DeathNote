# deathnote-neoforge-modern (SCAFFOLD)

NeoForge (modern, 1.20.4+ / 1.21.x) product line for the Death Note mod.

> **Status: SCAFFOLD / PLANNED.** Not wired into the root Gradle build; does not
> compile a working mod yet. See `IMPLEMENTATION_TODO.md`.

## Real custom item (mod line)

- Item id: `deathnote:death_note`
- JSON model + texture
- JSON data recipe (`data/deathnote/recipes/death_note.json`)
- Right-click opens a book-like input GUI; *Done* submits first page first line

## Why its own module

NeoForge forked from Forge and now has its own toolchain (NeoGradle / ModDevGradle),
its own event bus package (`net.neoforged.*`), and `neoforge.mods.toml` metadata.
It is **not** binary-compatible with Forge 1.20.1, hence a dedicated module.

## Toolchain required

- Java 21 (NeoForge 1.20.4+ targets 21)
- NeoGradle or ModDevGradle, NeoForge for the target MC version

## Shared logic

Reuses `deathnote-core`. Implement the platform adapter + screen + networking +
registration.
