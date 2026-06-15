package com.yaazyy.deathnote.config.text;

import java.util.ArrayList;
import java.util.List;

/** Translates {@code &} colour codes to Minecraft section-sign codes without Bukkit. */
public final class TextColors {

    private static final char SECTION = '\u00A7';

    private TextColors() {
    }

    public static String translate(String input) {
        if (input == null || input.isEmpty()) {
            return input == null ? "" : input;
        }
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '&' && isColorCodeChar(chars[i + 1])) {
                chars[i] = SECTION;
            }
        }
        return new String(chars);
    }

    public static List<String> translate(List<String> lines) {
        List<String> out = new ArrayList<>();
        if (lines != null) {
            for (String line : lines) {
                out.add(translate(line));
            }
        }
        return out;
    }

    private static boolean isColorCodeChar(char c) {
        String codes = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
        return codes.indexOf(c) >= 0;
    }
}
