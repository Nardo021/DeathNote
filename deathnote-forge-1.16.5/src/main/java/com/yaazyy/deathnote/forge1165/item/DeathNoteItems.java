package com.yaazyy.deathnote.forge1165.item;

import com.yaazyy.deathnote.forge1165.DeathNoteMod;
import com.yaazyy.deathnote.forge1165.ModContext;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = DeathNoteMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DeathNoteItems {

    @ObjectHolder(DeathNoteMod.MOD_ID + ":death_note")
    public static Item DEATH_NOTE;

    private DeathNoteItems() {
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        Item item = new DeathNoteItem(new Item.Properties().maxStackSize(1))
                .setRegistryName(DeathNoteMod.MOD_ID, "death_note");
        event.getRegistry().register(item);
        ModContext.get().bindDeathNoteItem(item);
    }
}
