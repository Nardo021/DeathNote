package com.yaazyy.deathnote.bukkit.common.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Abstracts the Material/ItemStack differences between Minecraft versions.
 *
 * <p>Material enum names and data values changed dramatically at the 1.13
 * "flattening". Legacy uses {@code BOOK_AND_QUILL} and skull data values;
 * modern uses {@code WRITABLE_BOOK} and dedicated materials. Every place that
 * needs a concrete material goes through this adapter so the shared listeners
 * and recipe layout stay version-agnostic.</p>
 */
public interface MaterialAdapter {

    /** The base material the Death Note is disguised as (writable book). */
    Material baseBookMaterial();

    /** Whether the given material is the base writable-book material. */
    boolean isBaseBookMaterial(Material material);

    /** One base writable book item (the recipe output base / item creation). */
    ItemStack baseBookItem();

    // --- Recipe ingredients -------------------------------------------------

    ItemStack witherSkeletonSkull();

    ItemStack netherStar();

    ItemStack eyeOfEnder();

    ItemStack inkSac();

    ItemStack nameTag();

    ItemStack ghastTear();

    ItemStack diamondBlock();
}
