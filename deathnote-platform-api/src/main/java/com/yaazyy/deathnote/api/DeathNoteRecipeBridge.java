package com.yaazyy.deathnote.api;

/**
 * Platform bridge for registering / removing the crafting recipe.
 *
 * <p>Recipe construction differs significantly between platform versions
 * (legacy Bukkit {@code ShapedRecipe(ItemStack)} vs modern
 * {@code ShapedRecipe(NamespacedKey, ItemStack)}), so each adapter implements
 * this directly while the shared recipe layout/preset logic lives in the core
 * / common module.</p>
 */
public interface DeathNoteRecipeBridge {

    /** Register the configured Death Note recipe with the server. */
    void registerRecipe();

    /** Remove the Death Note recipe (used on reload / disable). */
    void unregisterRecipe();
}
