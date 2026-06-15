package com.yaazyy.deathnote.forge1710.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.yaazyy.deathnote.forge1710.DeathNoteMod;
import com.yaazyy.deathnote.forge1710.ModContext;

/**
 * Death Note item — opens the edit GUI on right-click (client only).
 */
public final class DeathNoteItem extends Item {

    public DeathNoteItem() {
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            DeathNoteMod.proxy.openEditScreen(stack);
        } else {
            player.addChatMessage(new net.minecraft.util.ChatComponentText(
                    ModContext.get().messages().get("hint.open")));
        }
        return stack;
    }
}
