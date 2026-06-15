package com.yaazyy.deathnote.forge1201;

import java.util.UUID;

import com.yaazyy.deathnote.api.DNPlayer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

/** Forge 1.20.1 {@link DNPlayer} wrapper. */
public final class Forge1201DNPlayer implements DNPlayer {

    private final UUID uuid;
    private final String name;
    private final boolean op;

    public Forge1201DNPlayer(ServerPlayer player) {
        this.uuid = player.getUUID();
        this.name = player.getGameProfile().getName();
        this.op = player.hasPermissions(4);
    }

    public Forge1201DNPlayer(UUID uuid, String name, boolean op) {
        this.uuid = uuid;
        this.name = name;
        this.op = op;
    }

    public ServerPlayer serverPlayer() {
        if (ServerLifecycleHooks.getCurrentServer() == null) {
            return null;
        }
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isOp() {
        return op;
    }

    @Override
    public boolean isOnline() {
        return serverPlayer() != null;
    }
}
