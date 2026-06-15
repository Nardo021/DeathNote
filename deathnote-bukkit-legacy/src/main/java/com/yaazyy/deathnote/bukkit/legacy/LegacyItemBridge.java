package com.yaazyy.deathnote.bukkit.legacy;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.yaazyy.deathnote.core.SignatureService;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;
import com.yaazyy.deathnote.bukkit.common.item.AbstractItemBridge;
import com.yaazyy.deathnote.bukkit.common.item.MaterialAdapter;

/**
 * Legacy item bridge. There is no PersistentDataContainer on pre-1.13, so the
 * secure markers are stored in a single "hidden" lore line. The marker is
 * encoded as a run of colour codes ({@code §<hexdigit>}) which renders as
 * nothing visible, while still round-tripping the exact bytes.
 *
 * <p>The HMAC signature is part of that payload, so a renamed vanilla book or
 * a copied book without the correct signed marker is never accepted.</p>
 */
public final class LegacyItemBridge extends AbstractItemBridge {

    private static final char SECTION = '\u00A7';
    private static final Charset UTF8 = Charset.forName("UTF-8");
    /** Sentinel that prefixes the decoded marker so we can identify our line. */
    private static final String SENTINEL = "DNL1|";

    public LegacyItemBridge(DeathNoteConfig config, SignatureService signatures,
                            MaterialAdapter materials) {
        super(config, signatures, materials);
    }

    @Override
    protected void applyMarkers(ItemMeta meta, long createdAtMillis, String createdBy, String signature) {
        String plain = SENTINEL + createdAtMillis + "|" + (createdBy == null ? "-" : createdBy)
                + "|" + signature;
        String hidden = toHidden(plain);

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<String>();
        // Drop any previous marker line before adding the fresh one.
        Iterator<String> it = lore.iterator();
        while (it.hasNext()) {
            if (decodeMarker(it.next()) != null) {
                it.remove();
            }
        }
        lore.add(hidden);
        meta.setLore(lore);
    }

    @Override
    protected Optional<Markers> extractMarkers(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return Optional.empty();
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) {
            return Optional.empty();
        }
        for (String line : meta.getLore()) {
            String decoded = decodeMarker(line);
            if (decoded == null) {
                continue;
            }
            // decoded already has the sentinel stripped; parts: createdAt|createdBy|signature
            String[] parts = decoded.split("\\|", 3);
            if (parts.length != 3) {
                continue;
            }
            long createdAt;
            try {
                createdAt = Long.parseLong(parts[0]);
            } catch (NumberFormatException e) {
                continue;
            }
            return Optional.of(new Markers(createdAt, parts[1], parts[2]));
        }
        return Optional.empty();
    }

    /** Encode an arbitrary string as an invisible colour-code run. */
    private static String toHidden(String s) {
        String hex = toHex(s.getBytes(UTF8));
        StringBuilder sb = new StringBuilder(hex.length() * 2);
        for (int i = 0; i < hex.length(); i++) {
            sb.append(SECTION).append(hex.charAt(i));
        }
        return sb.toString();
    }

    /**
     * Decode a lore line if it is one of our marker lines.
     *
     * @return the payload WITHOUT the sentinel prefix, or {@code null} if the
     *         line is not a valid marker.
     */
    private static String decodeMarker(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == SECTION) {
                continue;
            }
            hex.append(c);
        }
        String h = hex.toString();
        if (h.length() < 2 || (h.length() % 2) != 0 || !isHex(h)) {
            return null;
        }
        String text = new String(fromHex(h), UTF8);
        if (!text.startsWith(SENTINEL)) {
            return null;
        }
        return text.substring(SENTINEL.length());
    }

    private static boolean isHex(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean ok = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f');
            if (!ok) {
                return false;
            }
        }
        return true;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    private static byte[] fromHex(String hex) {
        int len = hex.length() / 2;
        byte[] out = new byte[len];
        for (int i = 0; i < len; i++) {
            int hi = Character.digit(hex.charAt(i * 2), 16);
            int lo = Character.digit(hex.charAt(i * 2 + 1), 16);
            out[i] = (byte) ((hi << 4) | lo);
        }
        return out;
    }
}
