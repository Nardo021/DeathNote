package com.yaazyy.deathnote.forge189.recipe;

import com.yaazyy.deathnote.forge189.ModContext;
import com.yaazyy.deathnote.forge189.item.DeathNoteItem;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class Forge189Recipes {

    private Forge189Recipes() {
    }

    public static void register() {
        ModContext mod = ModContext.get();
        if (!mod.config().recipe().enabled) {
            return;
        }
        String preset = mod.config().recipe().difficultyPreset;
        if (preset == null || "disabled".equalsIgnoreCase(preset)) {
            return;
        }
        if ("extreme".equalsIgnoreCase(preset)) {
            registerExtreme();
        } else {
            registerHard();
        }
    }

    /** Hard preset: WNW / EBE / ITI */
    private static void registerHard() {
        ItemStack output = new ItemStack(DeathNoteItem.INSTANCE);
        GameRegistry.addRecipe(output,
                "WNW",
                "EBE",
                "ITI",
                'W', new ItemStack(Items.skull, 1, 1),
                'N', Items.nether_star,
                'E', Items.ender_eye,
                'B', Items.writable_book,
                'I', new ItemStack(Items.dye, 1, 0),
                'T', Items.name_tag);
    }

    /** Extreme preset: WNW / GBG / DTD */
    private static void registerExtreme() {
        ItemStack output = new ItemStack(DeathNoteItem.INSTANCE);
        GameRegistry.addRecipe(output,
                "WNW",
                "GBG",
                "DTD",
                'W', new ItemStack(Items.skull, 1, 1),
                'N', Items.nether_star,
                'G', Items.ghast_tear,
                'B', Items.writable_book,
                'D', Item.getItemFromBlock(Blocks.diamond_block),
                'T', Items.name_tag);
    }
}
