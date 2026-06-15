package com.yaazyy.deathnote.bukkit.common.listener;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.yaazyy.deathnote.bukkit.common.AbstractDeathNotePlugin;

/**
 * Lightweight join hook. It performs no gameplay action by default and exists
 * as a documented extension point (e.g. future item migration or admin
 * notices). Only a debug-level log line is emitted.
 */
public final class DeathNoteJoinListener implements Listener {

    private final AbstractDeathNotePlugin plugin;

    public DeathNoteJoinListener(AbstractDeathNotePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getLogger().log(Level.FINE,
                "Player joined while DeathNote active: {0}", event.getPlayer().getName());
    }
}
