package com.yaazyy.deathnote.bukkit.common.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.yaazyy.deathnote.bukkit.common.AbstractDeathNotePlugin;

/**
 * When a player right-clicks while holding a Death Note, the vanilla writable
 * book GUI opens automatically. This listener simply surfaces a hint so the
 * player knows what to do, and is the natural extension point for any future
 * custom open behaviour.
 */
public final class DeathNoteInteractListener implements Listener {

    private final AbstractDeathNotePlugin plugin;

    public DeathNoteInteractListener(AbstractDeathNotePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack inHand = player.getInventory().getItemInHand();
        if (!plugin.itemBridge().isDeathNoteItem(inHand)) {
            return;
        }
        player.sendMessage(plugin.messages().get("hint.open"));
    }
}
