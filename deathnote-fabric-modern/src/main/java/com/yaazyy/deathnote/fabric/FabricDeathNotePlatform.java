package com.yaazyy.deathnote.fabric;

import java.util.Optional;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.api.DeathNotePlatform;
import com.yaazyy.deathnote.api.KillExecutionResult;
import com.yaazyy.deathnote.api.KillRequest;

/**
 * SCAFFOLD - Fabric (modern) platform adapter. Compiles only against the
 * pure-Java platform-api; the Minecraft/Fabric integration is left as
 * {@code TODO}.
 */
public final class FabricDeathNotePlatform implements DeathNotePlatform {

    @Override
    public Optional<DNPlayer> findOnlinePlayerByName(String name) {
        throw todo(); // TODO: server.getPlayerManager().getPlayer(name)
    }

    @Override
    public boolean hasPermission(DNPlayer player, String permission) {
        throw todo(); // TODO: integrate a permissions API (e.g. fabric-permissions-api)
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
        throw todo(); // TODO: server.execute(task)
    }

    @Override
    public void runLater(Runnable task, long ticks) {
        throw todo();
    }

    @Override
    public String platformName() {
        return "Fabric";
    }

    @Override
    public String platformVersion() {
        return "modern";
    }

    private static UnsupportedOperationException todo() {
        return new UnsupportedOperationException(
                "deathnote-fabric-modern adapter is a scaffold and is not implemented yet");
    }
}
