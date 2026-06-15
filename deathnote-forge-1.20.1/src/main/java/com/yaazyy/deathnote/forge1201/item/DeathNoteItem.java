package com.yaazyy.deathnote.forge1201.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

/**
 * Death Note item — opens the edit GUI on right-click (client only).
 */
public final class DeathNoteItem extends Item {

    public static final net.minecraftforge.registries.RegistryObject<DeathNoteItem> DEATH_NOTE =
            DeathNoteItems.ITEMS.register("death_note", () -> new DeathNoteItem(new Properties().stacksTo(1)));

    public DeathNoteItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> com.yaazyy.deathnote.forge1201.client.DeathNoteClient.openEditScreen(stack));
        } else {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                            com.yaazyy.deathnote.forge1201.ModContext.get().messages().get("hint.open")),
                    true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
