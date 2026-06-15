package com.yaazyy.deathnote.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

class CooldownServiceTest {

    @Test
    void noCooldownBeforeFirstUse() {
        CooldownService svc = new CooldownService(() -> 0L);
        assertFalse(svc.isOnCooldown(UUID.randomUUID(), 60L));
    }

    @Test
    void activeImmediatelyAfterUse() {
        AtomicLong now = new AtomicLong(10_000L);
        CooldownService svc = new CooldownService(now::get);
        UUID actor = UUID.randomUUID();

        svc.recordUse(actor);
        assertTrue(svc.isOnCooldown(actor, 60L));
        assertEquals(60L, svc.remainingSeconds(actor, 60L));
    }

    @Test
    void expiresAfterWindow() {
        AtomicLong now = new AtomicLong(0L);
        CooldownService svc = new CooldownService(now::get);
        UUID actor = UUID.randomUUID();

        svc.recordUse(actor);
        now.set(59_000L); // 59s elapsed
        assertTrue(svc.isOnCooldown(actor, 60L));
        assertEquals(1L, svc.remainingSeconds(actor, 60L));

        now.set(60_000L); // exactly 60s
        assertFalse(svc.isOnCooldown(actor, 60L));
        assertEquals(0L, svc.remainingSeconds(actor, 60L));
    }

    @Test
    void zeroOrNegativeWindowMeansNoCooldown() {
        AtomicLong now = new AtomicLong(1234L);
        CooldownService svc = new CooldownService(now::get);
        UUID actor = UUID.randomUUID();
        svc.recordUse(actor);
        assertFalse(svc.isOnCooldown(actor, 0L));
        assertEquals(0L, svc.remainingSeconds(actor, 0L));
    }

    @Test
    void resetClearsCooldown() {
        AtomicLong now = new AtomicLong(5_000L);
        CooldownService svc = new CooldownService(now::get);
        UUID actor = UUID.randomUUID();
        svc.recordUse(actor);
        assertTrue(svc.isOnCooldown(actor, 60L));
        svc.reset(actor);
        assertFalse(svc.isOnCooldown(actor, 60L));
    }
}
