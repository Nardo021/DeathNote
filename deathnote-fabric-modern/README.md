# deathnote-fabric-modern (SCAFFOLD)

Fabric (modern, e.g. 1.20.x / 1.21.x) product line for the Death Note mod.

> **Status: SCAFFOLD / PLANNED.** Not wired into the root Gradle build; does not
> compile a working mod yet. See `IMPLEMENTATION_TODO.md`.

## Real custom item (mod line)

- Item id: `deathnote:death_note`
- JSON model + texture
- JSON data recipe (`data/deathnote/recipes/death_note.json`)
- Right-click opens a book-like input GUI; *Done* submits first page first line

## Why its own module

Fabric uses the **Fabric Loader + Fabric API + Yarn (or Mojang) mappings** and
the Loom build plugin - a completely different toolchain from Forge. Registration
uses `Registry.register(Registries.ITEM, ...)`, and the entrypoint is declared in
`fabric.mod.json`.

## Toolchain required

- Java 17+
- Fabric Loom, Fabric Loader, Fabric API for the target MC version

## Shared logic

Reuses `deathnote-core`. Implement the platform adapter + screen handler +
networking + registration.
