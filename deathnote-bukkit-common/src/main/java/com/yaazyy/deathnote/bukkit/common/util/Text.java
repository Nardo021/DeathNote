package com.yaazyy.deathnote.bukkit.common.util;

import java.util.ArrayList;
import java.util.List;

import com.yaazyy.deathnote.config.text.TextColors;

/** Small text helpers shared across the Bukkit modules. */
public final class Text {

    private Text() {
    }

    /** Translate {@code &}-style colour codes. */
    public static String color(String input) {
        if (input == null) {
            return "";
        }
        return TextColors.translate(input);
    }

    /** Translate colour codes for a whole list. */
    public static List<String> color(List<String> input) {
        return TextColors.translate(input);
    }
}
