package com.yaazyy.deathnote.config;

import com.yaazyy.deathnote.core.config.DeathNoteConfig;

/** Result of loading configuration, including an optional newly generated secret. */
public final class ConfigLoadResult {

    private final DeathNoteConfig config;
    private final String generatedSecret;

    public ConfigLoadResult(DeathNoteConfig config, String generatedSecret) {
        this.config = config;
        this.generatedSecret = generatedSecret;
    }

    public DeathNoteConfig config() {
        return config;
    }

    /** Non-null when a fresh HMAC secret was generated and should be persisted. */
    public String generatedSecret() {
        return generatedSecret;
    }
}
