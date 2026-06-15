package com.yaazyy.deathnote.forge1122.item;

import com.yaazyy.deathnote.forge1122.DeathNoteMod;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = DeathNoteMod.MOD_ID)
public final class DeathNoteItems {

    public static final DeathNoteItem DEATH_NOTE = new DeathNoteItem();

    private DeathNoteItems() {
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<net.minecraft.item.Item> event) {
        event.getRegistry().register(DEATH_NOTE);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event) {
        net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(
                DEATH_NOTE,
                0,
                new net.minecraft.client.renderer.block.model.ModelResourceLocation(
                        DEATH_NOTE.getRegistryName(), "inventory"));
    }
}
