package com.yaazyy.deathnote.bukkit.modern;

import java.util.Optional;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.yaazyy.deathnote.core.SignatureService;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;
import com.yaazyy.deathnote.bukkit.common.item.AbstractItemBridge;
import com.yaazyy.deathnote.bukkit.common.item.MaterialAdapter;

/**
 * Modern item bridge. Stores the secure markers in the item's
 * {@link PersistentDataContainer} under the {@code deathnote} namespace and
 * applies {@code CustomModelData} when configured.
 *
 * <p>Keys: {@code deathnote:item_type}, {@code deathnote:signature},
 * {@code deathnote:created_at}, {@code deathnote:created_by},
 * {@code deathnote:uses}.</p>
 */
public final class ModernItemBridge extends AbstractItemBridge {

    private static final String ITEM_TYPE_VALUE = "death_note";

    private final NamespacedKey keyItemType;
    private final NamespacedKey keySignature;
    private final NamespacedKey keyCreatedAt;
    private final NamespacedKey keyCreatedBy;
    private final NamespacedKey keyUses;

    public ModernItemBridge(Plugin plugin, DeathNoteConfig config,
                            SignatureService signatures, MaterialAdapter materials) {
        super(config, signatures, materials);
        this.keyItemType = new NamespacedKey(plugin, "item_type");
        this.keySignature = new NamespacedKey(plugin, "signature");
        this.keyCreatedAt = new NamespacedKey(plugin, "created_at");
        this.keyCreatedBy = new NamespacedKey(plugin, "created_by");
        this.keyUses = new NamespacedKey(plugin, "uses");
    }

    @Override
    protected void applyMarkers(ItemMeta meta, long createdAtMillis, String createdBy, String signature) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(keyItemType, PersistentDataType.STRING, ITEM_TYPE_VALUE);
        pdc.set(keySignature, PersistentDataType.STRING, signature);
        pdc.set(keyCreatedAt, PersistentDataType.LONG, createdAtMillis);
        pdc.set(keyCreatedBy, PersistentDataType.STRING, createdBy == null ? "-" : createdBy);
        if (!pdc.has(keyUses, PersistentDataType.INTEGER)) {
            pdc.set(keyUses, PersistentDataType.INTEGER, 0);
        }
    }

    @Override
    protected Optional<Markers> extractMarkers(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return Optional.empty();
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return Optional.empty();
        }
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!ITEM_TYPE_VALUE.equals(pdc.get(keyItemType, PersistentDataType.STRING))) {
            return Optional.empty();
        }
        String signature = pdc.get(keySignature, PersistentDataType.STRING);
        if (signature == null) {
            return Optional.empty();
        }
        Long createdAt = pdc.get(keyCreatedAt, PersistentDataType.LONG);
        String createdBy = pdc.get(keyCreatedBy, PersistentDataType.STRING);
        return Optional.of(new Markers(
                createdAt == null ? 0L : createdAt,
                createdBy == null ? "-" : createdBy,
                signature));
    }

    @Override
    protected void applyExtraMeta(ItemMeta meta) {
        int cmd = config.item().customModelData;
        if (cmd >= 0) {
            meta.setCustomModelData(cmd);
        }
    }
}
