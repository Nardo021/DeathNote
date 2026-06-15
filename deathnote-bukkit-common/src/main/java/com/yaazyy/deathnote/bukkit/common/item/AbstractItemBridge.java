package com.yaazyy.deathnote.bukkit.common.item;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.yaazyy.deathnote.api.DeathNoteItemBridge;
import com.yaazyy.deathnote.core.SignatureService;
import com.yaazyy.deathnote.core.SigningPayload;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;
import com.yaazyy.deathnote.bukkit.common.util.Text;

/**
 * Shared Death Note item logic for Bukkit. Subclasses decide HOW the secure
 * markers are stored (legacy hidden lore vs modern PersistentDataContainer),
 * but creation, display, and signature verification are shared here.
 *
 * <p>An item is only ever recognised as a Death Note when the base material
 * matches AND the stored HMAC signature verifies. Display name alone is never
 * trusted.</p>
 */
public abstract class AbstractItemBridge implements DeathNoteItemBridge<ItemStack> {

    protected final DeathNoteConfig config;
    protected final SignatureService signatures;
    protected final MaterialAdapter materials;

    protected AbstractItemBridge(DeathNoteConfig config, SignatureService signatures,
                                 MaterialAdapter materials) {
        this.config = config;
        this.signatures = signatures;
        this.materials = materials;
    }

    @Override
    public ItemStack createDeathNoteItem() {
        return createDeathNoteItem(null);
    }

    /** Create a signed Death Note, optionally bound to a creator UUID. */
    public ItemStack createDeathNoteItem(UUID creator) {
        ItemStack item = materials.baseBookItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.setDisplayName(Text.color(config.item().displayName));
        meta.setLore(Text.color(config.item().lore));
        applyExtraMeta(meta);
        stamp(meta, creator);

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean isDeathNoteItem(ItemStack item) {
        if (item == null || !materials.isBaseBookMaterial(item.getType())) {
            return false;
        }
        return verifySignature(item);
    }

    @Override
    public void signDeathNoteItem(ItemStack item) {
        signDeathNoteItem(item, null);
    }

    /** Re-sign an existing item, optionally binding a creator UUID. */
    public void signDeathNoteItem(ItemStack item, UUID creator) {
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        stamp(meta, creator);
        item.setItemMeta(meta);
    }

    /** Compute and write a fresh signed marker (created_at, created_by, HMAC). */
    private void stamp(ItemMeta meta, UUID creator) {
        long now = System.currentTimeMillis();
        String createdBy = creator == null ? "-" : creator.toString();
        applyMarkers(meta, now, createdBy, signatures.sign(SigningPayload.format(now, createdBy)));
    }

    @Override
    public Optional<String> readSignature(ItemStack item) {
        return extractMarkers(item).map(m -> m.signature);
    }

    @Override
    public boolean verifySignature(ItemStack item) {
        Optional<Markers> markers = extractMarkers(item);
        if (!markers.isPresent()) {
            return false;
        }
        Markers m = markers.get();
        if (m.signature == null || m.signature.isEmpty()) {
            return false;
        }
        return signatures.verify(SigningPayload.format(m.createdAtMillis, m.createdBy), m.signature);
    }

    // --- version-specific hooks --------------------------------------------

    /** Write the secure markers onto the meta (lore for legacy, PDC for modern). */
    protected abstract void applyMarkers(ItemMeta meta, long createdAtMillis,
                                         String createdBy, String signature);

    /** Read the secure markers back from the item, if present. */
    protected abstract Optional<Markers> extractMarkers(ItemStack item);

    /** Optional extra meta (e.g. CustomModelData on modern). Default: no-op. */
    protected void applyExtraMeta(ItemMeta meta) {
        // overridden by modern adapter
    }

    /** Decoded secure markers stored on an item. */
    protected static final class Markers {
        public final long createdAtMillis;
        public final String createdBy;
        public final String signature;

        public Markers(long createdAtMillis, String createdBy, String signature) {
            this.createdAtMillis = createdAtMillis;
            this.createdBy = createdBy;
            this.signature = signature;
        }
    }
}
