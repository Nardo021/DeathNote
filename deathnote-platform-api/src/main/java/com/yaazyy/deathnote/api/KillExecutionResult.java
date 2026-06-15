package com.yaazyy.deathnote.api;

/**
 * Result of a platform kill attempt.
 *
 * <p>The {@code strategy} field is the identifier of the strategy that
 * actually succeeded (e.g. {@code COMMAND_MINECRAFT_KILL}). It is kept as a
 * plain {@link String} so this abstraction layer does not need to depend on
 * the core's {@code KillStrategy} enum, avoiding a circular dependency.</p>
 */
public final class KillExecutionResult {

    private final boolean success;
    private final String strategy;
    private final String detail;

    private KillExecutionResult(boolean success, String strategy, String detail) {
        this.success = success;
        this.strategy = strategy;
        this.detail = detail;
    }

    public static KillExecutionResult success(String strategy) {
        return new KillExecutionResult(true, strategy, null);
    }

    public static KillExecutionResult failure(String detail) {
        return new KillExecutionResult(false, null, detail);
    }

    public boolean isSuccess() {
        return success;
    }

    /** Identifier of the strategy that succeeded, or {@code null} on failure. */
    public String strategy() {
        return strategy;
    }

    /** Optional human-readable detail (error message, command output, etc.). */
    public String detail() {
        return detail;
    }

    @Override
    public String toString() {
        return "KillExecutionResult{success=" + success
                + ", strategy=" + strategy
                + ", detail=" + detail + '}';
    }
}
