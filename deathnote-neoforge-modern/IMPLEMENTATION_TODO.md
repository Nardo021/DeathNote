# Implementation TODO - NeoForge (modern)

## 0. Build setup
- [ ] `build.gradle` with NeoGradle / ModDevGradle, NeoForge for target MC, Java 21.
- [ ] Add to root `settings.gradle` once compiling.
- [ ] Depend on `:deathnote-core` and `:deathnote-platform-api`.

## 1. Mod bootstrap
- [ ] `@Mod("deathnote")` main class (`net.neoforged.fml`).
- [ ] `DeferredRegister.Items` for `death_note`; register on the mod bus.
- [ ] Load config; build `SignatureService`.

## 2. Custom item `deathnote:death_note`
- [ ] `Item` subclass; use data components or `CustomData` for the signing keys
      (`item_type`, `signature`, `created_at`, `created_by`, `uses`).
- [ ] `use(...)` opens the screen / sends open packet.

## 3. Recipe
- [ ] `data/deathnote/recipes/death_note.json` (hard preset).

## 4. Book screen + networking
- [ ] `Screen` for editing; NeoForge payload/network for the submit.

## 5. Platform adapter (`NeoForgeDeathNotePlatform`)
- [ ] Implement `DeathNotePlatform` (`ServerPlayer`, `MinecraftServer`).
- [ ] `executeKill`: `server.getCommands().performPrefixedCommand(source, "minecraft:kill " + name)`
      then `kill ...`, then native `hurt(...)`, then `setHealth(0)` -
      honour `KillStrategy` order.
- [ ] `DeathNoteItemBridge<ItemStack>` + `DeathNoteBookBridge`.

## 6. Server validation
- [ ] Call `DeathNoteService.process` with the validated name only.

## 7. Packaging
- [ ] Include `deathnote-core` + `deathnote-platform-api` (JiJ or shade).
- [ ] Copy assets from `../deathnote-assets`.
