package com.yaazyy.deathnote.bukkit.common.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.core.DeathNoteResult;
import com.yaazyy.deathnote.bukkit.common.AbstractDeathNotePlugin;
import com.yaazyy.deathnote.bukkit.common.platform.BukkitDNPlayer;

/**
 * The primary input flow. When a player clicks "Done" on a Death Note's book
 * GUI, we read the first line of the first page and run the validated kill.
 *
 * <p>Raw page text is NEVER used as a command - only the first line, after it
 * passes {@code NameValidator} inside the core service.</p>
 */
public final class DeathNoteBookEditListener implements Listener {

    private final AbstractDeathNotePlugin plugin;

    public DeathNoteBookEditListener(AbstractDeathNotePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEdit(PlayerEditBookEvent event) {
        final Player player = event.getPlayer();

        // The book being edited is the one in hand. Verify it is a genuine,
        // signature-checked Death Note before doing anything.
        ItemStack inHand = player.getInventory().getItemInHand();
        if (!plugin.itemBridge().isDeathNoteItem(inHand)) {
            return;
        }

        final BookMeta newMeta = event.getNewBookMeta();
        final String firstLine = plugin.bookBridge().readFirstPageFirstLine(newMeta);

        // Sanitize so the written (potentially malicious) name is not persisted
        // onto the item and cannot be duplicated.
        if (plugin.dnConfig().input().clearBookAfterUse) {
            BookMeta sanitized = event.getNewBookMeta();
            plugin.bookBridge().clearPages(sanitized);
            event.setNewBookMeta(sanitized);
        }

        final DNPlayer actor = new BukkitDNPlayer(player);

        // Execute on the next sync tick(s) per kill.delayTicks. runLater runs
        // on the main thread, so the platform kill is always thread-safe.
        plugin.platform().runLater(() -> {
            DeathNoteResult result = plugin.service().process(actor, firstLine);
            sendResult(player, result, firstLine);
            if (result == DeathNoteResult.SUCCESS && plugin.dnConfig().input().consumeOnUse) {
                consumeOne(player);
            }
        }, plugin.dnConfig().kill().delayTicks);
    }

    private void sendResult(Player player, DeathNoteResult result, String target) {
        String key;
        switch (result) {
            case SUCCESS:             key = "result.success"; break;
            case INVALID_ITEM:        key = "result.invalidItem"; break;
            case INVALID_NAME:        key = "result.invalidName"; break;
            case TARGET_NOT_FOUND:    key = "result.targetNotFound"; break;
            case SELF_TARGET_BLOCKED: key = "result.selfBlocked"; break;
            case NO_PERMISSION:       key = "result.noPermission"; break;
            case TARGET_IMMUNE:       key = "result.targetImmune"; break;
            case COOLDOWN_ACTIVE:     key = "result.cooldown"; break;
            case KILL_FAILED:         key = "result.killFailed"; break;
            case CONFIG_ERROR:        key = "result.configError"; break;
            default:                  key = "result.killFailed"; break;
        }
        player.sendMessage(plugin.messages().get(key, "target", target == null ? "?" : target));
    }

    private void consumeOne(Player player) {
        ItemStack inHand = player.getInventory().getItemInHand();
        if (inHand == null) {
            return;
        }
        int amount = inHand.getAmount();
        if (amount <= 1) {
            player.getInventory().setItemInHand(null);
        } else {
            inHand.setAmount(amount - 1);
        }
    }
}
