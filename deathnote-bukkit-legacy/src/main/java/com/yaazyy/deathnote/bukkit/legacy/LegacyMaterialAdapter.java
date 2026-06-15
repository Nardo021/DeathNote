package com.yaazyy.deathnote.bukkit.legacy;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.yaazyy.deathnote.bukkit.common.item.MaterialAdapter;

/**
 * Legacy (pre-1.13) material mapping. Uses legacy Material names and data
 * values (e.g. {@code SKULL_ITEM} with data {@code 1} for a wither skeleton
 * skull, {@code BOOK_AND_QUILL} for the writable book).
 */
public final class LegacyMaterialAdapter implements MaterialAdapter {

    /** Data value for a wither skeleton skull on the legacy SKULL_ITEM. */
    private static final short WITHER_SKELETON_SKULL_DATA = 1;

    @Override
    public Material baseBookMaterial() {
        return Material.BOOK_AND_QUILL;
    }

    @Override
    public boolean isBaseBookMaterial(Material material) {
        return material == Material.BOOK_AND_QUILL;
    }

    @Override
    public ItemStack baseBookItem() {
        return new ItemStack(Material.BOOK_AND_QUILL);
    }

    @Override
    public ItemStack witherSkeletonSkull() {
        return new ItemStack(Material.SKULL_ITEM, 1, WITHER_SKELETON_SKULL_DATA);
    }

    @Override
    public ItemStack netherStar() {
        return new ItemStack(Material.NETHER_STAR);
    }

    @Override
    public ItemStack eyeOfEnder() {
        return new ItemStack(Material.EYE_OF_ENDER);
    }

    @Override
    public ItemStack inkSac() {
        // INK_SACK with data 0 is the black ink sac.
        return new ItemStack(Material.INK_SACK, 1, (short) 0);
    }

    @Override
    public ItemStack nameTag() {
        return new ItemStack(Material.NAME_TAG);
    }

    @Override
    public ItemStack ghastTear() {
        return new ItemStack(Material.GHAST_TEAR);
    }

    @Override
    public ItemStack diamondBlock() {
        return new ItemStack(Material.DIAMOND_BLOCK);
    }
}
