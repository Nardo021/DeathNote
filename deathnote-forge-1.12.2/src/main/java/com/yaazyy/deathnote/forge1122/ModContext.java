package com.yaazyy.deathnote.forge1122;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import org.apache.logging.log4j.Logger;

import com.yaazyy.deathnote.config.ConfigLoadResult;
import com.yaazyy.deathnote.config.DeathNoteMessages;
import com.yaazyy.deathnote.config.YamlConfigLoader;
import com.yaazyy.deathnote.config.yaml.YamlFiles;
import com.yaazyy.deathnote.core.CooldownService;
import com.yaazyy.deathnote.core.DeathNoteService;
import com.yaazyy.deathnote.core.SignatureService;
import com.yaazyy.deathnote.core.audit.AuditLog;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;
import com.yaazyy.deathnote.forge1122.audit.ForgeFileAuditSink;
import com.yaazyy.deathnote.forge1122.item.DeathNoteItems;
import com.yaazyy.deathnote.forge1122.item.Forge1122ItemBridge;

import net.minecraftforge.fml.common.Loader;

/**
 * Holds wired Death Note services for the Forge 1.12.2 mod.
 */
public final class ModContext {

    private static ModContext instance;

    private final File configDir;
    private DeathNoteConfig config;
    private SignatureService signatures;
    private Forge1122ItemBridge itemBridge;
    private Forge1122DeathNotePlatform platform;
    private CooldownService cooldowns;
    private AuditLog auditLog;
    private DeathNoteService service;
    private DeathNoteMessages messages;
    private Logger logger;

    private ModContext(File configDir) {
        this.configDir = configDir;
    }

    public static ModContext get() {
        if (instance == null) {
            throw new IllegalStateException("ModContext not initialized");
        }
        return instance;
    }

    public static void init(Logger logger) {
        File dir = new File(Loader.instance().getConfigDir(), "deathnote");
        instance = new ModContext(dir);
        instance.logger = logger;
        instance.ensureDefaults();
        instance.wire();
    }

    public void reload() {
        wire();
    }

    private void ensureDefaults() {
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        copyIfMissing("config.yml");
        copyIfMissing("messages.yml");
        copyIfMissing("recipe.yml");
    }

    private void copyIfMissing(String name) {
        File target = new File(configDir, name);
        if (target.exists()) {
            return;
        }
        try (InputStream in = ModContext.class.getResourceAsStream("/" + name)) {
            if (in == null) {
                logger.warn("Missing bundled default: " + name);
                return;
            }
            Files.copy(in, target.toPath());
        } catch (IOException e) {
            logger.warn("Failed to copy default " + name + ": " + e.getMessage());
        }
    }

    private void wire() {
        try {
            Map<String, Object> configRoot = YamlFiles.load(new File(configDir, "config.yml"));
            Map<String, Object> recipeRoot = YamlFiles.load(new File(configDir, "recipe.yml"));
            ConfigLoadResult load = YamlConfigLoader.fromMaps(configRoot, recipeRoot, true);
            this.config = load.config();
            if (load.generatedSecret() != null) {
                configRoot.put("security.hmacSecret", load.generatedSecret());
                YamlFiles.save(configRoot, new File(configDir, "config.yml"));
            }
        } catch (IOException e) {
            logger.error("Failed to load config, using defaults: " + e.getMessage());
            this.config = DeathNoteConfig.defaults();
        }

        this.signatures = new SignatureService(config.security().hmacSecret);
        this.itemBridge = new Forge1122ItemBridge(config, signatures, DeathNoteItems.DEATH_NOTE);
        this.platform = new Forge1122DeathNotePlatform(config);
        this.cooldowns = new CooldownService();
        this.auditLog = new AuditLog(new ForgeFileAuditSink(configDir, logger));
        this.service = new DeathNoteService(config, platform, cooldowns, auditLog);
        try {
            this.messages = DeathNoteMessages.load(new File(configDir, "messages.yml"));
        } catch (IOException e) {
            this.messages = DeathNoteMessages.empty();
        }
    }

    public DeathNoteConfig config() {
        return config;
    }

    public Forge1122ItemBridge itemBridge() {
        return itemBridge;
    }

    public Forge1122DeathNotePlatform platform() {
        return platform;
    }

    public DeathNoteService service() {
        return service;
    }

    public DeathNoteMessages messages() {
        return messages;
    }

    public File configDir() {
        return configDir;
    }

    public Logger logger() {
        return logger;
    }
}
