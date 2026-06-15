package com.yaazyy.deathnote.forge1201;

import com.yaazyy.deathnote.forge1201.command.DeathNoteCommands;
import com.yaazyy.deathnote.forge1201.item.DeathNoteItems;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DeathNoteMod.MOD_ID)
public final class DeathNoteMod {

    public static final String MOD_ID = "deathnote";

    public DeathNoteMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        DeathNoteItems.ITEMS.register(modBus);

        ModContext.init(java.util.logging.Logger.getLogger(MOD_ID));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        DeathNoteCommands.register(event.getDispatcher());
    }
}
