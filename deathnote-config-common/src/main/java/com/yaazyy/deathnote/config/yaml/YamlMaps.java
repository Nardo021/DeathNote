package com.yaazyy.deathnote.config.yaml;

import java.util.List;
import java.util.Map;

/**
 * Reads dotted paths (e.g. {@code input.firstPageOnly}) from a SnakeYAML root map.
 */
public final class YamlMaps {

    private YamlMaps() {
    }

    @SuppressWarnings("unchecked")
    public static Object get(Map<String, Object> root, String dottedPath) {
        if (root == null || dottedPath == null) {
            return null;
        }
        String[] parts = dottedPath.split("\\.");
        Object current = root;
        for (String part : parts) {
            if (!(current instanceof Map)) {
                return null;
            }
            current = ((Map<String, Object>) current).get(part);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    public static boolean getBoolean(Map<String, Object> root, String path, boolean def) {
        Object v = get(root, path);
        if (v instanceof Boolean) {
            return (Boolean) v;
        }
        if (v != null) {
            return Boolean.parseBoolean(String.valueOf(v));
        }
        return def;
    }

    public static long getLong(Map<String, Object> root, String path, long def) {
        Object v = get(root, path);
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        if (v != null) {
            try {
                return Long.parseLong(String.valueOf(v));
            } catch (NumberFormatException ignored) {
                return def;
            }
        }
        return def;
    }

    public static int getInt(Map<String, Object> root, String path, int def) {
        Object v = get(root, path);
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        if (v != null) {
            try {
                return Integer.parseInt(String.valueOf(v));
            } catch (NumberFormatException ignored) {
                return def;
            }
        }
        return def;
    }

    public static String getString(Map<String, Object> root, String path, String def) {
        Object v = get(root, path);
        return v == null ? def : String.valueOf(v);
    }

    @SuppressWarnings("unchecked")
    public static List<String> getStringList(Map<String, Object> root, String path) {
        Object v = get(root, path);
        if (v instanceof List) {
            return (List<String>) v;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static void set(Map<String, Object> root, String dottedPath, Object value) {
        String[] parts = dottedPath.split("\\.");
        Map<String, Object> current = root;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof Map)) {
                throw new IllegalStateException("Cannot set " + dottedPath + ": missing section " + parts[i]);
            }
            current = (Map<String, Object>) next;
        }
        current.put(parts[parts.length - 1], value);
    }
}
