package com.yaazyy.deathnote.bukkit.modern;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.yaazyy.deathnote.bukkit.common.item.MaterialAdapter;

/**
 * Modern (1.13+ "flattened") material mapping. All materials are dedicated
 * enum constants, so no data values are needed.
 */
public final class ModernMaterialAdapter implements MaterialAdapter {

    @Override
    public Material baseBookMaterial() {
        return Material.WRITABLE_BOOK;
    }

    @Override
    public boolean isBaseBookMaterial(Material material) {
        return material == Material.WRITABLE_BOOK;
    }

    @Override
    public ItemStack baseBookItem() {
        return new ItemStack(Material.WRITABLE_BOOK);
    }

    @Override
    public ItemStack witherSkeletonSkull() {
        return new ItemStack(Material.WITHER_SKELETON_SKULL);
    }

    @Override
    public ItemStack netherStar() {
        return new ItemStack(Material.NETHER_STAR);
    }

    @Override
    public ItemStack eyeOfEnder() {
        return new ItemStack(Material.ENDER_EYE);
    }

    @Override
    public ItemStack inkSac() {
        return new ItemStack(Material.INK_SAC);
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
