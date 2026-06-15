package com.yaazyy.deathnote.forge189.item;

import com.yaazyy.deathnote.forge189.DeathNoteMod;
import com.yaazyy.deathnote.forge189.ModContext;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Death Note item — opens the edit GUI on right-click (client only).
 */
public final class DeathNoteItem extends Item {

    public static DeathNoteItem INSTANCE;

    public DeathNoteItem() {
        setUnlocalizedName(DeathNoteMod.MOD_ID + ".death_note");
        setRegistryName("death_note");
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            openEditScreen(stack);
        } else {
            player.addChatMessage(new ChatComponentText(ModContext.get().messages().get("hint.open")));
        }
        return stack;
    }

    @SideOnly(Side.CLIENT)
    private void openEditScreen(ItemStack stack) {
        com.yaazyy.deathnote.forge189.client.DeathNoteClient.openEditScreen(stack);
    }
}
