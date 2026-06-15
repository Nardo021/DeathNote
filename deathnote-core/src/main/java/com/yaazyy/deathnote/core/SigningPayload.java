package com.yaazyy.deathnote.core;

/**
 * Canonical HMAC signing payload for Death Note items.
 *
 * <p>Shared by Bukkit (PDC / lore) and Forge (NBT) item bridges.</p>
 */
public final class SigningPayload {

    private static final String VERSION = "DN1";

    private SigningPayload() {
    }

    public static String format(long createdAtMillis, String createdBy) {
        return VERSION + "|" + createdAtMillis + "|" + (createdBy == null ? "-" : createdBy);
    }
}
