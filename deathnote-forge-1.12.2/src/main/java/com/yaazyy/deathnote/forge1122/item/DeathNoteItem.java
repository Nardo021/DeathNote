package com.yaazyy.deathnote.forge1122.item;

import com.yaazyy.deathnote.forge1122.DeathNoteMod;
import com.yaazyy.deathnote.forge1122.ModContext;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
/**
 * Death Note item — opens the edit GUI on right-click (client only).
 */
public final class DeathNoteItem extends Item {

    public DeathNoteItem() {
        setRegistryName(DeathNoteMod.MOD_ID, "death_note");
        setUnlocalizedName("death_note");
        setCreativeTab(CreativeTabs.MISC);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            com.yaazyy.deathnote.forge1122.DeathNoteMod.proxy.openEditScreen(stack);
        } else {
            player.sendStatusMessage(
                    new net.minecraft.util.text.TextComponentString(
                            ModContext.get().messages().get("hint.open")),
                    true);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}
