package com.yaazyy.deathnote.config;

import java.util.List;
import java.util.Map;

import com.yaazyy.deathnote.config.yaml.YamlMaps;
import com.yaazyy.deathnote.core.SignatureService;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;

/**
 * Maps {@code config.yml} and {@code recipe.yml} YAML maps into {@link DeathNoteConfig}.
 */
public final class YamlConfigLoader {

    private YamlConfigLoader() {
    }

    public static DeathNoteConfig fromMaps(Map<String, Object> configRoot, Map<String, Object> recipeRoot) {
        return fromMaps(configRoot, recipeRoot, false).config();
    }

    /**
     * @param persistGeneratedSecret when {@code true} and a secret was generated,
     *                               {@link ConfigLoadResult#generatedSecret()} is set
     *                               so the caller can write it back to disk.
     */
    public static ConfigLoadResult fromMaps(Map<String, Object> configRoot,
                                            Map<String, Object> recipeRoot,
                                            boolean persistGeneratedSecret) {
        DeathNoteConfig.Builder builder = DeathNoteConfig.builder();

        DeathNoteConfig.Input input = new DeathNoteConfig.Input();
        input.firstPageOnly = YamlMaps.getBoolean(configRoot, "input.firstPageOnly", true);
        input.firstLineOnly = YamlMaps.getBoolean(configRoot, "input.firstLineOnly", true);
        input.clearBookAfterUse = YamlMaps.getBoolean(configRoot, "input.clearBookAfterUse", true);
        input.consumeOnUse = YamlMaps.getBoolean(configRoot, "input.consumeOnUse", false);
        builder.input(input);

        DeathNoteConfig.Kill kill = new DeathNoteConfig.Kill();
        kill.delayTicks = YamlMaps.getLong(configRoot, "kill.delayTicks", 1L);
        kill.preferMinecraftNamespaceCommand =
                YamlMaps.getBoolean(configRoot, "kill.preferMinecraftNamespaceCommand", true);
        kill.allowFallbackNativeKill =
                YamlMaps.getBoolean(configRoot, "kill.allowFallbackNativeKill", true);
        kill.allowFallbackSetHealth =
                YamlMaps.getBoolean(configRoot, "kill.allowFallbackSetHealth", true);
        builder.kill(kill);

        DeathNoteConfig.Permissions perms = new DeathNoteConfig.Permissions();
        perms.use = YamlMaps.getString(configRoot, "permissions.use", "deathnote.use");
        perms.give = YamlMaps.getString(configRoot, "permissions.give", "deathnote.give");
        perms.admin = YamlMaps.getString(configRoot, "permissions.admin", "deathnote.admin");
        perms.bypass = YamlMaps.getString(configRoot, "permissions.bypass", "deathnote.bypass");
        builder.permissions(perms);

        DeathNoteConfig.Cooldown cooldown = new DeathNoteConfig.Cooldown();
        cooldown.enabled = YamlMaps.getBoolean(configRoot, "cooldown.enabled", true);
        cooldown.seconds = YamlMaps.getLong(configRoot, "cooldown.seconds", 60L);
        builder.cooldown(cooldown);

        DeathNoteConfig.Item item = new DeathNoteConfig.Item();
        item.displayName = YamlMaps.getString(configRoot, "item.displayName", "&cDeath Note");
        List<String> lore = YamlMaps.getStringList(configRoot, "item.lore");
        if (lore != null && !lore.isEmpty()) {
            item.lore.clear();
            item.lore.addAll(lore);
        }
        item.customModelData = YamlMaps.getInt(configRoot, "item.customModelData", -1);
        builder.item(item);

        DeathNoteConfig.Security security = new DeathNoteConfig.Security();
        security.hmacSecret = YamlMaps.getString(configRoot, "security.hmacSecret", "");
        security.blockSelfTarget = YamlMaps.getBoolean(configRoot, "security.blockSelfTarget", true);
        security.blockOpTarget = YamlMaps.getBoolean(configRoot, "security.blockOpTarget", true);
        security.debugChatInput = YamlMaps.getBoolean(configRoot, "security.debugChatInput", false);

        String generatedSecret = null;
        if (security.hmacSecret == null || security.hmacSecret.trim().isEmpty()) {
            security.hmacSecret = SignatureService.generateSecret();
            if (persistGeneratedSecret) {
                YamlMaps.set(configRoot, "security.hmacSecret", security.hmacSecret);
                generatedSecret = security.hmacSecret;
            }
        }
        builder.security(security);

        DeathNoteConfig.Recipe recipe = new DeathNoteConfig.Recipe();
        if (recipeRoot != null) {
            recipe.enabled = YamlMaps.getBoolean(recipeRoot, "recipe.enabled", true);
            recipe.difficultyPreset = YamlMaps.getString(recipeRoot, "recipe.difficultyPreset", "hard");
        }
        builder.recipe(recipe);

        return new ConfigLoadResult(builder.build(), generatedSecret);
    }
}
