# Implementation TODO - Fabric (modern)

## 0. Build setup
- [ ] `build.gradle` with Fabric Loom; pin MC + Yarn + Fabric API + Loader.
- [ ] Add to root `settings.gradle` once compiling.
- [ ] Depend on `:deathnote-core` and `:deathnote-platform-api`
      (include them via `include`/JiJ or shade).

## 1. Mod bootstrap
- [ ] `ModInitializer` (+ optional `ClientModInitializer`) declared in
      `fabric.mod.json`.
- [ ] Register item; load config; build `SignatureService`.

## 2. Custom item `deathnote:death_note`
- [ ] `Item` subclass; store signing keys in NBT (`item_type`, `signature`,
      `created_at`, `created_by`, `uses`).
- [ ] `use(...)` opens the screen / sends open packet.

## 3. Recipe
- [ ] `data/deathnote/recipes/death_note.json` (hard preset).

## 4. Book screen + networking
- [ ] Client `Screen` for editing; `ServerPlayNetworking` packet with the
      first page first line.

## 5. Platform adapter (`FabricDeathNotePlatform`)
- [ ] Implement `DeathNotePlatform` (`ServerPlayerEntity`, `MinecraftServer`).
- [ ] `executeKill`: `server.getCommandManager().executeWithPrefix(source, "minecraft:kill " + name)`
      then `kill ...`, then native `damage(...)`, then `setHealth(0)` -
      honour `KillStrategy` order.
- [ ] `DeathNoteItemBridge<ItemStack>` + `DeathNoteBookBridge<NbtCompound>`.

## 6. Server validation
- [ ] Call `DeathNoteService.process` with the validated name only.

## 7. Packaging
- [ ] Include `deathnote-core` + `deathnote-platform-api` (JiJ or shade).
- [ ] Copy assets from `../deathnote-assets`.
