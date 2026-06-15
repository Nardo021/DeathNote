package com.yaazyy.deathnote.forge1165.item;

import com.yaazyy.deathnote.forge1165.ModContext;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public final class DeathNoteItem extends Item {

    public DeathNoteItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> com.yaazyy.deathnote.forge1165.client.DeathNoteClient.openEditScreen(stack));
        } else {
            player.sendStatusMessage(
                    new net.minecraft.util.text.StringTextComponent(ModContext.get().messages().get("hint.open")),
                    true);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
}
