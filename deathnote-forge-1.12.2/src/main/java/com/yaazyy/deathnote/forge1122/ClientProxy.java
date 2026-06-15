package com.yaazyy.deathnote.forge1122;

import com.yaazyy.deathnote.forge1122.client.DeathNoteEditScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientProxy extends CommonProxy {

    @Override
    public void openEditScreen(ItemStack stack) {
        Minecraft.getMinecraft().displayGuiScreen(new DeathNoteEditScreen(stack));
    }
}
