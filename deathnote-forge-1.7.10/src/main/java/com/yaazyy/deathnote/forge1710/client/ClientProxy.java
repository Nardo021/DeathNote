package com.yaazyy.deathnote.forge1710.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.yaazyy.deathnote.forge1710.CommonProxy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientProxy extends CommonProxy {

    @Override
    public void openEditScreen(ItemStack stack) {
        Minecraft.getMinecraft().displayGuiScreen(new DeathNoteEditScreen(stack));
    }
}
