package com.yaazyy.deathnote.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.yaazyy.deathnote.config.text.TextColors;
import com.yaazyy.deathnote.config.yaml.YamlFiles;
import com.yaazyy.deathnote.config.yaml.YamlMaps;

/**
 * Loads {@code messages.yml} and resolves prefixed, coloured player-facing strings.
 */
public final class DeathNoteMessages {

    private final Map<String, Object> root;
    private final String prefix;

    private DeathNoteMessages(Map<String, Object> root) {
        this.root = root;
        this.prefix = YamlMaps.getString(root, "prefix", "&8[&cDeathNote&8] &r");
    }

    public static DeathNoteMessages load(File file) throws IOException {
        return new DeathNoteMessages(YamlFiles.load(file));
    }

    /** Empty messages used when the file cannot be loaded. */
    public static DeathNoteMessages empty() {
        return new DeathNoteMessages(new java.util.HashMap<String, Object>());
    }

    public String prefix() {
        return TextColors.translate(prefix);
    }

    public String raw(String dottedKey) {
        return TextColors.translate(YamlMaps.getString(root, dottedKey, dottedKey));
    }

    public String get(String dottedKey) {
        return prefix() + raw(dottedKey);
    }

    public String get(String dottedKey, String... replacements) {
        String message = raw(dottedKey);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
        }
        return prefix() + message;
    }
}
