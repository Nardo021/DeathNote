package com.yaazyy.deathnote.core;

/**
 * Structured outcome of processing a Death Note submission.
 */
public enum DeathNoteResult {

    /** Target was found, allowed, and the kill was executed successfully. */
    SUCCESS,

    /** The submitted item was not a genuine, signature-verified Death Note. */
    INVALID_ITEM,

    /** The written name failed validation (format, selector, injection, ...). */
    INVALID_NAME,

    /** No online player with that exact name. */
    TARGET_NOT_FOUND,

    /** Actor tried to target themselves and self-targeting is blocked. */
    SELF_TARGET_BLOCKED,

    /** Actor lacks the required permission. */
    NO_PERMISSION,

    /** Target is immune (bypass permission, op-target protection, ...). */
    TARGET_IMMUNE,

    /** Actor is still on cooldown. */
    COOLDOWN_ACTIVE,

    /** Every kill strategy failed. */
    KILL_FAILED,

    /** The plugin is misconfigured (no config loaded, missing secret, ...). */
    CONFIG_ERROR
}
