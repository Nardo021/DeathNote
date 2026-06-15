package com.yaazyy.deathnote.bukkit.common.listener;

import java.util.UUID;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import com.yaazyy.deathnote.bukkit.common.AbstractDeathNotePlugin;

/**
 * When the Death Note recipe completes, re-sign the crafted result so it is
 * bound to the crafter (created_by) with a fresh timestamp. This improves the
 * audit trail and guarantees every crafted copy carries a valid signature.
 */
public final class DeathNoteCraftListener implements Listener {

    private final AbstractDeathNotePlugin plugin;

    public DeathNoteCraftListener(AbstractDeathNotePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null || !plugin.itemBridge().isDeathNoteItem(result)) {
            return;
        }
        UUID creator = null;
        if (!event.getViewers().isEmpty()) {
            HumanEntity viewer = event.getViewers().get(0);
            creator = viewer.getUniqueId();
        }
        ItemStack signed = result.clone();
        plugin.itemBridge().signDeathNoteItem(signed, creator);
        event.getInventory().setResult(signed);
    }
}
