package com.yaazyy.deathnote.forge189.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class DeathNoteClient {

    private DeathNoteClient() {
    }

    public static void openEditScreen(ItemStack stack) {
        Minecraft.getMinecraft().displayGuiScreen(new DeathNoteEditScreen(stack));
    }
}
