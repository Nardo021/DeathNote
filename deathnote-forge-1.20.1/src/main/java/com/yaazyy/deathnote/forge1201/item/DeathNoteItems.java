package com.yaazyy.deathnote.forge1201.item;

import com.yaazyy.deathnote.forge1201.DeathNoteMod;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class DeathNoteItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DeathNoteMod.MOD_ID);

    private DeathNoteItems() {
    }
}
