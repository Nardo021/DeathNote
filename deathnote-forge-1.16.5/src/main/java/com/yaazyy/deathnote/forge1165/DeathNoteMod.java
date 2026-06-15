package com.yaazyy.deathnote.forge1165;

import com.yaazyy.deathnote.forge1165.command.DeathNoteCommands;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(DeathNoteMod.MOD_ID)
public final class DeathNoteMod {

    public static final String MOD_ID = "deathnote";

    public DeathNoteMod() {
        ModContext.init(java.util.logging.Logger.getLogger(MOD_ID));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        DeathNoteCommands.register(event.getDispatcher());
    }
}
