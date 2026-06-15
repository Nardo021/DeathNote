package com.yaazyy.deathnote.bukkit.common;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import com.yaazyy.deathnote.api.DeathNoteRecipeBridge;
import com.yaazyy.deathnote.core.CooldownService;
import com.yaazyy.deathnote.core.DeathNoteService;
import com.yaazyy.deathnote.core.SignatureService;
import com.yaazyy.deathnote.core.audit.AuditLog;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;
import com.yaazyy.deathnote.bukkit.common.audit.FileAuditSink;
import com.yaazyy.deathnote.bukkit.common.book.BukkitBookBridge;
import com.yaazyy.deathnote.bukkit.common.command.DeathNoteCommand;
import com.yaazyy.deathnote.bukkit.common.config.ConfigLoader;
import com.yaazyy.deathnote.bukkit.common.config.Messages;
import com.yaazyy.deathnote.bukkit.common.item.AbstractItemBridge;
import com.yaazyy.deathnote.bukkit.common.item.MaterialAdapter;
import com.yaazyy.deathnote.bukkit.common.listener.DeathNoteBookEditListener;
import com.yaazyy.deathnote.bukkit.common.listener.DeathNoteCraftListener;
import com.yaazyy.deathnote.bukkit.common.listener.DeathNoteInteractListener;
import com.yaazyy.deathnote.bukkit.common.listener.DeathNoteJoinListener;
import com.yaazyy.deathnote.bukkit.common.platform.BukkitPlatform;

/**
 * Shared {@link JavaPlugin} that wires the core to Bukkit. The legacy and
 * modern modules subclass this and provide the version-specific factories
 * ({@link #createMaterialAdapter()}, {@link #createItemBridge}, and
 * {@link #createRecipeBridge}).
 */
public abstract class AbstractDeathNotePlugin extends JavaPlugin {

    private DeathNoteConfig config;
    private SignatureService signatures;
    private MaterialAdapter materials;
    private AbstractItemBridge itemBridge;
    private BukkitBookBridge bookBridge;
    private BukkitPlatform platform;
    private CooldownService cooldowns;
    private AuditLog auditLog;
    private DeathNoteService service;
    private DeathNoteRecipeBridge recipeBridge;
    private Messages messages;

    // --- version-specific factories ----------------------------------------

    protected abstract MaterialAdapter createMaterialAdapter();

    protected abstract AbstractItemBridge createItemBridge(DeathNoteConfig config,
                                                           SignatureService signatures,
                                                           MaterialAdapter materials);

    protected abstract DeathNoteRecipeBridge createRecipeBridge(MaterialAdapter materials,
                                                                AbstractItemBridge itemBridge,
                                                                DeathNoteConfig config);

    // --- lifecycle ----------------------------------------------------------

    @Override
    public void onEnable() {
        saveDefaultResources();
        wire();

        getServer().getPluginManager().registerEvents(new DeathNoteBookEditListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathNoteInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathNoteCraftListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathNoteJoinListener(this), this);

        DeathNoteCommand command = new DeathNoteCommand(this);
        if (getCommand("deathnote") != null) {
            getCommand("deathnote").setExecutor(command);
            getCommand("deathnote").setTabCompleter(command);
        }

        if (config.recipe().enabled && recipeBridge != null) {
            recipeBridge.registerRecipe();
        }

        getLogger().info("DeathNote enabled on " + platform.platformName()
                + " (" + platform.platformVersion() + ").");
    }

    @Override
    public void onDisable() {
        if (recipeBridge != null) {
            recipeBridge.unregisterRecipe();
        }
    }

    /** Reload config files and rebuild the wired services and recipe. */
    public void reloadEverything() {
        if (recipeBridge != null) {
            recipeBridge.unregisterRecipe();
        }
        reloadConfig();
        wire();
        if (config.recipe().enabled && recipeBridge != null) {
            recipeBridge.registerRecipe();
        }
    }

    private void wire() {
        this.config = ConfigLoader.load(this);
        this.signatures = new SignatureService(config.security().hmacSecret);
        this.materials = createMaterialAdapter();
        this.itemBridge = createItemBridge(config, signatures, materials);
        this.bookBridge = new BukkitBookBridge();
        this.platform = new BukkitPlatform(this);
        this.cooldowns = new CooldownService();
        this.auditLog = new AuditLog(new FileAuditSink(getDataFolder(), getLogger()));
        this.service = new DeathNoteService(config, platform, cooldowns, auditLog);
        this.messages = Messages.load(new File(getDataFolder(), "messages.yml"));
        this.recipeBridge = createRecipeBridge(materials, itemBridge, config);
    }

    private void saveDefaultResources() {
        saveDefaultConfig();
        if (!new File(getDataFolder(), "messages.yml").exists()) {
            saveResource("messages.yml", false);
        }
        if (!new File(getDataFolder(), "recipe.yml").exists()) {
            saveResource("recipe.yml", false);
        }
    }

    // --- accessors used by listeners / commands -----------------------------

    public DeathNoteConfig dnConfig() {
        return config;
    }

    public AbstractItemBridge itemBridge() {
        return itemBridge;
    }

    public BukkitBookBridge bookBridge() {
        return bookBridge;
    }

    public DeathNoteService service() {
        return service;
    }

    public BukkitPlatform platform() {
        return platform;
    }

    public Messages messages() {
        return messages;
    }

    public DeathNoteRecipeBridge recipeBridge() {
        return recipeBridge;
    }
}
