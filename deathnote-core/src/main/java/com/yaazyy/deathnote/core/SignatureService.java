package com.yaazyy.deathnote.core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * HMAC-SHA256 signing used to prove an item is a genuine Death Note.
 *
 * <p>Both the legacy (hidden lore marker) and modern (PDC) item bridges store
 * the hex signature produced here. A renamed vanilla book has no valid
 * signature, so it can never act as a Death Note.</p>
 */
public final class SignatureService {

    private static final String HMAC_ALGO = "HmacSHA256";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final byte[] secret;

    public SignatureService(String secret) {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("HMAC secret must not be empty");
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    /** Generate a new random secret suitable for storing in config.yml. */
    public static String generateSecret() {
        byte[] buf = new byte[32];
        RANDOM.nextBytes(buf);
        return toHex(buf);
    }

    /** Compute the hex HMAC of the given payload. */
    public String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secret, HMAC_ALGO));
            byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return toHex(raw);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to compute HMAC signature", e);
        }
    }

    /** Constant-time verification of a signature against a payload. */
    public boolean verify(String payload, String signature) {
        if (payload == null || signature == null) {
            return false;
        }
        String expected = sign(payload);
        return constantTimeEquals(expected, signature);
    }

    private static boolean constantTimeEquals(String a, String b) {
        byte[] x = a.getBytes(StandardCharsets.UTF_8);
        byte[] y = b.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(x, y);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }
}
