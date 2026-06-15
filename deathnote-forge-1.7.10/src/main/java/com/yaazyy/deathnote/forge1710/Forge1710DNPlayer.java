package com.yaazyy.deathnote.forge1710;

import java.util.UUID;

import com.yaazyy.deathnote.api.DNPlayer;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/** Forge 1.7.10 {@link DNPlayer} wrapper. */
public final class Forge1710DNPlayer implements DNPlayer {

    private final UUID uuid;
    private final String name;
    private final boolean op;

    public Forge1710DNPlayer(EntityPlayerMP player) {
        this.uuid = player.getUniqueID();
        this.name = player.getCommandSenderName();
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        this.op = server != null && server.getConfigurationManager().func_152596_g(player.getGameProfile());
    }

    public Forge1710DNPlayer(UUID uuid, String name, boolean op) {
        this.uuid = uuid;
        this.name = name;
        this.op = op;
    }

    public EntityPlayerMP serverPlayer() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) {
            return null;
        }
        for (Object entry : server.getConfigurationManager().playerEntityList) {
            EntityPlayerMP player = (EntityPlayerMP) entry;
            if (player.getUniqueID().equals(uuid)) {
                return player;
            }
        }
        return null;
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
