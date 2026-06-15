package com.yaazyy.deathnote.forge1122;

import java.util.UUID;

import com.yaazyy.deathnote.api.DNPlayer;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

/** Forge 1.12.2 {@link DNPlayer} wrapper. */
public final class Forge1122DNPlayer implements DNPlayer {

    private final UUID uuid;
    private final String name;
    private final boolean op;

    public Forge1122DNPlayer(EntityPlayerMP player) {
        this.uuid = player.getUniqueID();
        this.name = player.getName();
        this.op = player.canUseCommand(4, "deathnote");
    }

    public Forge1122DNPlayer(UUID uuid, String name, boolean op) {
        this.uuid = uuid;
        this.name = name;
        this.op = op;
    }

    public EntityPlayerMP serverPlayer() {
        if (FMLCommonHandler.instance().getMinecraftServerInstance() == null) {
            return null;
        }
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid);
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
