package com.yaazyy.deathnote.forge189;

import com.yaazyy.deathnote.forge189.item.DeathNoteItem;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ModelLoader.setCustomModelResourceLocation(
                DeathNoteItem.INSTANCE,
                0,
                new ModelResourceLocation(DeathNoteMod.MOD_ID + ":death_note", "inventory"));
    }
}
