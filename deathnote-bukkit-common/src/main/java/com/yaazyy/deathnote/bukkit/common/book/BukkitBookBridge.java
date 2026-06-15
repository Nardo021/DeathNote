package com.yaazyy.deathnote.bukkit.common.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.inventory.meta.BookMeta;

import com.yaazyy.deathnote.api.DeathNoteBookBridge;

/**
 * Reads the writable-book input. {@link BookMeta#getPages()} returns plain
 * legacy-formatted strings on every supported version, so this bridge is
 * shared by both the legacy and modern artifacts.
 */
public final class BukkitBookBridge implements DeathNoteBookBridge<BookMeta> {

    @Override
    public List<String> readPages(BookMeta bookMeta) {
        if (bookMeta == null || !bookMeta.hasPages()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(bookMeta.getPages());
    }

    @Override
    public void clearPages(BookMeta bookMeta) {
        if (bookMeta != null) {
            bookMeta.setPages(Collections.<String>emptyList());
        }
    }

    @Override
    public String readFirstPageFirstLine(BookMeta bookMeta) {
        List<String> pages = readPages(bookMeta);
        if (pages.isEmpty()) {
            return "";
        }
        String firstPage = pages.get(0);
        if (firstPage == null) {
            return "";
        }
        for (String line : firstPage.split("\\r?\\n")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                return trimmed;
            }
        }
        return "";
    }
}
