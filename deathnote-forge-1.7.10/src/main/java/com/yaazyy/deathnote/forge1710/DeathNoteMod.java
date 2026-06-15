package com.yaazyy.deathnote.forge1710;

import java.util.logging.Logger;

import com.yaazyy.deathnote.forge1710.command.DeathNoteCommand;
import com.yaazyy.deathnote.forge1710.command.DeathNoteDnCommand;
import com.yaazyy.deathnote.forge1710.item.DeathNoteItem;
import com.yaazyy.deathnote.forge1710.network.DeathNoteNetwork;
import com.yaazyy.deathnote.forge1710.recipe.DeathNoteRecipes;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(
        modid = DeathNoteMod.MOD_ID,
        name = DeathNoteMod.MOD_NAME,
        version = DeathNoteMod.VERSION)
public final class DeathNoteMod {

    public static final String MOD_ID = "deathnote";
    public static final String MOD_NAME = "Death Note";
    public static final String VERSION = "1.0.0-SNAPSHOT";

    public static final Logger LOGGER = java.util.logging.Logger.getLogger(MOD_ID);

    @Mod.Instance(MOD_ID)
    public static DeathNoteMod instance;

    @SidedProxy(
            clientSide = "com.yaazyy.deathnote.forge1710.client.ClientProxy",
            serverSide = "com.yaazyy.deathnote.forge1710.CommonProxy")
    public static CommonProxy proxy;

    public DeathNoteItem deathNoteItem;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        deathNoteItem = new DeathNoteItem();
        deathNoteItem.setUnlocalizedName("death_note");
        deathNoteItem.setTextureName("deathnote:death_note");
        GameRegistry.registerItem(deathNoteItem, "death_note");
        ModContext.init(event.getModConfigurationDirectory(), LOGGER);
        DeathNoteRecipes.register(deathNoteItem);
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        DeathNoteNetwork.register();
        FMLCommonHandler.instance().bus().register(new ServerTasks());
        proxy.init();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new DeathNoteCommand());
        event.registerServerCommand(new DeathNoteDnCommand());
    }
}
