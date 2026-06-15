package com.yaazyy.deathnote.forge1201.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class DeathNoteClient {

    private DeathNoteClient() {
    }

    public static void openEditScreen(ItemStack stack) {
        Minecraft.getInstance().setScreen(new DeathNoteEditScreen(stack));
    }
}
