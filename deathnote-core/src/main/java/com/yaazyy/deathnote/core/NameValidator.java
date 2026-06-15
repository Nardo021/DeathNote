package com.yaazyy.deathnote.core;

import java.util.regex.Pattern;

/**
 * Strict validation of a target player name.
 *
 * <p>This is the single most important defence against command injection and
 * selector abuse. Only canonical Minecraft Java usernames are accepted:
 * {@code ^[A-Za-z0-9_]{3,16}$}. Everything else is rejected.</p>
 *
 * <p>The regex alone already excludes selectors, slashes, semicolons, quotes,
 * whitespace and control characters, but we also perform explicit checks so
 * the rejection reason is unambiguous and the intent is documented.</p>
 */
public final class NameValidator {

    /** Canonical Minecraft Java username pattern. */
    private static final Pattern VALID_NAME = Pattern.compile("^[A-Za-z0-9_]{3,16}$");

    /** Vanilla target selectors that must never be treated as a name. */
    private static final String[] SELECTORS = {"@p", "@a", "@r", "@e", "@s"};

    private NameValidator() {
    }

    /**
     * @return {@code true} only if the input is a safe, canonical username.
     */
    public static boolean isValid(String raw) {
        return reasonFor(raw) == Rejection.NONE;
    }

    /**
     * Detailed validation, useful for logging and tests.
     *
     * @return {@link Rejection#NONE} when valid, otherwise the failing rule.
     */
    public static Rejection reasonFor(String raw) {
        if (raw == null) {
            return Rejection.EMPTY;
        }
        String name = raw.trim();
        if (name.isEmpty()) {
            return Rejection.EMPTY;
        }
        // Explicit selector rejection (defence in depth).
        String lower = name.toLowerCase();
        for (String selector : SELECTORS) {
            if (lower.equals(selector) || lower.startsWith(selector)) {
                return Rejection.SELECTOR;
            }
        }
        if (name.indexOf('@') >= 0) {
            return Rejection.SELECTOR;
        }
        if (name.indexOf('/') >= 0 || name.indexOf('\\') >= 0) {
            return Rejection.SLASH;
        }
        if (name.indexOf(';') >= 0) {
            return Rejection.SEMICOLON;
        }
        if (name.indexOf('"') >= 0 || name.indexOf('\'') >= 0 || name.indexOf('`') >= 0) {
            return Rejection.QUOTE;
        }
        if (containsWhitespace(name)) {
            return Rejection.WHITESPACE;
        }
        if (containsControlChar(name)) {
            return Rejection.CONTROL_CHAR;
        }
        if (!VALID_NAME.matcher(name).matches()) {
            return Rejection.FORMAT;
        }
        return Rejection.NONE;
    }

    private static boolean containsWhitespace(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isWhitespace(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsControlChar(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isISOControl(c) || Character.getType(c) == Character.CONTROL
                    || Character.getType(c) == Character.FORMAT) {
                return true;
            }
        }
        return false;
    }

    /** Why a name was rejected. */
    public enum Rejection {
        NONE,
        EMPTY,
        SELECTOR,
        SLASH,
        SEMICOLON,
        QUOTE,
        WHITESPACE,
        CONTROL_CHAR,
        FORMAT
    }
}
