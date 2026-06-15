package com.yaazyy.deathnote.neoforge;

import java.util.Optional;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.api.DeathNotePlatform;
import com.yaazyy.deathnote.api.KillExecutionResult;
import com.yaazyy.deathnote.api.KillRequest;

/**
 * SCAFFOLD - NeoForge (modern) platform adapter. Compiles only against the
 * pure-Java platform-api; the Minecraft/NeoForge integration is left as
 * {@code TODO}.
 */
public final class NeoForgeDeathNotePlatform implements DeathNotePlatform {

    @Override
    public Optional<DNPlayer> findOnlinePlayerByName(String name) {
        throw todo(); // TODO: server.getPlayerList().getPlayerByName(name)
    }

    @Override
    public boolean hasPermission(DNPlayer player, String permission) {
        throw todo();
    }

    @Override
    public void sendMessage(DNPlayer player, String message) {
        throw todo();
    }

    @Override
    public void broadcast(String message) {
        throw todo();
    }

    @Override
    public KillExecutionResult executeKill(DNPlayer actor, DNPlayer target, KillRequest request) {
        throw todo();
    }

    @Override
    public void runSync(Runnable task) {
        throw todo();
    }

    @Override
    public void runLater(Runnable task, long ticks) {
        throw todo();
    }

    @Override
    public String platformName() {
        return "NeoForge";
    }

    @Override
    public String platformVersion() {
        return "modern";
    }

    private static UnsupportedOperationException todo() {
        return new UnsupportedOperationException(
                "deathnote-neoforge-modern adapter is a scaffold and is not implemented yet");
    }
}
