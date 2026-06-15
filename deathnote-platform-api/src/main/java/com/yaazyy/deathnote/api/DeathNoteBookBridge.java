package com.yaazyy.deathnote.api;

import java.util.List;

/**
 * Platform bridge for reading the book-like input.
 *
 * @param <B> the platform's book meta type (e.g. {@code org.bukkit.inventory.meta.BookMeta})
 */
public interface DeathNoteBookBridge<B> {

    /** All pages of the book as plain strings. */
    List<String> readPages(B bookMeta);

    /** Remove all pages from the book (used when {@code input.clearBookAfterUse}). */
    void clearPages(B bookMeta);

    /**
     * Convenience: the first non-empty line of the first page, trimmed.
     * Returns an empty string when there is no usable content.
     */
    String readFirstPageFirstLine(B bookMeta);
}
