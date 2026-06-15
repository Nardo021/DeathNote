package com.yaazyy.deathnote.forge1165;

import java.util.UUID;

import com.yaazyy.deathnote.api.DNPlayer;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public final class Forge1165DNPlayer implements DNPlayer {

    private final UUID uuid;
    private final String name;
    private final boolean op;

    public Forge1165DNPlayer(ServerPlayerEntity player) {
        this.uuid = player.getUniqueID();
        this.name = player.getGameProfile().getName();
        this.op = player.hasPermissionLevel(4);
    }

    public ServerPlayerEntity serverPlayer() {
        if (ServerLifecycleHooks.getCurrentServer() == null) {
            return null;
        }
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(uuid);
    }

    @Override
    public UUID uuid() { return uuid; }

    @Override
    public String name() { return name; }

    @Override
    public boolean isOp() { return op; }

    @Override
    public boolean isOnline() { return serverPlayer() != null; }
}
