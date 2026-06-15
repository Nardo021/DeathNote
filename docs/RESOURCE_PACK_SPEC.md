# Resource Pack Specification

How the **modern Bukkit** line gives the Death Note a unique look without adding
a new item ID.

## The core idea

- The Death Note is internally a vanilla `WRITABLE_BOOK`.
- A resource pack + `CustomModelData` makes *only* Death Note items render with
  the custom texture.
- **Ordinary writable books are unchanged.** The override only fires when the
  item's `CustomModelData` matches the configured value.

## How it works

1. Set `item.customModelData` in `config.yml` to a positive integer, e.g. `1001`.
2. The modern item bridge writes that value onto the item's meta.
3. The resource pack overrides the `writable_book` model with an `overrides`
   entry that switches to the Death Note model when
   `custom_model_data` equals that value.

## Expected file paths

```
assets/minecraft/models/item/writable_book.json    <- override table
assets/deathnote/models/item/death_note.json        <- the custom model
assets/deathnote/textures/item/death_note.png       <- the texture
pack.mcmeta
```

A ready-to-edit copy lives in `deathnote-assets/resourcepack/`.

### assets/minecraft/models/item/writable_book.json
```json
{
  "parent": "minecraft:item/generated",
  "textures": { "layer0": "minecraft:item/writable_book" },
  "overrides": [
    { "predicate": { "custom_model_data": 1001 }, "model": "deathnote:item/death_note" }
  ]
}
```

### assets/deathnote/models/item/death_note.json
```json
{
  "parent": "minecraft:item/generated",
  "textures": { "layer0": "deathnote:item/death_note" }
}
```

## Important rules

- The `custom_model_data` predicate value in the pack **must equal**
  `item.customModelData` in `config.yml` (default example: `1001`).
- Keep `pack.mcmeta`'s `pack_format` aligned with the target Minecraft version.
- The placeholder `death_note.png` is original art - replace it with your own.
  Do **not** use copyrighted Death Note anime assets.
- If `item.customModelData` is `-1` (default), no override fires and the item
  simply looks like a normal writable book - still fully functional.

## Mod line note

Forge/Fabric/NeoForge ship their own client assets and register a real
`deathnote:death_note` item, so they do **not** need a resource pack or
`CustomModelData`; they use `models/item/death_note.json` + the texture directly.
