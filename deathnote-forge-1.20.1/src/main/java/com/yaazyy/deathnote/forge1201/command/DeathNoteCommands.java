package com.yaazyy.deathnote.forge1201.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.yaazyy.deathnote.config.text.TextColors;
import com.yaazyy.deathnote.forge1201.Forge1201DNPlayer;
import com.yaazyy.deathnote.forge1201.ModContext;
import com.yaazyy.deathnote.forge1201.recipe.ForgeRecipeLayout;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class DeathNoteCommands {

    private DeathNoteCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("deathnote")
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

    private static boolean canUse(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer)) {
            return source.hasPermission(2);
        }
        return ModContext.get().platform().hasPermission(new Forge1201DNPlayer((ServerPlayer) source.getEntity()),
                ModContext.get().config().permissions().use);
    }

    private static boolean canGive(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer)) {
            return source.hasPermission(4);
        }
        return ModContext.get().platform().hasPermission(new Forge1201DNPlayer((ServerPlayer) source.getEntity()),
                ModContext.get().config().permissions().give);
    }

    private static boolean canAdmin(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer)) {
            return source.hasPermission(4);
        }
        return ModContext.get().platform().hasPermission(new Forge1201DNPlayer((ServerPlayer) source.getEntity()),
                ModContext.get().config().permissions().admin);
    }

    private static int give(CommandSourceStack source, ServerPlayer target) {
        ModContext mod = ModContext.get();
        ItemStack note = mod.itemBridge().createDeathNoteItem(target.getUUID());
        if (!target.getInventory().add(note)) {
            target.drop(note, false);
        }
        target.sendSystemMessage(msg(mod, "command.received"));
        source.sendSuccess(() -> msg(mod, "command.gaveItem", "target", target.getGameProfile().getName()), true);
        return 1;
    }

    private static int reload(CommandSourceStack source) {
        ModContext.get().reload();
        source.sendSuccess(() -> msg(ModContext.get(), "command.reloaded"), true);
        return 1;
    }

    private static int info(CommandSourceStack source) {
        ModContext mod = ModContext.get();
        source.sendSuccess(() -> Component.literal(TextColors.translate(mod.messages().raw("info.header"))), false);
        source.sendSuccess(() -> Component.literal(" platform: " + mod.platform().platformName()
                + " " + mod.platform().platformVersion()), false);
        source.sendSuccess(() -> Component.literal(" recipe: " + mod.config().recipe().difficultyPreset
                + " (enabled=" + mod.config().recipe().enabled + ")"), false);
        source.sendSuccess(() -> Component.literal(" cooldown: " + mod.config().cooldown().seconds + "s (enabled="
                + mod.config().cooldown().enabled + ")"), false);
        return 1;
    }

    private static int recipe(CommandSourceStack source) {
        ModContext mod = ModContext.get();
        String preset = mod.config().recipe().difficultyPreset;
        String[] shape = ForgeRecipeLayout.shapeForPreset(preset);
        source.sendSuccess(() -> Component.literal(TextColors.translate(mod.messages().raw("recipe.header"))), false);
        source.sendSuccess(() -> Component.literal(" preset: " + preset), false);
        if (shape == null) {
            source.sendSuccess(() -> Component.literal(" (recipe disabled)"), false);
        } else {
            for (String row : shape) {
                String line = row;
                source.sendSuccess(() -> Component.literal("  " + spaced(line)), false);
            }
            source.sendSuccess(() -> Component.literal(" W=WitherSkeletonSkull N=NetherStar E=EyeOfEnder"), false);
            source.sendSuccess(() -> Component.literal(" B=WritableBook I=InkSac T=NameTag"), false);
            source.sendSuccess(() -> Component.literal(" G=GhastTear D=DiamondBlock"), false);
        }
        return 1;
    }

    private static int help(CommandSourceStack source) {
        ModContext mod = ModContext.get();
        source.sendSuccess(() -> Component.literal(TextColors.translate(mod.messages().raw("help.header"))), false);
        for (String line : new String[] {
                " /dn give <player>",
                " /dn reload",
                " /dn info",
                " /dn recipe",
                " /dn help"
        }) {
            source.sendSuccess(() -> Component.literal(line), false);
        }
        return 1;
    }

    private static Component msg(ModContext mod, String key, String... pairs) {
        return Component.literal(TextColors.translate(mod.messages().get(key, pairs)));
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
