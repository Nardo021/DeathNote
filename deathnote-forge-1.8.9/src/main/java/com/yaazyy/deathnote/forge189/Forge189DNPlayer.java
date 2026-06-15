package com.yaazyy.deathnote.forge189;

import java.util.UUID;

import com.yaazyy.deathnote.api.DNPlayer;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/** Forge 1.8.9 {@link DNPlayer} wrapper. */
public final class Forge189DNPlayer implements DNPlayer {

    private final UUID uuid;
    private final String name;
    private final boolean op;

    public Forge189DNPlayer(EntityPlayerMP player) {
        this.uuid = player.getUniqueID();
        this.name = player.getName();
        this.op = player.canCommandSenderUseCommand(4, "give");
    }

    public Forge189DNPlayer(UUID uuid, String name, boolean op) {
        this.uuid = uuid;
        this.name = name;
        this.op = op;
    }

    public EntityPlayerMP serverPlayer() {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            return null;
        }
        return server.getConfigurationManager().getPlayerByUUID(uuid);
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
