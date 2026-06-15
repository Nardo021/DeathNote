package com.yaazyy.deathnote.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SignatureServiceTest {

    @Test
    void signAndVerifyRoundTrip() {
        SignatureService svc = new SignatureService("super-secret");
        String payload = "deathnote:item|owner=abc|created=123";
        String sig = svc.sign(payload);
        assertTrue(svc.verify(payload, sig));
    }

    @Test
    void verifyFailsOnTamperedPayload() {
        SignatureService svc = new SignatureService("super-secret");
        String sig = svc.sign("original");
        assertFalse(svc.verify("tampered", sig));
    }

    @Test
    void verifyFailsWithDifferentSecret() {
        String payload = "payload";
        String sig = new SignatureService("secret-a").sign(payload);
        assertFalse(new SignatureService("secret-b").verify(payload, sig));
    }

    @Test
    void verifyHandlesNulls() {
        SignatureService svc = new SignatureService("secret");
        assertFalse(svc.verify(null, "x"));
        assertFalse(svc.verify("x", null));
    }

    @Test
    void emptySecretRejected() {
        assertThrows(IllegalArgumentException.class, () -> new SignatureService(""));
        assertThrows(IllegalArgumentException.class, () -> new SignatureService(null));
    }

    @Test
    void generatedSecretsAreHexAndUnique() {
        String a = SignatureService.generateSecret();
        String b = SignatureService.generateSecret();
        assertEquals(64, a.length()); // 32 bytes -> 64 hex chars
        assertTrue(a.matches("[0-9a-f]+"));
        assertNotEquals(a, b);
    }
}
