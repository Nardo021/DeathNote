package com.yaazyy.deathnote.core;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * Per-actor cooldown tracking.
 *
 * <p>The time source is injectable ({@link LongSupplier} returning epoch
 * milliseconds) so the logic is fully unit-testable without sleeping.</p>
 */
public final class CooldownService {

    private final Map<UUID, Long> lastUseMillis = new ConcurrentHashMap<>();
    private final LongSupplier clock;

    public CooldownService() {
        this(System::currentTimeMillis);
    }

    public CooldownService(LongSupplier clock) {
        this.clock = clock;
    }

    /** Whether the actor is currently on cooldown for the given window. */
    public boolean isOnCooldown(UUID actor, long cooldownSeconds) {
        return remainingSeconds(actor, cooldownSeconds) > 0L;
    }

    /** Remaining cooldown in whole seconds (rounded up), or 0 if ready. */
    public long remainingSeconds(UUID actor, long cooldownSeconds) {
        if (cooldownSeconds <= 0L) {
            return 0L;
        }
        Long last = lastUseMillis.get(actor);
        if (last == null) {
            return 0L;
        }
        long elapsedMillis = clock.getAsLong() - last;
        long windowMillis = cooldownSeconds * 1000L;
        long remainingMillis = windowMillis - elapsedMillis;
        if (remainingMillis <= 0L) {
            return 0L;
        }
        return (remainingMillis + 999L) / 1000L;
    }

    /** Record a successful use now, starting the cooldown window. */
    public void recordUse(UUID actor) {
        lastUseMillis.put(actor, clock.getAsLong());
    }

    /** Clear a single actor's cooldown. */
    public void reset(UUID actor) {
        lastUseMillis.remove(actor);
    }

    /** Clear all cooldowns (e.g. on reload). */
    public void resetAll() {
        lastUseMillis.clear();
    }
}
