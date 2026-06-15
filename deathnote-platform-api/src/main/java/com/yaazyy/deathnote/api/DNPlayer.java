package com.yaazyy.deathnote.api;

import java.util.UUID;

/**
 * Platform-agnostic view of a player.
 *
 * <p>Concrete platforms (Bukkit, Forge, Fabric, ...) wrap their own player
 * type behind this interface so the core never imports a platform class.</p>
 */
public interface DNPlayer {

    /** Stable unique id of the player. May be an offline-mode UUID on some servers. */
    UUID uuid();

    /** Current player name. */
    String name();

    /** Whether the player is a server operator / has elevated privileges. */
    boolean isOp();

    /** Whether the player is currently connected. */
    boolean isOnline();
}
