package com.yaazyy.deathnote.bukkit.common.platform;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.yaazyy.deathnote.api.DNPlayer;

/**
 * Bukkit-backed {@link DNPlayer}. Wraps an {@link OfflinePlayer} (which a
 * {@link Player} also is) so the core never sees a Bukkit type.
 */
public final class BukkitDNPlayer implements DNPlayer {

    private final OfflinePlayer handle;

    public BukkitDNPlayer(OfflinePlayer handle) {
        this.handle = handle;
    }

    /** The wrapped Bukkit player, or {@code null} if not online. */
    public Player bukkitPlayer() {
        return handle.getPlayer();
    }

    /** The underlying offline player handle. */
    public OfflinePlayer offlinePlayer() {
        return handle;
    }

    @Override
    public UUID uuid() {
        return handle.getUniqueId();
    }

    @Override
    public String name() {
        String n = handle.getName();
        return n == null ? "" : n;
    }

    @Override
    public boolean isOp() {
        return handle.isOp();
    }

    @Override
    public boolean isOnline() {
        return handle.isOnline();
    }
}
