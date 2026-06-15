package com.yaazyy.deathnote.api;

/**
 * Immutable description of a requested kill, handed by the core to the
 * platform. It carries only the sanitized data and the strategy toggles the
 * platform needs to execute the kill in the correct priority order.
 *
 * <p>The {@code targetName} stored here has ALREADY been validated by the
 * core's {@code NameValidator}. The platform must still use this exact value
 * and never re-derive a name from raw user input.</p>
 */
public final class KillRequest {

    private final String targetName;
    private final boolean preferMinecraftNamespaceCommand;
    private final boolean allowFallbackNativeKill;
    private final boolean allowFallbackSetHealth;
    private final double nativeDamageAmount;

    private KillRequest(Builder b) {
        this.targetName = b.targetName;
        this.preferMinecraftNamespaceCommand = b.preferMinecraftNamespaceCommand;
        this.allowFallbackNativeKill = b.allowFallbackNativeKill;
        this.allowFallbackSetHealth = b.allowFallbackSetHealth;
        this.nativeDamageAmount = b.nativeDamageAmount;
    }

    /** Validated, sanitized target player name. Safe to use in a command. */
    public String targetName() {
        return targetName;
    }

    /** Whether to try {@code minecraft:kill} before the bare {@code kill} command. */
    public boolean preferMinecraftNamespaceCommand() {
        return preferMinecraftNamespaceCommand;
    }

    /** Whether the platform may fall back to a native damage call. */
    public boolean allowFallbackNativeKill() {
        return allowFallbackNativeKill;
    }

    /** Whether the platform may fall back to setting health to zero. */
    public boolean allowFallbackSetHealth() {
        return allowFallbackSetHealth;
    }

    /** Damage amount for the native-kill strategy. */
    public double nativeDamageAmount() {
        return nativeDamageAmount;
    }

    public static Builder builder(String targetName) {
        return new Builder(targetName);
    }

    public static final class Builder {
        private final String targetName;
        private boolean preferMinecraftNamespaceCommand = true;
        private boolean allowFallbackNativeKill = true;
        private boolean allowFallbackSetHealth = true;
        private double nativeDamageAmount = 100000.0D;

        private Builder(String targetName) {
            this.targetName = targetName;
        }

        public Builder preferMinecraftNamespaceCommand(boolean value) {
            this.preferMinecraftNamespaceCommand = value;
            return this;
        }

        public Builder allowFallbackNativeKill(boolean value) {
            this.allowFallbackNativeKill = value;
            return this;
        }

        public Builder allowFallbackSetHealth(boolean value) {
            this.allowFallbackSetHealth = value;
            return this;
        }

        public Builder nativeDamageAmount(double value) {
            this.nativeDamageAmount = value;
            return this;
        }

        public KillRequest build() {
            return new KillRequest(this);
        }
    }
}
