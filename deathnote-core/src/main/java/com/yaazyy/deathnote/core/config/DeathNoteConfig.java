package com.yaazyy.deathnote.core.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Platform-agnostic configuration model.
 *
 * <p>Adapters are responsible for loading their native config format (e.g.
 * Bukkit {@code config.yml}) and populating this object. The core only ever
 * reads from here, so it never touches a YAML/TOML library.</p>
 *
 * <p>Fields are grouped to mirror the dotted config keys
 * ({@code input.firstPageOnly}, {@code kill.delayTicks}, ...).</p>
 */
public final class DeathNoteConfig {

    private final Input input;
    private final Kill kill;
    private final Permissions permissions;
    private final Cooldown cooldown;
    private final Item item;
    private final Recipe recipe;
    private final Security security;

    private DeathNoteConfig(Builder b) {
        this.input = b.input;
        this.kill = b.kill;
        this.permissions = b.permissions;
        this.cooldown = b.cooldown;
        this.item = b.item;
        this.recipe = b.recipe;
        this.security = b.security;
    }

    public Input input() { return input; }
    public Kill kill() { return kill; }
    public Permissions permissions() { return permissions; }
    public Cooldown cooldown() { return cooldown; }
    public Item item() { return item; }
    public Recipe recipe() { return recipe; }
    public Security security() { return security; }

    /** A configuration filled entirely with the documented defaults. */
    public static DeathNoteConfig defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    // ----------------------------------------------------------------- groups

    /** {@code input.*} */
    public static final class Input {
        public boolean firstPageOnly = true;
        public boolean firstLineOnly = true;
        public boolean clearBookAfterUse = true;
        public boolean consumeOnUse = false;
    }

    /** {@code kill.*} */
    public static final class Kill {
        public long delayTicks = 1L;
        public boolean preferMinecraftNamespaceCommand = true;
        public boolean allowFallbackNativeKill = true;
        public boolean allowFallbackSetHealth = true;
    }

    /** {@code permissions.*} */
    public static final class Permissions {
        public String use = "deathnote.use";
        public String give = "deathnote.give";
        public String admin = "deathnote.admin";
        public String bypass = "deathnote.bypass";
    }

    /** {@code cooldown.*} */
    public static final class Cooldown {
        public boolean enabled = true;
        public long seconds = 60L;
    }

    /** {@code item.*} */
    public static final class Item {
        public String displayName = "Death Note";
        public List<String> lore = new ArrayList<>();
        /** -1 means "do not set CustomModelData". */
        public int customModelData = -1;

        public Item() {
            lore.add("&8A notebook that decides fate.");
            lore.add("&7Write a name. Click Done.");
        }
    }

    /** {@code recipe.*} */
    public static final class Recipe {
        public boolean enabled = true;
        public String difficultyPreset = "hard";
    }

    /** {@code security.*} */
    public static final class Security {
        /** Empty means "generate on first startup". */
        public String hmacSecret = "";
        /** Block targeting yourself. */
        public boolean blockSelfTarget = true;
        /** Treat operators as immune. */
        public boolean blockOpTarget = true;
        /** Chat-input debug fallback. Disabled by default. */
        public boolean debugChatInput = false;
    }

    // ----------------------------------------------------------------- builder

    public static final class Builder {
        private Input input = new Input();
        private Kill kill = new Kill();
        private Permissions permissions = new Permissions();
        private Cooldown cooldown = new Cooldown();
        private Item item = new Item();
        private Recipe recipe = new Recipe();
        private Security security = new Security();

        public Builder input(Input v) { this.input = v; return this; }
        public Builder kill(Kill v) { this.kill = v; return this; }
        public Builder permissions(Permissions v) { this.permissions = v; return this; }
        public Builder cooldown(Cooldown v) { this.cooldown = v; return this; }
        public Builder item(Item v) { this.item = v; return this; }
        public Builder recipe(Recipe v) { this.recipe = v; return this; }
        public Builder security(Security v) { this.security = v; return this; }

        public DeathNoteConfig build() {
            return new DeathNoteConfig(this);
        }
    }
}
