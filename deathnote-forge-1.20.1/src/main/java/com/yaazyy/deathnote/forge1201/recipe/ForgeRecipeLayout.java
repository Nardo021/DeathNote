package com.yaazyy.deathnote.forge1201.recipe;

/** Recipe layout strings for command display (mirrors Bukkit {@code RecipeLayout}). */
public final class ForgeRecipeLayout {

    private ForgeRecipeLayout() {
    }

    public static String[] shapeForPreset(String preset) {
        if (preset == null) {
            return null;
        }
        switch (preset.toLowerCase()) {
            case "extreme":
                return new String[] {"WNW", "GBG", "DTD"};
            case "disabled":
                return null;
            case "hard":
            default:
                return new String[] {"WNW", "EBE", "ITI"};
        }
    }
}
