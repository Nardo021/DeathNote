package com.yaazyy.deathnote.forge1710.recipe;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.yaazyy.deathnote.forge1710.ModContext;

import cpw.mods.fml.common.registry.GameRegistry;

/** Registers shaped Death Note recipes for 1.7.10 item ids. */
public final class DeathNoteRecipes {

    private DeathNoteRecipes() {
    }

    public static void register(Item deathNoteItem) {
        if (!ModContext.get().config().recipe().enabled) {
            return;
        }
        String preset = ModContext.get().config().recipe().difficultyPreset;
        ItemStack result = new ItemStack(deathNoteItem);
        if ("extreme".equalsIgnoreCase(preset)) {
            GameRegistry.addRecipe(result,
                    "WNW",
                    "GBG",
                    "DTD",
                    'W', skull(1),
                    'N', new ItemStack(Items.nether_star),
                    'G', new ItemStack(Items.ghast_tear),
                    'B', new ItemStack(Items.writable_book),
                    'D', new ItemStack(Blocks.diamond_block),
                    'T', new ItemStack(Items.name_tag));
        } else if (!"disabled".equalsIgnoreCase(preset)) {
            GameRegistry.addRecipe(result,
                    "WNW",
                    "EBE",
                    "ITI",
                    'W', skull(1),
                    'N', new ItemStack(Items.nether_star),
                    'E', new ItemStack(Items.ender_eye),
                    'B', new ItemStack(Items.writable_book),
                    'I', new ItemStack(Items.dye, 1, 0),
                    'T', new ItemStack(Items.name_tag));
        }
    }

    private static ItemStack skull(int meta) {
        return new ItemStack(Items.skull, 1, meta);
    }
}
