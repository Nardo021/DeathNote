# deathnote-assets

Shared, **original** placeholder art and model/resource-pack files for the
Death Note project. No copyrighted "Death Note" anime assets are used anywhere
in this repo. Replace the placeholder texture with your own final artwork later.

## Contents

```
textures/item/death_note.png        Placeholder 16x16 item texture (original)
models/item/death_note.json         Generic generated item model
resourcepack/                        Drop-in resource pack for the Bukkit-modern line
  pack.mcmeta
  assets/minecraft/models/item/writable_book.json   CustomModelData override
  assets/deathnote/models/item/death_note.json       Custom model
  assets/deathnote/textures/item/death_note.png      Texture used by the model
```

## Placeholder texture

`death_note.png` is a tiny, hand-generated placeholder: a dark notebook cover
with a red spine and two faint page lines. It exists only so the model/resource
pack resolve without errors. Draw the real 16x16 (or higher-res) texture later
and overwrite this file.

## Mod line

The Forge / Fabric / NeoForge modules register a real `deathnote:death_note`
item and should copy `models/item/death_note.json` and
`textures/item/death_note.png` into their own
`assets/deathnote/...` resource folders.

## Bukkit modern line

The Bukkit item is internally a `WRITABLE_BOOK`; the custom look is achieved
with a resource pack + `CustomModelData`. See `docs/RESOURCE_PACK_SPEC.md`.
