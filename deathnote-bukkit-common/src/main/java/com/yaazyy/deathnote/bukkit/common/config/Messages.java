package com.yaazyy.deathnote.bukkit.common.config;

import java.io.File;

import com.yaazyy.deathnote.config.DeathNoteMessages;

/** Bukkit-facing alias for {@link DeathNoteMessages}. */
public final class Messages {

    private final DeathNoteMessages delegate;

    private Messages(DeathNoteMessages delegate) {
        this.delegate = delegate;
    }

    public static Messages load(File messagesFile) {
        try {
            return new Messages(DeathNoteMessages.load(messagesFile));
        } catch (Exception e) {
            return new Messages(DeathNoteMessages.empty());
        }
    }

    public String prefix() {
        return delegate.prefix();
    }

    public String raw(String key) {
        return delegate.raw(key);
    }

    public String get(String key) {
        return delegate.get(key);
    }

    public String get(String key, String... replacements) {
        return delegate.get(key, replacements);
    }
}
