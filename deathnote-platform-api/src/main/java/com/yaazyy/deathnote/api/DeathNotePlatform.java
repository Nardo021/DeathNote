package com.yaazyy.deathnote.api;

import java.util.Optional;

/**
 * The bridge the core uses to talk to a concrete server platform.
 *
 * <p>Every method here is implemented by a platform adapter (Bukkit, Forge,
 * Fabric, ...). The core only ever sees this interface, never a platform
 * class.</p>
 */
public interface DeathNotePlatform {

    /** Find a currently online player by exact name. */
    Optional<DNPlayer> findOnlinePlayerByName(String name);

    /** Permission check. */
    boolean hasPermission(DNPlayer player, String permission);

    /** Send a message to a single player. */
    void sendMessage(DNPlayer player, String message);

    /** Broadcast a message to the whole server. */
    void broadcast(String message);

    /**
     * Execute a kill using the platform's capabilities. Implementations must
     * honour the strategy priority order defined by the core and reflected by
     * the toggles inside {@link KillRequest}.
     *
     * <p>Implementations MUST run any command/native call on the main server
     * thread and MUST NOT inject raw user input into a command.</p>
     */
    KillExecutionResult executeKill(DNPlayer actor, DNPlayer target, KillRequest request);

    /** Run a task on the main server thread as soon as possible. */
    void runSync(Runnable task);

    /** Run a task on the main server thread after a tick delay. */
    void runLater(Runnable task, long ticks);

    /** Human-readable platform name, e.g. {@code "Paper"} or {@code "Spigot"}. */
    String platformName();

    /** Server / API version string if available, else a best-effort value. */
    String platformVersion();
}
