package com.yaazyy.deathnote.bukkit.common.config;

import java.io.File;
import java.util.Map;

import org.bukkit.plugin.Plugin;

import com.yaazyy.deathnote.config.ConfigLoadResult;
import com.yaazyy.deathnote.config.YamlConfigLoader;
import com.yaazyy.deathnote.config.yaml.YamlFiles;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;

/** Bukkit adapter: loads YAML via {@link YamlConfigLoader} and persists generated secrets. */
public final class ConfigLoader {

    private ConfigLoader() {
    }

    public static DeathNoteConfig load(Plugin plugin) {
        try {
            File dataFolder = plugin.getDataFolder();
            Map<String, Object> configRoot = YamlFiles.load(new File(dataFolder, "config.yml"));
            File recipeFile = new File(dataFolder, "recipe.yml");
            Map<String, Object> recipeRoot = recipeFile.exists() ? YamlFiles.load(recipeFile) : null;

            ConfigLoadResult result = YamlConfigLoader.fromMaps(configRoot, recipeRoot, true);
            if (result.generatedSecret() != null) {
                YamlFiles.save(configRoot, new File(dataFolder, "config.yml"));
                plugin.getLogger().info("Generated a new Death Note HMAC secret in config.yml.");
            }
            return result.config();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load DeathNote config: " + e.getMessage());
            return DeathNoteConfig.defaults();
        }
    }
}
