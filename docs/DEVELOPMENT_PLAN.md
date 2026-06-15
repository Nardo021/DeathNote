# Development Plan

Phased roadmap. Phases 1-4 are done in this repo; phases 5-6 remain scaffolded.

## Phase 1 - Bukkit modern (DONE)
- [x] Pure-Java core (service, validator, cooldown, signature, audit, strategy).
- [x] Platform API.
- [x] Bukkit common (platform, listeners, command, config, recipe layout).
- [x] Modern artifact: WRITABLE_BOOK + PersistentDataContainer + CustomModelData,
      NamespacedKey shaped recipe, `plugin.yml` with `api-version`.
- [x] Builds to a shaded plugin jar.

## Phase 2 - Bukkit legacy (DONE)
- [x] Java 8, no PDC / CustomModelData / NamespacedKey.
- [x] BOOK_AND_QUILL + hidden lore HMAC marker.
- [x] Legacy shaped recipe with data values via `MaterialAdapter`.
- [x] Shares all logic with the modern artifact through bukkit-common.
- [ ] Optional: dedicated 1.7.10 Bukkit adapter (int/double API differences).

## Phase 3 - Shared config module (DONE)
- [x] `deathnote-config-common`: SnakeYAML config/messages loader, `TextColors`, `SigningPayload`.
- [x] Bukkit common delegates to config-common (behavior unchanged).

## Phase 4 - Forge 1.7.10 through 1.20.1 (DONE)
- [x] `deathnote-forge-1.20.1`: ForgeGradle 6, Java 17, Mojang mappings, full mod.
- [x] `deathnote-forge-1.16.5`: ForgeGradle 6, MCP snapshot mappings, full mod.
- [x] `deathnote-forge-1.12.2`: independent Gradle 4.9, ForgeGradle 2.3, JSON recipe.
- [x] `deathnote-forge-1.8.9`: independent Gradle 4.9, ForgeGradle 2.1, `GameRegistry` recipe.
- [x] `deathnote-forge-1.7.10`: independent Gradle 4.10.3, ForgeGradle 1.2, FML.
- [x] `scripts/build-all-forge.sh` for unified Forge builds.

## Phase 5 - Fabric / NeoForge modern
- [ ] Fabric: Loom + Fabric API, `fabric.mod.json` entrypoints.
- [ ] NeoForge: NeoGradle, `neoforge.mods.toml`, data components.

## Phase 6 - Resource pack polish
- [ ] Replace the placeholder texture with final original art.
- [ ] Ship a versioned resource pack per `pack_format`.
- [ ] Optional server resource-pack push for the Bukkit-modern line.

## Cross-cutting backlog
- [ ] Optional chat-input debug mode (disabled by default) wiring.
- [ ] Configurable recipe `override` block parsing on the modern line.
- [ ] Metrics / richer audit sink (database) behind `AuditSink`.
- [ ] LuckPerms Forge integration (OP-level fallback is implemented).
