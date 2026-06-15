package com.yaazyy.deathnote.bukkit.modern;

import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import com.yaazyy.deathnote.api.DeathNoteRecipeBridge;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;
import com.yaazyy.deathnote.bukkit.common.item.AbstractItemBridge;
import com.yaazyy.deathnote.bukkit.common.item.MaterialAdapter;
import com.yaazyy.deathnote.bukkit.common.recipe.RecipeLayout;

/**
 * Modern recipe registration using the NamespacedKey {@link ShapedRecipe}
 * constructor (1.13+).
 */
public final class ModernRecipeBridge implements DeathNoteRecipeBridge {

    private final Plugin plugin;
    private final MaterialAdapter materials;
    private final AbstractItemBridge itemBridge;
    private final DeathNoteConfig config;
    private final NamespacedKey recipeKey;

    public ModernRecipeBridge(Plugin plugin, MaterialAdapter materials,
                              AbstractItemBridge itemBridge, DeathNoteConfig config) {
        this.plugin = plugin;
        this.materials = materials;
        this.itemBridge = itemBridge;
        this.config = config;
        this.recipeKey = new NamespacedKey(plugin, "death_note");
    }

    @Override
    public void registerRecipe() {
        RecipeLayout layout = RecipeLayout.forPreset(config.recipe().difficultyPreset, materials);
        if (layout == null) {
            return;
        }
        ItemStack result = itemBridge.createDeathNoteItem();
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result);
        recipe.shape(layout.shape());
        for (Map.Entry<Character, ItemStack> entry : layout.ingredients().entrySet()) {
            recipe.setIngredient(entry.getKey(), entry.getValue().getType());
        }
        try {
            Bukkit.addRecipe(recipe);
        } catch (IllegalStateException duplicate) {
            // Already registered (e.g. after a partial reload); replace it.
            unregisterRecipe();
            Bukkit.addRecipe(recipe);
        }
    }

    @Override
    public void unregisterRecipe() {
        try {
            Bukkit.removeRecipe(recipeKey);
        } catch (Throwable t) {
            // removeRecipe may be unavailable on very old "modern" servers.
            plugin.getLogger().log(Level.FINE, "Could not remove recipe by key", t);
        }
    }
}
