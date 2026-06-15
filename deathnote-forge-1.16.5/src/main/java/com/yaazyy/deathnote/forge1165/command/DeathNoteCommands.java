package com.yaazyy.deathnote.forge1165.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.yaazyy.deathnote.config.text.TextColors;
import com.yaazyy.deathnote.forge1165.Forge1165DNPlayer;
import com.yaazyy.deathnote.forge1165.ModContext;
import com.yaazyy.deathnote.forge1165.recipe.ForgeRecipeLayout;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

public final class DeathNoteCommands {

    private DeathNoteCommands() {
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> root = Commands.literal("deathnote")
                .then(Commands.literal("give")
                        .requires(DeathNoteCommands::canGive)
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> give(ctx.getSource(),
                                        EntityArgument.getPlayer(ctx, "player")))))
                .then(Commands.literal("reload")
                        .requires(DeathNoteCommands::canAdmin)
                        .executes(ctx -> reload(ctx.getSource())))
                .then(Commands.literal("info")
                        .requires(DeathNoteCommands::canUse)
                        .executes(ctx -> info(ctx.getSource())))
                .then(Commands.literal("recipe")
                        .requires(DeathNoteCommands::canUse)
                        .executes(ctx -> recipe(ctx.getSource())))
                .then(Commands.literal("help")
                        .executes(ctx -> help(ctx.getSource())));

        dispatcher.register(root);
        dispatcher.register(Commands.literal("dn").redirect(dispatcher.getRoot().getChild("deathnote")));
    }

    private static boolean canUse(CommandSource source) {
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            return source.hasPermissionLevel(2);
        }
        return ModContext.get().platform().hasPermission(
                new Forge1165DNPlayer((ServerPlayerEntity) source.getEntity()),
                ModContext.get().config().permissions().use);
    }

    private static boolean canGive(CommandSource source) {
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            return source.hasPermissionLevel(4);
        }
        return ModContext.get().platform().hasPermission(
                new Forge1165DNPlayer((ServerPlayerEntity) source.getEntity()),
                ModContext.get().config().permissions().give);
    }

    private static boolean canAdmin(CommandSource source) {
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            return source.hasPermissionLevel(4);
        }
        return ModContext.get().platform().hasPermission(
                new Forge1165DNPlayer((ServerPlayerEntity) source.getEntity()),
                ModContext.get().config().permissions().admin);
    }

    private static int give(CommandSource source, ServerPlayerEntity target) {
        ModContext mod = ModContext.get();
        ItemStack note = mod.itemBridge().createDeathNoteItem(target.getUniqueID());
        if (!target.inventory.addItemStackToInventory(note)) {
            target.dropItem(note, false);
        }
        target.sendMessage(new StringTextComponent(mod.messages().get("command.received")), target.getUniqueID());
        source.sendFeedback(new StringTextComponent(mod.messages().get("command.gaveItem", "target",
                target.getGameProfile().getName())), true);
        return 1;
    }

    private static int reload(CommandSource source) {
        ModContext.get().reload();
        source.sendFeedback(new StringTextComponent(ModContext.get().messages().get("command.reloaded")), true);
        return 1;
    }

    private static int info(CommandSource source) {
        ModContext mod = ModContext.get();
        source.sendFeedback(new StringTextComponent(TextColors.translate(mod.messages().raw("info.header"))), false);
        source.sendFeedback(new StringTextComponent(" platform: " + mod.platform().platformName()
                + " " + mod.platform().platformVersion()), false);
        source.sendFeedback(new StringTextComponent(" recipe: " + mod.config().recipe().difficultyPreset
                + " (enabled=" + mod.config().recipe().enabled + ")"), false);
        source.sendFeedback(new StringTextComponent(" cooldown: " + mod.config().cooldown().seconds + "s (enabled="
                + mod.config().cooldown().enabled + ")"), false);
        return 1;
    }

    private static int recipe(CommandSource source) {
        ModContext mod = ModContext.get();
        String preset = mod.config().recipe().difficultyPreset;
        String[] shape = ForgeRecipeLayout.shapeForPreset(preset);
        source.sendFeedback(new StringTextComponent(TextColors.translate(mod.messages().raw("recipe.header"))), false);
        source.sendFeedback(new StringTextComponent(" preset: " + preset), false);
        if (shape == null) {
            source.sendFeedback(new StringTextComponent(" (recipe disabled)"), false);
        } else {
            for (String row : shape) {
                source.sendFeedback(new StringTextComponent("  " + spaced(row)), false);
            }
            source.sendFeedback(new StringTextComponent(" W=WitherSkeletonSkull N=NetherStar E=EyeOfEnder"), false);
            source.sendFeedback(new StringTextComponent(" B=WritableBook I=InkSac T=NameTag"), false);
            source.sendFeedback(new StringTextComponent(" G=GhastTear D=DiamondBlock"), false);
        }
        return 1;
    }

    private static int help(CommandSource source) {
        ModContext mod = ModContext.get();
        source.sendFeedback(new StringTextComponent(TextColors.translate(mod.messages().raw("help.header"))), false);
        for (String line : new String[] {
                " /dn give <player>",
                " /dn reload",
                " /dn info",
                " /dn recipe",
                " /dn help"
        }) {
            source.sendFeedback(new StringTextComponent(line), false);
        }
        return 1;
    }

    private static String spaced(String row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length(); i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(row.charAt(i));
        }
        return sb.toString();
    }
}
