package com.yaazyy.deathnote.forge1122;

import com.yaazyy.deathnote.forge1122.command.DeathNoteCommand;
import com.yaazyy.deathnote.forge1122.network.DeathNoteNetwork;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(
        modid = DeathNoteMod.MOD_ID,
        name = "Death Note",
        version = "1.0.0-SNAPSHOT",
        acceptedMinecraftVersions = "[1.12.2]"
)
public final class DeathNoteMod {

    public static final String MOD_ID = "deathnote";

    @Mod.Instance(MOD_ID)
    public static DeathNoteMod instance;

    @net.minecraftforge.fml.common.SidedProxy(
            clientSide = "com.yaazyy.deathnote.forge1122.ClientProxy",
            serverSide = "com.yaazyy.deathnote.forge1122.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModContext.init(event.getModLog());
        DeathNoteNetwork.register();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new DeathNoteCommand());
    }
}
