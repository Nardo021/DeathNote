package com.yaazyy.deathnote.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NameValidatorTest {

    @Test
    void acceptsCanonicalNames() {
        assertTrue(NameValidator.isValid("Notch"));
        assertTrue(NameValidator.isValid("jeb_"));
        assertTrue(NameValidator.isValid("Player_123"));
        assertTrue(NameValidator.isValid("abc"));            // min length 3
        assertTrue(NameValidator.isValid("ABCDEFGHIJKLMNOP")); // max length 16
    }

    @Test
    void trimsSurroundingWhitespace() {
        assertTrue(NameValidator.isValid("  Notch  "));
    }

    @Test
    void rejectsWrongLength() {
        assertEquals(NameValidator.Rejection.FORMAT, NameValidator.reasonFor("ab"));
        assertEquals(NameValidator.Rejection.FORMAT, NameValidator.reasonFor("ThisNameIsWayTooLong"));
    }

    @Test
    void rejectsEmpty() {
        assertEquals(NameValidator.Rejection.EMPTY, NameValidator.reasonFor(null));
        assertEquals(NameValidator.Rejection.EMPTY, NameValidator.reasonFor("   "));
    }

    @Test
    void rejectsSelectors() {
        assertEquals(NameValidator.Rejection.SELECTOR, NameValidator.reasonFor("@p"));
        assertEquals(NameValidator.Rejection.SELECTOR, NameValidator.reasonFor("@a"));
        assertEquals(NameValidator.Rejection.SELECTOR, NameValidator.reasonFor("@r"));
        assertEquals(NameValidator.Rejection.SELECTOR, NameValidator.reasonFor("@e"));
        assertEquals(NameValidator.Rejection.SELECTOR, NameValidator.reasonFor("@s"));
        assertEquals(NameValidator.Rejection.SELECTOR, NameValidator.reasonFor("@e[type=player]"));
        assertEquals(NameValidator.Rejection.SELECTOR, NameValidator.reasonFor("foo@bar"));
    }

    @Test
    void rejectsInjectionCharacters() {
        assertEquals(NameValidator.Rejection.SLASH, NameValidator.reasonFor("kill/Notch"));
        assertEquals(NameValidator.Rejection.SLASH, NameValidator.reasonFor("back\\slash"));
        assertEquals(NameValidator.Rejection.SEMICOLON, NameValidator.reasonFor("Notch;op"));
        assertEquals(NameValidator.Rejection.QUOTE, NameValidator.reasonFor("Notch\""));
        assertEquals(NameValidator.Rejection.QUOTE, NameValidator.reasonFor("Notch'"));
        assertEquals(NameValidator.Rejection.QUOTE, NameValidator.reasonFor("Notch`"));
    }

    @Test
    void rejectsWhitespaceInside() {
        assertEquals(NameValidator.Rejection.WHITESPACE, NameValidator.reasonFor("Not ch"));
        assertEquals(NameValidator.Rejection.WHITESPACE, NameValidator.reasonFor("kill\tNotch"));
    }

    @Test
    void rejectsControlCharacters() {
        assertEquals(NameValidator.Rejection.CONTROL_CHAR, NameValidator.reasonFor("Not\u0000ch"));
        assertEquals(NameValidator.Rejection.CONTROL_CHAR, NameValidator.reasonFor("Notch\u200B"));
    }

    @Test
    void rejectsNonAsciiLetters() {
        assertFalse(NameValidator.isValid("Nötch"));
        assertFalse(NameValidator.isValid("名前"));
    }
}
