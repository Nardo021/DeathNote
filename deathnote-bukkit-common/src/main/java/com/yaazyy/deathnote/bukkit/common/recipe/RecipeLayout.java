package com.yaazyy.deathnote.bukkit.common.recipe;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.yaazyy.deathnote.bukkit.common.item.MaterialAdapter;

/**
 * Version-agnostic description of the Death Note crafting recipe.
 *
 * <p>This resolves a difficulty preset into a 3x3 shape plus the concrete
 * ingredient {@link ItemStack}s (via the {@link MaterialAdapter}). The actual
 * platform {@code ShapedRecipe} object is built by each module's
 * {@code DeathNoteRecipeBridge}, because the recipe constructor differs across
 * versions.</p>
 *
 * <pre>
 * hard:        extreme:
 *   W N W        W N W
 *   E B E        G B G
 *   I T I        D T D
 *
 * W = Wither Skeleton Skull   N = Nether Star   E = Eye of Ender
 * B = Writable Book (base)    I = Ink Sac       T = Name Tag
 * G = Ghast Tear              D = Diamond Block
 * </pre>
 */
public final class RecipeLayout {

    private final String[] shape;
    private final Map<Character, ItemStack> ingredients;

    private RecipeLayout(String[] shape, Map<Character, ItemStack> ingredients) {
        this.shape = shape;
        this.ingredients = ingredients;
    }

    public String[] shape() {
        return shape.clone();
    }

    public Map<Character, ItemStack> ingredients() {
        return ingredients;
    }

    /** The 3-row shape for a preset (for display), or {@code null} if disabled. */
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

    /**
     * Build the layout for a preset, or {@code null} when the preset is
     * {@code disabled} / unknown.
     */
    public static RecipeLayout forPreset(String preset, MaterialAdapter materials) {
        if (preset == null) {
            return null;
        }
        switch (preset.toLowerCase()) {
            case "hard":
                return hard(materials);
            case "extreme":
                return extreme(materials);
            case "disabled":
                return null;
            default:
                return hard(materials);
        }
    }

    private static RecipeLayout hard(MaterialAdapter m) {
        Map<Character, ItemStack> ing = new LinkedHashMap<>();
        ing.put('W', m.witherSkeletonSkull());
        ing.put('N', m.netherStar());
        ing.put('E', m.eyeOfEnder());
        ing.put('B', m.baseBookItem());
        ing.put('I', m.inkSac());
        ing.put('T', m.nameTag());
        return new RecipeLayout(new String[] {"WNW", "EBE", "ITI"}, ing);
    }

    private static RecipeLayout extreme(MaterialAdapter m) {
        Map<Character, ItemStack> ing = new LinkedHashMap<>();
        ing.put('W', m.witherSkeletonSkull());
        ing.put('N', m.netherStar());
        ing.put('G', m.ghastTear());
        ing.put('B', m.baseBookItem());
        ing.put('D', m.diamondBlock());
        ing.put('T', m.nameTag());
        return new RecipeLayout(new String[] {"WNW", "GBG", "DTD"}, ing);
    }
}
