package com.yaazyy.deathnote.forge1201.item;

import java.util.Optional;
import java.util.UUID;

import com.yaazyy.deathnote.api.DeathNoteItemBridge;
import com.yaazyy.deathnote.config.text.TextColors;
import com.yaazyy.deathnote.core.SignatureService;
import com.yaazyy.deathnote.core.SigningPayload;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * NBT-backed Death Note item bridge. Keys mirror modern Bukkit PDC:
 * {@code item_type}, {@code signature}, {@code created_at}, {@code created_by}, {@code uses}.
 */
public final class Forge1201ItemBridge implements DeathNoteItemBridge<ItemStack> {

    private static final String ITEM_TYPE_VALUE = "death_note";
    private static final String KEY_ITEM_TYPE = "item_type";
    private static final String KEY_SIGNATURE = "signature";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_CREATED_BY = "created_by";
    private static final String KEY_USES = "uses";

    private final DeathNoteConfig config;
    private final SignatureService signatures;
    private final Item deathNoteItem;

    public Forge1201ItemBridge(DeathNoteConfig config, SignatureService signatures, Item deathNoteItem) {
        this.config = config;
        this.signatures = signatures;
        this.deathNoteItem = deathNoteItem;
    }

    @Override
    public ItemStack createDeathNoteItem() {
        return createDeathNoteItem(null);
    }

    public ItemStack createDeathNoteItem(UUID creator) {
        ItemStack stack = new ItemStack(deathNoteItem);
        stack.setHoverName(Component.literal(TextColors.translate(config.item().displayName)));
        signDeathNoteItem(stack, creator);
        return stack;
    }

    @Override
    public boolean isDeathNoteItem(ItemStack item) {
        if (item == null || item.isEmpty() || item.getItem() != deathNoteItem) {
            return false;
        }
        return verifySignature(item);
    }

    @Override
    public void signDeathNoteItem(ItemStack item) {
        signDeathNoteItem(item, null);
    }

    public void signDeathNoteItem(ItemStack item, UUID creator) {
        if (item == null || item.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        String createdBy = creator == null ? "-" : creator.toString();
        String signature = signatures.sign(SigningPayload.format(now, createdBy));
        CompoundTag tag = item.getOrCreateTag();
        tag.putString(KEY_ITEM_TYPE, ITEM_TYPE_VALUE);
        tag.putString(KEY_SIGNATURE, signature);
        tag.putLong(KEY_CREATED_AT, now);
        tag.putString(KEY_CREATED_BY, createdBy);
        if (!tag.contains(KEY_USES)) {
            tag.putInt(KEY_USES, 0);
        }
    }

    @Override
    public Optional<String> readSignature(ItemStack item) {
        CompoundTag tag = tagOf(item);
        if (tag == null || !tag.contains(KEY_SIGNATURE)) {
            return Optional.empty();
        }
        return Optional.of(tag.getString(KEY_SIGNATURE));
    }

    @Override
    public boolean verifySignature(ItemStack item) {
        CompoundTag tag = tagOf(item);
        if (tag == null) {
            return false;
        }
        if (!ITEM_TYPE_VALUE.equals(tag.getString(KEY_ITEM_TYPE))) {
            return false;
        }
        String signature = tag.getString(KEY_SIGNATURE);
        if (signature == null || signature.isEmpty()) {
            return false;
        }
        long createdAt = tag.getLong(KEY_CREATED_AT);
        String createdBy = tag.getString(KEY_CREATED_BY);
        return signatures.verify(SigningPayload.format(createdAt, createdBy), signature);
    }

    private CompoundTag tagOf(ItemStack item) {
        if (item == null || item.isEmpty() || !item.hasTag()) {
            return null;
        }
        return item.getTag();
    }
}
