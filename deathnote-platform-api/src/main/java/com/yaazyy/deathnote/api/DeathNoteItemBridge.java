package com.yaazyy.deathnote.api;

import java.util.Optional;

/**
 * Platform bridge for creating and recognising the Death Note item.
 *
 * @param <I> the platform's item stack type (e.g. {@code org.bukkit.inventory.ItemStack})
 */
public interface DeathNoteItemBridge<I> {

    /** Create a fully formed, signed Death Note item. */
    I createDeathNoteItem();

    /**
     * Whether the given item is a genuine Death Note. Implementations MUST NOT
     * rely on display name alone; a verified signature is required.
     */
    boolean isDeathNoteItem(I item);

    /** Apply the secure signature/metadata to the item in place. */
    void signDeathNoteItem(I item);

    /** Read the raw signature payload if present. */
    Optional<String> readSignature(I item);

    /** Verify the signature stored on the item against the configured secret. */
    boolean verifySignature(I item);
}
