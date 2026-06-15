package com.yaazyy.deathnote.bukkit.legacy;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import com.yaazyy.deathnote.api.DeathNoteRecipeBridge;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;
import com.yaazyy.deathnote.bukkit.common.item.AbstractItemBridge;
import com.yaazyy.deathnote.bukkit.common.item.MaterialAdapter;
import com.yaazyy.deathnote.bukkit.common.recipe.RecipeLayout;

/**
 * Legacy recipe registration using the data-value aware
 * {@code ShapedRecipe(ItemStack)} + {@code setIngredient(char, Material, int)}
 * API (no NamespacedKey on pre-1.13).
 */
public final class LegacyRecipeBridge implements DeathNoteRecipeBridge {

    private final MaterialAdapter materials;
    private final AbstractItemBridge itemBridge;
    private final DeathNoteConfig config;

    public LegacyRecipeBridge(MaterialAdapter materials, AbstractItemBridge itemBridge,
                              DeathNoteConfig config) {
        this.materials = materials;
        this.itemBridge = itemBridge;
        this.config = config;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void registerRecipe() {
        RecipeLayout layout = RecipeLayout.forPreset(config.recipe().difficultyPreset, materials);
        if (layout == null) {
            return;
        }
        // Avoid accumulating duplicates across reloads.
        unregisterRecipe();

        ItemStack result = itemBridge.createDeathNoteItem();
        ShapedRecipe recipe = new ShapedRecipe(result);
        recipe.shape(layout.shape());
        for (Map.Entry<Character, ItemStack> entry : layout.ingredients().entrySet()) {
            ItemStack ing = entry.getValue();
            recipe.setIngredient(entry.getKey(), ing.getType(), ing.getDurability());
        }
        Bukkit.addRecipe(recipe);
    }

    @Override
    public void unregisterRecipe() {
        try {
            Iterator<Recipe> it = Bukkit.recipeIterator();
            while (it.hasNext()) {
                Recipe recipe = it.next();
                ItemStack out = recipe.getResult();
                if (out != null && itemBridge.isDeathNoteItem(out)) {
                    it.remove();
                }
            }
        } catch (UnsupportedOperationException ignored) {
            // Some legacy implementations forbid iterator removal; ignore.
        }
    }
}
