package com.yaazyy.deathnote.core.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.api.DeathNotePlatform;
import com.yaazyy.deathnote.api.KillExecutionResult;
import com.yaazyy.deathnote.api.KillRequest;

/**
 * In-memory {@link DeathNotePlatform} for unit tests. No real server needed.
 */
public final class FakePlatform implements DeathNotePlatform {

    private final Map<String, DNPlayer> online = new HashMap<>();
    /** Keys formatted as {@code uuid|permission}. */
    private final Set<String> permissions = new HashSet<>();

    private boolean killShouldSucceed = true;
    private String killStrategy = "COMMAND_MINECRAFT_KILL";

    public KillRequest lastKillRequest;
    public DNPlayer lastKillTarget;

    public FakePlatform addOnline(DNPlayer player) {
        online.put(player.name(), player);
        return this;
    }

    public FakePlatform grant(DNPlayer player, String permission) {
        permissions.add(player.uuid() + "|" + permission);
        return this;
    }

    public FakePlatform killSucceeds(boolean value) {
        this.killShouldSucceed = value;
        return this;
    }

    public FakePlatform killStrategy(String strategy) {
        this.killStrategy = strategy;
        return this;
    }

    @Override
    public Optional<DNPlayer> findOnlinePlayerByName(String name) {
        return Optional.ofNullable(online.get(name));
    }

    @Override
    public boolean hasPermission(DNPlayer player, String permission) {
        return permissions.contains(player.uuid() + "|" + permission);
    }

    @Override
    public void sendMessage(DNPlayer player, String message) {
        // no-op for tests
    }

    @Override
    public void broadcast(String message) {
        // no-op for tests
    }

    @Override
    public KillExecutionResult executeKill(DNPlayer actor, DNPlayer target, KillRequest request) {
        this.lastKillRequest = request;
        this.lastKillTarget = target;
        return killShouldSucceed
                ? KillExecutionResult.success(killStrategy)
                : KillExecutionResult.failure("all strategies failed");
    }

    @Override
    public void runSync(Runnable task) {
        task.run();
    }

    @Override
    public void runLater(Runnable task, long ticks) {
        task.run();
    }

    @Override
    public String platformName() {
        return "FakePlatform";
    }

    @Override
    public String platformVersion() {
        return "test";
    }
}
