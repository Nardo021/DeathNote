package com.yaazyy.deathnote.forge189;

import com.yaazyy.deathnote.forge189.command.DeathNoteCommands;
import com.yaazyy.deathnote.forge189.item.DeathNoteItem;
import com.yaazyy.deathnote.forge189.network.DeathNoteNetwork;
import com.yaazyy.deathnote.forge189.recipe.Forge189Recipes;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(
        modid = DeathNoteMod.MOD_ID,
        name = DeathNoteMod.MOD_NAME,
        version = DeathNoteMod.VERSION,
        acceptedMinecraftVersions = "[1.8.9]"
)
public final class DeathNoteMod {

    public static final String MOD_ID = "deathnote";
    public static final String MOD_NAME = "Death Note";
    public static final String VERSION = "1.0.0-SNAPSHOT";

    @Instance(MOD_ID)
    public static DeathNoteMod instance;

    @SidedProxy(
            clientSide = "com.yaazyy.deathnote.forge189.ClientProxy",
            serverSide = "com.yaazyy.deathnote.forge189.CommonProxy"
    )
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModContext.init(event.getModConfigurationDirectory(), java.util.logging.Logger.getLogger(MOD_ID));
        DeathNoteNetwork.register();
        DeathNoteItem deathNote = new DeathNoteItem();
        GameRegistry.registerItem(deathNote, "death_note");
        DeathNoteItem.INSTANCE = deathNote;
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Forge189Recipes.register();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new DeathNoteCommands());
    }
}
